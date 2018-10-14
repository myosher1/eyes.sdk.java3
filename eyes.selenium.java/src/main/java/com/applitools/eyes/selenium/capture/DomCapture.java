package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.IDownloadListener;
import com.applitools.eyes.IServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.css.ECSSVersion;
import com.helger.css.decl.CSSImportRule;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ICSSTopLevelRule;
import com.helger.css.reader.CSSReader;
import com.helger.css.writer.CSSWriterSettings;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;

public class DomCapture {


    private static String CAPTURE_FRAME_SCRIPT;

    private static String CAPTURE_CSSOM_SCRIPT;
    private final Phaser treePhaser = new Phaser(1); // Phaser for syncing all callbacks on a single Frame
    private final Phaser mainPhaser = new Phaser(1); // Phaser for syncing all Frames


    static {
        try {
            CAPTURE_FRAME_SCRIPT = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/captureframe.js"));

            CAPTURE_CSSOM_SCRIPT = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/capturecssom.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static IServerConnector mServerConnector = null;
    private WebDriver mDriver;
    private final Logger mLogger;

    public DomCapture(Eyes eyes) {
        mServerConnector = eyes.getServerConnector();
        mLogger = eyes.getLogger();
    }

    public String getFullWindowDom(WebDriver driver) {
        this.mDriver = driver;
        Map dom = GetWindowDom();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            String json = objectMapper.writeValueAsString(dom);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Map<String, Object> GetWindowDom() {

        Map argsObj = initMapDom();

        Map<String, Object> result = getFrameDom_(argsObj);

        return result;
    }

    private Map initMapDom() {

        Map argsObj = new HashMap();
        argsObj.put("styleProps", new String[0]);

        argsObj.put("attributeProps", null);
        argsObj.put("rectProps", new String[]{
                "right",
                "bottom",
                "height",
                "width",
                "top",
                "left"});
        argsObj.put("ignoredTagNames", new String[]{
                "HEAD",
                "SCRIPT"});

        return argsObj;
    }

    private Map<String, Object> getFrameDom_(Map<String, Object> argsObj) {
        mLogger.verbose("Trying to get DOM from mDriver");
        long startingTime = System.currentTimeMillis();
        String executeScripString = (String) ((JavascriptExecutor) mDriver).executeScript(CAPTURE_FRAME_SCRIPT, argsObj);

        mLogger.verbose("Finished capturing DOM in - " + (System.currentTimeMillis() - startingTime));
        startingTime = System.currentTimeMillis();

        final Map<String, Object> executeScriptMap;
        try {
            executeScriptMap = parseStringToMap(executeScripString);

        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }

        mLogger.verbose("Finished converting DOM map in - " + (System.currentTimeMillis() - startingTime));
        startingTime = System.currentTimeMillis();

        traverseDomTree(mDriver, argsObj, executeScriptMap, -1, mDriver.getCurrentUrl());

        mainPhaser.arriveAndAwaitAdvance();

        mLogger.verbose("Finished going over DOM CSS in - " + (System.currentTimeMillis() - startingTime));

        return executeScriptMap;
    }

    private int level = 0;

    private void traverseDomTree(WebDriver mDriver, Map<String, Object> argsObj, final Map<String, Object> domTree
            , int frameIndex, String baseUrl) {

        mLogger.verbose("DomCapture.traverseDomTree  baseUrl - " + baseUrl);

        final Map<String, Object> dom;

        Object tagNameObj = domTree.get("tagName");
        if (null == tagNameObj) return;

        if (frameIndex > -1) {

            level++;

            mDriver.switchTo().frame(frameIndex);

            String json = (String) ((JavascriptExecutor) mDriver).executeScript(CAPTURE_FRAME_SCRIPT, argsObj);

            try {
                dom = parseStringToMap(json);
                domTree.put("childNodes", new Object[]{dom});
                String srcUrl = null;
                Object attrsNodeObj = domTree.get("attributes");
                if (null != attrsNodeObj) {
                    Map<String, Object> attrsNode = (Map<String, Object>) attrsNodeObj;

                    Object srcUrlObj = attrsNode.get("src");
                    if (null != srcUrlObj) {
                        srcUrl = (String) srcUrlObj;
                    }
                }
                if (srcUrl == null) {
                    mLogger.log("WARNING! IFRAME WITH NO SRC");
                }
                URI href = new URI(srcUrl);
                if (!href.isAbsolute()) {
                    String baseUrlTmp = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
                    String slash = baseUrlTmp.endsWith("/") ? "" : "/";
                    srcUrl = baseUrlTmp + slash + srcUrl;
                }
                traverseDomTree(mDriver, argsObj, dom, -1, srcUrl);
                mDriver.switchTo().parentFrame();
                level--;

            } catch (IOException | URISyntaxException e) {
                GeneralUtils.logExceptionStackTrace(e);
            }

        } else {
            String tagName = (String) tagNameObj;
            boolean isHTML = tagName.equalsIgnoreCase("HTML");
            if (isHTML) {
                URI baseUri = null;
                try {
                    baseUri = new URI(baseUrl);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                mLogger.verbose("Trying to get CSS");
                mainPhaser.register();
                getFrameBundledCss(baseUri, new IDownloadListener() {
                    @Override
                    public void onDownloadComplete(String downloadedString) {
                        domTree.put("css", downloadedString);
                        mLogger.verbose("Putting css in "  + " - CSS = "+downloadedString);
                        mainPhaser.arriveAndDeregister();
                    }

                    @Override
                    public void onDownloadFailed() {

                    }
                });
                mLogger.verbose("Finish getFrameBundledCss(baseUri)");
            }

            loop(mDriver, argsObj, domTree, baseUrl);
            mLogger.verbose("DomCapture.traverseDomTree - finish - Url - " + baseUrl);
        }
    }

    private void loop(WebDriver mDriver, Map<String, Object> argsObj, Map<String, Object> domTree, String baseUrl) {
        mLogger.verbose("DomCapture.loop");
        Object childNodesObj = domTree.get("childNodes");
        if (childNodesObj == null || !(childNodesObj instanceof List)) {
            return;
        }
        List childNodes = (List) childNodesObj;
        int index = 0;
        for (Object node : childNodes) {
            if (node instanceof Map) {
                final Map<String, Object> domSubTree = (Map<String, Object>) node;

                mLogger.verbose("Current DOM subtree hash : "+domSubTree.hashCode());

                Object tagNameObj = domSubTree.get("tagName");

                String tagName = (String) tagNameObj;
                boolean isIframe = tagName.equalsIgnoreCase("IFRAME");

                if (isIframe) {
                    traverseDomTree(mDriver, argsObj, domSubTree, index, baseUrl);
                    index++;
                } else {
                    Object childSubNodesObj = domSubTree.get("childNodes");
                    if (childSubNodesObj == null || !(childSubNodesObj instanceof List) || ((List) childSubNodesObj).isEmpty()) {
                        continue;
                    }
                    traverseDomTree(mDriver, argsObj, domSubTree, -1, baseUrl);
                }
            }
        }
        mLogger.verbose("DomCapture.loop - finish");
    }


    private void getFrameBundledCss(final URI baseUrl, IDownloadListener listener) {
        if (!baseUrl.isAbsolute()) {
            mLogger.log("WARNING! Base URL is not an absolute URL!");
        }
        CssTreeNode root = new CssTreeNode();
        root.setBaseUri(baseUrl);
        List<String> result = (List<String>) ((JavascriptExecutor) mDriver).executeScript(CAPTURE_CSSOM_SCRIPT);
        final List<CssTreeNode> nodes = new ArrayList<>();
        for (String item : result) {
            String kind = item.substring(0, 5);
            //Value can be either css style or link to a css file
            String value = item.substring(5);
            if (kind.equalsIgnoreCase("text:")) {
                parseCSS(root, value);
                root.downloadNodeCss();
            } else {
                final CssTreeNode cssTreeNode = new CssTreeNode();
                cssTreeNode.setBaseUri(root.baseUri);
                cssTreeNode.setUrlPostfix(value);
                downloadCss(cssTreeNode, new IDownloadListener() {
                    @Override
                    public void onDownloadComplete(String downloadedString) {
                        mLogger.verbose("DomCapture.onDownloadComplete");

                        parseCSS(cssTreeNode, downloadedString);
                        if (!cssTreeNode.allImportRules.isEmpty()) {

                            cssTreeNode.downloadNodeCss();
                        }
                    }

                    @Override

                    public void onDownloadFailed() {
                        mLogger.verbose("DomCapture.onDownloadFailed");
                    }
                });
                nodes.add(cssTreeNode);
            }
        }

        root.setDecedents(nodes);
        treePhaser.arriveAndAwaitAdvance();
        listener.onDownloadComplete(root.calcCss());
    }

    class CssTreeNode {

        URI baseUri;

        String urlPostfix;

        StringBuilder sb = new StringBuilder();
        List<CssTreeNode> decedents = new ArrayList<>();
        ICommonsList<CSSImportRule> allImportRules;
        ICommonsList<CSSStyleRule> styleRules;

        public void setDecedents(List<CssTreeNode> decedents) {
            this.decedents = decedents;
        }


        public void setBaseUri(URI baseUri) {
            this.baseUri = baseUri;
        }

        String calcCss() {
            if (decedents != null) {
                for (CssTreeNode decedent : decedents) {
                    sb.append(decedent.calcCss());
                }
            }

            if (styleRules != null) {
                for (CSSStyleRule styleRule : styleRules) {
                    sb.append(styleRule.getAsCSSString(new CSSWriterSettings()));
                }
            }

            return sb.toString();
        }

        void downloadNodeCss() {
            for (CSSImportRule importRule : allImportRules) {
                final CssTreeNode cssTreeNode;
                cssTreeNode = new CssTreeNode();
                cssTreeNode.setBaseUri(this.baseUri);
                String uri = importRule.getLocation().getURI();
                cssTreeNode.setUrlPostfix(uri);
                downloadCss(cssTreeNode, new IDownloadListener() {
                    @Override
                    public void onDownloadComplete(String downloadedString) {
                        parseCSS(cssTreeNode, downloadedString);
                        if (!cssTreeNode.allImportRules.isEmpty()) {
                            cssTreeNode.downloadNodeCss();

                        }
                    }

                    @Override
                    public void onDownloadFailed() {
                        mLogger.verbose("Download Failed");
                    }
                });
                decedents.add(cssTreeNode);
            }
        }

        public void setUrlPostfix(String urlPostfix) {
            this.urlPostfix = urlPostfix;
        }


        public void setAllImportRules(ICommonsList<CSSImportRule> allImportRules) {
            this.allImportRules = allImportRules;
        }

        public void setAllStyleRules(ICommonsList<CSSStyleRule> allStyleRules) {
            this.styleRules = allStyleRules;
        }


    }

    private void downloadCss(CssTreeNode node, final IDownloadListener listener) {
        try {
            treePhaser.register();
            mLogger.verbose("treePhaser.register();");
            mLogger.verbose("Given URL to download: " + node.urlPostfix);
            URI href = new URI(node.urlPostfix);
            if (!href.isAbsolute()) {
                String path = node.baseUri.getPath();
                //Remove postfix“
                path = path.substring(0, path.lastIndexOf("/"));
                href = new URI(node.baseUri.getScheme(), node.baseUri.getHost(), path + "/" + node.urlPostfix, null);
            }
            mServerConnector.downloadString(href, new IDownloadListener() {
                @Override
                public void onDownloadComplete(String downloadedString) {
                    mLogger.verbose("Download Complete");
                    listener.onDownloadComplete(downloadedString);
                    treePhaser.arriveAndDeregister();
                    mLogger.verbose("treePhaser.arriveAndDeregister();");
                }

                @Override
                public void onDownloadFailed() {
                    mLogger.verbose("Download Faild");
                    treePhaser.arriveAndDeregister();
                    mLogger.verbose("treePhaser.arriveAndDeregister();");
                }
            });
        } catch (Exception ex) {
            mLogger.verbose("DomCapture.downloadCss ");
            ex.printStackTrace();
            mLogger.verbose(ex.getMessage());
            mLogger.log(ex.getMessage());
        }
    }

    private void parseCSS(CssTreeNode node, String css) {
        if (css == null) {
            return;
        }
        CascadingStyleSheet cascadingStyleSheet = CSSReader.readFromString(css, ECSSVersion.CSS30);
        if (cascadingStyleSheet == null) {
            return;
        }
        ICommonsList<ICSSTopLevelRule> allRules = cascadingStyleSheet.getAllRules();
        if (allRules == null) {
            return;
        }

        final CascadingStyleSheet aCSS = CSSReader.readFromString(css, ECSSVersion.CSS30);
        ICommonsList<CSSImportRule> allImportRules = aCSS.getAllImportRules();
        node.setAllImportRules(allImportRules);
        node.setAllStyleRules(aCSS.getAllStyleRules());
    }

    private Map<String, Object> parseStringToMap(String executeScripString) throws IOException {
        Map<String, Object> executeScriptMap;
        ObjectMapper mapper = new ObjectMapper();
        executeScriptMap = mapper.readValue(executeScripString, new TypeReference<Map<String, Object>>() {
        });
        return executeScriptMap;
    }



}



