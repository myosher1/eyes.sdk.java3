package com.applitools.eyes.visualGridClient.data;

import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CheckRGSettings {

    public enum SizeMode {FULL_PAGE, VIEWPORT, SELECTOR, REGION}

    public static final String BEFORE_CAPTURE_SCREENSHOT = "beforeCaptureScreenshot";

    private SizeMode sizeMode = SizeMode.FULL_PAGE;
    private String selector;
    private Region region;
    private Map<String, List<String>> scriptHooks = new HashMap<>();
    private boolean isSendDom;

    public CheckRGSettings(SizeMode sizeMode, String selector, Region region, boolean isSendDom) {
        this.sizeMode = sizeMode;
        this.selector = selector;
        this.region = region;
        this.isSendDom = isSendDom;
    }

    public void addScriptHook(String script) {

        List<String> scripts = this.scriptHooks.get(BEFORE_CAPTURE_SCREENSHOT);
        if (scripts == null) {
            scripts = new ArrayList<>();
            this.scriptHooks.put(BEFORE_CAPTURE_SCREENSHOT, scripts);
        }
        scripts.add(script);
    }

    @JsonProperty("sizeMode")
    public String getSizeMode() {
        switch (this.sizeMode) {
            case REGION:
                return "region";
            case SELECTOR:
                return "selector";
            case VIEWPORT:
                return "viewport";
            case FULL_PAGE:
                return "full-page";
        }
        return null;
    }

    public String getSelector() {
        return selector;
    }

    public Region getRegion() {
        return region;
    }

    public Map<String, List<String>> getScriptHooks() {
        return scriptHooks;
    }

    public boolean isSendDom() {
        return isSendDom;
    }
}

