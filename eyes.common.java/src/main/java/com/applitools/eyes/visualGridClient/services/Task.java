package com.applitools.eyes.visualGridClient.services;

import com.applitools.ICheckSettings;
import com.applitools.eyes.Logger;
import com.applitools.eyes.MatchResult;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.visualGridClient.model.RenderStatusResults;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class Task implements Callable<TestResults> {


    private static AtomicBoolean isThrown = new AtomicBoolean(false);
    private final Logger logger;
    private MatchResult matchResult;


    public enum TaskType {OPEN, CHECK, CLOSE, ABORT}


    private TestResults testResults;
    private IEyesConnector eyesConnector;

    private TaskType type;
    private RenderStatusResults renderResult;
    private TaskListener runningTestListener;
    private RenderingConfiguration.RenderBrowserInfo browserInfo;
    private RenderingConfiguration configuration;
    private ICheckSettings checkSettings;

    interface TaskListener {

        void onTaskComplete(Task task);

        void onTaskFailed(Exception e);

        void onRenderComplete();
    }

    public Task(TestResults testResults, IEyesConnector eyesConnector, TaskType type, RenderingConfiguration.RenderBrowserInfo browserInfo,
                RenderingConfiguration configuration, Logger logger, TaskListener runningTestListener, ICheckSettings checkSettings) {
        this.testResults = testResults;
        this.eyesConnector = eyesConnector;
        this.type = type;
        this.runningTestListener = runningTestListener;
        this.browserInfo = browserInfo;
        this.configuration = configuration;
        this.logger = logger;
        this.checkSettings = checkSettings;
    }

    public RenderingConfiguration.RenderBrowserInfo getBrowserInfo() {
        return browserInfo;
    }

    public TaskType getType() {
        return type;
    }


    @Override
    public TestResults call() throws Exception {
        try {
            testResults = null;
            switch (type) {
                case OPEN:
                    System.out.println("Task.run opening task");
                    eyesConnector.open(configuration);
                    break;

                case CHECK:
                    logger.log("Task.run check task");
                    matchResult = eyesConnector.matchWindow(renderResult.getImageLocation(), checkSettings);
                    break;

                case CLOSE:
                    logger.log("Task.run close task");
                    testResults = eyesConnector.close(configuration.isThrowExceptionOn());
                    break;

                case ABORT:
                    logger.log("Task.run abort task");
                    eyesConnector.abortIfNotClosed();
            }
            //call the callback
            this.runningTestListener.onTaskComplete(this);
            return testResults;
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
            this.runningTestListener.onTaskFailed(e);
        }
        return null;
    }

    public IEyesConnector getEyesConnector() {
        return eyesConnector;
    }

    private static boolean isThrown() {
        return Task.isThrown.get();
    }

    public void setRenderResult(RenderStatusResults renderResult) {
        logger.verbose("enter");
        this.renderResult = renderResult;
        this.runningTestListener.onRenderComplete();
        logger.verbose("exit");
    }

    public boolean isTaskReadyToCheck() {
        return this.renderResult != null;
    }

    public MatchResult getMatchResult() {
        return matchResult;
    }
}

