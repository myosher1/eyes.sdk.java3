package com.applitools.eyes.selenium;

import com.applitools.eyes.visualgridclient.model.ChromeEmulationInfo;
import com.applitools.eyes.visualgridclient.model.RenderBrowserInfo;
import com.applitools.eyes.visualgridclient.model.ScreenOrientation;

import java.util.List;

public interface IConfigurationSetter extends com.applitools.eyes.config.IConfigurationSetter {
    IConfigurationSetter setWaitBeforeScreenshots(int waitBeforeScreenshots);

    IConfigurationSetter setStitchMode(StitchMode stitchMode);

    IConfigurationSetter setHideScrollbars(boolean hideScrollbars);

    IConfigurationSetter setHideCaret(boolean hideCaret);

    IConfigurationSetter addBrowsers(RenderBrowserInfo... browsersInfo);

    IConfigurationSetter addBrowser(RenderBrowserInfo browserInfo);

    IConfigurationSetter addBrowser(int width, int height, Configuration.BrowserType browserType, String baselineEnvName);

    IConfigurationSetter addBrowser(int width, int height, Configuration.BrowserType browserType);

    IConfigurationSetter addDeviceEmulation(ChromeEmulationInfo.DeviceName deviceName, ScreenOrientation orientation);

    IConfigurationSetter addDeviceEmulation(ChromeEmulationInfo.DeviceName deviceName);

    IConfigurationSetter addDeviceEmulation(ChromeEmulationInfo.DeviceName deviceName, String baselineEnvName);

    IConfigurationSetter addDeviceEmulation(ChromeEmulationInfo.DeviceName deviceName, ScreenOrientation orientation,  String baselineEnvName);

//    IConfigurationSetter addDeviceEmulation(int width, int height);
//
//    IConfigurationSetter addDeviceEmulation(int width, int height, double scaleFactor);

    IConfigurationSetter setBrowsersInfo(List<RenderBrowserInfo> browsersInfo);

    IConfigurationSetter setTestName(String testName);

    IConfigurationSetter setForceFullPageScreenshot(boolean forceFullPageScreenshot);

    IConfigurationSetter setRenderingConfig(boolean renderingConfig);

}
