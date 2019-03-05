package com.applitools.eyes.config;

import com.applitools.eyes.*;

public class Configuration implements IConfigurationSetter, IConfigurationGetter {
    private static final int DEFAULT_MATCH_TIMEOUT = 2000; // Milliseconds;

    private String branchName = System.getenv("APPLITOOLS_BRANCH");
    private String parentBranchName = System.getenv("APPLITOOLS_PARENT_BRANCH");
    private String baselineBranchName = System.getenv("APPLITOOLS_BASELINE_BRANCH");
    private String agentId;
    private String environmentName;
    private Boolean saveDiffs;
    private SessionType sessionType;
    protected BatchInfo batch;
    protected String baselineEnvName;
    protected String appName;
    protected String testName;
    protected RectangleSize viewportSize;
    private ImageMatchSettings defaultMatchSettings = new ImageMatchSettings();
    private int matchTimeout = DEFAULT_MATCH_TIMEOUT;
    private String hostApp;
    private String hostOS;
    // Used for automatic save of a test run.
    private boolean saveNewTests, saveFailedTests;
    private int stitchingOverlap = 50;
    private FailureReports failureReports =  FailureReports.ON_CLOSE;;

    protected Configuration(IConfigurationGetter configuration) {
        this.batch = configuration.getBatch();
        this.branchName = configuration.getBranchName();
        this.parentBranchName = configuration.getParentBranchName();
        this.baselineBranchName = configuration.getBaselineBranchName();
        this.agentId = configuration.getAgentId();
        this.baselineEnvName = configuration.getAgentId();
        this.environmentName = configuration.getEnvironmentName();
        this.saveDiffs = configuration.getSaveDiffs();
        this.appName = configuration.getAppName();
        this.testName = configuration.getTestName();
        this.viewportSize = configuration.getViewportSize();
        this.sessionType = configuration.getSessionType();
        defaultMatchSettings.setIgnoreCaret(true);
        agentId = null;
        saveNewTests = true;
        saveFailedTests = false;
    }

    public Configuration() {
        defaultMatchSettings.setIgnoreCaret(true);
        agentId = null;// New tests are automatically saved by default.
        saveNewTests = true;
        saveFailedTests = false;

    }

    @Override
    public boolean getSaveNewTests() {
        return saveNewTests;
    }

    @Override
    public void setSaveNewTests(boolean saveNewTests) {
        this.saveNewTests = saveNewTests;
    }

    @Override
    public boolean getSaveFailedTests() {
        return saveFailedTests;
    }

    @Override
    public void setSaveFailedTests(boolean saveFailedTests) {
        this.saveFailedTests = saveFailedTests;
    }


    @Override
    public ImageMatchSettings getDefaultMatchSettings() {
        return defaultMatchSettings;
    }

    @Override
    public void setDefaultMatchSettings(ImageMatchSettings defaultMatchSettings) {
        this.defaultMatchSettings = defaultMatchSettings;
    }

    @Override
    public int getMatchTimeout() {
        return matchTimeout;
    }

    @Override
    public void setMatchTimeout(int matchTimeout) {
        this.matchTimeout = matchTimeout;
    }
    @Override
    public String getHostApp() {
        return hostApp;
    }

    @Override
    public void setHostApp(String hostApp) {
        this.hostApp = hostApp;
    }

    @Override
    public String getHostOS() {
        return hostOS;
    }

    @Override
    public void setHostOS(String hostOS) {
        this.hostOS = hostOS;
    }

    @Override
    public int getStitchingOverlap() {
        return stitchingOverlap;
    }
    public void setStitchingOverlap(int stitchingOverlap) {
        this.stitchingOverlap = stitchingOverlap;
    }



    @Override
    public void setBatch(BatchInfo batch) {
        this.batch = batch;
    }

    @Override
    public BatchInfo getBatch() {
        return batch;
    }

    @Override
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    @Override
    public String getBranchName() {
        return branchName;
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public String getParentBranchName() {
        return parentBranchName;
    }

    @Override
    public void setParentBranchName(String parentBranchName) {
        this.parentBranchName = parentBranchName;
    }

    @Override
    public String getBaselineBranchName() {
        return baselineBranchName;
    }

    @Override
    public void setBaselineBranchName(String baselineBranchName) {
        this.baselineBranchName = baselineBranchName;
    }

    @Override
    public String getBaselineEnvName() {
        return baselineEnvName;
    }

    @Override
    public void setBaselineEnvName(String baselineEnvName) {
        this.baselineEnvName = baselineEnvName;
    }

    @Override
    public String getEnvironmentName() {
        return environmentName;
    }

    @Override
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    @Override
    public Boolean getSaveDiffs() {
        return saveDiffs;
    }

    @Override
    public void setSaveDiffs(Boolean saveDiffs) {
        this.saveDiffs = saveDiffs;
    }

    @Override
    public String getAppName() {
        return appName;
    }

    @Override
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String getTestName() {
        return testName;
    }

    @Override
    public void setTestName(String testName) {
        this.testName = testName;
    }

    @Override
    public RectangleSize getViewportSize() {
        return viewportSize;
    }

    @Override
    public void setViewportSize(RectangleSize viewportSize) {
        this.viewportSize = viewportSize;
    }

    @Override
    public SessionType getSessionType() {
        return sessionType;
    }

    @Override
    public void setSessionType(SessionType sessionType) {
        this.sessionType = sessionType;
    }

    public Configuration cloneConfig() {
        return new Configuration(this);
    }

    /**
     * @param failureReports The failure reports setting.
     * @see FailureReports
     */
    @Override
    public void setFailureReports(FailureReports failureReports) {
        this.failureReports = failureReports;
    }

    /**
     * @return the failure reports setting.
     */
    @Override
    public FailureReports getFailureReports() {
        return failureReports;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\n\tbatch = " + batch +
                "\n\tbranchName = " + branchName +
                "\n\tparentBranchName = " + parentBranchName +
                "\n\tagentId = " + agentId +
                "\n\tbaselineEnvName = " + baselineEnvName +
                "\n\tenvironmentName = " + environmentName +
                "\n\tsaveDiffs = " + saveDiffs +
                "\n\tappName = " + appName +
                "\n\ttestName = " + testName +
                "\n\tviewportSize = " + viewportSize +
                "\n\tsessionType = " + sessionType;
    }
}
