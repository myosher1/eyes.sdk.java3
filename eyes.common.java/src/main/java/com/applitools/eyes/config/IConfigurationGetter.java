package com.applitools.eyes.config;

import com.applitools.eyes.*;

public interface IConfigurationGetter {
    boolean getSaveNewTests();

    boolean getSaveFailedTests();

    ImageMatchSettings getDefaultMatchSettings();

    void setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings);

    int getMatchTimeout();

    String getHostApp();

    String getHostOS();

    int getStitchingOverlap();

    BatchInfo getBatch();

    String getBranchName();

    String getAgentId();

    String getParentBranchName();

    String getBaselineBranchName();

    String getBaselineEnvName();

    String getEnvironmentName();

    Boolean getSaveDiffs();

    String getAppName();

    String getTestName();

    RectangleSize getViewportSize();

    SessionType getSessionType();

    FailureReports getFailureReports();
}
