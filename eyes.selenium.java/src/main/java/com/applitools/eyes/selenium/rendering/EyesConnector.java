package com.applitools.eyes.selenium.rendering;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.capture.AppOutputWithScreenshot;
import com.applitools.eyes.config.*;
import com.applitools.eyes.fluent.ICheckSettingsInternal;
import com.applitools.eyes.selenium.IConfigurationGetter;
import com.applitools.eyes.visualgridclient.services.IEyesConnector;
import com.applitools.eyes.visualgridclient.services.IResourceFuture;
import com.applitools.eyes.visualgridclient.model.*;

import java.net.URI;
import java.net.URL;
import java.util.List;

class EyesConnector extends EyesBase implements IEyesConnector {

    private RenderBrowserInfo browserInfo;
    private String userAgent;
    private String device;
    private RectangleSize deviceSize;
    private IConfigurationGetter configurationGetter;

    public EyesConnector(RenderBrowserInfo browserInfo, RateLimiter rateLimiter) {
        this.browserInfo = browserInfo;
        //this.setServerConnector(new ThrottlingServerConnector(this.serverConnector, rateLimiter));
    }

    /**
     * ﻿Starts a new test without setting the viewport size of the AUT.
     * @param config
     */
    public void open(IConfigurationGetter config) {
        this.configurationGetter = config;
        logger.verbose("opening EyesConnector with viewport size: " + browserInfo.getViewportSize());
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
    public MatchResult matchWindow(String resultImageURL, String domLocation, ICheckSettings checkSettings, List<Region> regions, List<VisualGridSelector[]> regionSelectors, Location location) {

        ICheckSettingsInternal checkSettingsInternal = (ICheckSettingsInternal) checkSettings;

        MatchWindowTask matchWindowTask = new MatchWindowTask(this.logger, this.serverConnector, this.runningSession, getConfigGetter().getMatchTimeout(), this);

        ImageMatchSettings imageMatchSettings = matchWindowTask.createImageMatchSettings(checkSettingsInternal, this);

        String tag = checkSettingsInternal.getName();

        AppOutput appOutput = new AppOutput(tag, null, domLocation, resultImageURL);
        AppOutputWithScreenshot appOutputWithScreenshot = new AppOutputWithScreenshot(appOutput, null, location);

        return matchWindowTask.performMatch(appOutputWithScreenshot, tag, false, checkSettingsInternal, imageMatchSettings, regions, regionSelectors, this);
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
        return "eyes.selenium.visualgrid.java/3.149.2-beta";
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

    @Override
    protected <T extends com.applitools.eyes.config.IConfigurationGetter> T getConfigGetter() {
        return (T) configurationGetter;
    }

    @Override
    protected <T extends IConfigurationSetter> T getConfigSetter() {
        return (T) configurationGetter;
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
    public void setApiKey(String apiKey) {
        super.setApiKey(apiKey);
    }

    @Override
    public void setServerUrl(URI serverUrl) {
        super.setServerUrl(serverUrl);
    }

    @Override
    public void setBranchName(String branchName) {
        getConfigSetter().setBranchName(branchName);
    }

    @Override
    public void setParentBranchName(String parentBranchName) {
        getConfigSetter().setParentBranchName(parentBranchName);
    }

    @Override
    public void setDevice(String device) {
        this.device = device;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This override also checks for mobile operating system.
     */
    @Override
    protected AppEnvironment getAppEnvironment() {
        AppEnvironment appEnv = super.getAppEnvironment();
        appEnv.setDeviceInfo(device);
        logger.log("Done!");
        return appEnv;
    }

    @Override
    public RectangleSize getDeviceSize() {
        return deviceSize;
    }

    @Override
    public void setDeviceSize(RectangleSize deviceSize) {
        this.deviceSize = deviceSize;
    }

    @Override
    public RunningSession getSession() {
        return this.runningSession;
    }

    @Override
    protected RectangleSize getViewportSizeForOpen() {
        if (device != null) {
            return deviceSize;
        } else if (browserInfo.getViewportSize() != null) {
            return browserInfo.getViewportSize();
        } else {
            //this means it's a emulationInfo
            if (browserInfo.getEmulationInfo() instanceof EmulationDevice) {
                EmulationDevice emulationDevice = (EmulationDevice) browserInfo.getEmulationInfo();
                return new RectangleSize(emulationDevice.getWidth(), emulationDevice.getHeight());
            }
        }
        return super.getViewportSizeForOpen();
    }

    @Override
    protected String getBaselineEnvName() {
        String baselineEnvName = this.browserInfo.getBaselineEnvName();
        if (baselineEnvName != null) {
            return baselineEnvName;
        }
        return getConfigGetter().getBaselineEnvName();
    }
}
