package com.applitools.eyes.positioning;

import com.applitools.eyes.RegionF;

public class NullRegionProvider implements RegionProvider {
    @Override
    public RegionF getRegion() {
        return RegionF.EMPTY;
    }

    public static final NullRegionProvider INSTANCE = new NullRegionProvider();
}
