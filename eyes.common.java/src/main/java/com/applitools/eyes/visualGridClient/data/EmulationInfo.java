package com.applitools.eyes.visualGridClient.data;

import com.applitools.eyes.visualGridClient.ScreenOrientation;

public class EmulationInfo {

    private EmulationDevice device;
    private String deviceName;
    private ScreenOrientation screenOrientation;


    public EmulationInfo(EmulationDevice device, String deviceName, ScreenOrientation screenOrientation) {
        this.device = device;
        this.deviceName = deviceName;
        this.screenOrientation = screenOrientation;
    }

    public EmulationDevice getDevice() {
        return device;
    }

    public void setDevice(EmulationDevice device) {
        this.device = device;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public ScreenOrientation getScreenOrientation() {
        return screenOrientation;
    }

    public void setScreenOrientation(ScreenOrientation screenOrientation) {
        this.screenOrientation = screenOrientation;
    }
}
