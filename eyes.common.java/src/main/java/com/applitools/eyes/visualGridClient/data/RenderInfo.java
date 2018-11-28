package com.applitools.eyes.visualGridClient.data;

import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.JsonInclude;

public class RenderInfo {

    @JsonInclude
    private int width;

    @JsonInclude
    private int height;

    @JsonInclude
    private String sizeMode;

    @JsonInclude
    private Region region;

    @JsonInclude
    private EmulationInfo emulationInfo;

    public RenderInfo(int width, int height, String sizeMode, Region region, EmulationInfo emulationInfo) {
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

    public EmulationInfo getEmulationInfo() {
        return emulationInfo;
    }

    public void setEmulationInfo(EmulationInfo emulationInfo) {
        this.emulationInfo = emulationInfo;
    }
}
