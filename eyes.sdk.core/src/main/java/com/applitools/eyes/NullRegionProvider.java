package com.applitools.eyes;

public class NullRegionProvider implements RegionProvider {
    @Override
    public Region getRegion() {
        return Region.EMPTY;
    }

    public static final NullRegionProvider INSTANCE = new NullRegionProvider();
}
