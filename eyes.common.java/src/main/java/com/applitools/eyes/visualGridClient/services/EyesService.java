package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.visualGridClient.model.TestResultContainer;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.*;

public class EyesService extends Thread {

    protected final int threadPoolSize;
    protected ExecutorService executor;
    protected final EyesService.EyesServiceListener listener;
    private final Object debugLock;
    protected final Tasker tasker;
    protected boolean isServiceOn = true;
    private boolean isPaused;


    protected Logger logger;

    interface Tasker {
        FutureTask<TestResultContainer> getNextTask(Task.TaskListener taskListener);
    }

    public interface EyesServiceListener {
        FutureTask<TestResultContainer> getNextTask(Tasker tasker, Task.TaskListener taskListener);
    }

    public EyesService(String serviceName, ThreadGroup servicesGroup, Logger logger, int threadPoolSize, Object debugLock, EyesServiceListener listener, Tasker tasker) {
        super(servicesGroup, serviceName);
        this.threadPoolSize = threadPoolSize;
        this.executor = new ThreadPoolExecutor(this.threadPoolSize, threadPoolSize, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(20));
        this.listener = listener;
        this.logger = logger;
        this.debugLock = debugLock;
        this.tasker = tasker;
        this.isPaused = debugLock != null;
}

    @Override
    public void run() {
        while (isServiceOn) {
            pauseIfNeeded();
            runNextTask();
        }
        if (this.executor != null) {
            this.executor.shutdown();
        }
        logger.log("Service '" + this.getName() + "' is finished");
    }

    protected void pauseIfNeeded() {
        if (isPaused) {
            synchronized (debugLock) {
                try {
                    debugLock.wait();
                    this.isPaused = false;
                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }
        }
    }

    void runNextTask() {
        if (!isServiceOn) return;
        final FutureTask<TestResultContainer> task = this.listener.getNextTask(tasker, new Task.TaskListener() {
            @Override
            public void onTaskComplete(Task task) {
                debugNotify();
            }

            @Override
            public void onTaskFailed(Exception e, Task task) {
                debugNotify();
            }

            @Override
            public void onRenderComplete() {
                debugNotify();
            }
        });
        if (task != null) {
            pauseIfNeeded();
            this.executor.submit(task);
        }
    }

    private void debugNotify() {
        if (debugLock != null) {
            synchronized (debugLock) {
                debugLock.notify();
            }
        }
    }

    public void debugPauseService() {
        this.isPaused = true;
    }

    void stopService() {
        this.isServiceOn = false;
    }
}
