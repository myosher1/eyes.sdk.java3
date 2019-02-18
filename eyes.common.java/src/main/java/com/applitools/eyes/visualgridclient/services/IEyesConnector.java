package com.applitools.eyes.visualgridclient.services;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualgridclient.model.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public interface IEyesConnector {
    void log(String massage);

    void setProxy(AbstractProxySettings proxy);

    void setLogHandler(LogHandler logHandler);

    void setServerUrl(String serverUrl) throws URISyntaxException;

    URI getServerUrl();

    void open(Configuration config);

    TestResults close(boolean throwExceptionOn);

    TestResults abortIfNotClosed();

    IResourceFuture getResource(URL url);

    RenderingInfo getRenderingInfo();

    PutFuture renderPutResource(RunningRender runningRender, RGridResource resource);

    List<RunningRender> render(RenderRequest... renderRequests);

    List<RenderStatusResults> renderStatusById(String... renderIds);

    IResourceFuture createResourceFuture(RGridResource rg);

    MatchResult matchWindow(String resultImageURL, String domLocation, ICheckSettings checkSettings);

    void setRenderInfo(RenderingInfo renderingInfo);

    void setBatch(BatchInfo batchInfo);

    void setUserAgent(String userAgent);

    String getApiKey();

    void setApiKey(String apiKey);

    void setBranchName(String branchName);

    void setParentBranchName(String parentBranchName);

    void setHideCaret(boolean hideCaret);

    void setMatchLevel(MatchLevel matchLevel);

    void setDevice(String device);

}
