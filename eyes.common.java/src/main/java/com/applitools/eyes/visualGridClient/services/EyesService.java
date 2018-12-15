package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.*;

public class EyesService extends Thread {

    protected final int threadPoolSize;
    protected ExecutorService executor;
    protected final EyesService.EyesServiceListener listener;
    private final Object debugLock;
    protected final Tasker tasker;
    protected boolean isServiceOn = true;


    protected Logger logger;

    interface Tasker {
        FutureTask<TestResults> getOrWaitForNextTask(Task.TaskListener taskListener);
    }

    public interface EyesServiceListener {
        FutureTask<TestResults> getNextTask(Tasker tasker, Task.TaskListener taskListener);
    }

    public EyesService(String serviceName, ThreadGroup servicesGroup, Logger logger, int threadPoolSize, Object debugLock, EyesServiceListener listener, Tasker tasker) {
        super(servicesGroup, serviceName);
        this.threadPoolSize = threadPoolSize;
        this.executor = new ThreadPoolExecutor(this.threadPoolSize, threadPoolSize, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(20));
        this.listener = listener;
        this.logger = logger;
        this.debugLock = debugLock;
        this.tasker = tasker;
}

    @Override
    public void run() {
        if (debugLock != null) {
            synchronized (debugLock) {
                try {
                    debugLock.wait();
                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }
        }
        while (isServiceOn) {
            runNextTask();
        }
        if (this.executor != null) {
            this.executor.shutdown();
        }
        logger.log("Service '" + this.getName() + "' is finished");
    }

    void runNextTask() {
        if (!isServiceOn) return;
        final FutureTask<TestResults> task = this.listener.getNextTask(tasker, new Task.TaskListener() {
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
            this.executor.submit(task);
        }
    }

    private void debugNotify() {
        synchronized (debugLock) {
            debugLock.notify();
        }
    }

    void stopService() {
        this.isServiceOn = false;
    }
}
