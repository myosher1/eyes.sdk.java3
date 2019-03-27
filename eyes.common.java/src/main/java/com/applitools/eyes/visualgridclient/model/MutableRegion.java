package com.applitools.eyes.visualgridclient.model;

import com.applitools.eyes.CoordinatesType;
import com.applitools.eyes.Location;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;

public class MutableRegion extends Region {

    public MutableRegion(int left, int top, int width, int height) {
        super(left, top, width, height);
    }

    public MutableRegion(int left, int top, int width, int height, CoordinatesType coordinatesType) {
        super(left, top, width, height, coordinatesType);
    }

    public MutableRegion(Location location, RectangleSize size) {
        super(location, size);
    }

    public MutableRegion(Location location, RectangleSize size, CoordinatesType coordinatesType) {
        super(location, size, coordinatesType);
    }

    public MutableRegion(Region region) {
        super(region);
    }

    public Region offset(int x, int y) {
        this.setLeft(this.getLeft() + x);
        this.setTop(this.getTop() + y);
        return this;
    }

}
