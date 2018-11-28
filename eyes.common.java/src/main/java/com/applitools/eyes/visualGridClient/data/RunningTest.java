package com.applitools.eyes.visualGridClient.data;


import com.applitools.eyes.AbstractProxySettings;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.visualGridClient.IEyesConnector;

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
    private RunningTestListener listener;
    private RenderingConfiguration configuration;
    private HashMap<Task, FutureTask<TestResults>> taskToFutureMapping = new HashMap<>();
    private Task.TaskListener taskListener = new Task.TaskListener() {
        @Override
        public void onTaskComplete(Task task) {
            RunningTest runningTest = RunningTest.this;
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
            runningTest.taskList.remove(task);
        }
    };

    public interface RunningTestListener {

        void onTaskComplete(Task task, RunningTest test);
    }
    public RunningTest(AbstractProxySettings proxy, IEyesConnector eyes, RenderingConfiguration configuration, RenderingConfiguration.RenderBrowserInfo browserInfo, RunningTestListener listener) {
        this.eyes = eyes;
        this.browserInfo = browserInfo;
        this.configuration = configuration;
        this.listener = listener;
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

    public int getMark() {
        int mark = 0;
        for (Task task : this.getTaskList()) {
            if (task.getLinkToImage() != null) {
                mark++;
            }
        }
        return mark;
    }

    public synchronized FutureTask<TestResults> getNextTask() {
        if (!taskList.isEmpty()) {
            Task task = taskList.get(0);
            return taskToFutureMapping.get(task);
        }
        return null;
    }

    public RenderingConfiguration.RenderBrowserInfo getBrowserInfo() {
        return browserInfo;
    }

    public void open() {
        eyes.log("Open task was added");
        Task task = new Task(null, eyes, Task.TaskType.OPEN, this.getBrowserInfo(), this.configuration, taskListener);
        FutureTask<TestResults> futureTask = new FutureTask<>(task);
        this.taskToFutureMapping.put(task, futureTask);
        this.taskList.add(task);
    }

    public FutureTask<TestResults> close() {
        Task lastTask;
        if (!this.taskList.isEmpty()) {
            lastTask = this.taskList.get(taskList.size() - 1);
            if (lastTask.getType() == Task.TaskType.CLOSE) {
                return taskToFutureMapping.get(lastTask);
            }
        }

        Task  task = new Task(null, eyes, Task.TaskType.CLOSE, this.getBrowserInfo(), this.configuration, taskListener);
        FutureTask<TestResults> futureTask = new FutureTask<>(task);
        this.taskToFutureMapping.put(task, futureTask);
        this.taskList.add(task);
        eyes.log("Close Task was added");
        return this.taskToFutureMapping.get(task);
    }

    public void check(RenderRequest request) {
        Task  task = new Task(null, eyes, Task.TaskType.CHECK, this.getBrowserInfo(), this.configuration, taskListener);
        this.taskList.add(task);
        eyes.log("Close Task was added");
        this.taskToFutureMapping.get(task);
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
}