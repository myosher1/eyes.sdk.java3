package com.applitools.eyes.visualGridClient.data;

import java.util.List;

public interface RenderGridJobListener {

    void getRenderStatus(List<String> urls);

    void onEyesOpenFailed();
}
