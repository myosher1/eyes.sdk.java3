package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.RegionF;

public interface RegionPositionCompensation {
    RegionF compensateRegionPosition(RegionF region, double pixelRatio);
}
