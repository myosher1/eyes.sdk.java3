package com.applitools.eyes.rendering;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.visualGridClient.IEyesConnector;
import com.applitools.eyes.visualGridClient.IResourceFuture;
import com.applitools.eyes.visualGridClient.data.*;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;

class EyesConnector extends EyesBase implements IEyesConnector {

    public EyesConnector() {
        this.matchWindowTask = new MatchRGWindowTask(this.logger, this.serverConnector, this);
    }

    /**
     * ﻿Starts a new test without setting the viewport size of the AUT.
     *
     * @param appName  The name of the application under test.
     * @param testName The test name.
     * @see #open(String, String, RectangleSize)
     */
    public void open(String appName, String testName) {
        open(appName, testName, null);
    }

    @Override
    public IResourceFuture getResource(URL url, IDownloadListener<Byte[]> iDownloadListener) {
        return this.serverConnector.downloadResource(url, false, iDownloadListener);
    }

    @Override
    public Future<Boolean> renderPutResource(RunningRender runningRender, RGridResource resource) {
        return this.serverConnector.renderPutResource(runningRender, resource, true, null);
    }


    @Override
    public List<RunningRender> render(RenderRequest... renderRequests) {
        return this.serverConnector.render(renderRequests);
    }

    @Override
    public RenderStatusResults getRenderStatus() {
        return null;
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
    public MatchResult matchWindow(String url, String tag, ICheckSettings checkSettings) {
        return super.matchWindow(null, tag, false, checkSettings);
    }

    @Override
    protected void ensureRunningSession() {

    }

    /**
     * Starts a test.
     *
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
        return null;
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

    }

    @Override
    protected String getInferredEnvironment() {
        return null;
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
    }


}
