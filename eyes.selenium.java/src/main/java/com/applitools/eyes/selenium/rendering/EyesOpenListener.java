package com.applitools.eyes.selenium.rendering;

import com.applitools.eyes.EyesBase;
import com.applitools.eyes.RunningSession;

public interface EyesOpenListener {

    void onEyesOpenComplete(Eyes mainEyes, EyesBase openedEyes, RunningSession runningSession);

    void onEyesOpenFailed();
}
