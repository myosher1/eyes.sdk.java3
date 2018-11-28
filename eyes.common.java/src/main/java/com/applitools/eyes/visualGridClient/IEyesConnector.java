package com.applitools.eyes.visualGridClient;

import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.IDownloadListener;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.visualGridClient.data.RenderingInfo;

import java.net.URISyntaxException;
import java.net.URL;

public interface IEyesConnector {
    void log(String massage);

    void setProxy(AbstractProxySettings proxy);

    void setLogHandler(LogHandler logHandler);

    void setServerUrl(String serverUrl) throws URISyntaxException;

    void open(String appName, String testName);

    TestResults close(boolean throwExceptionOn);

    void abortIfNotClosed();

    IResourceFuture getResource(URL url, IDownloadListener<Byte[]> iDownloadListener);

    RenderingInfo getRenderingInfo();
}
