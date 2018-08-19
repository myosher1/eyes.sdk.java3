package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.RegionF;

public class NullRegionPositionCompensation implements RegionPositionCompensation {

    @Override
    public RegionF compensateRegionPosition(RegionF region, double pixelRatio) {
        return region;
    }
}
