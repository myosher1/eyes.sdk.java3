package com.applitools.eyes.visualgridclient.services;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.visualgridclient.model.TestResultContainer;

import java.util.List;
import java.util.concurrent.Future;

public interface IRenderingEyes {

    boolean isEyesClosed();

    RunningTest getNextTestToClose();

    void setListener(EyesListener listener);

    ScoreTask getBestScoreTaskForCheck();

    ScoreTask getBestScoreTaskForOpen();

    List<Future<TestResultContainer>> close();

    interface EyesListener{

        void onTaskComplete(Task task, IRenderingEyes eyes);

        void onRenderComplete();
    }

    List<RunningTest> getAllRunningTests();
}
