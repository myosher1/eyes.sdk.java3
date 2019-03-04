package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.IDownloadListener;
import com.applitools.eyes.IServerConnector;
import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesTargetLocator;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.EfficientStringReplace;
import com.applitools.utils.GeneralUtils;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.css.ECSSVersion;
import com.helger.css.decl.CSSImportRule;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.ICSSTopLevelRule;
import com.helger.css.reader.CSSReader;
import com.helger.css.writer.CSSWriterSettings;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DomCapture {
    private final String SEPARATOR = "separator";
    private final String CSS_START_TOKEN = "cssStartToken";
    private final String CSS_END_TOKEN = "cssEndToken";
    private final String IFRAME_START_TOKEN = "iframeStartToken";
    private final String IFRAME_END_TOKEN = "iframeEndToken";

    private static String CAPTURE_FRAME_SCRIPT;

    private final Phaser cssPhaser = new Phaser(); // Phaser for syncing all callbacks on a single Frame


    static {
        try {
            CAPTURE_FRAME_SCRIPT = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/captureDom.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static IServerConnector mServerConnector = null;
    private EyesWebDriver driver;
    private final Logger logger;
    private String cssStartToken;
    private String cssEndToken;
    private Map<String, String> cssData = Collections.synchronizedMap(new HashMap<String, String>());
    private String separator;
    private String frameEndToken;
    private String frameStartToken;
    private boolean shouldWaitForPhaser = false;

    public DomCapture(Eyes eyes) {
        mServerConnector = eyes.getServerConnector();
        logger = eyes.getLogger();
        driver = (EyesWebDriver) eyes.getDriver();
    }


    public String getFullWindowDom() {
        long currentTimeMillis = System.currentTimeMillis();
        String domJson = getDom();
        String timeDiff = String.valueOf(System.currentTimeMillis() - currentTimeMillis);
        logger.verbose("getting th DOM took " + timeDiff + " ms");
        return domJson;
    }

    public String getFullWindowDom(PositionProvider positionProvider) {
        PositionMemento originalPosition = positionProvider.getState();
        positionProvider.setPosition(Location.ZERO);
        String domJson = getDom();
        positionProvider.restoreState(originalPosition);
        return domJson;
    }

    private String getDom() {
        FrameChain originalFC = driver.getFrameChain();

        String dom = getFrameDom();

        if (originalFC != null) {
            ((EyesTargetLocator) driver.switchTo()).frames(originalFC);
        }

        try {

            if (shouldWaitForPhaser) {
                cssPhaser.awaitAdvanceInterruptibly(0, 30, TimeUnit.SECONDS);
            }

        } catch (InterruptedException | TimeoutException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        String inlaidString = EfficientStringReplace.efficientStringReplace(cssStartToken, cssEndToken, dom, cssData);
        return inlaidString;
    }

    private String getFrameDom() {
        logger.verbose("Trying to get DOM from driver");

        String scripts = "var callback = arguments[arguments.length - 1]; return (" + CAPTURE_FRAME_SCRIPT + ")().then(callback, function(err) {callback('__ERROR__:' + (err.stack || err.toString()))})";

        String executeScripString = (String) ((JavascriptExecutor) driver).executeAsyncScript(scripts);

        List<String> missingCssList = new ArrayList<>();
        List<String> missingFramesList = new ArrayList<>();
        List<String> data = new ArrayList<>();

        parseScriptResult(executeScripString, missingCssList, missingFramesList, data);

        fetchCssFiles(missingCssList);

        Map<String, String> framesData = null;
        try {
            framesData = recurseFrames(missingFramesList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String inlaidString = EfficientStringReplace.efficientStringReplace(frameStartToken, frameEndToken, data.get(0), framesData);

        return inlaidString;
    }

    private Map<String, String> recurseFrames(List<String> missingFramesList) {
        Map<String, String> framesData = new HashMap<>();
        EyesTargetLocator switchTo = (EyesTargetLocator) driver.switchTo();

        FrameChain fc = driver.getFrameChain().clone();
        for (String missingFrameLine : missingFramesList) {
            logger.verbose("Switching to frame line :" + missingFrameLine);
            String originLocation  = (String) driver.executeScript("return document.location.href");
            try {
                String[] missingFrameXpaths = missingFrameLine.split(",");
                for (String missingFrameXpath : missingFrameXpaths) {
                    logger.verbose("switching to specific frame : " + missingFrameXpath);
                    WebElement frame = driver.findElement(By.xpath((missingFrameXpath)));
                    logger.verbose("Switched to frame(" + missingFrameXpath + ") with src(" + frame.getAttribute("src") + ")");
                    switchTo.frame(frame);
                }
                String locationAfterSwitch  = (String) driver.executeScript("return document.location.href");
                if(locationAfterSwitch.equals(originLocation)){
                    logger.verbose("Switching to frame failed");
                    framesData.put(missingFrameLine, "");
                   continue;
                }
                String result = getFrameDom();
                framesData.put(missingFrameLine, result);
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
                framesData.put(missingFrameLine, "");
            }
            switchTo.frames(fc);
        }

        return framesData;
    }

    private void fetchCssFiles(List<String> missingCssList) {
        for (final String missingCssUrl : missingCssList) {
            if (missingCssUrl.startsWith("blob:")) {
                logger.log("Found blob url continuing - " + missingCssUrl);
                continue;
            }
            if (missingCssUrl.isEmpty()) continue;
            try {
                final CssTreeNode cssTreeNode = new CssTreeNode(new URL(missingCssUrl));
                downloadCss(cssTreeNode, new IDownloadListener<String>() {
                    @Override
                    public void onDownloadComplete(String downloadedString, String contentType) {
                        logger.verbose("DomCapture.onDownloadComplete");
                        parseCSS(cssTreeNode, downloadedString);
                        if (cssTreeNode.allImportRules != null && !cssTreeNode.allImportRules.isEmpty()) {
                            cssTreeNode.downloadNodeCss();
                        }
                        cssData.put(missingCssUrl, cssTreeNode.calcCss());
                    }

                    @Override
                    public void onDownloadFailed() {
                        logger.verbose("DomCapture.onDownloadFailed");
                        cssData.put(missingCssUrl, "");
                    }
                });

            } catch (MalformedURLException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

    private void parseScriptResult(String executeScripString, final List<String> missingCssList, final List<String> missingFramesList, final List<String> data) {

        List<List<String>> blocks = new ArrayList<>();
        blocks.add(missingCssList);
        blocks.add(missingFramesList);
        blocks.add(data);

        int index = 0;
        final Map<String, Object> separatorsMap;
        String[] splited = new String[0];
        try {

            //Get first line (first line is a JSON)
            separatorsMap = GeneralUtils.parseJsonToObject(splitLines(executeScripString)[0]);

            cssStartToken = (String) separatorsMap.get(CSS_START_TOKEN);
            cssEndToken = (String) separatorsMap.get(CSS_END_TOKEN);
            frameEndToken = (String) separatorsMap.get(IFRAME_END_TOKEN);
            frameStartToken = (String) separatorsMap.get(IFRAME_START_TOKEN);
            separator = (String) separatorsMap.get(SEPARATOR);

            //Remove first line(first line is the JSON)
            executeScripString = executeScripString.substring(executeScripString.indexOf('\n') + 1);
            splited = executeScripString.split(separator);

        } catch (IOException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);

        }

        removeFirstCharBR(splited);

        for (int i = 0; i < splited.length; i++) {
            String block = splited[i];
            List<String> linesList = Arrays.asList(splitLines(block));
            for (String str : linesList) {
                if (!str.isEmpty()) blocks.get(i).add(str);
            }
        }

        shouldWaitForPhaser |= !missingCssList.isEmpty();
    }

    private void removeFirstCharBR(String[] splited) {
        for (int i = 0; i < splited.length; i++) {
            String s = splited[i];
            if (s.startsWith("\n")) {
                splited[i] = s.replaceFirst("\n", "");
            }
        }
    }

    private String[] splitLines(String block) {
        return block.split(System.getProperty("line.separator"));
    }

    class CssTreeNode {
        URL url;
        String css;
        StringBuilder sb = new StringBuilder();
        List<CssTreeNode> decedents = new ArrayList<>();
        ICommonsList<CSSImportRule> allImportRules;
        ICommonsList<CSSStyleRule> styleRules;


        public CssTreeNode(URL url) {
            this.url = url;
        }

        public void setDecedents(List<CssTreeNode> decedents) {
            this.decedents = decedents;
        }

        String calcCss() {
            sb.append(css);
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

        public void setCss(String css) {
            this.css = css;
        }

        void downloadNodeCss() {
            if (allImportRules != null) {
                for (CSSImportRule importRule : allImportRules) {
                    final CssTreeNode cssTreeNode;
                    try {
                        cssTreeNode = new CssTreeNode(new URL(importRule.getLocation().getURI()));
                        String uri = importRule.getLocation().getURI();
                        cssTreeNode.setUrl(uri);
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
                    } catch (MalformedURLException e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                    }

                }
            }
        }

        public void setUrl(String url) {

            boolean absolute = false;
            try {
                url = URLEncoder.encode(url, "UTF-8");
                absolute = new URI(url).isAbsolute();
                this.url = absolute ? new URL(url) : new URL(url);
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
        cssPhaser.register();
        logger.verbose("Given URL to download: " + node.url);
        mServerConnector.downloadString(node.url, false, new IDownloadListener<String>() {
            @Override
            public void onDownloadComplete(String downloadedString, String contentType) {
                try {
                    logger.verbose("Download Complete");
                    node.setCss(URLEncoder.encode(downloadedString, "UTF-8"));
                    listener.onDownloadComplete(downloadedString, "String");

                } catch (Exception e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                } finally {
                    cssPhaser.arriveAndDeregister();
                    logger.verbose("cssPhaser.arriveAndDeregister(); " + node.url);
                    logger.verbose("current missing - " + cssPhaser.getUnarrivedParties());
                }
            }

            @Override
            public void onDownloadFailed() {
                cssPhaser.arriveAndDeregister();
                logger.verbose("Download Failed");
                logger.verbose("cssPhaser.arriveAndDeregister(); " + node.url);
                logger.verbose("current missing  - " + cssPhaser.getUnarrivedParties());
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



