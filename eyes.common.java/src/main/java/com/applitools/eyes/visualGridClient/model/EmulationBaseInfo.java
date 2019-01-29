package com.applitools.eyes.visualGridClient.model;

public abstract class EmulationBaseInfo {

    protected ScreenOrientation screenOrientation;

    public EmulationBaseInfo(ScreenOrientation screenOrientation) {
        this.screenOrientation = screenOrientation;
    }

    public ScreenOrientation getScreenOrientation() {
        return screenOrientation;
    }

    public void setScreenOrientation(ScreenOrientation screenOrientation) {
        this.screenOrientation = screenOrientation;
    }

    public abstract String getDeviceName();
}
