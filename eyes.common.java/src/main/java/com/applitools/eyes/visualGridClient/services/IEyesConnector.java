package com.applitools.eyes.visualGridClient.services;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualGridClient.model.*;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;

public interface IEyesConnector {
    void log(String massage);

    void setProxy(AbstractProxySettings proxy);

    void setLogHandler(LogHandler logHandler);

    void setServerUrl(String serverUrl) throws URISyntaxException;

    void open(Configuration config);

    TestResults close(boolean throwExceptionOn);

    TestResults abortIfNotClosed();

    IResourceFuture getResource(URL url, IDownloadListener<Byte[]> iDownloadListener);

    RenderingInfo getRenderingInfo();

    IPutFuture renderPutResource(RunningRender runningRender, RGridResource resource);

    List<RunningRender> render(RenderRequest... renderRequests);

    List<RenderStatusResults> renderStatusById(String... renderIds);

    IResourceFuture createResourceFuture(RGridResource rg);

    MatchResult matchWindow(String resultImageURL, ICheckSettings checkSettings);

    void setRenderInfo(RenderingInfo renderingInfo);

    void setBatch(BatchInfo batchInfo);

    void setUserAgent(String userAgent);
}
