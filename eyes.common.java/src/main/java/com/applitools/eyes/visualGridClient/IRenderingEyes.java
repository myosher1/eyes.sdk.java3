package com.applitools.eyes.visualGridClient;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.visualGridClient.data.RunningTest;
import com.applitools.eyes.visualGridClient.data.Task;

import java.util.List;
import java.util.concurrent.Future;

public interface IRenderingEyes {

    boolean isEyesClosed();

    RunningTest getNextTestToClose();

    void setListener(EyesListener listener);

    RunningTest getNextTestToOpen();

    List<Future<TestResults>> close();

    Task getNextCheckTask();

    int getBestMarkForCheck();

    interface EyesListener{

        void onTaskComplete(Task task, IRenderingEyes eyes);
    }
}
