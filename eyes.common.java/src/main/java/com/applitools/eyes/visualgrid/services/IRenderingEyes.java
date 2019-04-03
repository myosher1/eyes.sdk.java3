package com.applitools.eyes.visualgrid.services;

import com.applitools.eyes.visualgrid.model.TestResultContainer;

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

        void onTaskComplete(VisualGridTask visualGridTask, IRenderingEyes eyes);

        void onRenderComplete();
    }

    List<RunningTest> getAllRunningTests();
}
