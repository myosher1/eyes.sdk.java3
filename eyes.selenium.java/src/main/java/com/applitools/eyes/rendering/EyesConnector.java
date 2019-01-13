package com.applitools.eyes.rendering;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.capture.AppOutputWithScreenshot;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.visualGridClient.services.IEyesConnector;
import com.applitools.eyes.visualGridClient.services.IResourceFuture;
import com.applitools.eyes.visualGridClient.model.*;

import java.net.URI;
import java.net.URL;
import java.util.List;

class EyesConnector extends EyesBase implements IEyesConnector {

    private RenderBrowserInfo browserInfo;
    private String userAgent;

    public EyesConnector(RenderBrowserInfo browserInfo, RateLimiter rateLimiter) {
        this.browserInfo = browserInfo;
        //this.setServerConnector(new ThrottlingServerConnector(this.serverConnector, rateLimiter));
    }

    /**
     * ﻿Starts a new test without setting the viewport size of the AUT.
     */
    public void open(Configuration config) {
        logger.verbose("opening EyesConnector with viewport size: " + browserInfo.getViewportSize());
        this.config = config.cloneConfig();
        this.config.setViewportSize(browserInfo.getViewportSize());
        this.config.setBaselineEnvName(browserInfo.getBaselineEnvName());
        openBase();
    }

    @Override
    public IResourceFuture getResource(URL url) {
        return this.serverConnector.downloadResource(url, true, null);
    }

    @Override
    public PutFuture renderPutResource(RunningRender runningRender, RGridResource resource) {
        return this.serverConnector.renderPutResource(runningRender, resource, null);
    }


    @Override
    public List<RunningRender> render(RenderRequest... renderRequests) {
        return this.serverConnector.render(renderRequests);
    }

    @Override
    public List<RenderStatusResults> renderStatusById(String... renderIds) {
        return this.serverConnector.renderStatusById(renderIds);
    }

    @Override
    public IResourceFuture createResourceFuture(RGridResource rg) {
        return this.serverConnector.createResourceFuture(rg);
    }

    @Override
    public MatchResult matchWindow(String resultImageURL, String domLocation, ICheckSettings checkSettings) {

        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkSettings;

        MatchWindowTask matchWindowTask = new MatchWindowTask(this.logger, this.serverConnector, this.runningSession, getMatchTimeout(), this);

        ImageMatchSettings imageMatchSettings = matchWindowTask.createImageMatchSettings(checkSettingsInternal, null);

        String tag = checkSettingsInternal.getName();

        AppOutput appOutput = new AppOutput(tag, null, domLocation, resultImageURL);
        AppOutputWithScreenshot appOutputWithScreenshot = new AppOutputWithScreenshot(appOutput, null);

        return matchWindowTask.performMatch(new Trigger[0], appOutputWithScreenshot, tag, false, imageMatchSettings);
    }

    /**
     * Starts a test.
     * @param appName    The name of the application under test.
     * @param testName   The test name.
     * @param dimensions Determines the resolution used for the baseline.
     *                   {@code null} will automatically grab the resolution from the image.
     */
    public void open(String appName, String testName,
                     RectangleSize dimensions) {
        openBase(appName, testName, dimensions, null);
    }


    public void setProxy(AbstractProxySettings proxy) {
        super.setProxy(proxy);
    }

    @Override
    protected String getBaseAgentId() {
        return "eyes.selenium.visualgrid.java/3.143.1";
    }

    @Override
    public String tryCaptureDom() {
        return null;
    }

    @Override
    protected RectangleSize getViewportSize() {
        return null;
    }

    @Override
    protected void setViewportSize(RectangleSize size) {
        logger.log("WARNING setViewportSize() was called in Visual-Grid context");
    }

    @Override
    protected String getInferredEnvironment() {
        return "useragent:" + userAgent;
    }

    @Override
    protected EyesScreenshot getScreenshot() {
        return null;
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected String getAUTSessionId() {
        return null;
    }

    public void setRenderInfo(RenderingInfo renderInfo) {
        this.renderInfo = renderInfo;
        this.serverConnector.setRenderingInfo(renderInfo);
    }

    @Override
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    protected void openLogger(){
        // do nothing.
    }

    @Override
    protected void closeLogger(){
        // do nothing.
    }

    @Override
    public void setHideCaret(boolean hideCaret) {

    }

    @Override
    public void setApiKey(String apiKey) {
        super.setApiKey(apiKey);
    }

    @Override
    public void setAgentId(String agentId) {
        super.setAgentId(agentId);
    }

    @Override
    public void setServerUrl(URI serverUrl) {
        super.setServerUrl(serverUrl);
    }

    @Override
    public void setAppName(String appName) {
        super.setAppName(appName);
    }

    @Override
    public void setBranchName(String branchName) {
        super.setBranchName(branchName);
    }

    @Override
    public String getParentBranchName() {
        return super.getParentBranchName();
    }

    @Override
    public void setBaselineBranchName(String branchName) {
        super.setBaselineBranchName(branchName);
    }

    @Override
    public void setMatchLevel(MatchLevel matchLevel) {
        super.setMatchLevel(matchLevel);
    }
}
