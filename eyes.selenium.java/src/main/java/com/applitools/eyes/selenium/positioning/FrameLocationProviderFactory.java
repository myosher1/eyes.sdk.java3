package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.BrowserNames;
import com.applitools.eyes.UserAgent;

public class FrameLocationProviderFactory {

    public static FrameLocationProvider getFrameLocationProvider(UserAgent ua) {
        if (ua.getBrowser().equals(BrowserNames.Firefox)) {
            try {
                if (Integer.parseInt(ua.getBrowserMajorVersion()) >= 48) {
                    return FirefoxFrameLocationProvider.getInstance();
                }
            }
            catch (NumberFormatException e) {
                return DefaultFrameLocationProvider.getInstance();
            }
        }
        return DefaultFrameLocationProvider.getInstance();
    }
}
