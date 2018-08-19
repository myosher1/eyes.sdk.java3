package com.applitools.eyes.fluent;

import com.applitools.eyes.RegionF;

/**
 *
 */
public class CheckTarget {

    public static ICheckSettings window()
    {
        return new CheckSettings();
    }

    public static ICheckSettings region(RegionF rect)
    {
        return new CheckSettings();
    }
}
