package com.applitools.eyes.visualGridClient.data;

import com.applitools.eyes.Region;
import com.applitools.eyes.config.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderingConfiguration extends Configuration {

    public static final String BEFORE_CAPTURE_SCREENSHOT = "beforeCaptureScreenshot";

    public RenderingConfiguration() {

    }

    public enum SizeMode{FULL_PAGE, VIEWPORT, SELECTOR, REGION}


    public enum BrowserType{CHROME, FIREFOX}
    private List<RenderBrowserInfo> browsersInfo = new ArrayList<>();

    private int concurrentSessions = 3;
    private boolean isThrowExceptionOn = false;

    private String testName = null;

    public RenderingConfiguration(int concurrentSessions, boolean isThrowExceptionOn, String testName) {
        this.concurrentSessions = concurrentSessions;
        this.isThrowExceptionOn = isThrowExceptionOn;
        this.testName = testName;
    }

    public RenderingConfiguration addBrowser(int width, int height, BrowserType browserType){
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(width, height, browserType);
        browsersInfo.add(browserInfo);
        return this;
    }

    public int getConcurrentSessions() {
        return concurrentSessions;
    }

    public class RenderBrowserInfo{

        private int width;

        private int height;
        private BrowserType browserType;
        private String platform = "linux";
        private EmulationInfo emulationInfo = null;
        private String sizeMode;
        public RenderBrowserInfo(int width, int height, BrowserType browserType, EmulationInfo emulationInfo) {
            this.width = width;
            this.height = height;
            this.browserType = browserType;
            this.emulationInfo = emulationInfo;
        }

        public RenderBrowserInfo(int width, int height, BrowserType browserType) {
            this.width = width;
            this.height = height;
            this.browserType = browserType;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getBrowserType() {
            switch (this.browserType){
                case CHROME:
                    return "chrome";
                case FIREFOX:
                    return "firefox";
            }
            return "";
        }

        public void setBrowserType(BrowserType browserType) {
            this.browserType = browserType;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public EmulationInfo getEmulationInfo() {
            return emulationInfo;
        }

        public String getSizeMode() {
            return this.sizeMode;
        }

    }
    public class checkRGSettings{

        private SizeMode sizeMode = SizeMode.FULL_PAGE;
        private String selector;
        private Region region;
        private Map<String, List<String>> scriptHooks = new HashMap<>();
        private boolean isSendDom;

        public checkRGSettings(SizeMode sizeMode, String selector, Region region, boolean isSendDom) {
            this.sizeMode = sizeMode;
            this.selector = selector;
            this.region = region;
            this.isSendDom = isSendDom;
        }

        public void addScriptHook(String script){

            List<String> scripts = this.scriptHooks.get(BEFORE_CAPTURE_SCREENSHOT);
            if (scripts == null) {
                scripts = new ArrayList<>();
                this.scriptHooks.put(BEFORE_CAPTURE_SCREENSHOT, scripts);
            }
            scripts.add(script);
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

    public List<RenderBrowserInfo> getBrowsersInfo() {
        return browsersInfo;
    }

    public void setBrowsersInfo(List<RenderBrowserInfo> browsersInfo) {
        this.browsersInfo = browsersInfo;
    }

    public boolean isThrowExceptionOn() {
        return isThrowExceptionOn;
    }

    public void setThrowExceptionOn(boolean throwExceptionOn) {
        isThrowExceptionOn = throwExceptionOn;
    }

    @Override
    public String getTestName() {
        return testName;
    }

    @Override
    public void setTestName(String testName) {
        this.testName = testName;
    }
}
