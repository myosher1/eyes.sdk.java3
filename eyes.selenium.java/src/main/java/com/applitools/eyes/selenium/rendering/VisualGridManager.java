package com.applitools.eyes.selenium.rendering;

import com.sun.jmx.remote.internal.ArrayQueue;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

class VisualGridManager {

    private static VisualGridManager instance = null;

    private int concurrentOpenSessions;

    private HashMap<Eyes, Boolean> openedEyesMap = new HashMap<>();

    private EyesOpenerService eyesOpenerService;

    private EyesCloserService eyesCloserService;

    private EyesCheckerService eyesCheckerService;

    private RenderingGridService renderingGridService;

    private HashMap<String, String> resourceCache = new HashMap<>();

    private ArrayQueue<Eyes> eyesToOpen = new ArrayQueue<>(200);

    private VisualGridManager(int concurrentOpenSessions) {
        this.concurrentOpenSessions = concurrentOpenSessions;
        this.eyesOpenerService = new EyesOpenerService(this.concurrentOpenSessions);
        this.eyesCloserService = new EyesCloserService();
        this.renderingGridService = new RenderingGridService(concurrentOpenSessions);
    }


    static VisualGridManager getInstance(int threadPoolSize) {
        if (instance == null) {
            synchronized (VisualGridManager.class) {
                if (instance == null) {
                    instance = new VisualGridManager(threadPoolSize);
                }
            }
        }
        return instance;
    }

    public void open(Eyes eyes) {
        eyesToOpen.add(eyes);
    }


    void check(Eyes eyes) {
        switch (eyes.getCurrentEyesOpenStatus()) {
            case NONE:
                break;


            case OPEN:
                break;


            case PARTIAL_OPEN:
                break;
        }
    }


    private class EyesOpenerService extends Thread {

        private volatile boolean isServiceOn = true;

        private AtomicInteger currentSessions;

        ArrayBlockingQueue eyesToOpenQueue;

        private EyesOpenerService(int concurrentOpenSessions) {
            this.eyesToOpenQueue = new ArrayBlockingQueue(100);
        }

        @Override
        public void run() {
            while (isServiceOn) {
                if (!eyesToOpenQueue.isEmpty()) {
                    Object element = eyesToOpenQueue.poll();
                    Eyes eyes = (Eyes) element;

                }
            }
        }

        void incCurrentSessions() {
            this.currentSessions.incrementAndGet();
        }

        public int getCurrentSessions() {
            return currentSessions.get();
        }
    }
}
