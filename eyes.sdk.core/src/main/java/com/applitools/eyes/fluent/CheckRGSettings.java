package com.applitools.eyes.fluent;

import com.applitools.ICheckRGSettings;
import com.applitools.ICheckRGSettingsInternal;
import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckRGSettings extends CheckSettings implements ICheckRGSettings, ICheckRGSettingsInternal, Cloneable {

    public enum SizeMode {FULL_PAGE, VIEWPORT, SELECTOR, REGION}

    private static final String BEFORE_CAPTURE_SCREENSHOT = "beforeCaptureScreenshot";

    private SizeMode sizeMode;
    private String selector;
    private Region region;
    private Map<String, List<String>> scriptHooks = new HashMap<>();
    private boolean isSendDom;

    public CheckRGSettings() {
        this.sizeMode = SizeMode.VIEWPORT;
    }

    public CheckRGSettings(String selector) {
        this.selector = selector;
        this.sizeMode = SizeMode.SELECTOR;
    }

    public CheckRGSettings(Region region) {
        this.region = region;
        this.sizeMode = SizeMode.REGION;
    }

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

    @Override
    public ICheckRGSettings sendDom(boolean sendDom) {
        CheckRGSettings clone = this.clone();
        clone.isSendDom = sendDom;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckRGSettings fully() {
        CheckRGSettings clone = (CheckRGSettings) super.fully();
        clone.sizeMode = SizeMode.FULL_PAGE;
        return clone;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckRGSettings fully(boolean fully) {
        CheckRGSettings clone = (CheckRGSettings) super.fully(fully);
        clone.sizeMode = fully ? SizeMode.FULL_PAGE : SizeMode.VIEWPORT;
        return clone;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public CheckRGSettings clone(){
        CheckRGSettings clone = new CheckRGSettings();
        populateClone(clone);
        clone.sizeMode = this.sizeMode;
        clone.region = this.region;
        clone.selector = this.selector;
        clone.isSendDom = this.isSendDom;
        return clone;
    }
}
