package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.RegionF;

public class SafariRegionPositionCompensation implements RegionPositionCompensation {

    @Override
    public RegionF compensateRegionPosition(RegionF region, double pixelRatio) {

        if (pixelRatio == 1.0) {
            return region;
        }

        if (region.getWidth() <= 0 || region.getHeight() <= 0) {
            return RegionF.EMPTY;
        }

        return region.offset(0, (int) Math.ceil(pixelRatio));
    }

}
