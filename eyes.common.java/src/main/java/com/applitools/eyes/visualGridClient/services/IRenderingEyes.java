package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.TestResults;

import java.util.List;
import java.util.concurrent.Future;

public interface IRenderingEyes {

    boolean isEyesClosed();

    RunningTest getNextTestToClose();

    void setListener(EyesListener listener);

    RunningTest getNextTestToOpen();

    List<Future<TestResults>> close();

    RunningTest getNextCheckTask();

    int getBestScoreForCheck();

    int getBestScoreForOpen();

    interface EyesListener{

        void onTaskComplete(Task task, IRenderingEyes eyes);

        void onRenderComplete();
    }
}
