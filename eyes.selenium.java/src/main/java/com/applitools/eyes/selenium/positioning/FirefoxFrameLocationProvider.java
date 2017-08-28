package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.frames.Frame;

public class FirefoxFrameLocationProvider implements FrameLocationProvider {

    private static FrameLocationProvider instance = new FirefoxFrameLocationProvider();

    public static FrameLocationProvider getInstance() {
        return instance;
    }

    public Region getFrameLocation(Frame frame){
        RectangleSize size = frame.getSize();
        return new Region(0,0,size.getWidth(), size.getHeight());
    }
}
