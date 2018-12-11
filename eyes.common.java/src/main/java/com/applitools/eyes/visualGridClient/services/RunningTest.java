package com.applitools.eyes.visualGridClient.services;


import com.applitools.ICheckSettings;
import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.config.Configuration;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class RunningTest {
    private final AbstractProxySettings proxy;
    private List<Task> taskList = new ArrayList<>();
    private IEyesConnector eyes;
    private RenderingConfiguration.RenderBrowserInfo browserInfo;
    private AtomicBoolean isTestOpen = new AtomicBoolean(false);
    private AtomicBoolean isTestClose = new AtomicBoolean(false);
    private AtomicBoolean isTestInExceptionMode = new AtomicBoolean(false);
    private Exception exception = null;
    private RunningTestListener listener;
    private RenderingConfiguration configuration;
    private HashMap<Task, FutureTask<TestResults>> taskToFutureMapping = new HashMap<>();
    private Logger logger;

    public interface RunningTestListener {

        void onTaskComplete(Task task, RunningTest test);

        void onRenderComplete();

    }
    private Task.TaskListener taskListener = new Task.TaskListener() {
        @Override
        public void onTaskComplete(Task task) {
            RunningTest runningTest = RunningTest.this;
            runningTest.taskList.remove(task);
            switch (task.getType()) {
                case OPEN:
                    runningTest.setTestOpen(true);
                    break;
                case CLOSE:
                    RunningTest.this.isTestClose.set(true);
                    break;
            }
            if (runningTest.listener != null) {
                RunningTest.this.listener.onTaskComplete(task, RunningTest.this);
            }
        }

        @Override
        public void onTaskFailed(Exception e) {
            setTestInExceptionMode(e);
        }

        @Override
        public void onRenderComplete() {
            logger.verbose("enter");
            listener.onRenderComplete();
            logger.verbose("exit");
        }
    };

    public RunningTest(AbstractProxySettings proxy, IEyesConnector eyes, RenderingConfiguration configuration, RenderingConfiguration.RenderBrowserInfo browserInfo, Logger logger, RunningTestListener listener) {
        this.eyes = eyes;
        this.browserInfo = browserInfo;
        this.configuration = configuration;
        this.listener = listener;
        this.logger = logger;
        this.proxy = proxy;
    }

    public boolean isTestOpen() {
        return isTestOpen.get();
    }

    public void setTestOpen(boolean testOpen) {
        isTestOpen.set(testOpen);
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public ScoreTask getScoreTaskObjectByType(Task.TaskType taskType) {
        int score = 0;
        for (Task task : this.getTaskList()) {
            if (task.isTaskReadyToCheck()) {
                score++;
            }
        }
        if (this.taskList.isEmpty())
            return null;

        Task task = this.taskList.get(0);
        if (task.getType() != taskType || task.isSent() || (taskType == Task.TaskType.OPEN && !task.isTaskReadyToCheck()))
            return null;

        return new ScoreTask(task, score);
    }

    public synchronized FutureTask<TestResults> getNextCloseTask() {
        logger.verbose("enter");
        if (!taskList.isEmpty()) {
            Task task = taskList.get(0);
            taskList.remove(task);
            logger.verbose("removing task " + task.toString() + " and exiting");
            logger.verbose("tasks in taskList: " + taskList.size());
            return taskToFutureMapping.get(task);
        }
        logger.verbose("exit with null");
        return null;
    }

    public RenderingConfiguration.RenderBrowserInfo getBrowserInfo() {
        return browserInfo;
    }

    public Task open() {
        logger.verbose("adding Open task...");
        Task task = new Task(null, eyes, Task.TaskType.OPEN, taskListener, null, this);
        FutureTask<TestResults> futureTask = new FutureTask<>(task);
        this.taskToFutureMapping.put(task, futureTask);
        this.taskList.add(task);
        logger.verbose("Open task was added: " + task.toString());
        logger.verbose("tasks in taskList: " + taskList.size());
        eyes.log("Open task was added");
        return task;
    }

    public FutureTask<TestResults> close() {
        Task lastTask;
        if (!this.taskList.isEmpty()) {
            lastTask = this.taskList.get(taskList.size() - 1);
            if (lastTask.getType() == Task.TaskType.CLOSE) {
                return taskToFutureMapping.get(lastTask);
            }
        }

        logger.verbose("adding close task...");
        Task task = new Task(null, eyes, Task.TaskType.CLOSE, taskListener, null, this);
        FutureTask<TestResults> futureTask = new FutureTask<>(task);
        this.taskToFutureMapping.put(task, futureTask);
        this.taskList.add(task);
        logger.verbose("Close task was added: " + task.toString());
        logger.verbose("tasks in taskList: " + taskList.size());
        eyes.log("Close Task was added");
        return this.taskToFutureMapping.get(task);
    }

    public Task check(ICheckSettings checkSettings) {
        logger.verbose("adding check task...");
        Task task = new Task(null, eyes, Task.TaskType.CHECK, taskListener, checkSettings, this);
        this.taskList.add(task);
        logger.verbose("Check Task was added: " + task.toString());
        eyes.log("Check Task was added");
        logger.verbose("tasks in taskList: " + taskList.size());
        this.taskToFutureMapping.get(task);
        return task;
    }

    /**
     * @return true if the only task left is CLOSE task
     */
    public boolean isTestIsReadyToClose() {
        for (Task task : taskList) {
            if (task.getType() == Task.TaskType.CHECK || task.getType() == Task.TaskType.OPEN) return false;
        }
        return true;
    }

    public boolean isTestClose() {
        return isTestClose.get();
    }

    public IEyesConnector getEyes() {
        return eyes;
    }

    public void setTestInExceptionMode(Exception e) {
        exception = e;
        this.isTestInExceptionMode.set(true);
        //TODO abort if not closed - create new result object containing the exception.
    }

    Logger getLogger() {
        return logger;
    }

    RenderingConfiguration getConfiguration() {
        return configuration;
    }
}