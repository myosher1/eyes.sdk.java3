package com.applitools.eyes.selenium;

import com.applitools.eyes.visualgridclient.model.EmulationDevice;
import com.applitools.eyes.visualgridclient.model.EmulationInfo;
import com.applitools.eyes.visualgridclient.model.RenderBrowserInfo;
import com.applitools.eyes.visualgridclient.model.ScreenOrientation;

import java.util.List;

public interface IConfigurationSetter extends com.applitools.eyes.config.IConfigurationSetter {
    void setWaitBeforeScreenshots(int waitBeforeScreenshots);

    void setStitchMode(StitchMode stitchMode);

    void setHideScrollbars(boolean hideScrollbars);

    void setHideCaret(boolean hideCaret);

    IConfigurationSetter addBrowsers(RenderBrowserInfo... browsersInfo);

    IConfigurationSetter addBrowser(RenderBrowserInfo browserInfo);

    IConfigurationSetter addBrowser(int width, int height, Configuration.BrowserType browserType, String baselineEnvName);

    IConfigurationSetter addBrowser(int width, int height, Configuration.BrowserType browserType);

    IConfigurationSetter addDeviceEmulation(EmulationDevice emulationDevice, String baselineEnvName);

    IConfigurationSetter addDeviceEmulation(EmulationDevice emulationDevice);

    IConfigurationSetter addDeviceEmulation(EmulationInfo emulationInfo, String baselineEnvName);

    IConfigurationSetter addDeviceEmulation(EmulationInfo emulationInfo);

    IConfigurationSetter addDeviceEmulation(EmulationInfo.DeviceName deviceName, ScreenOrientation orientation);

    IConfigurationSetter addDeviceEmulation(EmulationInfo.DeviceName deviceName);

    IConfigurationSetter addDeviceEmulation(int width, int height);


    void setBrowsersInfo(List<RenderBrowserInfo> browsersInfo);

    void setTestName(String testName);

    boolean isForceFullPageScreenshot();

    void setForceFullPageScreenshot(boolean forceFullPageScreenshot);

    void setRenderingConfig(boolean renderingConfig);

}
