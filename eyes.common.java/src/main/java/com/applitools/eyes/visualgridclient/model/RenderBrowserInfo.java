package com.applitools.eyes.visualgridclient.model;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.config.SeleniumConfiguration;

public class RenderBrowserInfo {

    private RectangleSize viewportSize;
    private SeleniumConfiguration.BrowserType browserType;
    private final String platform = "linux";
    private EmulationBaseInfo emulationInfo;
    private final String sizeMode = "full-page";
    private String baselineEnvName;


    public RenderBrowserInfo(RectangleSize viewportSize, SeleniumConfiguration.BrowserType browserType, String baselineEnvName) {
        this.viewportSize = viewportSize;
        this.browserType = browserType;
        this.baselineEnvName = baselineEnvName;
    }

    public RenderBrowserInfo(RectangleSize viewportSize, SeleniumConfiguration.BrowserType browserType) {
        this.viewportSize = viewportSize;
        this.browserType = browserType;
    }

    public RenderBrowserInfo(EmulationBaseInfo emulationInfo, String baselineEnvName) {
        this.emulationInfo = emulationInfo;
        this.baselineEnvName = baselineEnvName;
    }

    public RenderBrowserInfo(EmulationBaseInfo emulationInfo) {
        this.emulationInfo = emulationInfo;
    }

    public RenderBrowserInfo(int width, int height, SeleniumConfiguration.BrowserType browserType, String baselineEnvName) {
        this(new RectangleSize(width, height), browserType, baselineEnvName);
    }

    public RenderBrowserInfo(int width, int height, SeleniumConfiguration.BrowserType browserType) {
        this(new RectangleSize(width, height), browserType, null);
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

    public void setBrowserType(SeleniumConfiguration.BrowserType browserType) {
        this.browserType = browserType;
    }

    public String getPlatform() {
        return platform;
    }

    public EmulationBaseInfo getEmulationInfo() {
        return emulationInfo;
    }

    public String getSizeMode() {
        return this.sizeMode;
    }

    @Override
    public String toString() {
        return "RenderBrowserInfo{" +
                "viewportSize=" + viewportSize +
                ", browserType=" + browserType +
                ", platform='" + platform + '\'' +
                ", emulationInfo=" + emulationInfo +
                ", sizeMode='" + sizeMode + '\'' +
                '}';
    }

    public String getBaselineEnvName() {
        return baselineEnvName;
    }
}
