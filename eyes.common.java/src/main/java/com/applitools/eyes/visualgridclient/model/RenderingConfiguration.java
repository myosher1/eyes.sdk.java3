package com.applitools.eyes.visualgridclient.model;

import com.applitools.eyes.config.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenderingConfiguration extends Configuration {


    public enum BrowserType {CHROME, FIREFOX}

    private List<RenderBrowserInfo> browsersInfo = new ArrayList<>();

    public RenderingConfiguration() {
    }

    private RenderingConfiguration(RenderingConfiguration configuration) {
        super(configuration);
        this.browsersInfo = configuration.browsersInfo;
        this.concurrentSessions = configuration.concurrentSessions;
        this.isThrowExceptionOn = configuration.isThrowExceptionOn;
        this.testName = configuration.testName;
    }

    private int concurrentSessions = 3;
    private boolean isThrowExceptionOn = false;

    private String testName = null;

    public RenderingConfiguration(int concurrentSessions, boolean isThrowExceptionOn, String testName) {
        this.concurrentSessions = concurrentSessions;
        this.isThrowExceptionOn = isThrowExceptionOn;
        this.testName = testName;
    }

    public RenderingConfiguration addBrowsers(RenderBrowserInfo... browsersInfo) {
        this.browsersInfo.addAll(Arrays.asList(browsersInfo));
        return this;
    }

    public RenderingConfiguration addBrowser(RenderBrowserInfo browserInfo) {
        browsersInfo.add(browserInfo);
        return this;
    }

    public RenderingConfiguration addBrowser(int width, int height, BrowserType browserType, String baselineEnvName, EmulationBaseInfo emulationBaseInfo) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(width, height, browserType, baselineEnvName, emulationBaseInfo);
        addBrowser(browserInfo);
        return this;
    }
    public RenderingConfiguration addBrowser(int width, int height, BrowserType browserType, String baselineEnvName) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(width, height, browserType, baselineEnvName, null);
        addBrowser(browserInfo);
        return this;
    }
    public RenderingConfiguration addBrowser(int width, int height, BrowserType browserType) {
        return addBrowser(width, height, browserType, null, null);
    }

    public RenderingConfiguration addBrowser(int width, int height, BrowserType browserType, EmulationBaseInfo emulationInfo) {
        return addBrowser(width, height, browserType, null, emulationInfo);
    }

    public int getConcurrentSessions() {
        return concurrentSessions;
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
