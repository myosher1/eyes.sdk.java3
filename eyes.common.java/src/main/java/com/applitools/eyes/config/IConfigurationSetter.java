package com.applitools.eyes.config;

import com.applitools.eyes.*;

public interface IConfigurationSetter{

    void setSaveNewTests(boolean saveNewTests);

    void setSaveFailedTests(boolean saveFailedTests);

    void setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings);

    void setMatchTimeout(int matchTimeout);

    void setHostApp(String hostApp);

    void setHostOS(String hostOS);

    void setStitchingOverlap(int stitchingOverlap);

    void setBatch(BatchInfo batch);

    void setBranchName(String branchName);

    void setAgentId(String agentId);

    void setParentBranchName(String parentBranchName);

    void setBaselineBranchName(String baselineBranchName);

    void setBaselineEnvName(String baselineEnvName);

    void setEnvironmentName(String environmentName);

    void setSaveDiffs(Boolean saveDiffs);

    void setAppName(String appName);

    void setTestName(String testName);

    void setViewportSize(RectangleSize viewportSize);

    void setSessionType(SessionType sessionType);

    void setFailureReports(FailureReports failureReports);
}
