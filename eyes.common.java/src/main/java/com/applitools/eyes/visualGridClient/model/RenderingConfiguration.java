package com.applitools.eyes.visualGridClient.model;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class RenderingConfiguration extends Configuration {


    public RenderingConfiguration() {
    }

    private RenderingConfiguration(RenderingConfiguration configuration) {
        super(configuration);
        this.browsersInfo = configuration.browsersInfo;
        this.concurrentSessions = configuration.concurrentSessions;
        this.isThrowExceptionOn = configuration.isThrowExceptionOn;
        this.testName = configuration.testName;
    }

    public enum BrowserType {CHROME, FIREFOX}

    private List<RenderBrowserInfo> browsersInfo = new ArrayList<>();

    private int concurrentSessions = 3;
    private boolean isThrowExceptionOn = false;

    private String testName = null;

    public RenderingConfiguration(int concurrentSessions, boolean isThrowExceptionOn, String testName) {
        this.concurrentSessions = concurrentSessions;
        this.isThrowExceptionOn = isThrowExceptionOn;
        this.testName = testName;
    }

    public RenderingConfiguration addBrowser(int width, int height, BrowserType browserType) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(width, height, browserType);
        browsersInfo.add(browserInfo);
        return this;
    }

    public int getConcurrentSessions() {
        return concurrentSessions;
    }

    public class RenderBrowserInfo {

        private RectangleSize viewportSize;
        private BrowserType browserType;
        private String platform = "linux";
        private EmulationInfo emulationInfo = null;
        private String sizeMode = "full-page";

        public RenderBrowserInfo(int width, int height, BrowserType browserType, EmulationInfo emulationInfo) {
            this.viewportSize = new RectangleSize(width, height);
            this.browserType = browserType;
            this.emulationInfo = emulationInfo;
        }

        public RenderBrowserInfo(int width, int height, BrowserType browserType) {
            this.viewportSize = new RectangleSize(width, height);
            this.browserType = browserType;
        }

        public int getWidth() {
            return viewportSize.getWidth();
        }

        public int getHeight() {
            return viewportSize.getHeight();
        }

        public RectangleSize getViewportSize() {
            return viewportSize;
        }

        public String getBrowserType() {
            switch (this.browserType) {
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

    @Override
    public RenderingConfiguration cloneConfig() {
        return new RenderingConfiguration(this);
    }
}
