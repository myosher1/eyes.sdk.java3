package com.applitools.eyes.fluent;

import com.applitools.eyes.EyesBase;
import com.applitools.eyes.EyesScreenshot;
import com.applitools.eyes.RegionF;

import java.util.List;

public interface GetRegionsF {
    List<RegionF> getRegions(EyesBase eyesBase, EyesScreenshot screenshot, boolean adjustLocation);
}
