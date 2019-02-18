package com.applitools.eyes.visualgridclient.model;

import com.applitools.eyes.Region;
import com.fasterxml.jackson.annotation.JsonInclude;

public class RenderInfo {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int width;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int height;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sizeMode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Region region;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private EmulationBaseInfo emulationInfo;

    public RenderInfo(int width, int height, String sizeMode, Region region, EmulationBaseInfo emulationInfo) {
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

    public EmulationBaseInfo getEmulationInfo() {
        return emulationInfo;
    }

    public void setEmulationInfo(EmulationInfo emulationInfo) {
        this.emulationInfo = emulationInfo;
    }
}
