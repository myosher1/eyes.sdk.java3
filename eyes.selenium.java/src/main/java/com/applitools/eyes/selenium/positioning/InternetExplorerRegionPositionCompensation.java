package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.RegionF;

public class InternetExplorerRegionPositionCompensation implements RegionPositionCompensation {

    @Override
    public RegionF compensateRegionPosition(RegionF region, double pixelRatio) {
        return region.offset(0, (int)Math.ceil(pixelRatio));
    }
}
