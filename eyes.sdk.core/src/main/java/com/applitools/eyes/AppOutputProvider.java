package com.applitools.eyes;

/**
 * Encapsulates a callback which returns an application output.
 */
public interface AppOutputProvider {
    AppOutputWithScreenshot getAppOutput(Region region, EyesScreenshot lastScreenshot);
}
