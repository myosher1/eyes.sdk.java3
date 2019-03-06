package com.applitools.eyes.config;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.visualgridclient.model.EmulationDevice;
import com.applitools.eyes.visualgridclient.model.EmulationInfo;
import com.applitools.eyes.visualgridclient.model.RenderBrowserInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SeleniumConfiguration extends Configuration implements ISeleniumConfigurationGetter, ISeleniumConfigurationSetter {

    private static final int DEFAULT_WAIT_BEFORE_SCREENSHOTS = 100;
    private boolean forceFullPageScreenshot;
    private int waitBeforeScreenshots = DEFAULT_WAIT_BEFORE_SCREENSHOTS;
    private StitchMode stitchMode = StitchMode.SCROLL;
    private boolean hideScrollbars = true;
    private boolean hideCaret = true;

    //Rendering Configuration
    private int concurrentSessions = 3;
    private boolean isThrowExceptionOn = false;
    private Boolean isRenderingConfig = false;

    public enum BrowserType {CHROME, FIREFOX}
    private List<RenderBrowserInfo> browsersInfo = new ArrayList<>();



    public SeleniumConfiguration(SeleniumConfiguration configuration) {
        super(configuration);

        this.browsersInfo = configuration.browsersInfo;
        this.concurrentSessions = configuration.concurrentSessions;
        this.isThrowExceptionOn = configuration.isThrowExceptionOn;
        this.testName = configuration.testName;
    }

    public SeleniumConfiguration() {}

    public SeleniumConfiguration(RectangleSize viewportSize) {
        super();
        ArrayList<RenderBrowserInfo> browsersInfo = new ArrayList<>();
        browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, null));
        this.browsersInfo = browsersInfo;
        this.concurrentSessions = 1;
    }

    public SeleniumConfiguration(int concurrentSessions, boolean isThrowExceptionOn, String testName) {
        this.concurrentSessions = concurrentSessions;
        this.isThrowExceptionOn = isThrowExceptionOn;
        this.testName = testName;
    }

    public SeleniumConfiguration(IConfigurationGetter configuration) {
        super(configuration);
        ArrayList<RenderBrowserInfo> browsersInfo = new ArrayList<>();
        RectangleSize viewportSize = configuration.getViewportSize();
        browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, configuration.getBaselineEnvName()));
        this.browsersInfo = browsersInfo;
        this.concurrentSessions = 1;
        this.testName = configuration.getTestName();
    }


    public SeleniumConfiguration(String appName, String testName,
                                  RectangleSize viewportSize) {
        super();
        ArrayList<RenderBrowserInfo> browsersInfo = new ArrayList<>();
        browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, null));
        this.browsersInfo = browsersInfo;
        this.concurrentSessions = 1;
        this.testName = testName;
        this.setAppName(appName);
    }
    @Override
    public boolean getForceFullPageScreenshot() {
        return forceFullPageScreenshot;
    }

    @Override
    public int getWaitBeforeScreenshots() {
        return waitBeforeScreenshots;
    }

    @Override
    public void setWaitBeforeScreenshots(int waitBeforeScreenshots) {
        if (waitBeforeScreenshots <= 0) {
            this.waitBeforeScreenshots = DEFAULT_WAIT_BEFORE_SCREENSHOTS;
        } else {
            this.waitBeforeScreenshots = waitBeforeScreenshots;
        }
    }

    @Override
    public StitchMode getStitchMode() {
        return stitchMode;
    }

    @Override
    public void setStitchMode(StitchMode stitchMode) {
        this.stitchMode = stitchMode;
    }

    @Override
    public boolean getHideScrollbars() {
        return hideScrollbars;
    }

    @Override
    public void setHideScrollbars(boolean hideScrollbars) {
        this.hideScrollbars = hideScrollbars;
    }

    @Override
    public boolean getHideCaret() {
        return hideCaret;
    }

    @Override
    public void setHideCaret(boolean hideCaret) {
        this.hideCaret = hideCaret;
    }

    @Override
    public ISeleniumConfigurationSetter addBrowsers(RenderBrowserInfo... browsersInfo) {
        this.browsersInfo.addAll(Arrays.asList(browsersInfo));
        return this;
    }

    @Override
    public ISeleniumConfigurationSetter addBrowser(RenderBrowserInfo browserInfo) {
        browsersInfo.add(browserInfo);
        return this;
    }

    @Override
    public ISeleniumConfigurationSetter addBrowser(int width, int height, BrowserType browserType, String baselineEnvName) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(width, height, browserType, baselineEnvName, null);
        addBrowser(browserInfo);
        return this;
    }

    @Override
    public ISeleniumConfigurationSetter addBrowser(int width, int height, BrowserType browserType) {
        return addBrowser(width, height, browserType, null);
    }

    @Override
    public ISeleniumConfigurationSetter addDeviceEmulation(EmulationDevice emulationDevice, String baselineEnvName){
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationDevice.getWidth(), emulationDevice.getWidth(),
                BrowserType.CHROME, baselineEnvName);
        this.browsersInfo.add(browserInfo);
        return this;
    }
    @Override
    public ISeleniumConfigurationSetter addDeviceEmulation(EmulationDevice emulationDevice){
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationDevice.getWidth(), emulationDevice.getWidth(),
                BrowserType.CHROME, null);
        this.browsersInfo.add(browserInfo);
        return this;
    }

    @Override
    public ISeleniumConfigurationSetter addDeviceEmulation(EmulationInfo emulationInfo, String baselineEnvName){
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(0, 0, BrowserType.CHROME, baselineEnvName, emulationInfo);
        this.browsersInfo.add(browserInfo);
        return this;
    }
    @Override
    public ISeleniumConfigurationSetter addDeviceEmulation(EmulationInfo emulationInfo){
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(0, 0, BrowserType.CHROME, null, emulationInfo);
        this.browsersInfo.add(browserInfo);
        return this;
    }

    @Override
    public int getConcurrentSessions() {
        return concurrentSessions;
    }

    @Override
    public List<RenderBrowserInfo> getBrowsersInfo() {
        return browsersInfo;
    }

    @Override
    public void setBrowsersInfo(List<RenderBrowserInfo> browsersInfo) {
        this.browsersInfo = browsersInfo;
    }

    @Override
    public boolean isThrowExceptionOn() {
        return isThrowExceptionOn;
    }

    @Override
    public void setThrowExceptionOn(boolean throwExceptionOn) {
        isThrowExceptionOn = throwExceptionOn;
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
    public SeleniumConfiguration cloneConfig() {
        return new SeleniumConfiguration(this);
    }

    @Override
    public RectangleSize getViewportSize(){
        if (isRenderingConfig) {
            RenderBrowserInfo renderBrowserInfo = this.browsersInfo.get(0);
            return new RectangleSize(renderBrowserInfo.getWidth(), renderBrowserInfo.getHeight());
        }
        return super.viewportSize;
    }

    @Override
    public boolean isForceFullPageScreenshot() {
        return forceFullPageScreenshot;
    }

    @Override
    public void setForceFullPageScreenshot(boolean forceFullPageScreenshot) {
        this.forceFullPageScreenshot = forceFullPageScreenshot;
    }

    @Override
    public boolean isRenderingConfig() {
        return isRenderingConfig;
    }

    @Override
    public void setRenderingConfig(boolean renderingConfig) {
        isRenderingConfig = renderingConfig;
    }
    @Override
    public String toString() {
        return super.toString() +
                "\n\tforceFullPageScreenshot = " + forceFullPageScreenshot +
                "\n\twaitBeforeScreenshots = " + waitBeforeScreenshots +
                "\n\tstitchMode = " + stitchMode +
                "\n\thideScrollbars = " + hideScrollbars +
                "\n\thideCaret = " + hideCaret ;
    }
}
