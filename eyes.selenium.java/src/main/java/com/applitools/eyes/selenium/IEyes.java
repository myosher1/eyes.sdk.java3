package com.applitools.eyes.selenium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;

import java.net.URISyntaxException;

public interface IEyes {
    void setLogHandler(LogHandler logHandler);

    void setServerUrl(String serverUrl) throws URISyntaxException;

    void setProxy(AbstractProxySettings abstractProxySettings);

    Logger getLogger();

    void check(ICheckSettings checkSettings);

    void check(String testName, ICheckSettings checkSettings);

    TestResults close();

    TestResults close(boolean throwException);

    TestResults abortIfNotClosed();

    boolean getIsOpen();

    boolean getIsDisabled();

    String getApiKey();

    void setApiKey(String apiKey);

    void setDisabled(boolean disabled);

    void setBranchName(String branchName);

    void setParentBranchName(String branchName);

    void setHideCaret(boolean hideCaret);

    void setMatchLevel(MatchLevel level);

}
