package com.applitools.eyes.visualGridClient.services;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.visualGridClient.model.CompletableTask;
import com.applitools.eyes.visualGridClient.model.RenderBrowserInfo;
import com.applitools.eyes.visualGridClient.model.RenderStatusResults;
import com.applitools.eyes.visualGridClient.model.TestResultContainer;
import com.applitools.utils.GeneralUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class Task implements Callable<TestResultContainer>, CompletableTask {


    private final Logger logger;
    private boolean isSent;

    public enum TaskType {OPEN, CHECK, CLOSE, ABORT}

    private TestResults testResults;

    private IEyesConnector eyesConnector;
    private TaskType type;

    private RenderStatusResults renderResult;
    private List<TaskListener> listeners = new ArrayList<>();
    private ICheckSettings checkSettings;

    private RunningTest runningTest;
    private Exception exception;

    private AtomicBoolean isTaskComplete = new AtomicBoolean(false);

    interface TaskListener {

        void onTaskComplete(Task task);

        void onTaskFailed(Exception e, Task task);

        void onRenderComplete();

    }

    public Task(TestResults testResults, IEyesConnector eyesConnector, TaskType type, TaskListener runningTestListener,
                ICheckSettings checkSettings, RunningTest runningTest) {
        this.testResults = testResults;
        this.eyesConnector = eyesConnector;
        this.type = type;
        this.listeners.add(runningTestListener);
        this.logger = runningTest.getLogger();
        this.checkSettings = checkSettings;
        this.runningTest = runningTest;
    }

    public RenderBrowserInfo getBrowserInfo() {
        return runningTest.getBrowserInfo();
    }

    public TaskType getType() {
        return type;
    }

    boolean isSent() {
        return isSent;
    }

    void setIsSent() {
        this.isSent = true;
    }

    @Override
    public TestResultContainer call() {
        try {
            testResults = null;
            switch (type) {
                case OPEN:
                    logger.log("Task.run opening task");
                    String userAgent = renderResult.getUserAgent();
                    eyesConnector.setUserAgent(userAgent);
                    eyesConnector.open(runningTest.getConfiguration());
                    break;

                case CHECK:
                    logger.log("Task.run check task");
                    try {
                        String imageLocation = renderResult.getImageLocation();
                        if (imageLocation == null) {
                            logger.verbose("CHECKING IMAGE WITH NULL LOCATION - ");
                            logger.verbose(renderResult.toString());
                        }
                        eyesConnector.matchWindow(imageLocation, checkSettings);
                    } catch (Exception e) {

                        GeneralUtils.logExceptionStackTrace(logger,e);
                    }
                    break;

                case CLOSE:
                    logger.log("Task.run close task");
                    testResults = eyesConnector.close(runningTest.getConfiguration().isThrowExceptionOn());
                    break;

                case ABORT:
                    logger.log("Task.run abort task");
                    if (runningTest.isTestOpen()) {
                        testResults = eyesConnector.abortIfNotClosed();
                    }
            }
            @SuppressWarnings("UnnecessaryLocalVariable")
            TestResultContainer testResultContainer = new TestResultContainer(testResults, this.exception);
            return testResultContainer;
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            notifyFailureAllListeners(e);
        } finally {
            this.isTaskComplete.set(true);
            //call the callback
            notifySuccessAllListeners();
        }
        return null;
    }

    private void notifySuccessAllListeners() {
        for (TaskListener listener : listeners) {
            listener.onTaskComplete(this);
        }
    }

    private void notifyFailureAllListeners(Exception e) {
        for (TaskListener listener : listeners) {
            listener.onTaskFailed(e, this);
        }
    }

    private void notifyRenderCompleteAllListeners() {
        for (TaskListener listener : listeners) {
            listener.onRenderComplete();
        }
    }

    public IEyesConnector getEyesConnector() {
        return eyesConnector;
    }

    public void setRenderResult(RenderStatusResults renderResult) {
        logger.verbose("enter");
        this.renderResult = renderResult;
        notifyRenderCompleteAllListeners();
        logger.verbose("exit");
    }

    public boolean isTaskReadyToCheck() {
        return this.renderResult != null;
    }

    public RunningTest getRunningTest() {
        return runningTest;
    }

    public boolean getIsTaskComplete() {
        return isTaskComplete.get();
    }

    public void addListener(TaskListener listener) {
        this.listeners.add(listener);
    }

    public void setRenderError(String renderId) {
        logger.verbose("enter - renderId: " + renderId);
        for (TaskListener listener : listeners) {
            listener.onTaskFailed(new Exception("Render Failed for " + this.getBrowserInfo() + " (renderId: " + renderId + ")"), this);
        }
        logger.verbose("exit - renderId: " + renderId);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        logger.verbose("aborting task with exception");
        this.exception = exception;
        this.type = TaskType.ABORT;
    }

    @Override
    public String toString() {
        return "Task - Type: " + type + " ; Browser Info: " + getBrowserInfo();
    }
}

