package com.applitools.eyes.fluent;

import com.applitools.eyes.*;

import java.util.ArrayList;
import java.util.List;

public class SimpleRegionByRectangleF implements GetRegionsF {
    private RegionF region;

    public SimpleRegionByRectangleF(RegionF region) {
        this.region = region;
    }

    @Override
    public List<RegionF> getRegions(EyesBase eyesBase, EyesScreenshot screenshot, boolean adjustLocation) {
        List<RegionF> value = new ArrayList<>();
        if (adjustLocation) {
            RegionF adjustedRegion = screenshot.convertRegionLocation(this.region, CoordinatesType.CONTEXT_RELATIVE, CoordinatesType.SCREENSHOT_AS_IS);
            value.add(adjustedRegion);
        } else {
            value.add(this.region);
        }
        return value;
    }
}
