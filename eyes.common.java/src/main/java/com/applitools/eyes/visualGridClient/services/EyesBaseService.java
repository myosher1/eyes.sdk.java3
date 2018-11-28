package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

public class EyesBaseService extends Thread {

    private final int threadPoolSize;
    private final EyesBaseService.EyesServiceListener listener;
    private final String serviceName;
    private boolean isServiceOn = true;
    private ExecutorService executor;
    private AtomicInteger currentWorkingSession = new AtomicInteger(1);
    private Logger logger;

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
        logger.verbose(serviceName + ".run()");
        Object task = null;
        while (isServiceOn || task != null) {
            task = checkAndRunNextTask();
        }
        if (this.executor != null) {
            this.executor.shutdown();
        }
        System.out.println("Service '" + this.getName() + "' is dead - R.I.P");
    }

    private FutureTask<TestResults> checkAndRunNextTask() {
        logger.verbose("EyesOpenerService. checkAndRunNextTask");
        if (currentWorkingSession.get() < threadPoolSize) {
            return runNextTask();
        }
        return null;
    }

    private FutureTask<TestResults> runNextTask() {
        logger.verbose("EyesOpenerService.runNextTask");
        final FutureTask<TestResults> task = this.listener.getNextTask();
        if (task != null) {
            this.executor.submit(task);
        } else {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                GeneralUtils.logExceptionStackTrace(e);
            }
        }
        return task;
    }

    public void stopService() {
        this.isServiceOn = false;
    }
}
