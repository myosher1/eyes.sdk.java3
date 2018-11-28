package com.applitools.eyes.visualGridClient;

import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.visualGridClient.data.RenderRequest;
import com.applitools.eyes.visualGridClient.data.RenderStatusResults;
import com.applitools.utils.GeneralUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
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
        FutureTask<RenderStatusResults> getNextTask();

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

    private FutureTask<RenderStatusResults> checkAndRunNextTask() {
        logger.verbose("EyesOpenerService. checkAndRunNextTask");
        if (currentWorkingSession.get() < threadPoolSize) {
            return runNextTask();
        }
        return null;
    }

    private RGThread runNextTask() {
        logger.verbose("EyesOpenerService.runNextTask");
        FutureTask<RenderStatusResults> task = this.listener.getNextTask();
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
