package com.applitools.eyes.visualGridClient.model;

import com.applitools.eyes.Logger;
import com.applitools.utils.GeneralUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Future;

public class RateLimiter {
    private final Logger logger;
    private Map<String, PutFuture> allTasks = Collections.synchronizedMap(new HashMap<String, PutFuture>());
    private Map<String, Future> runningTasks = Collections.synchronizedMap(new HashMap<String, Future>());

    private int maxConcurrentTasks;

    class RateLimiterRunnable implements Runnable {
        @Override
        public void run() {
            logger.verbose("enter");
            do {
                executeTasks();
                removeCompletedTasks();

                GeneralUtils.sleep(250);
            } while (!allTasks.isEmpty());
            logger.verbose("exit");
        }

        private void executeTasks() {
            logger.verbose("enter");
            Iterator<Map.Entry<String, PutFuture>> iterator = allTasks.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, PutFuture> taskEntry = iterator.next();
                if (runningTasks.size() >= maxConcurrentTasks) {
                    logger.verbose("throttling level reached.");
                    break;
                }
                String key = taskEntry.getKey();
                if (runningTasks.containsKey(key)) {
                    continue;
                }
                PutFuture putFuture = taskEntry.getValue();
                if (!putFuture.isDone()) {
                    //Future future = putFuture.run();
                    logger.verbose("executing task " + key);
                    runningTasks.put(key, putFuture);
                } else {
                    logger.verbose("removing marked as done task " + key);
                    iterator.remove();
                }
            }
            logger.verbose("exit");
        }

        private void removeCompletedTasks() {
            logger.verbose("enter");
            Iterator<Map.Entry<String, Future>> iterator = runningTasks.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Future> next = iterator.next();
                Future future = next.getValue();
                String key = next.getKey();
                if (future.isDone()) {
                    logger.verbose("removing done task " + key);
                    iterator.remove();
                    allTasks.remove(key);
                }
            }
            logger.verbose("exit");
        }
    }

    private Thread pollingThread = new Thread(new RateLimiterRunnable(), "PutThrottler");

    public RateLimiter(Logger logger, Map<String, PutFuture> allTasks, int maxConcurrentTasks) {
        this.logger = logger;
        this.allTasks.putAll(allTasks);
        this.maxConcurrentTasks = maxConcurrentTasks;
    }

    public void run() {
        logger.verbose("enter");
        pollingThread.run();
        logger.verbose("exit");
    }
}
