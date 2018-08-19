package com.applitools.eyes.fluent;

import com.applitools.eyes.*;

import java.util.ArrayList;
import java.util.List;

public class IgnoreRegionByRectangle implements GetRegion {
    private Region region;

    public IgnoreRegionByRectangle(Region region) {
        this.region = region;
    }

    @Override
    public List<Region> getRegions(EyesBase eyesBase, EyesScreenshot screenshot, boolean adjustLocation) {
        List<Region> value = new ArrayList<>();
        if (adjustLocation) {
            Region adjustedRegion = new Region(screenshot.convertRegionLocation(new RegionF(this.region), CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS));
            value.add(adjustedRegion);
        } else {
            value.add(this.region);
        }
        return value;
    }
}
