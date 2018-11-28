package com.applitools.eyes.visualGridClient.data;

public class EmulationDevice {

    private int width;
    private int height;
    private String deviceScaleFactor;
    private String mobile;

    public EmulationDevice(int width, int height, String deviceScaleFactor, String mobile) {
        this.width = width;
        this.height = height;
        this.deviceScaleFactor = deviceScaleFactor;
        this.mobile = mobile;
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

    public String getDeviceScaleFactor() {
        return deviceScaleFactor;
    }

    public void setDeviceScaleFactor(String deviceScaleFactor) {
        this.deviceScaleFactor = deviceScaleFactor;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
