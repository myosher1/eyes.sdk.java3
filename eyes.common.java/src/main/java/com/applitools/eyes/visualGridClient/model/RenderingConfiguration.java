package com.applitools.eyes.visualGridClient.model;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.config.Configuration;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
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

    public RenderingConfiguration addBrowsers(RenderBrowserInfo... browsersInfo) {
        this.browsersInfo.addAll(Arrays.asList(browsersInfo));
        return this;
    }

    public RenderingConfiguration addBrowser(RenderBrowserInfo browserInfo) {
        browsersInfo.add(browserInfo);
        return this;
    }

    public RenderingConfiguration addBrowser(int width, int height, BrowserType browserType) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(width, height, browserType);
        addBrowser(browserInfo);
        return this;
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
