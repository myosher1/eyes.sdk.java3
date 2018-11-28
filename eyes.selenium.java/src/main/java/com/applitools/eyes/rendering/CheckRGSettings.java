package com.applitools.eyes.rendering;

import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.CheckSettings;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckRGSettings extends CheckSettings {

    public static final String BEFORE_CAPTURE_SCREENSHOT = "beforeCaptureScreenshot";

    public enum SizeMode{FULL_PAGE, VIEWPORT, SELECTOR, REGION}

    private SizeMode sizeMode = SizeMode.FULL_PAGE;
    private String selector;
    private Region region;
    private Map<String, List<String>> scriptHooks = new HashMap<>();
    private boolean isSendDom;

    public CheckRGSettings() {
    }

    public CheckRGSettings(SizeMode sizeMode, String selector, Region region, boolean isSendDom) {
        this.sizeMode = sizeMode;
        this.selector = selector;
        this.region = region;
        this.isSendDom = isSendDom;
    }

    @JsonProperty("sizeMode")
    public String getSizeMode() {
        switch (this.sizeMode){
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

    public void setSizeMode(SizeMode sizeMode) {
        this.sizeMode = sizeMode;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void addScriptHook(String script){
        List<String> hooks = this.scriptHooks.get(BEFORE_CAPTURE_SCREENSHOT);
        if (hooks == null) {
            hooks = new ArrayList<>();
            this.scriptHooks.put(BEFORE_CAPTURE_SCREENSHOT, hooks);
        }
        hooks.add(script);
    }

    public Map<String, List<String>> getScriptHooks() {
        return scriptHooks;
    }

    public boolean isSendDom() {
        return isSendDom;
    }

    public void setSendDom(boolean sendDom) {
        isSendDom = sendDom;
    }
}
