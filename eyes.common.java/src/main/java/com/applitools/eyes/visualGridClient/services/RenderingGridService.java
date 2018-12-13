package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.Logger;
import com.applitools.eyes.visualGridClient.model.RenderingTask;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RenderingGridService extends Thread{

    private final int threadPoolSize;
    private final RGServiceListener listener;
    private boolean isServiceOn = true;
    private ExecutorService executor;
    protected Logger logger;


    public interface RGServiceListener {
        RenderingTask getNextTask();
    }

    public RenderingGridService(String serviceName, ThreadGroup servicesGroup, Logger logger, int threadPoolSize, RGServiceListener listener) {
        super(servicesGroup, serviceName);
        this.threadPoolSize = threadPoolSize;
        this.executor = new ThreadPoolExecutor(this.threadPoolSize, 1, 1, TimeUnit.DAYS, new ArrayBlockingQueue<Runnable>(20));
        this.listener = listener;
        this.logger = logger;
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

    private void runNextTask() {
        final RenderingTask task = this.listener.getNextTask();
        if (task != null) {
            this.executor.submit(task);
        }
    }

    public void stopService() {
        this.isServiceOn = false;
    }
}
