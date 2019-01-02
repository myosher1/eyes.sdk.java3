package com.applitools.eyes.visualGridClient.model;

import com.applitools.eyes.RectangleSize;

public class RenderBrowserInfo {

    private RectangleSize viewportSize;
    private RenderingConfiguration.BrowserType browserType;
    private String platform = "linux";
    private EmulationInfo emulationInfo = null;
    private String sizeMode = "full-page";
    private String environment;


    public RenderBrowserInfo(int width, int height, RenderingConfiguration.BrowserType browserType, EmulationInfo emulationInfo, String environment) {
        this.environment = environment;
        this.viewportSize = new RectangleSize(width, height);
        this.browserType = browserType;
        this.emulationInfo = emulationInfo;
    }

    public RenderBrowserInfo(int width, int height, RenderingConfiguration.BrowserType browserType, String environment) {
        this.environment = environment;
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

    public void setBrowserType(RenderingConfiguration.BrowserType browserType) {
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

    public String getEnvironment() {
        return environment;
    }
}