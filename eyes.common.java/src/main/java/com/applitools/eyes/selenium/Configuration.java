package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.visualgridclient.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Configuration extends com.applitools.eyes.config.Configuration implements IConfigurationGetter, IConfigurationSetter {

    private static final int DEFAULT_WAIT_BEFORE_SCREENSHOTS = 100;
    private boolean forceFullPageScreenshot;
    private int waitBeforeScreenshots = DEFAULT_WAIT_BEFORE_SCREENSHOTS;
    private StitchMode stitchMode = StitchMode.SCROLL;
    private boolean hideScrollbars = true;
    private boolean hideCaret = true;

    //Rendering Configuration
    private boolean isThrowExceptionOn = false;
    private Boolean isRenderingConfig = false;

    public enum BrowserType {CHROME, FIREFOX, IE, EDGE}

    private List<RenderBrowserInfo> browsersInfo = new ArrayList<>();

    @SuppressWarnings("IncompleteCopyConstructor")
    public Configuration(IConfigurationGetter other) {
        super(other);
        this.forceFullPageScreenshot = other.getForceFullPageScreenshot();
        this.waitBeforeScreenshots = other.getWaitBeforeScreenshots();
        this.stitchMode = other.getStitchMode();
        this.hideScrollbars = other.getHideScrollbars();
        this.hideCaret = other.getHideCaret();
        this.isThrowExceptionOn = other.isThrowExceptionOn();
        this.isRenderingConfig = other.isRenderingConfig();
        if (other.getBrowsersInfo() == null) {
            ArrayList<RenderBrowserInfo> browsersInfo = new ArrayList<>();
            RectangleSize viewportSize = other.getViewportSize();
            browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, other.getBaselineEnvName()));
            this.browsersInfo = browsersInfo;
        } else {
            this.browsersInfo = other.getBrowsersInfo();
        }
    }

    public Configuration() {
    }

    public Configuration(RectangleSize viewportSize) {
        super();
        ArrayList<RenderBrowserInfo> browsersInfo = new ArrayList<>();
        browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, null));
        this.browsersInfo = browsersInfo;
    }

    public Configuration(boolean isThrowExceptionOn, String testName) {
        this.isThrowExceptionOn = isThrowExceptionOn;
        this.testName = testName;
    }

    public Configuration(String appName, String testName,
                         RectangleSize viewportSize) {
        super();
        ArrayList<RenderBrowserInfo> browsersInfo = new ArrayList<>();
        browsersInfo.add(new RenderBrowserInfo(viewportSize.getWidth(), viewportSize.getHeight(), BrowserType.CHROME, null));
        this.browsersInfo = browsersInfo;
        this.testName = testName;
        this.viewportSize = viewportSize;
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
    public IConfigurationSetter addBrowsers(RenderBrowserInfo... browsersInfo) {
        this.browsersInfo.addAll(Arrays.asList(browsersInfo));
        return this;
    }

    @Override
    public IConfigurationSetter addBrowser(RenderBrowserInfo browserInfo) {
        addBrowserInfo(browserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addBrowser(int width, int height, BrowserType browserType, String baselineEnvName) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(width, height, browserType, baselineEnvName);
        addBrowser(browserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addBrowser(int width, int height, BrowserType browserType) {
        return addBrowser(width, height, browserType, baselineEnvName);
    }

    @Override
    public IConfigurationSetter addDeviceEmulation(EmulationDevice emulationDevice, String baselineEnvName) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationDevice.getWidth(), emulationDevice.getWidth(),
                BrowserType.CHROME, baselineEnvName);
        addBrowserInfo(browserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addDeviceEmulation(EmulationDevice emulationDevice) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationDevice, baselineEnvName);
        addBrowserInfo(browserInfo);
        return this;
    }

    private void addBrowserInfo(RenderBrowserInfo browserInfo) {
        this.browsersInfo.add(browserInfo);
    }

    @Override
    public IConfigurationSetter addDeviceEmulation(EmulationInfo emulationInfo, String baselineEnvName) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        addBrowserInfo(browserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addDeviceEmulation(EmulationInfo emulationInfo) {
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        addBrowserInfo(browserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addDeviceEmulation(EmulationInfo.DeviceName deviceName, ScreenOrientation orientation) {
        EmulationBaseInfo emulationInfo = new EmulationInfo(deviceName, orientation);
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        addBrowserInfo(browserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addDeviceEmulation(EmulationInfo.DeviceName deviceName) {
        EmulationBaseInfo emulationInfo = new EmulationInfo(deviceName, ScreenOrientation.PORTRAIT);
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        addBrowserInfo(browserInfo);
        return this;
    }

    @Override
    public IConfigurationSetter addDeviceEmulation(int width, int height) {
        EmulationBaseInfo emulationInfo = new EmulationDevice(width, height, 1, ScreenOrientation.PORTRAIT);
        RenderBrowserInfo browserInfo = new RenderBrowserInfo(emulationInfo, baselineEnvName);
        addBrowserInfo(browserInfo);
        return this;
    }

    @Override
    public List<RenderBrowserInfo> getBrowsersInfo() {
        if (browsersInfo != null && !browsersInfo.isEmpty()) {
            return browsersInfo;
        }

        RenderBrowserInfo renderBrowserInfo = new RenderBrowserInfo(this.viewportSize.getWidth(), this.viewportSize.getHeight(), BrowserType.CHROME, baselineEnvName);
        return Collections.singletonList(renderBrowserInfo);
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
    public Configuration cloneConfig() {
        return new Configuration(this);
    }

    @Override
    public RectangleSize getViewportSize() {
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
                "\n\thideCaret = " + hideCaret;
    }
}
