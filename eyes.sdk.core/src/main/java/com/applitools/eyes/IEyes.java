package com.applitools.eyes;

import com.applitools.ICheckSettings;

import java.net.URI;
import java.net.URISyntaxException;

public interface IEyes {
    void setLogHandler(LogHandler logHandler);

    void setServerUrl(String serverUrl) throws URISyntaxException;

    void setProxy(AbstractProxySettings abstractProxySettings);

    void setIsDisabled(boolean isDisabled);

    Logger getLogger();

    void check(ICheckSettings checkSettings);

    void check(String testName, ICheckSettings checkSettings);

    TestResults abortIfNotClosed();

    boolean getIsOpen();

    boolean getIsDisabled();

    String getApiKey();

    void setApiKey(String apiKey);

    void setBranchName(String branchName);

    void setParentBranchName(String branchName);

    void setHideCaret(boolean hideCaret);

    void setMatchLevel(MatchLevel level);

    URI getServerUrl();
}
