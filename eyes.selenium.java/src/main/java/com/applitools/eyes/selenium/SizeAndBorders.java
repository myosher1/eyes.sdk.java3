package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSizeF;

public class SizeAndBorders {

    private RectangleSizeF size;
    private Borders borders;

    public SizeAndBorders(int width, int height, int left, int top, int right, int bottom) {
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
