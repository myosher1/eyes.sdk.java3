package com.applitools.eyes.selenium.rendering;

import com.applitools.eyes.EyesBase;

public class Task {

    private String linkToImage = null;
    private EyesBase eyes;
    private RenderingConfiguration.RenderBrowserInfo browserInfo;

    public Task(EyesBase eyes, RenderingConfiguration.RenderBrowserInfo browserInfo) {
        this.eyes = eyes;
        this.browserInfo = browserInfo;
    }

    public String getLinkToImage() {
        return linkToImage;
    }

    public EyesBase getEyes() {
        return eyes;
    }

    boolean isTaskReady() {
        return linkToImage != null;
    }

    public RenderingConfiguration.RenderBrowserInfo getBrowserInfo() {
        return browserInfo;
    }

}

