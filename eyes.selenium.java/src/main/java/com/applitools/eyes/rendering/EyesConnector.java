package com.applitools.eyes.rendering;

import com.applitools.eyes.*;
import com.applitools.eyes.visualGridClient.IEyesConnector;
import com.applitools.eyes.visualGridClient.IResourceFuture;
import com.applitools.eyes.visualGridClient.data.RGridResource;
import com.applitools.eyes.visualGridClient.data.RenderRequest;
import com.applitools.eyes.visualGridClient.data.RenderStatusResults;
import com.applitools.eyes.visualGridClient.data.RunningRender;

import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;

class EyesConnector extends EyesBase implements IEyesConnector {

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
        return null;
    }

    @Override
    public RenderStatusResults getRenderStatus() {
        return null;
    }

    @Override
    public List<RenderStatusResults> renderStatusById(String... renderIds) {
        return this.serverConnector.renderStatusById(renderIds);
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
}
