package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Logger;
import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;

public class FirefoxRegionPositionCompensation implements RegionPositionCompensation {

    private final Eyes eyes;
    private final Logger logger;

    public FirefoxRegionPositionCompensation(Eyes eyes, Logger logger) {
        this.eyes = eyes;
        this.logger = logger;
    }

    @Override
    public Region compensateRegionPosition(Region region, double pixelRatio) {
        EyesWebDriver eyesWebDriver = (EyesWebDriver) eyes.getDriver();
        FrameChain frameChain = eyesWebDriver.getFrameChain();
        if (frameChain.size() > 0) {
            return region;
        }

        return region.offset(0, -(int) Math.ceil(pixelRatio / 2));
    }
}
