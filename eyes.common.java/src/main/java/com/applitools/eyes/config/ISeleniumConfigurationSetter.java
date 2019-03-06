package com.applitools.eyes.config;

import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.visualgridclient.model.EmulationDevice;
import com.applitools.eyes.visualgridclient.model.EmulationInfo;
import com.applitools.eyes.visualgridclient.model.RenderBrowserInfo;

import java.util.List;

public interface ISeleniumConfigurationSetter extends IConfigurationSetter{
    void setWaitBeforeScreenshots(int waitBeforeScreenshots);

    void setStitchMode(StitchMode stitchMode);

    void setHideScrollbars(boolean hideScrollbars);

    void setHideCaret(boolean hideCaret);

    ISeleniumConfigurationSetter addBrowsers(RenderBrowserInfo... browsersInfo);

    ISeleniumConfigurationSetter addBrowser(RenderBrowserInfo browserInfo);

    ISeleniumConfigurationSetter addBrowser(int width, int height, SeleniumConfiguration.BrowserType browserType, String baselineEnvName);

    ISeleniumConfigurationSetter addBrowser(int width, int height, SeleniumConfiguration.BrowserType browserType);

    ISeleniumConfigurationSetter addDeviceEmulation(EmulationDevice emulationDevice, String baselineEnvName);

    ISeleniumConfigurationSetter addDeviceEmulation(EmulationDevice emulationDevice);

    ISeleniumConfigurationSetter addDeviceEmulation(EmulationInfo emulationInfo, String baselineEnvName);

    ISeleniumConfigurationSetter addDeviceEmulation(EmulationInfo emulationInfo);

    void setBrowsersInfo(List<RenderBrowserInfo> browsersInfo);

    void setThrowExceptionOn(boolean throwExceptionOn);

    void setTestName(String testName);

    boolean isForceFullPageScreenshot();

    void setForceFullPageScreenshot(boolean forceFullPageScreenshot);

    void setRenderingConfig(boolean renderingConfig);

}
