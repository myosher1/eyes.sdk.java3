package com.applitools.eyes.visualGridClient.data;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.visualGridClient.IEyesConnector;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class Task implements Callable<TestResults> {


    private static AtomicBoolean isThrown = new AtomicBoolean(false);

    public enum TaskType {OPEN, CHECK, CLOSE, ABORT}


    private TestResults testResults;
    private String linkToImage = null;
    private IEyesConnector eyesConnector;

    private TaskType type;
    private TaskListener runningTestListener;
    private RenderingConfiguration.RenderBrowserInfo browserInfo;
    private RenderingConfiguration configuration;

    interface TaskListener {

        void onTaskComplete(Task task);

    }

    public Task(String linkToImage, IEyesConnector eyes, TaskType type, RenderingConfiguration.RenderBrowserInfo browserInfo, RenderingConfiguration configuration, TaskListener listener) {
        super();
        this.linkToImage = linkToImage;
        this.eyesConnector = eyes;
        this.type = type;
        this.browserInfo = browserInfo;
        this.configuration = configuration;
        this.runningTestListener = listener;
    }

    String getLinkToImage() {
        return linkToImage;
    }

    boolean isTaskReadyToCheck() {
        return linkToImage != null;
    }

    public RenderingConfiguration.RenderBrowserInfo getBrowserInfo() {
        return browserInfo;
    }

    public TaskType getType() {
        return type;
    }


    @Override
    public TestResults call() throws Exception {
        testResults = null;
            System.out.println("Task.run()");
            switch (type) {
                case OPEN:
                        System.out.println("Task.run opening task");
                        eyesConnector.open(configuration.getAppName(), configuration.getTestName());
                    break;
                case CHECK:
                    System.out.println("Task.call CHECK");

                    break;
                case CLOSE:
                    if(!Task.isThrown()){
                        isThrown.set(true);
                        throw new Exception("Michael's Exception! ");
                    }
                        testResults = eyesConnector.close(configuration.isThrowExceptionOn());
                    break;
                case ABORT:
                    eyesConnector.abortIfNotClosed();
            }
        //call the callback
        this.runningTestListener.onTaskComplete(this);
        return testResults;
    }

    public IEyesConnector getEyesConnector() {
        return eyesConnector;
    }

    private static boolean isThrown() {
        return Task.isThrown.get();
    }

}

