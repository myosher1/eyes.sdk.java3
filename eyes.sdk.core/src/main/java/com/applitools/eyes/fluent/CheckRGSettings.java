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

    private static final String BEFORE_CAPTURE_SCREENSHOT = "beforeCaptureScreenshot";

    private String selector;
    private Region region;
    private Map<String, List<String>> scriptHooks = new HashMap<>();
    private boolean isSendDom;

    public CheckRGSettings() {
        super.setStitchContent(true);
    }

    public CheckRGSettings(String selector) {
        this.selector = selector;
    }

    public CheckRGSettings(Region region) {
        this.region = region;
    }

    public CheckRGSettings(String selector, Region region, boolean isSendDom) {
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
        if (region == null && selector == null) {
            if (getStitchContent()) {
                return "full-page";
            } else {
                return "viewport";
            }
        } else if (region != null) {
            if (getStitchContent()) {
                return "region";
            } else {
                return "region";
            }
        } else /* if (selector != null) */ {
            if (getStitchContent()) {
                return "selector";
            } else {
                return "selector";
            }
        }
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
        return (CheckRGSettings) super.fully();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckRGSettings fully(boolean fully) {
        return (CheckRGSettings) super.fully(fully);
    }

    @Override
    public ICheckRGSettings withName(String name) {
        return (CheckRGSettings) super.withName(name);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public CheckRGSettings clone() {
        CheckRGSettings clone = new CheckRGSettings();
        populateClone(clone);
        clone.region = this.region;
        clone.selector = this.selector;
        clone.isSendDom = this.isSendDom;
        return clone;
    }
}
