package com.applitools.eyes;

public class NullRegionProvider implements RegionProvider {
    @Override
    public Region getRegion() {
        return Region.EMPTY;
    }

    @Override
    public CoordinatesType getCoordinatesType() {
        return CoordinatesType.CONTEXT_AS_IS;
    }
}
