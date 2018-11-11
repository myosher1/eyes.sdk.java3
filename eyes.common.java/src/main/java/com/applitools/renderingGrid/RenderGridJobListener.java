package com.applitools.renderingGrid;

import com.applitools.eyes.RunningSession;

import java.util.List;

public interface RenderGridJobListener {

    void getRenderStatus(List<String> urls);

    void onEyesOpenFailed();
}
