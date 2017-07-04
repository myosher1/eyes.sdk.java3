package com.applitools.eyes.fluent;

import com.applitools.eyes.Region;

/**
 *
 */
public class CheckTarget {

    public static ICheckSettings Window()
    {
        return new CheckSettings();
    }

    public static ICheckSettings Region(Region rect)
    {
        return new CheckSettings();
    }
}
