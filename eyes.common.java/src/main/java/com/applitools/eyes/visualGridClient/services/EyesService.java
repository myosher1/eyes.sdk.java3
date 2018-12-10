package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;

import java.util.concurrent.*;
// Needs to be refactored to join on jobRequest and on 

public class EyesService extends Thread {

    private final int threadPoolSize;
    private final EyesService.EyesServiceListener listener;
    private final Tasker tasker;
    private boolean isServiceOn = true;
    private ExecutorService executor;
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
        this.executor = new ThreadPoolExecutor(this.threadPoolSize, threadPoolSize, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(1));
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
        logger.log("Service '" + this.getName() + "' is dead - R.I.P");
    }

    private void runNextTask() {
        if (!isServiceOn) return;
        final FutureTask<TestResults> task = this.listener.getNextTask(tasker);
        if (task != null) {
            this.executor.submit(task);
        }
    }

    public void stopService() {
        this.isServiceOn = false;
    }
}
