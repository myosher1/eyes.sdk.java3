package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
// Needs to be refactored to join on jobRequest and on 

public class EyesBaseService extends Thread {

    private final int threadPoolSize;
    private final EyesBaseService.EyesServiceListener listener;
    private final String serviceName;
    private boolean isServiceOn = true;
    private ExecutorService executor;
    private AtomicInteger currentWorkingSession = new AtomicInteger(1);
    protected Logger logger;

    public interface EyesServiceListener {
        FutureTask<TestResults> getNextTask();
    }

    public EyesBaseService(String serviceName, ThreadGroup servicesGroup, Logger logger, int threadPoolSize, EyesServiceListener listener) {
        super(servicesGroup, serviceName);
        this.serviceName = serviceName;
        this.threadPoolSize = threadPoolSize;
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.listener = listener;
        this.logger = logger;
    }

    @Override
    public void run() {
        Object task = null;
        while (isServiceOn || task != null) {
            task = checkAndRunNextTask();
            // Go to sleep if task is null
            if (task == null) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }
        }
        if (this.executor != null) {
            this.executor.shutdown();
        }
        logger.log("Service '" + this.getName() + "' is dead - R.I.P");
    }

    private FutureTask<TestResults> checkAndRunNextTask() {
        if (currentWorkingSession.get() < threadPoolSize) {
            return runNextTask();
        }
        return null;
    }

    private FutureTask<TestResults> runNextTask() {
        final FutureTask<TestResults> task = this.listener.getNextTask();
        if (task != null) {
            this.executor.submit(task);
        }
        return task;
    }

    public void stopService() {
        this.isServiceOn = false;
    }
}
