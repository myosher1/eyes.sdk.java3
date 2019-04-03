package com.applitools.eyes.visualgrid.model;

import com.applitools.IResourceUploadListener;
import com.applitools.eyes.*;
import com.applitools.eyes.visualgrid.services.IResourceFuture;

import java.net.URI;
import java.net.URL;
import java.util.List;

public class ThrottlingServerConnector implements IServerConnector {
    private IServerConnector serverConnector;
    private RateLimiter rateLimiter;

    public ThrottlingServerConnector(IServerConnector serverConnector, RateLimiter rateLimiter) {
        this.serverConnector = serverConnector;
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void setApiKey(String apiKey) {
        this.serverConnector.setApiKey(apiKey);
    }

    @Override
    public String getApiKey() {
        return this.serverConnector.getApiKey();
    }

    @Override
    public void setServerUrl(URI serverUrl) {
        this.serverConnector.setServerUrl(serverUrl);
    }

    @Override
    public URI getServerUrl() {
        return this.serverConnector.getServerUrl();
    }

    @Override
    public void setLogger(Logger logger) {
        this.serverConnector.setLogger(logger);
    }

    @Override
    public Logger getLogger() {
        return this.serverConnector.getLogger();
    }

    @Override
    public void setProxy(AbstractProxySettings proxySettings) {
        this.serverConnector.setProxy(proxySettings);
    }

    @Override
    public AbstractProxySettings getProxy() {
        return this.serverConnector.getProxy();
    }

    @Override
    public int getTimeout() {
        return this.serverConnector.getTimeout();
    }

    @Override
    public RunningSession startSession(SessionStartInfo sessionStartInfo) {
        return this.serverConnector.startSession(sessionStartInfo);
    }

    @Override
    public TestResults stopSession(RunningSession runningSession, boolean isAborted, boolean save) {
        return this.serverConnector.stopSession(runningSession, isAborted, save);
    }

    @Override
    public void deleteSession(TestResults testResults) {
        this.serverConnector.deleteSession(testResults);
    }

    @Override
    public MatchResult matchWindow(RunningSession runningSession, MatchWindowData matchData) {
        return this.serverConnector.matchWindow(runningSession, matchData);
    }

    @Override
    public void downloadString(URL uri, boolean isSecondRetry, IDownloadListener<String> listener) {
        this.serverConnector.downloadString(uri, isSecondRetry, listener);
    }

    @Override
    public IResourceFuture downloadResource(URL uri, boolean isSecondRetry, IDownloadListener<Byte[]> listener) {
        return this.serverConnector.downloadResource(uri, isSecondRetry, listener);
    }

    @Override
    public String postDomSnapshot(String domJson) {
        return this.serverConnector.postDomSnapshot(domJson);
    }

    @Override
    public RenderingInfo getRenderInfo() {
        return this.serverConnector.getRenderInfo();
    }

    @Override
    public List<RunningRender> render(RenderRequest... renderRequests) {
        return this.serverConnector.render(renderRequests);
    }

    @Override
    public boolean renderCheckResource(RunningRender runningRender, RGridResource resource) {
        return this.serverConnector.renderCheckResource(runningRender, resource);
    }

    @Override
    public PutFuture renderPutResource(RunningRender runningRender, RGridResource resource, IResourceUploadListener listener) {
        PutFuture putFuture;
        if (rateLimiter.isSlotAvailable()) {
            putFuture = this.serverConnector.renderPutResource(runningRender, resource, listener);
        } else {
            putFuture = new PutFuture(resource, runningRender, this.serverConnector, getLogger());
        }
        rateLimiter.handle(putFuture);
        return putFuture;
    }

    @Override
    public RenderStatusResults renderStatus(RunningRender runningRender) {
        return this.serverConnector.renderStatus(runningRender);
    }

    @Override
    public List<RenderStatusResults> renderStatusById(String... renderIds) {
        return this.serverConnector.renderStatusById(renderIds);
    }

    @Override
    public IResourceFuture createResourceFuture(RGridResource gridResource) {
        return this.serverConnector.createResourceFuture(gridResource);
    }

    @Override
    public void setRenderingInfo(RenderingInfo renderInfo) {
        this.serverConnector.setRenderingInfo(renderInfo);
    }
}
