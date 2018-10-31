package com.applitools.renderingGrid;

import com.applitools.eyes.Region;

public class RenderInfo {

    private int width;
    private int height;
    private String sizeMode;
    private Region region;
    private String emulationInfo;

    public RenderInfo(int width, int height, String sizeMode, Region region, String emulationInfo) {
        this.width = width;
        this.height = height;
        this.sizeMode = sizeMode;
        this.region = region;
        this.emulationInfo = emulationInfo;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSizeMode() {
        return sizeMode;
    }

    public void setSizeMode(String sizeMode) {
        this.sizeMode = sizeMode;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getEmulationInfo() {
        return emulationInfo;
    }

    public void setEmulationInfo(String emulationInfo) {
        this.emulationInfo = emulationInfo;
    }
}
