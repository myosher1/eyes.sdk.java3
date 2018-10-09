package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.IServerConnector;
import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.SACParserCSS3;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleSheet;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class DomCapture {


    private static String CAPTURE_FRAME_SCRIPT;

    private static String CAPTURE_CSSOM_SCRIPT;

    static {
        try {
            CAPTURE_FRAME_SCRIPT = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/captureframe.js"));

            CAPTURE_CSSOM_SCRIPT = GeneralUtils.readToEnd(DomCapture.class.getResourceAsStream("/capturecssom.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static IServerConnector mServerConnector = null;
    private final Logger mLogger;

    public DomCapture(Eyes eyes) {
        mServerConnector = eyes.getServerConnector();
        mLogger = eyes.getLogger();
    }

    public String getFullWindowDom(WebDriver driver) {

        Map dom = GetWindowDom(driver);

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

    public Map<String, Object> GetWindowDom(WebDriver driver) {

        Map argsObj = initMapDom();

        Map<String, Object> result = getFrameDom_(driver, argsObj);

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
                "margin"});

        Map attrMap = new HashMap();
        attrMap.put("all", new String[]{"id", "class"});
        attrMap.put("IMG", new String[]{"src"});
        attrMap.put("IFRAME", new String[]{"src"});
        attrMap.put("A", new String[]{"href"});


        argsObj.put("attributeProps", attrMap);
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

    private Map<String, Object> getFrameDom_(WebDriver driver, Map<String, Object> argsObj) {
        System.out.println("Trying to get DOM from driver");
        long startingTime = System.currentTimeMillis();
        Map<String, Object> executeScriptMap = (Map<String, Object>) ((JavascriptExecutor) driver).executeScript(CAPTURE_FRAME_SCRIPT, argsObj);
        System.out.println("Finished capturing DOM in - " + (System.currentTimeMillis() - startingTime));
        startingTime = System.currentTimeMillis();
        Map<String, Object> domTree = transformToMap(executeScriptMap);
        System.out.println("Finished converting DOM map in - " + (System.currentTimeMillis() - startingTime));
        Stack<String> baseUrls = new Stack<>();
        baseUrls.push(driver.getCurrentUrl());
        startingTime = System.currentTimeMillis();
        traverseDomTree_(driver, argsObj, domTree, -1, driver.getCurrentUrl());
        System.out.println("Finished going over DOM CSS in - " + (System.currentTimeMillis() - startingTime));
        return domTree;
    }


    //Converting the object that was returned from the driver to a simple HashMap<String,Object>
    private Map<String, Object> transformToMap(Map<String, Object> executeScriptMap) {
        Map<String, Object> transformedMap = new HashMap<>();
        for (String key : executeScriptMap.keySet()) {
            Object value = executeScriptMap.get(key);
            transformedMap.put(key, parseValue(value));
        }
        return transformedMap;
    }

    private Object parseValue(Object value) {
        if (value instanceof Map) {
            return transformToMap((Map<String, Object>) value);
        }
        if (value instanceof List) {
            List valueAsList = (List) value;
            ArrayList<Object> newList = new ArrayList<>();
            for (Object o : valueAsList) {
                newList.add(parseValue(o));
            }
            return newList;
        } else {
            return value;
        }

    }


    private void traverseDomTree_(WebDriver driver, Map<String, Object> argsObj, Map<String, Object> domTree
            , int frameIndex, String baseUrl) {

        Object childNodesObj = domTree.get("childNodes");
        if (null == childNodesObj) return;

        Object tagNameObj = domTree.get("tagName");
        if (null == tagNameObj) return;

        if (frameIndex > -1) {
            driver.switchTo().frame(frameIndex);

            Map<String, Object> dom = (Map<String, Object>) ((JavascriptExecutor) driver).executeScript(CAPTURE_FRAME_SCRIPT, argsObj);
            Map<String, Object> parsedDom = transformToMap(dom);
            domTree.put("childNodes", new Object[]{parsedDom});

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
            traverseDomTree_(driver, argsObj, dom, -1, srcUrl);
            driver.switchTo().parentFrame();
        }
        String tagName = (String) tagNameObj;
        boolean isHTML = tagName.equalsIgnoreCase("HTML");
        if (isHTML) {
            URI baseUri = null;
            try {
                baseUri = new URI(baseUrl);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String css = getFrameBundledCss(driver, baseUri);
            domTree.put("css", css);
        }

        loop(driver, argsObj, domTree, baseUrl);
    }

    private void loop(WebDriver driver, Map<String, Object> argsObj, Map<String, Object> domTree, String baseUrl) {
        List childNodes = (List) domTree.get("childNodes");
        if (childNodes == null) {
            return;
        }
        int index = 0;
        for (Object node : childNodes) {
            if (node instanceof Map) {
                Map<String, Object> domSubTree = (Map<String, Object>) node;
                Object tagNameObj = domSubTree.get("tagName");

                String tagName = (String) tagNameObj;
                boolean isIframe = tagName.equalsIgnoreCase("IFRAME");

                if (isIframe) {
                    traverseDomTree_(driver, argsObj, domSubTree, index, baseUrl);
                    index++;
                }
            }
        }
    }


    public String getFrameBundledCss(WebDriver driver, URI baseUrl) {
        if (!baseUrl.isAbsolute()) {
            mLogger.log("WARNING! Base URL is not an absolute URL!");
        }
        StringBuilder sb = new StringBuilder();
        List<String> result = (List<String>) ((JavascriptExecutor) driver).executeScript(CAPTURE_CSSOM_SCRIPT);
        for (String item : result) {
            String kind = item.substring(0, 5);
            String value = item.substring(5);
            String css;
            if (kind.equalsIgnoreCase("text:")) {
                css = value;
            } else {
                css = downloadCss(baseUrl, value);

            }

            if (css == null) {
                continue;
            }
            CSSStyleSheet stylesheet;

            InputSource source = new InputSource(new StringReader(css));
            CSSOMParser parser = new CSSOMParser(new SACParserCSS3());
            try {

                stylesheet = parser.parseStyleSheet(source, null, null);
                CSSRuleList cssRules = stylesheet.getCssRules();
                for (int i = 0; i < cssRules.getLength(); i++) {
                    CSSRule cssRule = cssRules.item(i);
                    sb.append(cssRule.getCssText());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        return sb.toString();
    }


    private String downloadCss(URI baseUrl, String value) {
        String response = null;
        try {
            mLogger.verbose("Given URL to download: " + value);
            URI href = new URI(value);
            if (!href.isAbsolute()) {
                href = new URI(baseUrl.getScheme(), baseUrl.getHost(), "/" + href.getPath(), href.getFragment());
            }
            response = mServerConnector.downloadString(href);
        } catch (Exception ex) {
            mLogger.log(ex.getMessage());
        }
        return response;
    }
}



