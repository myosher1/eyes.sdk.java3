package com.applitools.eyes.visualGridClient.model;

public class EmulationDevice extends EmulationBaseInfo{

    private int width;
    private int height;
    private float deviceScaleFactor;
    private boolean mobile;

    public EmulationDevice(int width, int height, float deviceScaleFactor, boolean isMobile, ScreenOrientation screenOrientation) {
        super(screenOrientation);
        this.width = width;
        this.height = height;
        this.deviceScaleFactor = deviceScaleFactor;
        this.mobile = isMobile;
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

    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    @Override
    public String getDeviceName() {
        return null;
    }
}
