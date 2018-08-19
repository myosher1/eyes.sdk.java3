package com.applitools.eyes.selenium;

import com.applitools.eyes.RegionF;

public class BoundsAndBorders {

    private RegionF bounds;
    private Borders borders;

    public BoundsAndBorders(float top, float left, float width, float height,
                            float leftBorder, float topBorder, float rightBorder, float bottomBorder) {
        bounds = new RegionF(top,left, width, height);
        borders = new Borders(leftBorder, topBorder, rightBorder, bottomBorder);
    }

    public RegionF getBounds() {
        return bounds;
    }

    public Borders getBorders() {
        return borders;
    }
}
