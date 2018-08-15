package com.applitools.eyes.fluent;

import com.applitools.eyes.*;

import java.util.ArrayList;
import java.util.List;

public class FloatingRegionByRectangle implements GetFloatingRegion {
    private Region rect;
    private int maxUpOffset;
    private int maxDownOffset;
    private int maxLeftOffset;
    private int maxRightOffset;

    public FloatingRegionByRectangle(Region rect, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.rect = rect;
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }

    @Override
    public List<FloatingMatchSettings> getRegions(EyesBase eyesBase, EyesScreenshot screenshot) {
        List<FloatingMatchSettings> value = new ArrayList<>();
        value.add(new FloatingMatchSettings(
                Math.round(rect.getLeft()), Math.round(rect.getTop()),
                Math.round(rect.getWidth()), Math.round(rect.getHeight()),
                maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));
        return value;
    }
}
