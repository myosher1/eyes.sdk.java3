package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.frames.Frame;

public interface FrameLocationProvider {
    Region getFrameLocation(Frame frame);
}
