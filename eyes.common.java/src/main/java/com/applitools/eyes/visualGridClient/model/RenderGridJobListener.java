package com.applitools.eyes.visualGridClient.model;

import java.util.List;

public interface RenderGridJobListener {

    void getRenderStatus(List<String> urls);

    void onEyesOpenFailed();
}
