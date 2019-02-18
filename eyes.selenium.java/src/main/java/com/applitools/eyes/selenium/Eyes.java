package com.applitools.eyes.selenium;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.exceptions.TestFailedException;
import com.applitools.eyes.selenium.config.Configuration;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.selenium.positioning.ImageRotation;
import com.applitools.eyes.selenium.rendering.VisualGridEyes;
import com.applitools.eyes.triggers.MouseAction;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.model.TestResultContainer;
import com.applitools.eyes.visualGridClient.services.EyesRunner;
import com.applitools.eyes.visualGridClient.services.VisualGridRunner;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Eyes {

    private boolean isVisualGridEyes = false;
    private VisualGridEyes visualGridEyes = null;
    private SeleniumEyes seleniumEyes = null;


    public Eyes() {
        seleniumEyes = new SeleniumEyes();
    }

    public Eyes(EyesRunner runner) {
        if (runner instanceof VisualGridRunner) {
            visualGridEyes = new VisualGridEyes(new VisualGridRunner(2));
            isVisualGridEyes = true;
        } else {
            seleniumEyes = new SeleniumEyes();
        }
    }

    public void open(WebDriver webDriver, RenderingConfiguration renderingConfiguration) {
        visualGridEyes.open(webDriver, renderingConfiguration);
    }

    public void open(WebDriver webDriver, Configuration configuration) {
        seleniumEyes.open(webDriver, configuration);
    }


    public void setServerUrl(String serverUrl) {
        if (isVisualGridEyes) {
            visualGridEyes.setServerUrl(serverUrl);
        } else {
            seleniumEyes.setServerUrl(serverUrl);
        }
    }


    public void setProxy(AbstractProxySettings proxySettings) {
        if (isVisualGridEyes) {
            visualGridEyes.setProxy(proxySettings);
        } else {
            seleniumEyes.setProxy(proxySettings);
        }
    }


    public void setIsDisabled(boolean isDisabled) {
        if (isVisualGridEyes) {
            visualGridEyes.setIsDisabled(isDisabled);
        } else {
            seleniumEyes.setIsDisabled(isDisabled);
        }
    }

    public void check(ICheckSettings checkSettings) {
        if (isVisualGridEyes) {
            visualGridEyes.check(checkSettings);
        } else {
            seleniumEyes.check(checkSettings);
        }
    }

    public TestResults close() {
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
            return seleniumEyes.close();
        }
        return null;
    }

    public void abortIfNotClosed() {
        if (isVisualGridEyes) {
            visualGridEyes.abortIfNotClosed();
        } else {
            seleniumEyes.abortIfNotClosed();
        }
    }


    public boolean getIsDisabled() {
        if (isVisualGridEyes) {
            return visualGridEyes.getIsDisabled();
        } else {
            return seleniumEyes.getIsDisabled();
        }
    }


    public String getApiKey() {
        if (isVisualGridEyes) {
            return visualGridEyes.getApiKey();
        } else {
            return seleniumEyes.getApiKey();
        }
    }


    public void setApiKey(String apiKey) {
        if (isVisualGridEyes) {
            visualGridEyes.setApiKey(apiKey);
        } else {
            seleniumEyes.setApiKey(apiKey);
        }
    }


    public void setBranchName(String branchName) {
        if (isVisualGridEyes) {
            visualGridEyes.setBranchName(branchName);
        } else {
            seleniumEyes.setBranchName(branchName);
        }
    }


    public void setParentBranchName(String branchName) {
        if (isVisualGridEyes) {
            visualGridEyes.setParentBranchName(branchName);
        } else {
            seleniumEyes.setParentBranchName(branchName);
        }
    }


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
        this.seleniumEyes.setMatchTimeout(ms);
    }

    /**
     * @return The maximum time in ms waits for a match.
     */
    public int getMatchTimeout() {
        return this.seleniumEyes.getMatchTimeout();
    }

    /**
     * Set whether or not new tests are saved by default.
     *
     * @param saveNewTests True if new tests should be saved by default. False otherwise.
     */
    public void setSaveNewTests(boolean saveNewTests) {
        this.seleniumEyes.setSaveNewTests(saveNewTests);
    }

    /**
     * @return True if new tests are saved by default.
     */
    public boolean getSaveNewTests() {
        return this.seleniumEyes.getSaveNewTests();
    }

    /**
     * Set whether or not failed tests are saved by default.
     *
     * @param saveFailedTests True if failed tests should be saved by default, false otherwise.
     */
    public void setSaveFailedTests(boolean saveFailedTests) {
        this.seleniumEyes.setSaveFailedTests(saveFailedTests);
    }

    /**
     * @return True if failed tests are saved by default.
     */
    public boolean getSaveFailedTests() {
        return this.seleniumEyes.getSaveFailedTests();
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
     * @return The currently set batch info.
     */
    public BatchInfo getBatch() {
        return this.seleniumEyes.getBatch();
    }

    /**
     * @param failureReports The failure reports setting.
     * @see FailureReports
     */
    public void setFailureReports(FailureReports failureReports) {
        this.seleniumEyes.setFailureReports(failureReports);
    }

    /**
     * @return the failure reports setting.
     */
    public FailureReports getFailureReports() {
        return this.seleniumEyes.getFailureReports();
    }

    /**
     * Updates the match settings to be used for the session.
     *
     * @param defaultMatchSettings The match settings to be used for the session.
     */
    public void setDefaultMatchSettings(ImageMatchSettings
                                                defaultMatchSettings) {
        this.seleniumEyes.setDefaultMatchSettings(defaultMatchSettings);
    }

    /**
     * @return The match settings used for the session.
     */
    public ImageMatchSettings getDefaultMatchSettings() {
        return this.seleniumEyes.getDefaultMatchSettings();
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
        this.seleniumEyes.setMatchLevel(matchLevel);
    }

    /**
     * @return The test-wide match level.
     * @deprecated Please use{@link #getDefaultMatchSettings} instead.
     */
    public MatchLevel getMatchLevel() {
        return seleniumEyes.getMatchLevel();
    }

    /**
     * @return The full agent id composed of both the base agent id and the
     * user given agent id.
     */
    public String getFullAgentId() {
        return this.seleniumEyes.getFullAgentId();
    }

    /**
     * @return Whether a session is open.
     */
    public boolean getIsOpen() {
        if (isVisualGridEyes) {
            return visualGridEyes.getIsOpen();
        } else {
            return seleniumEyes.getIsOpen();
        }
    }

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
     * @return The currently set log handler.
     */
    public LogHandler getLogHandler() {
        return this.seleniumEyes.getLogHandler();
    }

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
        this.seleniumEyes.setImageCut(cutProvider);
    }

    public boolean getIsCutProviderExplicitlySet() {
        return this.seleniumEyes.getIsCutProviderExplicitlySet();
    }


    public void check(String tag, ICheckSettings checkSettings) {
        if (isVisualGridEyes) {
            visualGridEyes.check(tag, checkSettings);
        } else {
            seleniumEyes.check(tag, checkSettings);
        }
    }

    public TestResults close(boolean shoudThrowException) {
        if (isVisualGridEyes) {
            List<Future<TestResultContainer>> close = visualGridEyes.close(shoudThrowException);
            if (close != null && !close.isEmpty()) {
                try {
                    return close.get(0).get().getTestResults();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

            }
        } else {
            return seleniumEyes.close(shoudThrowException);
        }
        return null;
    }


    /**
     * Manually set the scale ratio for the images being validated.
     *
     * @param scaleRatio The scale ratio to use, or {@code null} to reset
     *                   back to automatic scaling.
     */
    public void setScaleRatio(Double scaleRatio) {
        this.seleniumEyes.setScaleRatio(scaleRatio);

    }

    /**
     * @return The ratio used to scale the images being validated.
     */
    public double getScaleRatio() {
        return this.seleniumEyes.getScaleRatio();
    }

    /**
     * Adds a property to be sent to the server.
     *
     * @param name  The property name.
     * @param value The property value.
     */
    public void addProperty(String name, String value) {
        this.seleniumEyes.addProperty(name, value);
    }

    /**
     * Clears the list of custom properties.
     */
    public void clearProperties() {
        this.seleniumEyes.clearProperties();
    }

    /**
     * @param saveDebugScreenshots If true, will save all screenshots to local directory.
     */
    public void setSaveDebugScreenshots(boolean saveDebugScreenshots) {
        this.seleniumEyes.setSaveDebugScreenshots(saveDebugScreenshots);
    }

    /**
     * @return True if screenshots saving enabled.
     */
    public boolean getSaveDebugScreenshots() {
        return seleniumEyes.getSaveDebugScreenshots();
    }

    /**
     * @param pathToSave Path where you want to save the debug screenshots.
     */
    public void setDebugScreenshotsPath(String pathToSave) {
        this.seleniumEyes.setDebugScreenshotsPath(pathToSave);
    }

    /**
     * @return The path where you want to save the debug screenshots.
     */
    public String getDebugScreenshotsPath() {
        return this.seleniumEyes.getDebugScreenshotsPath();
    }

    /**
     * @param prefix The prefix for the screenshots' names.
     */
    public void setDebugScreenshotsPrefix(String prefix) {
        this.seleniumEyes.setDebugScreenshotsPrefix(prefix);
    }

    /**
     * @return The prefix for the screenshots' names.
     */
    public String getDebugScreenshotsPrefix() {
        return this.seleniumEyes.getDebugScreenshotsPrefix();
    }

    public DebugScreenshotsProvider getDebugScreenshotsProvider() {
        return this.seleniumEyes.getDebugScreenshotsProvider();
    }

    /**
     * @return Whether to ignore or the blinking caret or not when comparing images.
     */
    public boolean getIgnoreCaret() {
        return this.seleniumEyes.getIgnoreCaret();
    }

    /**
     * Sets the ignore blinking caret value.
     *
     * @param value The ignore value.
     */
    public void setIgnoreCaret(boolean value) {
        this.seleniumEyes.setIgnoreCaret(value);
    }

    /**
     * Returns the stitching overlap in pixels.
     */
    public int getStitchOverlap() {
        return this.seleniumEyes.getStitchOverlap();
    }

    /**
     * Sets the stitching overlap in pixels.
     *
     * @param pixels The width (in pixels) of the overlap.
     */
    public void setStitchOverlap(int pixels) {
        this.seleniumEyes.setStitchOverlap(pixels);
    }

    /**
     * See {@link #checkRegion(Region, int, String)}.
     * {@code tag} defaults to {@code null}.
     * Default match timeout is used.
     */
    public void checkRegion(Region region) {
        this.seleniumEyes.checkRegion(region);
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
        this.seleniumEyes.checkRegion(region, matchTimeout, tag);
    }

    /**
     * See {@link #checkRegion(WebElement, String)}.
     * {@code tag} defaults to {@code null}.
     *
     * @param element The element which represents the region to check.
     */
    public void checkRegion(WebElement element) {
        this.seleniumEyes.checkRegion(element);
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
        this.seleniumEyes.checkRegion(element, stitchContent);
    }

    /**
     * See {@link #checkRegion(WebElement, int, String)}.
     * Default match timeout is used.
     *
     * @param element The element which represents the region to check.
     * @param tag     An optional tag to be associated with the snapshot.
     */
    public void checkRegion(WebElement element, String tag) {
        this.seleniumEyes.checkRegion(element, tag);
    }

    /**
     * if {@code stitchContent} is {@code false} then behaves the same {@link
     * #checkRegion(WebElement, String)}. Otherwise
     * behaves the same as {@link #checkElement(WebElement, String)}.
     *
     * @param element       The element which represents the region to check.
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(WebElement element, String tag, boolean stitchContent) {
        this.seleniumEyes.checkRegion(element, tag, stitchContent);
    }

    /**
     * Takes a snapshot of the application under test and matches a region of
     * a specific element with the expected region output.
     *
     * @param element      The element which represents the region to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException if a mismatch is detected and
     *                             immediate failure reports are enabled
     */
    public void checkRegion(final WebElement element, int matchTimeout, String tag) {
        this.seleniumEyes.checkRegion(element, matchTimeout, tag);
    }

    /**
     * if {@code stitchContent} is {@code false} then behaves the same {@link
     * #checkRegion(WebElement, int, String)}. Otherwise
     * behaves the same as {@link #checkElement(WebElement, String)}.
     *
     * @param element       The element which represents the region to check.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     * @param stitchContent Whether to take a screenshot of the whole region and stitch if needed.
     */
    public void checkRegion(WebElement element, int matchTimeout, String tag, boolean stitchContent) {
        this.seleniumEyes.checkRegion(element, matchTimeout, tag, stitchContent);
    }

    /**
     * See {@link #checkRegion(By, String)}.
     * {@code tag} defaults to {@code null}.
     *
     * @param selector The selector by which to specify which region to check.
     */
    public void checkRegion(By selector) {
        this.seleniumEyes.checkRegion(selector);
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
        this.seleniumEyes.checkRegion(selector, stitchContent);
    }

    /**
     * See {@link #checkRegion(By, int, String)}.
     * Default match timeout is used.
     *
     * @param selector The selector by which to specify which region to check.
     * @param tag      An optional tag to be associated with the screenshot.
     */
    public void checkRegion(By selector, String tag) {
        this.seleniumEyes.checkRegion(selector, tag);
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
        this.seleniumEyes.checkRegion(selector, tag, stitchContent);
    }

    /**
     * Takes a snapshot of the application under test and matches a region
     * specified by the given selector with the expected region output.
     *
     * @param selector     The selector by which to specify which region to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the screenshot.
     * @throws TestFailedException if a mismatch is detected and
     *                             immediate failure reports are enabled
     */
    public void checkRegion(By selector, int matchTimeout, String tag) {
        this.seleniumEyes.checkRegion(selector, matchTimeout, tag);
    }

    /**
     * See {@link #checkRegionInFrame(int, By, String)}.
     * {@code tag} defaults to {@code null}.
     *
     * @param frameIndex The index of the frame to switch to. (The same index
     *                   as would be used in a call to
     *                   driver.switchTo().frame()).
     * @param selector   The selector by which to specify which region to check inside the frame.
     */
    public void checkRegionInFrame(int frameIndex, By selector) {
        this.seleniumEyes.checkRegionInFrame(frameIndex, selector);
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
        this.seleniumEyes.checkRegionInFrame(frameIndex, selector, stitchContent);
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
        this.seleniumEyes.checkRegionInFrame(frameIndex, selector, tag);
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
        this.seleniumEyes.checkRegionInFrame(frameIndex, selector, tag, stitchContent);
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
        this.seleniumEyes.checkRegionInFrame(frameIndex, selector, matchTimeout, tag);
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
        this.seleniumEyes.checkRegionInFrame(frameIndex, selector, matchTimeout, tag, stitchContent);
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
        this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector);
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
        this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector, stitchContent);
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
        this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector, tag);
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
        this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector, tag, stitchContent);
    }

    /**
     * See {@link #checkRegionInFrame(String, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     *
     * @param frameNameOrId The name or id of the frame to switch to. (as would
     *                      be used in a call to driver.switchTo().frame()).
     * @param selector      A Selector specifying the region to check inside the frame.
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the snapshot.
     */
    public void checkRegionInFrame(String frameNameOrId, By selector,
                                   int matchTimeout, String tag) {
        this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector, matchTimeout, tag);
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
        this.seleniumEyes.checkRegionInFrame(frameNameOrId, selector, matchTimeout, tag, stitchContent);
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, boolean)}.
     * {@code stitchContent} defaults to {@code null}.
     *
     * @param frameReference The element which is the frame to switch to. (as
     *                       would be used in a call to
     *                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector) {
        this.seleniumEyes.checkRegionInFrame(frameReference, selector);
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, String, boolean)}.
     * {@code tag} defaults to {@code null}.
     *
     * @param frameReference The element which is the frame to switch to. (as
     *                       would be used in a call to
     *                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param stitchContent  If {@code true}, stitch the internal content of
     *                       the region (i.e., perform
     *                       {@link #checkElement(By, int, String)} on the
     *                       region.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector, boolean stitchContent) {
        this.seleniumEyes.checkRegionInFrame(frameReference, selector, stitchContent);
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     *
     * @param frameReference The element which is the frame to switch to. (as
     *                       would be used in a call to
     *                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param tag            An optional tag to be associated with the snapshot.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector, String tag) {
        this.seleniumEyes.checkRegionInFrame(frameReference, selector, tag);
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, int, String, boolean)}.
     * Default match timeout is used.
     *
     * @param frameReference The element which is the frame to switch to. (as
     *                       would be used in a call to
     *                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param tag            An optional tag to be associated with the snapshot.
     * @param stitchContent  If {@code true}, stitch the internal content of
     *                       the region (i.e., perform
     *                       {@link #checkElement(By, int, String)} on the
     *                       region.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   String tag, boolean stitchContent) {
        this.seleniumEyes.checkRegionInFrame(frameReference, selector, tag, stitchContent);
    }

    /**
     * See {@link #checkRegionInFrame(WebElement, By, int, String, boolean)}.
     * {@code stitchContent} defaults to {@code false}.
     *
     * @param frameReference The element which is the frame to switch to. (as
     *                       would be used in a call to
     *                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check inside the frame.
     * @param matchTimeout   The amount of time to retry matching. (Milliseconds)
     * @param tag            An optional tag to be associated with the snapshot.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   int matchTimeout, String tag) {
        this.seleniumEyes.checkRegionInFrame(frameReference, selector, matchTimeout, tag);
    }

    /**
     * Switches into the given frame, takes a snapshot of the application under
     * test and matches a region specified by the given selector.
     *
     * @param frameReference The element which is the frame to switch to. (as
     *                       would be used in a call to
     *                       driver.switchTo().frame()).
     * @param selector       A Selector specifying the region to check.
     * @param matchTimeout   The amount of time to retry matching. (Milliseconds)
     * @param tag            An optional tag to be associated with the snapshot.
     * @param stitchContent  If {@code true}, stitch the internal content of
     *                       the region (i.e., perform
     *                       {@link #checkElement(By, int, String)} on the
     *                       region.
     */
    public void checkRegionInFrame(WebElement frameReference, By selector,
                                   int matchTimeout, String tag,
                                   boolean stitchContent) {
        this.seleniumEyes.checkRegionInFrame(frameReference, selector, matchTimeout, tag, stitchContent);
    }

    /**
     * See {@link #checkElement(WebElement, String)}.
     * {@code tag} defaults to {@code null}.
     */
    public void checkElement(WebElement element) {
        this.seleniumEyes.checkElement(element);
    }

    /**
     * See {@link #checkElement(WebElement, int, String)}.
     * Default match timeout is used.
     */
    public void checkElement(WebElement element, String tag) {
        this.seleniumEyes.checkElement(element, tag);
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
        this.seleniumEyes.checkElement(element, matchTimeout, tag);
    }

    /**
     * See {@link #checkElement(By, String)}.
     * {@code tag} defaults to {@code null}.
     */
    public void checkElement(By selector) {
        this.seleniumEyes.checkElement(selector);
    }

    /**
     * See {@link #checkElement(By, int, String)}.
     * Default match timeout is used.
     */
    public void checkElement(By selector, String tag) {
        check(tag, Target.region(selector).fully());
    }

    /**
     * Takes a snapshot of the application under test and matches an element
     * specified by the given selector with the expected region output.
     *
     * @param selector     Selects the element to check.
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the screenshot.
     * @throws TestFailedException if a mismatch is detected and
     *                             immediate failure reports are enabled
     */
    public void checkElement(By selector, int matchTimeout, String tag) {
        this.seleniumEyes.checkElement(selector, matchTimeout, tag);
    }

    /**
     * Adds a mouse trigger.
     *
     * @param action  Mouse action.
     * @param control The control on which the trigger is activated (context relative coordinates).
     * @param cursor  The cursor's position relative to the control.
     */
    public void addMouseTrigger(MouseAction action, Region control, Location cursor) {
        this.seleniumEyes.addMouseTrigger(action, control, cursor);
    }

    /**
     * Adds a mouse trigger.
     *
     * @param action  Mouse action.
     * @param element The WebElement on which the click was called.
     */
    public void addMouseTrigger(MouseAction action, WebElement element) {
        this.seleniumEyes.addMouseTrigger(action, element);
    }

    /**
     * Adds a keyboard trigger.
     *
     * @param control The control's context-relative region.
     * @param text    The trigger's text.
     */
    public void addTextTrigger(Region control, String text) {
        this.seleniumEyes.addTextTrigger(control, text);
    }

    /**
     * Adds a keyboard trigger.
     *
     * @param element The element for which we sent keys.
     * @param text    The trigger's text.
     */
    public void addTextTrigger(WebElement element, String text) {
        this.seleniumEyes.addTextTrigger(element, text);
    }

    /**
     * Use this method only if you made a previous call to {@link #open
     * (WebDriver, String, String)} or one of its variants.
     * <p>
     * {@inheritDoc}
     */
    public RectangleSize getViewportSize() {
        return this.seleniumEyes.getViewportSize();
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
     * @param driver   The web driver that controls the browser hosting
     *                 the application under test.
     * @param appName  The name of the application under test.
     * @param testName The test name.
     *                 (i.e., the visible part of the document's body) or
     *                 {@code null} to use the current window's viewport.
     * @return A wrapped WebDriver which enables SeleniumEyes trigger recording and
     * frame handling.
     */
    public WebDriver open(WebDriver driver, String appName, String testName) {
        return this.seleniumEyes.open(driver, appName, testName);
    }

    /**
     * Starts a test.
     *
     * @param driver       The web driver that controls the browser hosting
     *                     the application under test.
     * @param appName      The name of the application under test.
     * @param testName     The test name.
     * @param viewportSize The required browser's viewport size
     *                     (i.e., the visible part of the document's body) or
     *                     {@code null} to use the current window's viewport.
     * @return A wrapped WebDriver which enables SeleniumEyes trigger recording and
     * frame handling.
     * {@code sessionType} defaults to {@code null}.
     */
    public WebDriver open(WebDriver driver, String appName, String testName,
                          RectangleSize viewportSize) {
        return this.seleniumEyes.open(driver, appName, testName, viewportSize);
    }

    public boolean getHideCaret() {
        return this.seleniumEyes.getHideCaret();
    }


    public boolean shouldStitchContent() {
        return this.seleniumEyes.shouldStitchContent();
    }

    /**
     * ﻿Forces a full page screenshot (by scrolling and stitching) if the
     * browser only ﻿supports viewport screenshots).
     *
     * @param shouldForce Whether to force a full page screenshot or not.
     */
    public void setForceFullPageScreenshot(boolean shouldForce) {
        this.seleniumEyes.setForceFullPageScreenshot(shouldForce);
    }

    /**
     * @return Whether SeleniumEyes should force a full page screenshot.
     */
    public boolean getForceFullPageScreenshot() {
        return this.seleniumEyes.getForceFullPageScreenshot();
    }

    /**
     * Sets the time to wait just before taking a screenshot (e.g., to allow
     * positioning to stabilize when performing a full page stitching).
     *
     * @param waitBeforeScreenshots The time to wait (Milliseconds). Values
     *                              smaller or equal to 0, will cause the
     *                              default value to be used.
     */
    public void setWaitBeforeScreenshots(int waitBeforeScreenshots) {
        this.seleniumEyes.setWaitBeforeScreenshots(waitBeforeScreenshots);
    }

    /**
     * @return The time to wait just before taking a screenshot.
     */
    public int getWaitBeforeScreenshots() {
        return this.seleniumEyes.getWaitBeforeScreenshots();
    }


    /**
     * Turns on/off the automatic scrolling to a region being checked by
     * {@code checkRegion}.
     *
     * @param shouldScroll Whether to automatically scroll to a region being validated.
     */
    public void setScrollToRegion(boolean shouldScroll) {
        this.seleniumEyes.setScrollToRegion(shouldScroll);
    }

    /**
     * @return Whether to automatically scroll to a region being validated.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean getScrollToRegion() {
        return this.seleniumEyes.getScrollToRegion();
    }

    /**
     * Set the type of stitching used for full page screenshots. When the
     * page includes fixed position header/sidebar, use {@link StitchMode#CSS}.
     * Default is {@link StitchMode#SCROLL}.
     *
     * @param mode The stitch mode to set.
     */
    public void setStitchMode(StitchMode mode) {
        this.seleniumEyes.setStitchMode(mode);
    }

    /**
     * @return The current stitch mode settings.
     */
    public StitchMode getStitchMode() {
        return this.seleniumEyes.getStitchMode();
    }

    /**
     * Hide the scrollbars when taking screenshots.
     *
     * @param shouldHide Whether to hide the scrollbars or not.
     */
    public void setHideScrollbars(boolean shouldHide) {
        this.seleniumEyes.setHideScrollbars(shouldHide);
    }

    /**
     * @return Whether or not scrollbars are hidden when taking screenshots.
     */
    public boolean getHideScrollbars() {
        return this.seleniumEyes.getHideScrollbars();
    }

    /**
     * @return The image rotation model.
     */
    public ImageRotation getRotation() {
        return this.seleniumEyes.getRotation();
    }

    /**
     * @param rotation The image rotation model.
     */
    public void setRotation(ImageRotation rotation) {
        this.seleniumEyes.setRotation(rotation);
    }

    /**
     * @return The device pixel ratio, or
     * if the DPR is not known yet or if it wasn't possible to extract it.
     */
    public double getDevicePixelRatio() {
        return this.seleniumEyes.getDevicePixelRatio();
    }

    /**
     * See {@link #checkWindow(String)}.
     * {@code tag} defaults to {@code null}.
     * Default match timeout is used.
     */
    public void checkWindow() {
        this.seleniumEyes.checkWindow();
    }

    /**
     * See {@link #checkWindow(int, String)}.
     * Default match timeout is used.
     *
     * @param tag An optional tag to be associated with the snapshot.
     */
    public void checkWindow(String tag) {
        this.seleniumEyes.checkWindow(tag);
    }

    /**
     * Takes a snapshot of the application under test and matches it with
     * the expected output.
     *
     * @param matchTimeout The amount of time to retry matching (Milliseconds).
     * @param tag          An optional tag to be associated with the snapshot.
     * @throws TestFailedException Thrown if a mismatch is detected and
     *                             immediate failure reports are enabled.
     */
    public void checkWindow(int matchTimeout, String tag) {
        this.seleniumEyes.checkWindow(matchTimeout, tag);
    }

    /**
     * Runs a test on the current window.
     *
     * @param driver       The web driver that controls the browser hosting
     *                     the application under test.
     * @param appName      The name of the application under test.
     * @param testName     The test name (will also be used as the tag name for the step).
     * @param viewportSize The required browser's viewport size
     *                     (i.e., the visible part of the document's body) or
     *                     {@code null} to use the current window's viewport.
     */
    public void testWindow(WebDriver driver, String appName, String testName,
                           RectangleSize viewportSize) {
        this.seleniumEyes.testWindow(driver, appName, testName, viewportSize);
    }

    /**
     * See {@link #testWindow(WebDriver, String, String, RectangleSize)}.
     * {@code viewportSize} defaults to {@code null}.
     */
    public void testWindow(WebDriver driver, String appName, String testName) {
        this.seleniumEyes.testWindow(driver, appName, testName);
    }

    /**
     * See {@link #testWindow(WebDriver, String, String, RectangleSize)}.
     * {@code appName} defaults to {@code null} (which means the name set in
     */
    public void testWindow(WebDriver driver, String testName,
                           RectangleSize viewportSize) {
        this.seleniumEyes.testWindow(driver, testName, viewportSize);
    }

    /**
     * See {@link #testWindow(WebDriver, String, RectangleSize)}.
     * {@code viewportSize} defaults to {@code null}.
     */
    public void testWindow(WebDriver driver, String testName) {
        this.seleniumEyes.testWindow(driver, testName);
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
        this.seleniumEyes.testResponseTime(driver, appName, testName, action, deadline, timeout);
    }

    /**
     * See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, SeleniumEyes.WebDriverAction action,
                                 int deadline) {
        this.seleniumEyes.testResponseTime(driver, appName, testName, action, deadline);
    }

    /**
     * See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, SeleniumEyes.WebDriverAction action) {
        this.seleniumEyes.testResponseTime(driver, appName, testName, action);
    }

    /**
     * See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int)}.
     * {@code action} defaults to {@code null}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, int deadline) {
        this.seleniumEyes.testResponseTime(driver, appName, testName, deadline);
    }

    /**
     * See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int)}.
     * {@code action} defaults to {@code null}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName) {
        this.seleniumEyes.testResponseTime(driver, appName, testName);
    }

    /**
     * Similar to {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int)},
     * except this method sets the viewport size before starting the
     * performance test.
     *
     * @param viewportSize The required viewport size.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, SeleniumEyes.WebDriverAction action,
                                 int deadline, int timeout,
                                 RectangleSize viewportSize) {
        this.seleniumEyes.testResponseTime(driver, appName, testName, action, deadline, timeout, viewportSize);
    }

    /**
     * See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int, RectangleSize)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, SeleniumEyes.WebDriverAction action,
                                 int deadline, RectangleSize viewportSize) {
        this.seleniumEyes.testResponseTime(driver, appName, testName, action, deadline, viewportSize);
    }

    /**
     * See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int, RectangleSize)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, SeleniumEyes.WebDriverAction action,
                                 RectangleSize viewportSize) {
        this.seleniumEyes.testResponseTime(driver, appName, testName, action, viewportSize);
    }

    /**
     * See {@link #testResponseTime(WebDriver, String, String, SeleniumEyes.WebDriverAction, int, int, RectangleSize)}.
     * {@code action} defaults to {@code null}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, int deadline, int timeout,
                                 RectangleSize viewportSize) {
        testResponseTime(driver, appName, testName, null, deadline, timeout,
                viewportSize);
    }

    /**
     * See {@link #testResponseTime(WebDriver, String, String, int, int, RectangleSize)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, int deadline,
                                 RectangleSize viewportSize) {
        this.seleniumEyes.testResponseTime(driver, appName, testName, deadline, viewportSize);
    }

    /**
     * See {@link #testResponseTime(WebDriver, String, String, int, int, RectangleSize)}.
     */
    public void testResponseTime(WebDriver driver, String appName,
                                 String testName, RectangleSize viewportSize) {
        this.seleniumEyes.testResponseTime(driver, appName, testName, viewportSize);
    }

    /**
     * Takes multiple screenshots at once (given all <code>ICheckSettings</code> objects are on the same level).
     *
     * @param checkSettings Multiple <code>ICheckSettings</code> object representing different regions in the viewport.
     */
    public void check(ICheckSettings... checkSettings) {
        this.seleniumEyes.check(checkSettings);
    }

    /**
     * See {@link #checkFrame(String, int, String)}.
     * {@code tag} defaults to {@code null}. Default match timeout is used.
     */
    public void checkFrame(String frameNameOrId) {
        this.seleniumEyes.checkFrame(frameNameOrId);
    }

    /**
     * See {@link #checkFrame(String, int, String)}.
     * Default match timeout is used.
     */
    public void checkFrame(String frameNameOrId, String tag) {
        check(tag, Target.frame(frameNameOrId).fully());
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     * @param frameNameOrId The name or id of the frame to check. (The same
     *                      name/id as would be used in a call to
     *                      driver.switchTo().frame()).
     * @param matchTimeout  The amount of time to retry matching. (Milliseconds)
     * @param tag           An optional tag to be associated with the match.
     */
    public void checkFrame(String frameNameOrId, int matchTimeout, String tag) {
        this.seleniumEyes.checkFrame(frameNameOrId, matchTimeout, tag);
    }

    /**
     * See {@link #checkFrame(int, int, String)}.
     * {@code tag} defaults to {@code null}. Default match timeout is used.
     */
    public void checkFrame(int frameIndex) {
        this.seleniumEyes.checkFrame(frameIndex);
    }

    /**
     * See {@link #checkFrame(int, int, String)}.
     * Default match timeout is used.
     */
    public void checkFrame(int frameIndex, String tag) {
        this.seleniumEyes.checkFrame(frameIndex, tag);
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     * @param frameIndex   The index of the frame to switch to. (The same index
     *                     as would be used in a call to
     *                     driver.switchTo().frame()).
     * @param matchTimeout The amount of time to retry matching. (Milliseconds)
     * @param tag          An optional tag to be associated with the match.
     */
    public void checkFrame(int frameIndex, int matchTimeout, String tag) {
        this.seleniumEyes.checkFrame(frameIndex, matchTimeout, tag);
    }

    /**
     * See {@link #checkFrame(WebElement, int, String)}.
     * {@code tag} defaults to {@code null}.
     * Default match timeout is used.
     */
    public void checkFrame(WebElement frameReference) {
        this.seleniumEyes.checkFrame(frameReference);
    }

    /**
     * See {@link #checkFrame(WebElement, int, String)}.
     * Default match timeout is used.
     */
    public void checkFrame(WebElement frameReference, String tag) {
        this.seleniumEyes.checkFrame(frameReference, tag);
    }

    /**
     * Matches the frame given as parameter, by switching into the frame and
     * using stitching to get an image of the frame.
     * @param frameReference The element which is the frame to switch to. (as
     *                       would be used in a call to
     *                       driver.switchTo().frame() ).
     * @param matchTimeout   The amount of time to retry matching (milliseconds).
     * @param tag            An optional tag to be associated with the match.
     */
    public void checkFrame(WebElement frameReference, int matchTimeout, String tag) {
        check(tag, Target.frame(frameReference).timeout(matchTimeout));
    }

    /**
     * Matches the frame given by the frames path, by switching into the frame
     * and using stitching to get an image of the frame.
     * @param framePath    The path to the frame to check. This is a list of
     *                     frame names/IDs (where each frame is nested in the
     *                     previous frame).
     * @param matchTimeout The amount of time to retry matching (milliseconds).
     * @param tag          An optional tag to be associated with the match.
     */
    public void checkFrame(String[] framePath, int matchTimeout, String tag) {

        this.seleniumEyes.checkFrame(framePath, matchTimeout, tag);
    }

    /**
     * See {@link #checkFrame(String[], int, String)}.
     * Default match timeout is used.
     */
    public void checkFrame(String[] framesPath, String tag) {
        this.checkFrame(framesPath, tag);
    }

    /**
     * See {@link #checkFrame(String[], int, String)}.
     * Default match timeout is used.
     * {@code tag} defaults to {@code null}.
     */
    public void checkFrame(String[] framesPath) {
        this.seleniumEyes.checkFrame(framesPath);
    }

    /**
     * @return The URI of the eyes server.
     */
    public URI getServerUrl() {
       if(this.isVisualGridEyes){
           return this.visualGridEyes.getServerUrl();
       }
       else{
           return this.seleniumEyes.getServerUrl();
       }
    }

    public void setSendDom(boolean isSendDom) {
        seleniumEyes.setSendDom(isSendDom);
    }

}
