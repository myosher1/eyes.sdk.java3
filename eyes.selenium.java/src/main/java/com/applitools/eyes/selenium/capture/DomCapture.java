package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.IDownloadListener;
import com.applitools.eyes.IServerConnector;
import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.concurrent.Phaser;

public class DomCapture {


    private static String CAPTURE_FRAME_SCRIPT;

    private static String CAPTURE_CSSOM_SCRIPT;
    private final Phaser treePhaser = new Phaser(1); // Phaser for syncing all callbacks on a single Frame
    private final Phaser mainPhaser = new Phaser(1); // Phaser for syncing all Frames


    static {
        try {
            CAPTURE_FRAME_SCRIPT = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/captureDom.js"));
            CAPTURE_CSSOM_SCRIPT = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/capturecssom.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static IServerConnector mServerConnector = null;
    private WebDriver driver;
    private final Logger logger;

    public DomCapture(Eyes eyes) {
        mServerConnector = eyes.getServerConnector();
        logger = eyes.getLogger();
    }

    public String getFullWindowDom(WebDriver driver, PositionProvider positionProvider) {
        this.driver = driver;
        Location initialPosition = positionProvider.getCurrentPosition();
        positionProvider.setPosition(Location.ZERO);
        Map dom = GetWindowDom();
        positionProvider.setPosition(initialPosition);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            String json = objectMapper.writeValueAsString(dom);
            return json;
        } catch (JsonProcessingException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return "";
    }

    public Map<String, Object> GetWindowDom() {

        Map argsObj = initMapDom();

        Map<String, Object> result = getFrameDom(argsObj);

        return result;
    }

    private Map initMapDom() {

        Map argsObj = new HashMap();
        argsObj.put("styleProps", new String[]{
                "background-color",
                "background-image",
                "background-size",
                "color",
                "border-width",
                "border-color",
                "border-style",
                "padding",
                "margin"
        });

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

    private Map<String, Object> getFrameDom(Map<String, Object> argsObj) {
        logger.verbose("Trying to get DOM from driver");
        long startingTime = System.currentTimeMillis();

        String scripts = "var callback = arguments[arguments.length - 1]; return (" + CAPTURE_FRAME_SCRIPT + ")().then(callback, function(err) {callback('__ERROR__:' + (err.stack || err.toString()))})";

        String executeScripString = (String) ((JavascriptExecutor) driver).executeAsyncScript(scripts);

        logger.verbose("Finished capturing DOM in - " + (System.currentTimeMillis() - startingTime));
        startingTime = System.currentTimeMillis();

        final Map<String, Object> executeScriptMap;
        try {
            executeScriptMap = GeneralUtils.parseJsonToObject(executeScripString);

        } catch (IOException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            return null;

        }

        logger.verbose("Finished converting DOM map in - " + (System.currentTimeMillis() - startingTime));
        startingTime = System.currentTimeMillis();

        try {
            traverseDomTree(driver, argsObj, executeScriptMap, -1, new URL(driver.getCurrentUrl()));
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }

        mainPhaser.arriveAndAwaitAdvance();

        logger.verbose("Finished going over DOM CSS in - " + (System.currentTimeMillis() - startingTime));

        return executeScriptMap;
    }

    private Stack<Integer> frameIndices = new Stack<>();


    private void traverseDomTree(WebDriver mDriver, Map<String, Object> argsObj, final Map<String, Object> domTree
            , int frameIndex, URL baseUrl) {

        logger.verbose("DomCapture.traverseDomTree  baseUrl - " + baseUrl);

        Map<String, Object> dom = null;


        Object tagNameObj = domTree.get("tagName");

        boolean frameHasContent = true;

        if (null == tagNameObj) return;

        if (frameIndex > -1) {

            //Try switching - if frame index is valid
            try {
                mDriver.switchTo().frame(frameIndex);

                frameIndices.push(0);

            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
                mDriver.switchTo().parentFrame();
                return;
            }

            String srcUrl = null;

            Object childNodes = domTree.get("childNodes");
            List childNodesAsMap = null;
            if (childNodes != null) {
                childNodesAsMap = (List) childNodes;
            }

            if (childNodesAsMap == null || childNodesAsMap.isEmpty()) {

                String json = (String) ((JavascriptExecutor) mDriver).executeScript(CAPTURE_FRAME_SCRIPT, argsObj);

                try {
                    dom = GeneralUtils.parseJsonToObject(json);
                } catch (IOException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }

                if (dom != null) {

                    frameHasContent = false;

                    domTree.put("childNodes", new Object[]{dom});

                    Object attrsNodeObj = domTree.get("attributes");
                    if (null != attrsNodeObj) {
                        Map<String, Object> attrsNode = (Map<String, Object>) attrsNodeObj;

                        Object srcUrlObj = attrsNode.get("src");
                        if (null != srcUrlObj) {
                            srcUrl = (String) srcUrlObj;
                        }
                    }

                    if (srcUrl == null) {
                        logger.log("WARNING! IFRAME WITH NO SRC");
                    } else {
                        try {
                            URL urlHref = new URL(baseUrl, srcUrl);
                            traverseDomTree(mDriver, argsObj, dom, -1, urlHref);

                        } catch (MalformedURLException e) {
                            GeneralUtils.logExceptionStackTrace(logger, e);
                        }
                    }
                }
            }
            frameIndices.pop();
            mDriver.switchTo().parentFrame();
        }

        if (frameHasContent) {
            String tagName = (String) tagNameObj;
            boolean isHTML = tagName.equalsIgnoreCase("HTML");

            if (isHTML) {
                mainPhaser.register();
                getFrameBundledCss(baseUrl, new IDownloadListener<String>() {
                    @Override
                    public void onDownloadComplete(String downloadedString, String contentType) {
                        domTree.put("css", downloadedString);
                        logger.verbose("Putting css in " + " - CSS = " + downloadedString);
                        mainPhaser.arriveAndDeregister();
                    }

                    @Override
                    public void onDownloadFailed() {
                        logger.verbose("mainPhaser.arriveAndDeregister()");
                        mainPhaser.arriveAndDeregister();

                    }
                });
                logger.verbose("Finish getFrameBundledCss(baseUrl)");
            }

            loop(mDriver, argsObj, domTree, baseUrl);
        }

    }

    private void loop(WebDriver mDriver, Map<String, Object> argsObj, Map<String, Object> domTree, URL baseUrl) {
        logger.verbose("DomCapture.loop");
        Object childNodesObj = domTree.get("childNodes");
        int index = -1;
        if (frameIndices.size() > 0) {
            index = frameIndices.peek();
        }
        if (!(childNodesObj instanceof List)) {
            return;
        }
        List childNodes = (List) childNodesObj;
        for (Object node : childNodes) {
            if (node instanceof Map) {
                final Map<String, Object> domSubTree = (Map<String, Object>) node;

                logger.verbose("Current DOM subtree hash : " + domSubTree.hashCode());

                Object tagNameObj = domSubTree.get("tagName");

                String tagName = (String) tagNameObj;
                boolean isIframe = tagName.equalsIgnoreCase("IFRAME");

                if (isIframe) {
                    if (frameIndices.size() > 0) {
                        frameIndices.pop();
                    } else {
                        logger.verbose("frameIndices size is 0");
                    }
                    frameIndices.push(index + 1);
                    traverseDomTree(mDriver, argsObj, domSubTree, index, baseUrl);
                    index++;
                } else {
                    Object childSubNodesObj = domSubTree.get("childNodes");
                    if (childSubNodesObj == null || (childSubNodesObj instanceof List) && ((List) childSubNodesObj).isEmpty()) {
                        continue;
                    }
                    traverseDomTree(mDriver, argsObj, domSubTree, -1, baseUrl);
                }
            }
        }
        logger.verbose("DomCapture.loop - finish");
    }


    private void getFrameBundledCss(final URL baseUrl, IDownloadListener listener) {
        String baseUrlStr = baseUrl.toString();
        URI uri = URI.create(baseUrlStr);
        if (!uri.isAbsolute()) {
            logger.log("WARNING! Base URL is not an absolute URL!");
            logger.log("uri: " + uri);
        }
        CssTreeNode root = new CssTreeNode();
        root.setBaseUrl(baseUrl);

        List<String> result = (List<String>) ((JavascriptExecutor) driver).executeScript(CAPTURE_CSSOM_SCRIPT);
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
                cssTreeNode.setBaseUrl(root.baseUrl);
                cssTreeNode.setUrlPostfix(value);
                downloadCss(cssTreeNode, new IDownloadListener<String>() {
                    @Override
                    public void onDownloadComplete(String downloadedString, String contentType) {
                        logger.verbose("DomCapture.onDownloadComplete");

                        parseCSS(cssTreeNode, downloadedString);
                        if (cssTreeNode.allImportRules != null && !cssTreeNode.allImportRules.isEmpty()) {

                            cssTreeNode.downloadNodeCss();
                        }
                    }

                    @Override

                    public void onDownloadFailed() {
                        logger.verbose("DomCapture.onDownloadFailed");
                    }
                });
                nodes.add(cssTreeNode);
            }
        }
        root.setDecedents(nodes);
        treePhaser.arriveAndAwaitAdvance();
        listener.onDownloadComplete(root.calcCss(), "String");
    }

    class CssTreeNode {

        URL baseUrl;

        URL urlPostfix;

        StringBuilder sb = new StringBuilder();
        List<CssTreeNode> decedents = new ArrayList<>();
        ICommonsList<CSSImportRule> allImportRules;
        ICommonsList<CSSStyleRule> styleRules;

        public void setDecedents(List<CssTreeNode> decedents) {
            this.decedents = decedents;
        }


        public void setBaseUrl(URL baseUri) {
            this.baseUrl = baseUri;
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
            if (allImportRules != null) {

                for (CSSImportRule importRule : allImportRules) {
                    final CssTreeNode cssTreeNode;
                    cssTreeNode = new CssTreeNode();
                    cssTreeNode.setBaseUrl(this.baseUrl);
                    String uri = importRule.getLocation().getURI();
                    cssTreeNode.setUrlPostfix(uri);
                    downloadCss(cssTreeNode, new IDownloadListener<String>() {
                        @Override
                        public void onDownloadComplete(String downloadedString, String contentType) {
                            parseCSS(cssTreeNode, downloadedString);
                            if (!cssTreeNode.allImportRules.isEmpty()) {
                                cssTreeNode.downloadNodeCss();

                            }
                        }

                        @Override
                        public void onDownloadFailed() {
                            logger.verbose("Download Failed");
                        }
                    });
                    decedents.add(cssTreeNode);
                }
            }
        }

        public void setUrlPostfix(String urlPostfix) {

            boolean absolute = false;
            try {
                urlPostfix = URLEncoder.encode(urlPostfix, "UTF-8");
                absolute = new URI(urlPostfix).isAbsolute();
                this.urlPostfix = absolute ? new URL(urlPostfix) : new URL(baseUrl, urlPostfix);
            } catch (UnsupportedEncodingException | URISyntaxException | MalformedURLException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }


        public void setAllImportRules(ICommonsList<CSSImportRule> allImportRules) {
            this.allImportRules = allImportRules;
        }

        public void setAllStyleRules(ICommonsList<CSSStyleRule> allStyleRules) {
            this.styleRules = allStyleRules;
        }


    }

    private void downloadCss(final CssTreeNode node, final IDownloadListener<String> listener) {
        treePhaser.register();
        logger.verbose("Given URL to download: " + node.urlPostfix);
        mServerConnector.downloadString(node.urlPostfix, false, new IDownloadListener<String>() {
            @Override
            public void onDownloadComplete(String downloadedString, String contentType) {
                try {
                    logger.verbose("Download Complete");
                    listener.onDownloadComplete(downloadedString, "String");

                } catch (Exception e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                } finally {
                    treePhaser.arriveAndDeregister();
                    logger.verbose("treePhaser.arriveAndDeregister(); " + node.urlPostfix);
                    logger.verbose("current missing - " + treePhaser.getUnarrivedParties());
                }
            }

            @Override
            public void onDownloadFailed() {
                treePhaser.arriveAndDeregister();
                logger.verbose("Download Failed");
                logger.verbose("treePhaser.arriveAndDeregister(); " + node.urlPostfix);
                logger.verbose("current missing  - " + treePhaser.getUnarrivedParties());
            }
        });
    }

    private void parseCSS(CssTreeNode node, String css) {
        if (css == null) {
            return;
        }

        final CascadingStyleSheet cascadingStyleSheet = CSSReader.readFromString(css, ECSSVersion.CSS30);
        if (cascadingStyleSheet == null) {
            return;
        }

        ICommonsList<ICSSTopLevelRule> allRules = cascadingStyleSheet.getAllRules();
        if (allRules.isEmpty()) {
            return;
        }

        ICommonsList<CSSImportRule> allImportRules = cascadingStyleSheet.getAllImportRules();
        node.setAllImportRules(allImportRules);
        node.setAllStyleRules(cascadingStyleSheet.getAllStyleRules());
    }

}



