package com.applitools.eyes.visualGridClient;

import com.applitools.eyes.Logger;
import com.applitools.eyes.visualGridClient.data.RenderStatusResults;
import com.applitools.eyes.visualGridClient.data.RenderingTask;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class RenderingGridService extends Thread{

    public static final int FACTOR = 5;

    private final int threadPoolSize;
    private final RenderGridServiceListener listener;
    private final String serviceName;
    private boolean isServiceOn = true;
    private ExecutorService executor;
    private AtomicInteger currentWorkingSession = new AtomicInteger(1);
    private Logger logger;

    public interface RenderGridServiceListener {
        RenderingTask getNextTask();

    }

    public RenderingGridService(String serviceName, Logger logger, ThreadGroup servicesGroup, int threadPoolSize, RenderGridServiceListener listener) {
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

    private Future<RenderStatusResults> checkAndRunNextTask() {
        if (currentWorkingSession.get() < threadPoolSize) {
            return runNextTask();
        }
        return null;
    }

    private Future<RenderStatusResults> runNextTask() {
        RenderingTask task = this.listener.getNextTask();
        if (task != null) {
            return this.executor.submit(task);
        } else {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
        return null;
    }

    public void stopService() {
        this.isServiceOn = false;
    }
}
