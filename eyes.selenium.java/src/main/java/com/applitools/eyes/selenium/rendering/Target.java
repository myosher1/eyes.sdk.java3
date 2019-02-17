package com.applitools.eyes.selenium.rendering;

import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.CheckRGSettings;

public class Target {

    public static CheckRGSettings window() {
        return new CheckRGSettings();
    }

    public static CheckRGSettings region(Region region)
    {
        return new CheckRGSettings(region);
    }

    public static CheckRGSettings region(String selector) {
        return new CheckRGSettings(selector);
    }
}
