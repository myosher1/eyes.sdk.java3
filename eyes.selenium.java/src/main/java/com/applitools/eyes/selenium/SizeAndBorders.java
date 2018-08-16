package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSizeF;

public class SizeAndBorders {

    private RectangleSizeF size;
    private Borders borders;

    public SizeAndBorders(float width, float height, float left, float top, float right, float bottom) {
        size = new RectangleSizeF(width, height);
        borders = new Borders(left, top, right, bottom);
    }

    public RectangleSizeF getSize() {
        return size;
    }

    public Borders getBorders() {
        return borders;
    }
}
