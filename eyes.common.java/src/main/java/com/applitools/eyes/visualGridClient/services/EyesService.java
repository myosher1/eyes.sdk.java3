package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;

import java.util.concurrent.*;

public class EyesService extends Thread {

    protected final int threadPoolSize;
    protected ExecutorService executor;
    protected final EyesService.EyesServiceListener listener;
    protected final Tasker tasker;
    protected boolean isServiceOn = true;


    protected Logger logger;

    interface Tasker {
        FutureTask<TestResults> getOrWaitForNextTask();
    }

    public interface EyesServiceListener {
        FutureTask<TestResults> getNextTask(Tasker tasker);
    }

    public EyesService(String serviceName, ThreadGroup servicesGroup, Logger logger, int threadPoolSize, EyesServiceListener listener, Tasker tasker) {
        super(servicesGroup, serviceName);
        this.threadPoolSize = threadPoolSize;
        this.executor = new ThreadPoolExecutor(this.threadPoolSize, threadPoolSize, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(20 ));
        this.listener = listener;
        this.logger = logger;
        this.tasker = tasker;
    }

    @Override
    public void run() {
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
        final FutureTask<TestResults> task = this.listener.getNextTask(tasker);
        if (task != null) {
            this.executor.submit(task);
        }
    }

    void stopService() {
        this.isServiceOn = false;
    }
}
