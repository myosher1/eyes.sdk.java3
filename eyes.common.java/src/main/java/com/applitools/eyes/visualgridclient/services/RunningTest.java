package com.applitools.eyes.visualgridclient.services;


import com.applitools.ICheckSettings;
import com.applitools.eyes.Logger;
import com.applitools.eyes.config.SeleniumConfiguration;
import com.applitools.eyes.visualgridclient.model.RenderBrowserInfo;
import com.applitools.eyes.visualgridclient.model.TestResultContainer;

import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class RunningTest {
    private final List<Task> taskList = Collections.synchronizedList(new ArrayList<Task>());
    private IEyesConnector eyes;
    private RenderBrowserInfo browserInfo;
    private AtomicBoolean isTestOpen = new AtomicBoolean(false);
    private AtomicBoolean isTestClose = new AtomicBoolean(false);
    private AtomicBoolean isTestInExceptionMode = new AtomicBoolean(false);
    private RunningTestListener listener;
    private SeleniumConfiguration configuration;
    private HashMap<Task, FutureTask<TestResultContainer>> taskToFutureMapping = new HashMap<>();
    private Logger logger;

    public interface RunningTestListener {

        void onTaskComplete(Task task, RunningTest test);

        void onRenderComplete();

    }

    private Task.TaskListener taskListener = new Task.TaskListener() {
        @Override
        public void onTaskComplete(Task task) {
            RunningTest runningTest = RunningTest.this;
            logger.verbose("locking runningTest.taskList");
            synchronized (runningTest.taskList) {
                runningTest.taskList.remove(task);
            }
            logger.verbose("releasing runningTest.taskList");
            switch (task.getType()) {
                case OPEN:
                    runningTest.setTestOpen(true);
                    break;
                case CLOSE:
                case ABORT:
                    RunningTest.this.isTestClose.set(true);
                    break;
            }
            if (runningTest.listener != null) {
                RunningTest.this.listener.onTaskComplete(task, RunningTest.this);
            }
        }

        @Override
        public void onTaskFailed(Error e, Task task) {
            setTestInExceptionMode(e);
            listener.onTaskComplete(task, RunningTest.this);
        }

        @Override
        public void onRenderComplete() {
            logger.verbose("enter");
            listener.onRenderComplete();
            logger.verbose("exit");
        }
    };

    public RunningTest(IEyesConnector eyes, SeleniumConfiguration configuration, RenderBrowserInfo browserInfo, Logger logger, RunningTestListener listener) {
        this.eyes = eyes;
        this.browserInfo = browserInfo;
        this.configuration = configuration;
        this.listener = listener;
        this.logger = logger;
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
        Task chosenTask;
        synchronized (this.taskList) {
            for (Task task : this.taskList) {
                if (task.isTaskReadyToCheck() && task.getType() == Task.TaskType.CHECK) {
                    score++;
                }
            }

            if (this.taskList.isEmpty())
                return null;

            chosenTask = this.taskList.get(0);
            if (chosenTask.getType() != taskType || chosenTask.isSent() || (taskType == Task.TaskType.OPEN && !chosenTask.isTaskReadyToCheck()))
                return null;
        }
        return new ScoreTask(chosenTask, score);
    }

    public synchronized FutureTask<TestResultContainer> getNextCloseTask() {
//        logger.verbose("enter");
        if (!taskList.isEmpty()) {
            Task task = taskList.get(0);
//            logger.verbose("locking taskList");
            synchronized (taskList) {
//                logger.verbose("removing task " + task.toString() + " and exiting");
                taskList.remove(task);
//                logger.verbose("tasks in taskList: " + taskList.size());
            }
//            logger.verbose("releasing taskList");
            return taskToFutureMapping.get(task);
        }
//        logger.verbose("exit with null");
        return null;
    }

    public RenderBrowserInfo getBrowserInfo() {
        return browserInfo;
    }

    public Task open() {
        logger.verbose("adding Open task...");
        Task task = new Task(configuration, null, eyes, Task.TaskType.OPEN, taskListener, null, this);
        FutureTask<TestResultContainer> futureTask = new FutureTask<>(task);
        this.taskToFutureMapping.put(task, futureTask);
        logger.verbose("locking taskList");
        synchronized (this.taskList) {
            this.taskList.add(task);
            logger.verbose("Open task was added: " + task.toString());
            logger.verbose("tasks in taskList: " + taskList.size());
        }
        logger.verbose("releasing taskList");
        return task;
    }

    public FutureTask<TestResultContainer> close() {
        Task lastTask;
        if (!this.taskList.isEmpty()) {
            lastTask = this.taskList.get(taskList.size() - 1);
            if (lastTask.getType() == Task.TaskType.CLOSE) {
                return taskToFutureMapping.get(lastTask);
            }
        }

        logger.verbose("adding close task...");
        Task task = new Task(configuration, null, eyes, Task.TaskType.CLOSE, taskListener, null, this);
        FutureTask<TestResultContainer> futureTask = new FutureTask<>(task);
        this.taskToFutureMapping.put(task, futureTask);
        logger.verbose("locking taskList");
        synchronized (taskList) {
            this.taskList.add(task);
            logger.verbose("Close task was added: " + task.toString());
            logger.verbose("tasks in taskList: " + taskList.size());
        }
        logger.verbose("releasing taskList");
        return this.taskToFutureMapping.get(task);
    }

    public Task check(ICheckSettings checkSettings) {
        logger.verbose("adding check task...");
        Task task = new Task(configuration, null, eyes, Task.TaskType.CHECK, taskListener, checkSettings, this);
        logger.verbose("locking taskList");
        synchronized (taskList) {
            this.taskList.add(task);
            logger.verbose("Check Task was added: " + task.toString());
            logger.verbose("tasks in taskList: " + taskList.size());
        }
        logger.verbose("releasing taskList");
        this.taskToFutureMapping.get(task);
        return task;
    }

    /**
     * @return true if the only task left is CLOSE task
     */
    public boolean isTestReadyToClose() {
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

    private void setTestInExceptionMode(Error e) {
        this.isTestInExceptionMode.set(true);

        logger.verbose("locking taskList.");
        synchronized (taskList) {
            Iterator<Task> iterator = this.taskList.iterator();
            while (iterator.hasNext()) {
                Task next = iterator.next();
                Task.TaskType type = next.getType();
                if (type == Task.TaskType.CHECK || type == Task.TaskType.OPEN) {
                    logger.verbose("removing element from taskList.");
                    iterator.remove();
                } else if (type == Task.TaskType.CLOSE) {
                    next.setException(e);
                }
            }
        }
        logger.verbose("releasing taskList.");
    }

    Logger getLogger() {
        return logger;
    }

    public SeleniumConfiguration getConfiguration() {
        return configuration;
    }
}