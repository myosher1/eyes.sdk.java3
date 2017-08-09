package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.EyesBase;
import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.fluent.GetFloatingRegion;
import org.openqa.selenium.By;

public class FloatingRegionBySelector implements GetFloatingRegion{

    private By regionSelector;
    private int maxUpOffset;
    private int maxDownOffset;
    private int maxLeftOffset;
    private int maxRightOffset;

    public FloatingRegionBySelector(By regionSelector, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {

        this.regionSelector = regionSelector;
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }

    @Override
    public FloatingMatchSettings getRegion(EyesBase eyesBase) {
        return null;
    }
}
