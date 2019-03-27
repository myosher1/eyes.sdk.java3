package com.applitools.eyes.visualgridclient.model;

public class EmulationDevice extends EmulationBaseInfo{

    private int width;
    private int height;
    private float deviceScaleFactor;

    public EmulationDevice(int width, int height, float deviceScaleFactor, ScreenOrientation screenOrientation) {
        super(screenOrientation);
        this.width = width;
        this.height = height;
        this.deviceScaleFactor = deviceScaleFactor;
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

    public float getDeviceScaleFactor() {
        return deviceScaleFactor;
    }

    public void setDeviceScaleFactor(float deviceScaleFactor) {
        this.deviceScaleFactor = deviceScaleFactor;
    }

    @Override
    public String getDeviceName() {
        return null;
    }
}
