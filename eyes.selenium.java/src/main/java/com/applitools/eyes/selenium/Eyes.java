package com.applitools.eyes.selenium;

import com.applitools.ICheckSettings;
import com.applitools.IDomCaptureListener;
import com.applitools.eyes.*;
import com.applitools.eyes.config.ISeleniumConfigurationGetter;
import com.applitools.eyes.config.SeleniumConfiguration;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.events.ISessionEventHandler;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.ImageRotation;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.eyes.triggers.MouseAction;
import com.applitools.eyes.visualgridclient.model.RenderingInfo;
import com.applitools.eyes.visualgridclient.model.TestResultContainer;
import com.applitools.eyes.visualgridclient.services.EyesRunner;
import com.applitools.eyes.visualgridclient.services.VisualGridRunner;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The type Eyes.
 */
public class Eyes {

    private boolean isVisualGridEyes = false;
    private VisualGridEyes visualGridEyes = null;
    private SeleniumEyes seleniumEyes = null;
    private EyesRunner runner = null;
    private SeleniumConfiguration globalConfiguration = new SeleniumConfiguration();
    private WebDriver driver;
    private ImageRotation rotation;
    private IConfigurationProvider

    /**
     * Instantiates a new Eyes.
     */
    public Eyes() {
        seleniumEyes = new SeleniumEyes(globalConfiguration);
    }

    /**
     * Instantiates a new Eyes.
     *
     * @param runner the runner
     */
    public Eyes(EyesRunner runner) {
        ArgumentGuard.notNull(runner, "EyesRunner");
        this.runner = runner;
        if (runner instanceof VisualGridRunner) {
            visualGridEyes = new VisualGridEyes((VisualGridRunner) runner);
            isVisualGridEyes = true;
        } else {
            seleniumEyes = new SeleniumEyes(globalConfiguration);
            ((SeleniumRunner) runner).addEyes(this);
        }
    }

    /**
     * Open web driver.
     *
     * @param webDriver             the web driver
     * @param seleniumConfiguration the selenium configuration
     * @return the web driver
     */
    public WebDriver open(WebDriver webDriver, SeleniumConfiguration seleniumConfiguration) {
        ArgumentGuard.notNull(seleniumConfiguration, "seleniumConfiguration");
        seleniumConfiguration.setRenderingConfig(isVisualGridEyes);

        configuration = mergeConfigurations(seleniumConfiguration);

        this.driver = webDriver;
        if (isVisualGridEyes) {
            return visualGridEyes.open(webDriver, configuration);
        } else {
            return seleniumEyes.open(webDriver, configuration);
        }
    }

    private SeleniumConfiguration mergeConfigurations(SeleniumConfiguration renderingConfiguration) {
        if (renderingConfiguration.getTestName() == null) {
            renderingConfiguration.setTestName(this.globalConfiguration.getTestName());
        }
        if (renderingConfiguration.getBaselineEnvName() == null) {
            renderingConfiguration.setBaselineEnvName(this.globalConfiguration.getBaselineEnvName());
        }
        if (renderingConfiguration.getBatch() == null) {
            renderingConfiguration.setBatch(this.globalConfiguration.getBatch());
        }
        if (renderingConfiguration.getBranchName() == null) {
            renderingConfiguration.setBranchName(this.globalConfiguration.getBranchName());
        }
        if (renderingConfiguration.getParentBranchName() == null) {
            renderingConfiguration.setParentBranchName(this.globalConfiguration.getParentBranchName());
        }
        if (renderingConfiguration.getAgentId() == null) {
            renderingConfiguration.setAgentId(this.globalConfiguration.getAgentId());
        }
        if (renderingConfiguration.getAppName() == null) {
            renderingConfiguration.setAppName(this.globalConfiguration.getAppName());
        }
        if (renderingConfiguration.getEnvironmentName() == null) {
            renderingConfiguration.setEnvironmentName(this.globalConfiguration.getEnvironmentName());
        }
        if (renderingConfiguration.getSaveDiffs() == null) {
            renderingConfiguration.setSaveDiffs(this.globalConfiguration.getSaveDiffs());
        }
        if (renderingConfiguration.getSessionType() == null) {
            renderingConfiguration.setSessionType(this.globalConfiguration.getSessionType());
        }
        if (renderingConfiguration.getTestName() == null) {
            renderingConfiguration.setTestName(this.globalConfiguration.getTestName());
        }
        if (renderingConfiguration.getViewportSize() == null) {
            renderingConfiguration.setViewportSize(this.globalConfiguration.getViewportSize());
        }
        if (!renderingConfiguration.isThrowExceptionOn()) {
            renderingConfiguration.setThrowExceptionOn(this.globalConfiguration.isThrowExceptionOn());
        }
        if (renderingConfiguration.getBaselineBranchName() == null) {
            renderingConfiguration.setBaselineBranchName(this.globalConfiguration.getBaselineBranchName());
        }
        if (!renderingConfiguration.getForceFullPageScreenshot()) {
            renderingConfiguration.setForceFullPageScreenshot(this.globalConfiguration.getForceFullPageScreenshot());
        }
        if (!renderingConfiguration.getHideCaret()) {
            renderingConfiguration.setHideCaret(this.globalConfiguration.getHideCaret());
        }
        if (!renderingConfiguration.getHideScrollbars()) {
            renderingConfiguration.setHideScrollbars(this.globalConfiguration.getHideScrollbars());
        }
        return renderingConfiguration;
    }

    /**
     * Sets server url.
     *
     * @param serverUrl the server url
     */
    public void setServerUrl(String serverUrl) {
        if (isVisualGridEyes) {
            visualGridEyes.setServerUrl(serverUrl);
        } else {
            seleniumEyes.setServerUrl(serverUrl);
        }
    }

    /**
     * Sets the proxy settings to be used by the rest client.
     *
     * @param proxySettings The proxy settings to be used by the rest client.
     *                      If {@code null} then no proxy is set.
     */
    public void setProxy(AbstractProxySettings proxySettings) {
        if (isVisualGridEyes) {
            visualGridEyes.setProxy(proxySettings);
        } else {
            seleniumEyes.setProxy(proxySettings);
        }
    }

    /**
     * Sets is disabled.
     *
     * @param isDisabled If true, all interactions with this API will be                   silently ignored.
     */
    public void setIsDisabled(boolean isDisabled) {
        if (isVisualGridEyes) {
            visualGridEyes.setIsDisabled(isDisabled);
        } else {
            seleniumEyes.setIsDisabled(isDisabled);
        }
    }

    /**
     * Check.
     *
     * @param checkSettings the check settings
     */
    public void check(ICheckSettings checkSettings) {
        if (isVisualGridEyes) {
            visualGridEyes.check(checkSettings);
        } else {
            seleniumEyes.check(checkSettings);
        }
    }

    /**
     * See {@link #close(boolean)}.
     * {@code throwEx} defaults to {@code true}.
     *
     * @return The test results.
     */
    public TestResults close() {
        configuration = null;
        if (isVisualGridEyes) {
            List<Future<TestResultContainer>> futures = visualGridEyes.close();
            if (futures != null && !futures.isEmpty()) {
                try {
                    return futures.get(0).get().getTestResults();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } else {
            TestResults close = seleniumEyes.close();
            if (runner != null) {
                SeleniumRunner seleniumRunner = (SeleniumRunner) runner;
                return seleniumRunner.setTestResults(close);
            }
            return close;
        }
        return null;
    }


    /**
     * If a test is running, aborts it. Otherwise, does nothing.
     */
    public void abortIfNotClosed() {
        configuration = null;
        if (isVisualGridEyes) {
            visualGridEyes.abortIfNotClosed();
        } else {
            seleniumEyes.abortIfNotClosed();
        }
    }

    /**
     * Gets is disabled.
     *
     * @return Whether eyes is disabled.
     */
    public boolean getIsDisabled() {
        if (isVisualGridEyes) {
            return visualGridEyes.getIsDisabled();
        } else {
            return seleniumEyes.getIsDisabled();
        }
    }


    /**
     * Gets api key.
     *
     * @return the api key
     */
    public String getApiKey() {
        if (isVisualGridEyes) {
            return visualGridEyes.getApiKey();
        } else {
            return seleniumEyes.getApiKey();
        }
    }


    /**
     * Sets api key.
     *
     * @param apiKey the api key
     */
    public void setApiKey(String apiKey) {
        if (isVisualGridEyes) {
            visualGridEyes.setApiKey(apiKey);
        } else {
            seleniumEyes.setApiKey(apiKey);
        }
    }


    /**
     * Sets branch name.
     *
     * @param branchName the branch name
     */
    public void setBranchName(String branchName) {
        if (this.configuration != null) {
            this.configuration.setBranchName(branchName);
        }
        globalConfiguration.setBranchName(branchName);
    }


    /**
     * Sets parent branch name.
     *
     * @param branchName the branch name
     */
    public void setParentBranchName(String branchName) {
        if (this.configuration != null) {
            this.configuration.setParentBranchName(branchName);
        }
        globalConfiguration.setParentBranchName(branchName);
    }


    /**
     * Sets hide caret.
     *
     * @param hideCaret the hide caret
     */
    public void setHideCaret(boolean hideCaret) {
        if (isVisualGridEyes) {
            visualGridEyes.setHideCaret(hideCaret);
        } else {
            seleniumEyes.setHideCaret(hideCaret);
        }
    }

    /**
     * Sets the maximum time (in ms) a match operation tries to perform a match.
     *
     * @param ms Total number of ms to wait for a match.
     */
    public void setMatchTimeout(int ms) {
        if (isVisualGridEyes) {
            this.visualGridEyes.setMatchTimeout(ms);
        } else {
            this.configuration.setMatchTimeout(ms);
        }

    }

    /**
     * Gets match timeout.
     *
     * @return The maximum time in ms waits for a match.
     */
    public int getMatchTimeout() {
        if (isVisualGridEyes) {
            return this.visualGridEyes.getMatchTimeout();
        } else {
            return this.configuration.getMatchTimeout();
        }
    }

    /**
     * Set whether or not new tests are saved by default.
     *
     * @param saveNewTests True if new tests should be saved by default. False otherwise.
     */
    public void setSaveNewTests(boolean saveNewTests) {
        if (isVisualGridEyes) {
            this.visualGridEyes.setSaveNewTests(saveNewTests);
        } else {
            this.configuration.setSaveNewTests(saveNewTests);
        }
    }

    /**
     * Gets save new tests.
     *
     * @return True if new tests are saved by default.
     */
    public boolean getSaveNewTests() {
        if (isVisualGridEyes) {
            return this.visualGridEyes.getSaveNewTests();
        } else {
            return this.configuration.isSaveNewTests();
        }
    }

    /**
     * Set whether or not failed tests are saved by default.
     *
     * @param saveFailedTests True if failed tests should be saved by default, false otherwise.
     */
    public void setSaveFailedTests(boolean saveFailedTests) {
        if (isVisualGridEyes) {
            this.visualGridEyes.setSaveFailedTests(saveFailedTests);
        } else {
            this.configuration.setSaveFailedTests(saveFailedTests);
        }
    }

    /**
     * Gets save failed tests.
     *
     * @return True if failed tests are saved by default.
     */
    public boolean getSaveFailedTests() {
        if (isVisualGridEyes) {
            return this.visualGridEyes.getSaveNewTests();
        } else {
            return this.configuration.getSaveNewTests();
        }
    }

    /**
     * Sets the batch in which context future tests will run or {@code null}
     * if tests are to run standalone.
     *
     * @param batch The batch info to set.
     */
    public void setBatch(BatchInfo batch) {
        if (isVisualGridEyes) {
            visualGridEyes.setBatch(batch);
        } else {
            seleniumEyes.setBatch(batch);
        }
    }

    /**
     * Gets batch.
     *
     * @return The currently set batch info.
     */
    public BatchInfo getBatch() {
        if (isVisualGridEyes) {
            return this.visualGridEyes.getBatch();
        } else {
            return this.seleniumEyes.getBatch();
        }

    }

    /**
     * Sets failure reports.
     *
     * @param failureReports The failure reports setting.
     * @see FailureReports
     */
    public void setFailureReports(FailureReports failureReports) {
        if (isVisualGridEyes) {
            visualGridEyes.setFailureReports(failureReports);
        } else {
            seleniumEyes.setFailureReports(failureReports);
        }
    }

    /**
     * Gets failure reports.
     *
     * @return the failure reports setting.
     */
    public FailureReports getFailureReports() {
        if (isVisualGridEyes) {
            return this.visualGridEyes.getFailureReports();
        } else {
            return this.seleniumEyes.getFailureReports();
        }
    }

    /**
     * Updates the match settings to be used for the session.
     *
     * @param defaultMatchSettings The match settings to be used for the session.
     */
    public void setDefaultMatchSettings(ImageMatchSettings
                                                defaultMatchSettings) {
        if (isVisualGridEyes) {
            visualGridEyes.setDefaultMatchSettings(defaultMatchSettings);
        } else {
            seleniumEyes.setDefaultMatchSettings(defaultMatchSettings);
        }
    }

    /**
     * Gets default match settings.
     *
     * @return The match settings used for the session.
     */
    public ImageMatchSettings getDefaultMatchSettings() {
        if (isVisualGridEyes) {
            return this.visualGridEyes.getDefaultMatchSettings();
        } else {
            return this.seleniumEyes.getDefaultMatchSettings();
        }

    }

    /**
     * This function is deprecated. Please use {@link #setDefaultMatchSettings} instead.
     * <p>
     * The test-wide match level to use when checking application screenshot
     * with the expected output.
     *
     * @param matchLevel The match level setting.
     * @see com.applitools.eyes.MatchLevel
     */
    public void setMatchLevel(MatchLevel matchLevel) {
        if (isVisualGridEyes) {
            this.visualGridEyes.setMatchLevel(matchLevel);
        } else {
            this.seleniumEyes.setMatchLevel(matchLevel);
        }
    }

    /**
     * Gets match level.
     *
     * @return The test-wide match level.
     * @deprecated Please use{@link #getDefaultMatchSettings} instead.
     */
    public MatchLevel getMatchLevel() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getMatchLevel();
        } else {
            return this.visualGridEyes.getMatchLevel();
        }
    }

    /**
     * Gets full agent id.
     *
     * @return The full agent id composed of both the base agent id and the user given agent id.
     */
    public String getFullAgentId() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getFullAgentId();
        } else {
            return this.visualGridEyes.getFullAgentId();
        }
    }

    /**
     * Gets is open.
     *
     * @return Whether a session is open.
     */
    public boolean getIsOpen() {
        if (isVisualGridEyes) {
            return visualGridEyes.getIsOpen();
        } else {
            return seleniumEyes.getIsOpen();
        }
    }

    /**
     * Gets default server url.
     *
     * @return the default server url
     */
    public static URI getDefaultServerUrl() {
        return SeleniumEyes.getDefaultServerUrl();
    }

    /**
     * Sets a handler of log messages generated by this API.
     *
     * @param logHandler Handles log messages generated by this API.
     */
    public void setLogHandler(LogHandler logHandler) {
        if (isVisualGridEyes) {
            visualGridEyes.setLogHandler(logHandler);
        } else {
            seleniumEyes.setLogHandler(logHandler);
        }
    }

    /**
     * Gets log handler.
     *
     * @return The currently set log handler.
     */
    public LogHandler getLogHandler() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getLogHandler();
        } else {
            if (this.visualGridEyes.getLogger() != null) {
                return this.visualGridEyes.getLogger().getLogHandler();
            }
        }
        return null;
    }

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public Logger getLogger() {
        if (isVisualGridEyes) {
            return this.visualGridEyes.getLogger();
        } else {
            return this.seleniumEyes.getLogger();
        }
    }

    /**
     * Manually set the the sizes to cut from an image before it's validated.
     *
     * @param cutProvider the provider doing the cut.
     */
    public void setImageCut(CutProvider cutProvider) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.setImageCut(cutProvider);
        }
    }

    /**
     * Gets is cut provider explicitly set.
     *
     * @return the is cut provider explicitly set
     */
    public boolean getIsCutProviderExplicitlySet() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getIsCutProviderExplicitlySet();
        }
        return false;
    }


    /**
     * Check.
     *
     * @param tag           the tag
     * @param checkSettings the check settings
     */
    public void check(String tag, ICheckSettings checkSettings) {
        if (isVisualGridEyes) {
            visualGridEyes.check(tag, checkSettings);
        } else {
            seleniumEyes.check(tag, checkSettings);
        }
    }

    /**
     * Close test results.
     *
     * @param shouldThrowException the should throw exception
     * @return the test results
     */
    public TestResults close(boolean shouldThrowException) {
        configuration = null;
        if (isVisualGridEyes) {
            List<Future<TestResultContainer>> close = visualGridEyes.close(shouldThrowException);
            if (close != null && !close.isEmpty()) {
                try {
                    Future<TestResultContainer> testResultContainerFuture = close.get(0);
                    TestResultContainer testResultContainer = testResultContainerFuture.get();
                    Error exception = testResultContainer.getException();
                    if (exception != null) {
                        throw exception;
                    }
                    return testResultContainer.getTestResults();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

            }
        } else {
            TestResults close = seleniumEyes.close(shouldThrowException);
            if (runner != null) {
                SeleniumRunner seleniumRunner = (SeleniumRunner) runner;
                return seleniumRunner.setTestResults(close);
            }
            return close;
        }
        return null;
    }


    /**
     * Manually set the scale ratio for the images being validated.
     *
     * @param scaleRatio The scale ratio to use, or {@code null} to reset                   back to automatic scaling.
     */
    public void setScaleRatio(Double scaleRatio) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setScaleRatio(scaleRatio);
        }

    }

    /**
     * Gets scale ratio.
     *
     * @return The ratio used to scale the images being validated.
     */
    public double getScaleRatio() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getScaleRatio();
        }
        return 0;
    }

    /**
     * Adds a property to be sent to the server.
     *
     * @param name  The property name.
     * @param value The property value.
     */
    public void addProperty(String name, String value) {
        if (isVisualGridEyes) {
            this.seleniumEyes.addProperty(name, value);
        }
    }

    /**
     * Clears the list of custom properties.
     */
    public void clearProperties() {
        if (isVisualGridEyes) {
            this.seleniumEyes.clearProperties();
        }
    }

    /**
     * Sets save debug screenshots.
     *
     * @param saveDebugScreenshots If true, will save all screenshots to local directory.
     */
    public void setSaveDebugScreenshots(boolean saveDebugScreenshots) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setSaveDebugScreenshots(saveDebugScreenshots);
        }
    }

    /**
     * Gets save debug screenshots.
     *
     * @return True if screenshots saving enabled.
     */
    public boolean getSaveDebugScreenshots() {
        if (!isVisualGridEyes) {
            return seleniumEyes.getSaveDebugScreenshots();
        }
        return false;
    }

    /**
     * Sets debug screenshots path.
     *
     * @param pathToSave Path where you want to save the debug screenshots.
     */
    public void setDebugScreenshotsPath(String pathToSave) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setDebugScreenshotsPath(pathToSave);
        }
    }

    /**
     * Gets debug screenshots path.
     *
     * @return The path where you want to save the debug screenshots.
     */
    public String getDebugScreenshotsPath() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getDebugScreenshotsPath();
        }
        return null;
    }

    /**
     * Sets debug screenshots prefix.
     *
     * @param prefix The prefix for the screenshots' names.
     */
    public void setDebugScreenshotsPrefix(String prefix) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setDebugScreenshotsPrefix(prefix);
        }

    }

    /**
     * Gets debug screenshots prefix.
     *
     * @return The prefix for the screenshots' names.
     */
    public String getDebugScreenshotsPrefix() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getDebugScreenshotsPrefix();
        }
        return null;
    }

    /**
     * Gets debug screenshots provider.
     *
     * @return the debug screenshots provider
     */
    public DebugScreenshotsProvider getDebugScreenshotsProvider() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getDebugScreenshotsProvider();
        }
        return null;
    }

    /**
     * Gets ignore caret.
     *
     * @return Whether to ignore or the blinking caret or not when comparing images.
     */
    public boolean getIgnoreCaret() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getIgnoreCaret();
        }
        return false;
    }

    /**
     * Sets the ignore blinking caret value.
     *
     * @param value The ignore value.
     */
    public void setIgnoreCaret(boolean value) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setIgnoreCaret(value);
        }
    }

    /**
     * Gets stitch overlap.
     *
     * @return Returns the stitching overlap in pixels.
     */
    public int getStitchOverlap() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getStitchOverlap();
        }
        return 0;
    }

    /**
     * Sets the stitching overlap in pixels.
     *
     * @param pixels The width (in pixels) of the overlap.
     */
    public void setStitchOverlap(int pixels) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setStitchOverlap(pixels);
        }
    }

    /**
     * Check region.
     *
     * @param region the region the check               See {@link #checkRegion(Region, int, String)}.               {@code tag} defaults to {@code null}.               Default match timeout is used.
     */
    public void checkRegion(Region region) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(region);
        }
    }

    /**
     * Takes a snapshot of the application under test and matches a specific region within it with the expected output.
     *
     * @param region       A non empty region representing the screen region to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException Thrown if a mismatch is detected and immediate failure reports are enabled.
     */
    public void checkRegion(final Region region, int matchTimeout, String tag) throws TestFailedException {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(region, matchTimeout, tag);
        }
    }

    /**
     * See {@link #checkRegion(WebElement, String)}.
     * {@code tag} defaults to {@code null}.
     *
     * @param element The element which represents the region to check.
     */
    public void checkRegion(WebElement element) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(element);
        }
    }

    /**
     * If {@code stitchContent} is {@code false} then behaves the same as
     * {@link #checkRegion(WebElement)}, otherwise
     * behaves the same as {@link #checkElement(WebElement)}.
     *
     * @param element       The element which represents the region to check.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(WebElement element, boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(element, stitchContent);
        }
    }

    /**
     * See {@link #checkRegion(WebElement, int, String)}.
     * Default match timeout is used.
     *
     * @param element The element which represents the region to check.
     * @param tag     An optional tag to be associated with the snapshot.
     */
    public void checkRegion(WebElement element, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(element, tag);
        }
    }

    /**
     * if {@code stitchContent} is {@code false} then behaves the same {@link
     * #checkRegion(WebElement, String)}***. Otherwise
     * behaves the same as {@link #checkElement(WebElement, String)}.
     *
     * @param element       The element which represents the region to check.
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(WebElement element, String tag, boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(element, tag, stitchContent);
        }
    }

    /**
     * Takes a snapshot of the application under test and matches a region of
     * a specific element with the expected region output.
     *
     * @param element      The element which represents the region to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException if a mismatch is detected and                             immediate failure reports are enabled
     */
    public void checkRegion(final WebElement element, int matchTimeout, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(element, matchTimeout, tag);
        }
    }

    /**
     * if {@code stitchContent} is {@code false} then behaves the same {@link
     * #checkRegion(WebElement, int, String)}***. Otherwise
     * behaves the same as {@link #checkElement(WebElement, String)}.
     *
     * @param element       The element which represents the region to check.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(WebElement element, int matchTimeout, String tag, boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(element, matchTimeout, tag, stitchContent);
        }
    }

    /**
     * See {@link #checkRegion(By, String)}.
     * {@code tag} defaults to {@code null}.
     *
     * @param selector The selector by which to specify which region to check.
     */
    public void checkRegion(By selector) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(selector);
        }
    }

    /**
     * If {@code stitchContent} is {@code false} then behaves the same as
     * {@link #checkRegion(By)}. Otherwise, behaves the
     * same as {@code #checkElement(org.openqa.selenium.By)}
     *
     * @param selector      The selector by which to specify which region to check.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(By selector, boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(selector, stitchContent);
        }
    }

    /**
     * See {@link #checkRegion(By, int, String)}.
     * Default match timeout is used.
     *
     * @param selector The selector by which to specify which region to check.
     * @param tag      An optional tag to be associated with the screenshot.
     */
    public void checkRegion(By selector, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(selector, tag);
        }
    }

    /**
     * If {@code stitchContent} is {@code false} then behaves the same as
     * {@link #checkRegion(By, String)}. Otherwise,
     * behaves the same as {@link #checkElement(By, String)}.
     *
     * @param selector      The selector by which to specify which region to check.
     * @param tag           An optional tag to be associated with the screenshot.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(By selector, String tag, boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(selector, tag, stitchContent);
        }
    }

    /**
     * Takes a snapshot of the application under test and matches a region
     * specified by the given selector with the expected region output.
     *
     * @param selector     The selector by which to specify which region to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the screenshot.
     * @throws TestFailedException if a mismatch is detected and                             immediate failure reports are enabled
     */
    public void checkRegion(By selector, int matchTimeout, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegion(selector, matchTimeout, tag);
        }
    }

    /**
     * See {@link #checkRegionInFrame(int, By, String)}.
     * {@code tag} defaults to {@code null}.
     *
     * @param frameIndex The index of the frame to switch to. (The same index                   as would be used in a call to                   driver.switchTo().frame()).
     * @param selector   The selector by which to specify which region to check inside the frame.
     */
    public void checkRegionInFrame(int frameIndex, By selector) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameIndex, selector);
        }
    }

    /**
     * See {@link #checkRegionInFrame(int, By, String)}.
     * {@code tag} defaults to {@code null}.
     *
     * @param frameIndex    The index of the frame to switch to. (The same index
     *                      as would be used in a call to
     *                      driver.switchTo().frame()).
     * @param selector      The selector by which to specify which region to check inside the frame.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(int frameIndex, By selector, boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameIndex, selector, stitchContent);
        }
    }

    /**
     * See {@link #checkRegionInFrame(int, By, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     *
     * @param frameIndex The index of the frame to switch to. (The same index
     *                   as would be used in a call to
     *                   driver.switchTo().frame()).
     * @param selector   The selector by which to specify which region to check inside the frame.
     * @param tag        An optional tag to be associated with the screenshot.
     */
    public void checkRegionInFrame(int frameIndex, By selector, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameIndex, selector, tag);
        }
    }

    /**
     * See {@link #checkRegionInFrame(int, By, int, String, boolean)}.
     * Default match timeout is used.
     *
     * @param frameIndex    The index of the frame to switch to. (The same index
     *                      as would be used in a call to
     *                      driver.switchTo().frame()).
     * @param selector      The selector by which to specify which region to check inside the frame.
     * @param tag           An optional tag to be associated with the screenshot.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(int frameIndex, By selector, String tag, boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameIndex, selector, tag, stitchContent);
        }
    }

    /**
     * See {@link #checkRegionInFrame(int, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     *
     * @param frameIndex   The index of the frame to switch to. (The same index
     *                     as would be used in a call to
     *                     driver.switchTo().frame()).
     * @param selector     The selector by which to specify which region to check inside the frame.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the screenshot.
     */
    public void checkRegionInFrame(int frameIndex, By selector, int matchTimeout, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameIndex, selector, matchTimeout, tag);
        }
    }

    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     *
     * @param frameIndex    The index of the frame to switch to. (The same index
     *                      as would be used in a call to
     *                      driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(int frameIndex, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameIndex, selector, matchTimeout, tag, stitchContent);
        }
    }

    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code null}.
     *
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector);
        }
    }

    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code tag} defaults to {@code null}.
     *
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector, boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector, stitchContent);
        }
    }

    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code null}.
     *
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     * @param tag           An optional tag to be associated with the snapshot.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector, tag);
        }
    }

    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * Default match timeout is used
     *
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check.
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the
     *                      region.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   String tag, boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector, tag, stitchContent);
        }
    }

    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     *
     * @param frameNameOrId The name or id of the frame to switch to. (as would                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check inside the frame.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   int matchTimeout, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector, matchTimeout, tag);
        }
    }

    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     *
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check inside the frame.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent If {@code true}, stitch the internal content of
     *                      the region (i.e., perform
     *                      {@link #checkElement(By, int, String)} on the region.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector, matchTimeout, tag, stitchContent);
        }
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, boolean)}.
     * {@code stitchContent} defaults to {@code null}.
     *
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameReference, selector);
        }
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, String, boolean)}.
     * {@code tag} defaults to {@code null}.
     *
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param stitchContent  If {@code true}, stitch the internal content of                       the region (i.e., perform                       {@link #checkElement(By, int, String)} on the                       region.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector, boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameReference, selector, stitchContent);
        }
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     *
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param tag            An optional tag to be associated with the snapshot.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameReference, selector, tag);
        }
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, int, String, boolean)}.
     * Default match timeout is used.
     *
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param tag            An optional tag to be associated with the snapshot.
     * @param stitchContent  If {@code true}, stitch the internal content of                       the region (i.e., perform                       {@link #checkElement(By, int, String)} on the                       region.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   String tag, boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameReference, selector, tag, stitchContent);
        }
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     *
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param matchTimeout   The amount of time to retry matching. (Milliseconds)
     * @param tag            An optional tag to be associated with the snapshot.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   int matchTimeout, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameReference, selector, matchTimeout, tag);
        }
    }

    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     *
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check.
     * @param matchTimeout   The amount of time to retry matching. (Milliseconds)
     * @param tag            An optional tag to be associated with the snapshot.
     * @param stitchContent  If {@code true}, stitch the internal content of                       the region (i.e., perform                       {@link #checkElement(By, int, String)} on the                       region.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkRegionInFrame(frameReference, selector, matchTimeout, tag, stitchContent);
        }
    }

    /**
     * See {@link #checkElement(WebElement, String)}.
     * {@code tag} defaults to {@code null}.
     *
     * @param element the element
     */
    public void checkElement(WebElement element) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkElement(element);
        }
    }

    /**
     * Check element.
     *
     * @param element the element to check
     * @param tag     See {@link #checkElement(WebElement, int, String)}.                Default match timeout is used.
     */
    public void checkElement(WebElement element, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkElement(element, tag);
        }
    }

    /**
     * Takes a snapshot of the application under test and matches a specific
     * element with the expected region output.
     *
     * @param element      The element to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException if a mismatch is detected and immediate failure reports are enabled
     */
    public void checkElement(WebElement element, int matchTimeout, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkElement(element, matchTimeout, tag);
        }
    }

    /**
     * Check element.
     *
     * @param selector the selector                 See {@link #checkElement(By, String)}.                 {@code tag} defaults to {@code null}.
     */
    public void checkElement(By selector) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkElement(selector);
        }
    }

    /**
     * Check element.
     *
     * @param selector selector
     * @param tag      tg                 See {@link #checkElement(By, int, String)}.                 Default match timeout is used.
     */
    public void checkElement(By selector, String tag) {
        if (!isVisualGridEyes) {
            check(tag, Target.region(selector).fully());
        }
    }

    /**
     * Takes a snapshot of the application under test and matches an element
     * specified by the given selector with the expected region output.
     *
     * @param selector     Selects the element to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the screenshot.
     * @throws TestFailedException if a mismatch is detected and                             immediate failure reports are enabled
     */
    public void checkElement(By selector, int matchTimeout, String tag) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.checkElement(selector, matchTimeout, tag);
        }
    }

    /**
     * Adds a mouse trigger.
     *
     * @param action  Mouse action.
     * @param control The control on which the trigger is activated (context relative coordinates).
     * @param cursor  The cursor's position relative to the control.
     */
    public void addMouseTrigger(MouseAction action, Region control, Location cursor) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.addMouseTrigger(action, control, cursor);
        }
    }

    /**
     * Adds a mouse trigger.
     *
     * @param action  Mouse action.
     * @param element The WebElement on which the click was called.
     */
    public void addMouseTrigger(MouseAction action, WebElement element) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.addMouseTrigger(action, element);
        }
    }

    /**
     * Adds a keyboard trigger.
     *
     * @param control The control's context-relative region.
     * @param text    The trigger's text.
     */
    public void addTextTrigger(Region control, String text) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.addTextTrigger(control, text);
        }
    }

    /**
     * Adds a keyboard trigger.
     *
     * @param element The element for which we sent keys.
     * @param text    The trigger's text.
     */
    public void addTextTrigger(WebElement element, String text) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.addTextTrigger(element, text);
        }
    }

    /**
     * Use this method only if you made a previous call to {@link #open
     * (WebDriver, String, String)}*** or one of its variants.
     * <p>
     * {@inheritDoc}
     *
     * @return the viewport size
     */
    public RectangleSize getViewportSize() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getViewportSize();
        }
        return null;
    }

    /**
     * Call this method if for some
     * reason you don't want to call {@link #open(WebDriver, String, String)}
     * (or one of its variants) yet.
     *
     * @param driver The driver to use for getting the viewport.
     * @return The viewport size of the current context.
     */
    public static RectangleSize getViewportSize(WebDriver driver) {
        return SeleniumEyes.getViewportSize(driver);
    }

    /**
     * Set the viewport size using the driver. Call this method if for some
     * reason you don't want to call {@link #open(WebDriver, String, String)}
     * (or one of its variants) yet.
     *
     * @param driver The driver to use for setting the viewport.
     * @param size   The required viewport size.
     */
    public static void setViewportSize(WebDriver driver, RectangleSize size) {
        ArgumentGuard.notNull(driver, "driver");
        EyesSeleniumUtils.setViewportSize(new Logger(), driver, size);
    }

    /**
     * Starts a test.
     *
     * @param driver   The web driver that controls the browser hosting                 the application under test.
     * @param appName  The name of the application under test.
     * @param testName The test name.                 (i.e., the visible part of the document's body) or                 {@code null} to use the current window's viewport.
     * @return A wrapped WebDriver which enables SeleniumEyes trigger recording and frame handling.
     */
    public WebDriver open(WebDriver driver, String appName, String testName) {
        RectangleSize viewportSize = SeleniumEyes.getViewportSize(driver);
        return open(driver, new SeleniumConfiguration(appName, testName, viewportSize));
    }

    /**
     * Starts a test.
     *
     * @param driver       The web driver that controls the browser hosting                     the application under test.
     * @param appName      The name of the application under test.
     * @param testName     The test name.
     * @param viewportSize The required browser's viewport size                     (i.e., the visible part of the document's body) or                     {@code null} to use the current window's viewport.
     * @return A wrapped WebDriver which enables SeleniumEyes trigger recording and frame handling. {@code sessionType} defaults to {@code null}.
     */
    public WebDriver open(WebDriver driver, String appName, String testName,
                          RectangleSize viewportSize) {
        return open(driver, new SeleniumConfiguration(appName, testName, viewportSize));
    }

    /**
     * Gets hide caret.
     *
     * @return gets the hide caret flag
     */
    public boolean getHideCaret() {
        if (this.configuration != null) {
            return this.configuration.getHideCaret();
        }
        return globalConfiguration.getHideCaret();
    }

    /**
     * Should stitch content boolean.
     *
     * @return the should stitch flag
     */
    public boolean shouldStitchContent() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.shouldStitchContent();
        }
        return false;
    }

    /**
     * ﻿Forces a full page screenshot (by scrolling and stitching) if the
     * browser only ﻿supports viewport screenshots).
     *
     * @param shouldForce Whether to force a full page screenshot or not.
     */
    public void setForceFullPageScreenshot(boolean shouldForce) {
        if (this.configuration != null) {
            this.configuration.setForceFullPageScreenshot(shouldForce);
        }
        globalConfiguration.setForceFullPageScreenshot(shouldForce);
    }

    /**
     * Gets force full page screenshot.
     *
     * @return Whether SeleniumEyes should force a full page screenshot.
     */
    public boolean getForceFullPageScreenshot() {
        if (this.configuration != null) {
            return this.configuration.getForceFullPageScreenshot();
        }
        return globalConfiguration.getForceFullPageScreenshot();
    }

    /**
     * Sets the time to wait just before taking a screenshot (e.g., to allow
     * positioning to stabilize when performing a full page stitching).
     *
     * @param waitBeforeScreenshots The time to wait (Milliseconds). Values                              smaller or equal to 0, will cause the                              default value to be used.
     */
    public void setWaitBeforeScreenshots(int waitBeforeScreenshots) {
        if (this.configuration != null) {
            this.configuration.setWaitBeforeScreenshots(waitBeforeScreenshots);
        }
        globalConfiguration.setWaitBeforeScreenshots(waitBeforeScreenshots);
    }

    /**
     * Gets wait before screenshots.
     *
     * @return The time to wait just before taking a screenshot.
     */
    public int getWaitBeforeScreenshots() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getWaitBeforeScreenshots();
        }
        return 0;
    }


    /**
     * Turns on/off the automatic scrolling to a region being checked by
     * {@code checkRegion}.
     *
     * @param shouldScroll Whether to automatically scroll to a region being validated.
     */
    public void setScrollToRegion(boolean shouldScroll) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.setScrollToRegion(shouldScroll);
        }
    }

    /**
     * Gets scroll to region.
     *
     * @return Whether to automatically scroll to a region being validated.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean getScrollToRegion() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getScrollToRegion();
        }
        return false;
    }

    /**
     * Set the type of stitching used for full page screenshots. When the
     * page includes fixed position header/sidebar, use {@link StitchMode#CSS}.
     * Default is {@link StitchMode#SCROLL}.
     *
     * @param mode The stitch mode to set.
     */
    public void setStitchMode(StitchMode mode) {
        this.globalConfiguration.setStitchMode(mode);
        if (this.configuration != null) {
            this.globalConfiguration.setStitchMode(mode);
        }
    }

    /**
     * Gets stitch mode.
     *
     * @return The current stitch mode settings.
     */
    public StitchMode getStitchMode() {
        if (this.configuration != null) {
            return this.configuration.getStitchMode();
        }
        return globalConfiguration.getStitchMode();
    }

    /**
     * Hide the scrollbars when taking screenshots.
     *
     * @param shouldHide Whether to hide the scrollbars or not.
     */
    public void setHideScrollbars(boolean shouldHide) {
        this.globalConfiguration.setHideScrollbars(shouldHide);
        if (this.configuration != null) {
            this.globalConfiguration.setHideScrollbars(shouldHide);
        }
    }

    /**
     * Gets hide scrollbars.
     *
     * @return Whether or not scrollbars are hidden when taking screenshots.
     */
    public boolean getHideScrollbars() {
        if (this.configuration != null) {
            return this.configuration.getHideScrollbars();
        }
        return globalConfiguration.getHideScrollbars();
    }

    /**
     * Gets rotation.
     *
     * @return The image rotation model.
     */
    public ImageRotation getRotation() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getRotation();
        }
        return null;
    }

    /**
     * Sets rotation.
     *
     * @param rotation The image rotation model.
     */
    public void setRotation(ImageRotation rotation) {
        this.rotation = rotation;
        if (!isVisualGridEyes) {
            if (driver != null) {
                ((EyesWebDriver) driver).setRotation(rotation);
            }
        }
    }

    /**
     * Gets device pixel ratio.
     *
     * @return The device pixel ratio, or if the DPR is not known yet or if it wasn't possible to extract it.
     */
    public double getDevicePixelRatio() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getDevicePixelRatio();
        }
        return 0;
    }

    /**
     * See {@link #checkWindow(String)}.
     * {@code tag} defaults to {@code null}.
     * Default match timeout is used.
     */
    public void checkWindow() {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.checkWindow();
        } else {
            this.visualGridEyes.check(null, new CheckSettings(15000));
        }
    }

    /**
     * See {@link #checkWindow(int, String)}.
     * Default match timeout is used.
     *
     * @param tag An optional tag to be associated with the snapshot.
     */
    public void checkWindow(String tag) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.checkWindow();
        } else {
            this.visualGridEyes.check(tag, new CheckSettings(15000));
        }
    }

    /**
     * Takes a snapshot of the application under test and matches it with
     * the expected output.
     *
     * @param matchTimeout The amount of time to retry matching (Milliseconds).
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException Thrown if a mismatch is detected and                             immediate failure reports are enabled.
     */
    public void checkWindow(int matchTimeout, String tag) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.checkWindow(matchTimeout, tag);
        } else {
            this.visualGridEyes.check(tag, new CheckSettings(15000));
        }
    }

    /**
     * Runs a test on the current window.
     *
     * @param driver       The web driver that controls the browser hosting                     the application under test.
     * @param appName      The name of the application under test.
     * @param testName     The test name (will also be used as the tag name for the step).
     * @param viewportSize The required browser's viewport size                     (i.e., the visible part of the document's body) or                     {@code null} to use the current window's viewport.
     */
    public void testWindow(WebDriver driver, String appName, String testName,
                           RectangleSize viewportSize) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testWindow(driver, appName, testName, viewportSize);
        }
    }

    /**
     * Test window.
     *
     * @param driver   driver
     * @param appName  app name
     * @param testName test name                 See {@link #testWindow(WebDriver, String, String, RectangleSize)}.                 {@code viewportSize} defaults to {@code null}.
     */
    public void testWindow(WebDriver driver, String appName, String testName) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testWindow(driver, appName, testName);
        }
    }

    /**
     * Test window.
     *
     * @param driver       driver
     * @param testName     test name
     * @param viewportSize viewport size                     See {@link #testWindow(WebDriver, String, String, RectangleSize)}.                     {@code appName} defaults to {@code null} (which means the name set in
     */
    public void testWindow(WebDriver driver, String testName,
                           RectangleSize viewportSize) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testWindow(driver, testName, viewportSize);
        }
    }

    /**
     * Test window.
     *
     * @param driver   driver
     * @param testName test name                 See {@link #testWindow(WebDriver, String, RectangleSize)}.                 {@code viewportSize} defaults to {@code null}.
     */
    public void testWindow(WebDriver driver, String testName) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testWindow(driver, testName);
        }
    }

    /**
     * Run a visual performance test.
     *
     * @param driver   The driver to use.
     * @param appName  The name of the application being tested.
     * @param testName The test name.
     * @param action   Actions to be performed in parallel to starting the test.
     * @param deadline The expected time until the application should have been loaded. (Seconds)
     * @param timeout  The maximum time until the application should have been loaded. (Seconds)
     */
    public void testResponseTime(final WebDriver driver, String appName,
                                 String testName, final SeleniumEyes.WebDriverAction action,
                                 int deadline, int timeout) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testResponseTime(driver, appName, testName, action, deadline, timeout);
        }
    }

    /**
     * Test response time.
     *
     * @param driver   driver
     * @param appName  app name
     * @param testName test name
     * @param action   web driver action
     * @param deadline deadline in secs                 See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, SeleniumEyes.WebDriverAction action,
                                 int deadline) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testResponseTime(driver, appName, testName, action, deadline);
        }
    }

    /**
     * Test response time.
     *
     * @param driver   dirver
     * @param appName  app name
     * @param testName test name
     * @param action   web driver action                 See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, SeleniumEyes.WebDriverAction action) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testResponseTime(driver, appName, testName, action);
        }
    }

    /**
     * Test response time.
     *
     * @param driver   driver
     * @param appName  app name
     * @param testName test name                 See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int)}.                 {@code action} defaults to {@code null}.
     * @param deadline deadline in secs
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, int deadline) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testResponseTime(driver, appName, testName, deadline);
        }
    }

    /**
     * Test response time.
     *
     * @param driver   driver
     * @param appName  app name
     * @param testName test name                 See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int)}.                 {@code action} defaults to {@code null}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testResponseTime(driver, appName, testName);
        }
    }

    /**
     * Test response time.
     *
     * @param driver       driver
     * @param appName      app name
     * @param testName     test name                     Similar to {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int)},                     except this method sets the viewport size before starting the                     performance test.
     * @param action       web driver action
     * @param deadline     deadline in secs
     * @param timeout      timeout for check
     * @param viewportSize The required viewport size.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, SeleniumEyes.WebDriverAction action,
                                 int deadline, int timeout,
                                 RectangleSize viewportSize) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testResponseTime(driver, appName, testName, action, deadline, timeout, viewportSize);
        }
    }

    /**
     * Test response time.
     *
     * @param driver       driver
     * @param appName      app name
     * @param testName     test name
     * @param action       web driver action
     * @param deadline     deadline in secs
     * @param viewportSize viewport size for check                     See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int, RectangleSize)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, SeleniumEyes.WebDriverAction action,
                                 int deadline, RectangleSize viewportSize) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testResponseTime(driver, appName, testName, action, deadline, viewportSize);
        }
    }

    /**
     * Test response time.
     *
     * @param driver       driver
     * @param appName      app name
     * @param testName     test name
     * @param action       web driver action
     * @param viewportSize viewport size to check                     See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int, RectangleSize)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, SeleniumEyes.WebDriverAction action,
                                 RectangleSize viewportSize) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testResponseTime(driver, appName, testName, action, viewportSize);
        }
    }

    /**
     * Test response time.
     *
     * @param driver       driver
     * @param appName      app name
     * @param testName     test name
     * @param deadline     deadline in secs
     * @param timeout      timeout
     * @param viewportSize viewport size to check                 See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int, RectangleSize)}.                 {@code action} defaults to {@code null}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, int deadline, int timeout,
                                 RectangleSize viewportSize) {
        if (!this.isVisualGridEyes) {
            testResponseTime(driver, appName, testName, null, deadline, timeout,
                    viewportSize);
        }
    }

    /**
     * Test response time.
     *
     * @param driver       driver
     * @param appName      app name
     * @param testName     test name
     * @param deadline     deadline in secs
     * @param viewportSize viewport size to check                     See {@link #testResponseTime(WebDriver, String, String, int, int, RectangleSize)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, int deadline,
                                 RectangleSize viewportSize) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testResponseTime(driver, appName, testName, deadline, viewportSize);
        }
    }

    /**
     * Test response time.
     *
     * @param driver       driver
     * @param appName      app name
     * @param testName     test name
     * @param viewportSize viewport size to check See {@link #testResponseTime(WebDriver, String, String, int, int, RectangleSize)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, RectangleSize viewportSize) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.testResponseTime(driver, appName, testName, viewportSize);
        }
    }

    /**
     * Takes multiple screenshots at once (given all <code>ICheckSettings</code> objects are on the same level).
     *
     * @param checkSettings Multiple <code>ICheckSettings</code> object representing different regions in the viewport.
     */
    public void check(ICheckSettings... checkSettings) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.check(checkSettings);
        } else {
            for (ICheckSettings checkSetting : checkSettings) {
                this.visualGridEyes.check(checkSetting);
            }
        }
    }

    /**
     * Check frame.
     *
     * @param frameNameOrId frame to check(name or id)                      See {@link #checkFrame(String, int, String)}.                      {@code tag} defaults to {@code null}. Default match timeout is used.
     */
    public void checkFrame(String frameNameOrId) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.checkFrame(frameNameOrId);
        } else {
            this.visualGridEyes.check(Target.frame(frameNameOrId));
        }
    }

    /**
     * Check frame.
     *
     * @param frameNameOrId frame to check(name or id)
     * @param tag           See {@link #checkFrame(String, int, String)}.                      Default match timeout is used.
     */
    public void checkFrame(String frameNameOrId, String tag) {
        check(tag, Target.frame(frameNameOrId).fully());
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     *
     * @param frameNameOrId The name or id of the frame to check. (The same                      name/id as would be used in a call to                      driver.switchTo().frame()).
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the match.
     */
    public void checkFrame(String frameNameOrId, int matchTimeout, String tag) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.checkFrame(frameNameOrId, matchTimeout, tag);
        } else {
            this.visualGridEyes.check(tag, Target.frame(frameNameOrId));
        }
    }

    /**
     * Check frame.
     *
     * @param frameIndex index of frame                   See {@link #checkFrame(int, int, String)}.                   {@code tag} defaults to {@code null}. Default match timeout is used.
     */
    public void checkFrame(int frameIndex) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.checkFrame(frameIndex);
        } else {
            this.visualGridEyes.check(Target.frame(frameIndex));
        }
    }

    /**
     * Check frame.
     *
     * @param frameIndex index of frame
     * @param tag        See {@link #checkFrame(int, int, String)}.                   Default match timeout is used.
     */
    public void checkFrame(int frameIndex, String tag) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.checkFrame(frameIndex, tag);
        } else {
            this.visualGridEyes.check(tag, Target.frame(frameIndex));
        }
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     *
     * @param frameIndex   The index of the frame to switch to. (The same index                     as would be used in a call to                     driver.switchTo().frame()).
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the match.
     */
    public void checkFrame(int frameIndex, int matchTimeout, String tag) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.checkFrame(frameIndex, matchTimeout, tag);
        } else {
            this.visualGridEyes.check(tag, Target.frame(frameIndex));
        }
    }

    /**
     * Check frame.
     *
     * @param frameReference web element to check                       See {@link #checkFrame(WebElement, int, String)}.                       {@code tag} defaults to {@code null}.                       Default match timeout is used.
     */
    public void checkFrame(WebElement frameReference) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.checkFrame(frameReference);
        } else {
            this.visualGridEyes.check(Target.frame(frameReference));
        }
    }

    /**
     * Check frame.
     *
     * @param frameReference web element to check
     * @param tag            tag                       See {@link #checkFrame(WebElement, int, String)}.                       Default match timeout is used.
     */
    public void checkFrame(WebElement frameReference, String tag) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.checkFrame(frameReference, tag);
        } else {
            this.visualGridEyes.check(tag, Target.frame(frameReference));
        }
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     *
     * @param frameReference The element which is the frame to switch to. (as                       would be used in a call to                       driver.switchTo().frame() ).
     * @param matchTimeout   The amount of time to retry matching (milliseconds).
     * @param tag            An optional tag to be associated with the match.
     */
    public void checkFrame(WebElement frameReference, int matchTimeout, String tag) {
        check(tag, Target.frame(frameReference).timeout(matchTimeout));
    }

    /**
     * Matches the frame given by the frames path, by switching into the frame
     * and using stitching to get an image of the frame.
     *
     * @param framePath    The path to the frame to check. This is a list of                     frame names/IDs (where each frame is nested in the                     previous frame).
     * @param matchTimeout The amount of time to retry matching (milliseconds).
     * @param tag          An optional tag to be associated with the match.
     */
    public void checkFrame(String[] framePath, int matchTimeout, String tag) {

        this.seleniumEyes.checkFrame(framePath, matchTimeout, tag);
    }

    /**
     * See {@link #checkFrame(String[], int, String)}.
     * Default match timeout is used.
     *
     * @param framesPath the frames path
     * @param tag        the tag
     */
    public void checkFrame(String[] framesPath, String tag) {
        this.checkFrame(framesPath, tag);
    }

    /**
     * See {@link #checkFrame(String[], int, String)}.
     * Default match timeout is used.
     * {@code tag} defaults to {@code null}.
     *
     * @param framesPath the frames path
     */
    public void checkFrame(String[] framesPath) {
        this.seleniumEyes.checkFrame(framesPath);
    }

    /**
     * Gets server url.
     *
     * @return The URI of the eyes server.
     */
    public URI getServerUrl() {
        if (this.isVisualGridEyes) {
            return this.visualGridEyes.getServerUrl();
        } else {
            return this.seleniumEyes.getServerUrl();
        }
    }

    /**
     * Sets the user given agent id of the SDK. {@code null} is referred to
     * as no id.
     *
     * @param agentId The agent ID to set.
     */
    public void setAgentId(String agentId) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setAgentId(agentId);
        } else {
            this.visualGridEyes.setAgentId(agentId);
        }
    }

    /**
     * Gets agent id.
     *
     * @return The user given agent id of the SDK.
     */
    public String getAgentId() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getAgentId();
        }
        return this.visualGridEyes.getAgentId();
    }

    /**
     * Sets the server connector to use. MUST BE SET IN ORDER FOR THE EYES OBJECT TO WORK!
     *
     * @param serverConnector The server connector object to use.
     */
    public void setServerConnector(IServerConnector serverConnector) {
        if (this.isVisualGridEyes) {
            this.visualGridEyes.setServerConnector(serverConnector);
        }
    }


    /**
     * Gets proxy.
     *
     * @return The current proxy settings used by the server connector, or {@code null} if no proxy is set.
     */
    public AbstractProxySettings getProxy() {
        if (isVisualGridEyes) {
            return this.visualGridEyes.getProxy();
        } else {
            return this.seleniumEyes.getProxy();
        }
    }


    /**
     * Sets app name.
     *
     * @param appName The name of the application under test.
     */
    public void setAppName(String appName) {
        this.globalConfiguration.setAppName(appName);
        if (this.configuration != null) {
            this.globalConfiguration.setAppName(appName);
        }
    }

    /**
     * Gets app name.
     *
     * @return The name of the application under test.
     */
    public String getAppName() {
        if (configuration != null) {
            return configuration.getAppName();
        }
        return globalConfiguration.getAppName();
    }


    /**
     * Gets host os.
     *
     * @return the host os
     */
    public String getHostOS() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getHostOS();
        }
        return "";
    }

    /**
     * Gets host app.
     *
     * @return The application name running the AUT.
     */
    public String getHostApp() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getHostOS();
        }
        return "";
    }

    /**
     * Sets baseline name.
     *
     * @param baselineName If specified, determines the baseline to compare                     with and disables automatic baseline inference.
     * @deprecated Only available for backward compatibility. See {@link #setBaselineEnvName(String)}.
     */
    public void setBaselineName(String baselineName) {
        setBaselineEnvName(baselineName);
    }

    /**
     * Gets baseline name.
     *
     * @return The baseline name, if specified.
     * @deprecated Only available for backward compatibility. See {@link #getBaselineEnvName()}.
     */
    @SuppressWarnings("UnusedDeclaration")
    public String getBaselineName() {
        return getBaselineEnvName();
    }

    /**
     * If not {@code null}, determines the name of the environment of the baseline.
     *
     * @param baselineEnvName The name of the baseline's environment.
     */
    public void setBaselineEnvName(String baselineEnvName) {
        this.globalConfiguration.setBaselineEnvName(baselineEnvName);
        if (this.configuration != null) {
            this.globalConfiguration.setBaselineEnvName(baselineEnvName);
        }
    }

    /**
     * If not {@code null}, determines the name of the environment of the baseline.
     *
     * @return The name of the baseline's environment, or {@code null} if no such name was set.
     */
    public String getBaselineEnvName() {
        if (configuration != null) {
            return configuration.getBaselineEnvName();
        }
        return globalConfiguration.getBaselineEnvName();
    }


    /**
     * If not {@code null} specifies a name for the environment in which the application under test is running.
     *
     * @param envName The name of the environment of the baseline.
     */
    public void setEnvName(String envName) {
        this.globalConfiguration.setEnvironmentName(envName);
        if (this.configuration != null) {
            this.globalConfiguration.setEnvironmentName(envName);
        }
    }

    /**
     * If not {@code null} specifies a name for the environment in which the application under test is running.
     *
     * @return The name of the environment of the baseline, or {@code null} if no such name was set.
     */
    public String getEnvName() {
        if (isVisualGridEyes) {
            return this.visualGridEyes.getEnvName();
        } else {
            return this.seleniumEyes.getEnvName();
        }
    }


    /**
     * Gets position provider.
     *
     * @return The currently set position provider.
     */
    public PositionProvider getPositionProvider() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getPositionProvider();
        }
        return null;
    }

    /**
     * Sets position provider.
     *
     * @param positionProvider The position provider to be used.
     */
    public void setPositionProvider(PositionProvider positionProvider) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setPositionProvider(positionProvider);
        }
    }

    /**
     * Sets explicit viewport size.
     *
     * @param explicitViewportSize sets the viewport
     */
    public void setExplicitViewportSize(RectangleSize explicitViewportSize) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setExplicitViewportSize(explicitViewportSize);
        }
    }

    /**
     * Gets agent setup.
     *
     * @return the agent setup.
     */
    public Object getAgentSetup() {
        if (!isVisualGridEyes) {
            this.seleniumEyes.getAgentSetup();
        }
        return null;
    }

    /**
     * Log.
     *
     * @param message the massage to log
     */
    public void log(String message) {
        if (isVisualGridEyes) {
            this.visualGridEyes.getLogger().log(message);
        } else {
            this.seleniumEyes.log(message);
        }
    }

    /**
     * Add session event handler.
     *
     * @param eventHandler adds the event handler
     */
    public void addSessionEventHandler(ISessionEventHandler eventHandler) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.addSessionEventHandler(eventHandler);
        }
    }

    /**
     * Remove session event handler.
     *
     * @param eventHandler sets the event handler
     */
    public void removeSessionEventHandler(ISessionEventHandler eventHandler) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.removeSessionEventHandler(eventHandler);
        }
    }

    /**
     * clears session event.
     */
    public void clearSessionEventHandlers() {
        if (!isVisualGridEyes) {
            this.seleniumEyes.clearSessionEventHandlers();
        }
    }

    /**
     * Is send dom boolean.
     *
     * @return isSendDom flag
     */
    public boolean isSendDom() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.isSendDom();
        }

        return true;
    }

    /**
     * for internal usage
     *
     * @param listener the listener
     */
    public void setOnDomCapture(IDomCaptureListener listener) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setOnDomCapture(listener);
        }
    }

    /**
     * Sets send dom.
     *
     * @param isSendDom should send dom flag
     */
    public void setSendDom(boolean isSendDom) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setSendDom(isSendDom);
        }
    }

    /**
     * Sets host os.
     *
     * @param hostOS the hosting host
     */
    public void setHostOS(String hostOS) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setHostApp(hostOS);
        }
    }

    /**
     * Sets host app.
     *
     * @param hostApp The application running the AUT (e.g., Chrome).
     */
    public void setHostApp(String hostApp) {
        if (!isVisualGridEyes) {
            this.seleniumEyes.setHostApp(hostApp);
        }
    }

    /**
     * for internal usage
     *
     * @return rendering info
     */
    public RenderingInfo getRenderingInfo() {
        return null;
    }

    /**
     * Gets branch name.
     *
     * @return The current branch (see {@link #setBranchName(String)}).
     */
    public String getBranchName() {
        return configuration.getBranchName();
    }

    /**
     * Gets parent branch name.
     *
     * @return The name of the current parent branch under which new branches will be created. (see {@link #setParentBranchName(String)}).
     */
    public String getParentBranchName() {
        return configuration.getParentBranchName();
    }

    /**
     * Sets the branch under which new branches are created. (see {@link
     * #setBranchName(String)}***.
     *
     * @param branchName Branch name or {@code null} to specify the default branch.
     */
    public void setBaselineBranchName(String branchName) {
        this.configuration.setBaselineBranchName(branchName);
    }

    /**
     * Gets baseline branch name.
     *
     * @return The name of the current parent branch under which new branches will be created. (see {@link #setBaselineBranchName(String)}).
     */
    public String getBaselineBranchName() {
        return configuration.getBaselineBranchName();
    }

    /**
     * Automatically save differences as a baseline.
     *
     * @param saveDiffs Sets whether to automatically save differences as baseline.
     */
    public void setSaveDiffs(Boolean saveDiffs) {
        this.configuration.setSaveDiffs(saveDiffs);
    }

    /**
     * Returns whether to automatically save differences as a baseline.
     *
     * @return Whether to automatically save differences as baseline.
     */
    public Boolean getSaveDiffs() {
        return this.configuration.getSaveDiffs();
    }

    /**
     * Superseded by {@link #setHostOS(String)} and {@link #setHostApp(String)}.
     * Sets the OS (e.g., Windows) and application (e.g., Chrome) that host the application under test.
     *
     * @param hostOS  The name of the OS hosting the application under test or {@code null} to auto-detect.
     * @param hostApp The name of the application hosting the application under test or {@code null} to auto-detect.
     */
    @Deprecated
    public void setAppEnvironment(String hostOS, String hostApp) {
        setHostOS(hostOS);
        setHostApp(hostApp);
    }

    /**
     * Gets driver.
     *
     * @return the driver
     */
    public WebDriver getDriver() {
        return this.driver;
    }

    /**
     * Gets original fc.
     *
     * @return Original frame chain
     */
    public FrameChain getOriginalFC() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getOriginalFC();
        }
        return null;
    }

    /**
     * Gets current frame position provider.
     *
     * @return get Current Frame Position Provider
     */
    public PositionProvider getCurrentFramePositionProvider() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getCurrentFramePositionProvider();
        }
        return null;
    }

    /**
     * Gets region to check.
     *
     * @return the region to check
     */
    public Region getRegionToCheck() {
        if (!this.isVisualGridEyes) {
            return this.seleniumEyes.getRegionToCheck();
        }
        return null;
    }

    /**
     * Sets region to check.
     *
     * @param regionToCheck the region to check
     */
    public void setRegionToCheck(Region regionToCheck) {
        if (!this.isVisualGridEyes) {
            this.seleniumEyes.setRegionToCheck(regionToCheck);
        }
    }

    /**
     * Gets current frame scroll root element.
     *
     * @return the current scroll root web element
     */
    public WebElement getCurrentFrameScrollRootElement() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getCurrentFrameScrollRootElement();
        }
        return null;
    }

    /**
     * Gets server connector.
     *
     * @return the server connector
     */
    public IServerConnector getServerConnector() {
        if (!isVisualGridEyes) {
            return this.seleniumEyes.getServerConnector();
        } else {
//            this.visualGridEyes.getS
        }
        return null;
    }

    private ISeleniumConfigurationGetter getConfigGetter(){
        this.
    }

}
