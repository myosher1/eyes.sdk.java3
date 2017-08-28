package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.frames.Frame;

public class DefaultFrameLocationProvider implements FrameLocationProvider {

    private static FrameLocationProvider instance = new DefaultFrameLocationProvider();

    public static FrameLocationProvider getInstance() {
        return instance;
    }

    public Region getFrameLocation(Frame frame){
        return new Region(frame.getLocation(),frame.getSize());
    }
}
