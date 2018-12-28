package com.applitools.eyes.visualGridClient.model;

import com.applitools.eyes.Logger;
import com.applitools.utils.GeneralUtils;

import java.util.*;

public class RateLimiter {
    private final Logger logger;
    private List<PutFuture> awaitingTasks = Collections.synchronizedList(new ArrayList<PutFuture>());
    private List<PutFuture> runningTasks = Collections.synchronizedList(new ArrayList<PutFuture>());

    private int maxConcurrentTasks;
    private final Object lock = new Object();

    public void handle(PutFuture putFuture) {
        synchronized (lock) {
            awaitingTasks.add(putFuture);
            if (!pollingThread.isAlive()) {
                pollingThread.start();
            }
        }
    }

    public boolean isSlotAvailable() {
        return runningTasks.size() < maxConcurrentTasks;
    }

    class RateLimiterRunnable implements Runnable {
        @Override
        public void run() {
            logger.verbose("enter");
            do {

                while (!isSlotAvailable()){
                    GeneralUtils.sleep(250);
                }

                executeTask();

                removeCompletedTasks();

            } while (!awaitingTasks.isEmpty() || !runningTasks.isEmpty());
            logger.verbose("exit");
        }

        private void executeTask() {
            logger.verbose("enter");
            if (awaitingTasks.isEmpty()) return;
            PutFuture putFuture;
            synchronized (lock) {
                putFuture = awaitingTasks.get(0);
                logger.verbose("executing task " + putFuture);
                runningTasks.add(putFuture);
                awaitingTasks.remove(putFuture);
            }
            putFuture.get();
            logger.verbose("exit");
        }

        private void removeCompletedTasks() {
            logger.verbose("enter");
            Iterator<PutFuture> iterator = runningTasks.iterator();
            while (iterator.hasNext()) {
                PutFuture future = iterator.next();
                if (future.isDone()) {
                    logger.verbose("removing done task " + future);
                    iterator.remove();
                }
            }
            logger.verbose("exit");
        }
    }

    private Thread pollingThread = new Thread(new RateLimiterRunnable(), "PutThrottler");

    public RateLimiter(Logger logger, int maxConcurrentTasks) {
        this.logger = logger;
        this.maxConcurrentTasks = maxConcurrentTasks;
    }

    public void run() {
        logger.verbose("enter");
        pollingThread.run();
        logger.verbose("exit");
    }
}
