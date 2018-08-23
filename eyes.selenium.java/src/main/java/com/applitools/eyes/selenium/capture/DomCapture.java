package com.applitools.eyes.selenium.capture;

import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class DomCapture {

    private static final InputStream captureFrameScriptStream = DomCapture.class.getResourceAsStream("/CaptureFrame.js");
    private static String captureFrameScript;

    static {
        try {
            captureFrameScript = GeneralUtils.readToEnd(captureFrameScriptStream);
        } catch (IOException e) {
            GeneralUtils.logExceptionStackTrace(e);
        }
    }

    public static String getWindowDOM(WebDriver driver) {
        HashMap<String, Object> argsObj = new HashMap<>();
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

        HashMap<String, Object> props = new HashMap<>();
        props.put("all", new String[]{"id", "class"});
        props.put("IMG", new String[]{"src"});
        props.put("IFRAME", new String[]{"src"});
        props.put("A", new String[]{"href"});

        argsObj.put("attributeProps", props);

        argsObj.put("rectProps", new String[]{
                "width",
                "height",
                "top",
                "left"}
        );
        argsObj.put("ignoredTagNames", new String[]{
                "HEAD",
                "SCRIPT"}
        );

        Map<String, Object> result = getFrameDom(driver, argsObj);
        String domJson = null;
        ObjectMapper jsonMapper = new ObjectMapper();
        try {
            domJson = jsonMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            GeneralUtils.logExceptionStackTrace(e);
        }
        return domJson;
    }

    private static Map<String, Object> getFrameDom(WebDriver driver, Map<String, Object> argsObj) {
        Object result = ((JavascriptExecutor) driver).executeScript(captureFrameScript, argsObj);
        //noinspection unchecked
        Map<String, Object> resultAsMap = (Map<String, Object>) result;
        populateIFrames(driver, argsObj, resultAsMap, new Stack<Integer>());
        return resultAsMap;
    }

    @SuppressWarnings("unchecked")
    private static void populateIFrames(WebDriver driver, Map<String, Object> argsObj, Map<String, Object> result, Stack<Integer> indexTrail) {
        if (!result.containsKey("tagName") || !result.containsKey("childNodes")) {
            return;
        }

        Object tagNameObj = result.get("tagName");
        Object childNodesObj = result.get("childNodes");

        String tagName = (String) tagNameObj;
        boolean isIframe = tagName.compareToIgnoreCase("IFRAME") == 0;

        List<Object> childNodes = (List<Object>) childNodesObj;

        int index = 0;

        if (isIframe && childNodes.size() == 0) {
            for (int frameIndex : indexTrail) {
                driver.switchTo().frame(frameIndex);
            }
            Map<String, Object> dom = (Map<String, Object>) ((JavascriptExecutor) driver).executeScript(captureFrameScript, argsObj);
            result.put("childNodes", dom);
            populateIFrames(driver, argsObj, dom, (Stack<Integer>) indexTrail.clone());
            for (int i = 0; i < indexTrail.size(); ++i) {
                driver.switchTo().parentFrame();
            }
        } else {
            for (Object node : childNodes) {
                if (node instanceof Map) {
                    Map<String, Object> dict = (Map<String, Object>) node;
                    Stack<Integer> childIndexTrail = (Stack<Integer>) indexTrail.clone();
                    if (isIframe) {
                        childIndexTrail.push(index++);
                    }
                    populateIFrames(driver, argsObj, dict, childIndexTrail);
                }
            }
        }
    }
}
