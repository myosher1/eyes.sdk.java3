package com.applitools.eyes.visualGridClient.services;

import com.applitools.ICheckRGSettings;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.visualGridClient.model.*;
import com.applitools.utils.GeneralUtils;

import java.util.*;
import java.util.concurrent.*;

public class RenderingGridManager {

    private int concurrentOpenSessions;
    private EyesService eyesOpenerService;
    private EyesService eyesCloserService;
    private EyesService eyesCheckerService;
    private RenderingGridService renderingGridService;
    private ThreadGroup servicesGroup = new ThreadGroup("Services Group");
    private final ArrayList<IRenderingEyes> eyesToOpenList = new ArrayList<>(200);
    private final ArrayList<IRenderingEyes> eyesToCloseList = new ArrayList<>(200);
    private final ArrayList<IRenderingEyes> allEyes = new ArrayList<>(200);
    private Map<String, IResourceFuture> cachedResources = Collections.synchronizedMap(new HashMap<String, IResourceFuture>());
    private Map<String, Future<Boolean>> putResourceCache = Collections.synchronizedMap(new HashMap<String, Future<Boolean>>());

    private final Logger logger = new Logger();
    private int totalEyesCount = 0;
    private int eyesClosedCount = 0;

    private final Object openerServiceLock = new Object();
    private final Object checkerServiceLock = new Object();
    private final Object closerServiceLock = new Object();
    private final Object renderingServiceLock = new Object();

    public interface RenderListener {

        void onRenderSuccess();

        void onRenderFailed(Exception e);
    }

    private IRenderingEyes.EyesListener eyesListener = new IRenderingEyes.EyesListener() {
        @Override
        public void onTaskComplete(Task task, IRenderingEyes eyes) {
            switch (task.getType()) {

                case OPEN:
                    synchronized (eyesToOpenList) {
                        RenderingGridManager.this.eyesToOpenList.remove(task);
                    }
                    break;
                case CLOSE:
                case ABORT:
                    if (eyes.isEyesClosed()) {
                        eyesClosedCount++;
                        eyesToCloseList.remove(eyes);
                    }
                    //check if it's the last close then shutdown
                    if (eyesClosedCount == totalEyesCount) {

                        eyesOpenerService.stopService();

                        eyesCloserService.stopService();

                        logger.verbose("Closed All Services");
                    }
                    break;
                case CHECK:
            }

            notifyAllServices();
        }

        @Override
        public void onRenderComplete() {
            logger.verbose("enter");
            notifyAllServices();
            logger.verbose("exit");
        }

    };
    private final List<RenderingTask> renderingTaskList = new ArrayList<>();
    private RenderingInfo renderingInfo;

    public RenderingGridManager(int concurrentOpenSessions) {
        this.concurrentOpenSessions = concurrentOpenSessions;

        init();
        startServices();
    }

    private void init() {
        this.eyesOpenerService = new EyesService("EyesOpenerService", servicesGroup, logger, this.concurrentOpenSessions, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResults> getNextTask(EyesService.Tasker tasker) {

                return getOrWaitForTask(openerServiceLock, tasker, "EyesOpenerService");
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResults> getOrWaitForNextTask() {
                return getNextTestToOpen();
            }
        });

        this.eyesCloserService = new EyesService("eyesCloserService", servicesGroup, logger, this.concurrentOpenSessions, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResults> getNextTask(EyesService.Tasker tasker) {

                return getOrWaitForTask(closerServiceLock, tasker, "eyesCloserService");
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResults> getOrWaitForNextTask() {
                return getNextTestToClose();
            }
        });

        this.renderingGridService = new RenderingGridService("renderingGridService", servicesGroup, logger, this.concurrentOpenSessions, new RenderingGridService.RGServiceListener() {
            @Override
            public RenderingTask getNextTask() {
                RenderingTask nextTestToRender = getNextRenderingTask();
                if (nextTestToRender == null) {
                    synchronized (renderingServiceLock) {
                        try {
                            nextTestToRender = getNextRenderingTask();
                            if (nextTestToRender == null) {
                                renderingServiceLock.wait();
                                logger.log("Rendering service woke up");
                                nextTestToRender = getNextRenderingTask();
                            }

                        } catch (InterruptedException e) {
                            GeneralUtils.logExceptionStackTrace(logger, e);
                        }
                    }
                }
                return nextTestToRender;
            }
        });

        this.eyesCheckerService = new EyesService("eyesCheckerService", servicesGroup, logger, this.concurrentOpenSessions, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResults> getNextTask(EyesService.Tasker tasker) {

                return getOrWaitForTask(checkerServiceLock, tasker, "eyesCheckerService");
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResults> getOrWaitForNextTask() {
                return getNextCheckTask();
            }
        });
        ;
    }

    private FutureTask<TestResults> getOrWaitForTask(Object lock, EyesService.Tasker tasker, String serviceName) {
        FutureTask<TestResults> nextTestToOpen = tasker.getOrWaitForNextTask();
        if (nextTestToOpen == null) {
            synchronized (lock) {
                try {
                    nextTestToOpen = tasker.getOrWaitForNextTask();
                    if (nextTestToOpen == null) {
                        lock.wait();
                        logger.log(serviceName + " woke up");
                        nextTestToOpen = tasker.getOrWaitForNextTask();
                    }

                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }
        }
        return nextTestToOpen;
    }

    /**
     * Sets a handler of log messages generated by this API.
     * @param logHandler Handles log messages generated by this API.
     */
    public void setLogHandler(LogHandler logHandler) {
        logger.setLogHandler(logHandler);
    }

    public Logger getLogger() {
        return logger;
    }

    private FutureTask<TestResults> getNextCheckTask() {
        RunningTest bestTest = null;
        int bestMark = -1;
        synchronized (allEyes) {
            for (IRenderingEyes eyes : allEyes) {
                int currentTestMark = eyes.getBestScoreForCheck();
                if (bestMark < currentTestMark) {
                    bestTest = eyes.getNextCheckTask();
                    bestMark = currentTestMark;
                }
            }
        }

        if (bestTest == null) {
            return null;
        }

        return new FutureTask<>(bestTest.getNextCheckTaskAndRemove());
    }

    private synchronized RenderingTask getNextRenderingTask() {
        if (this.renderingTaskList.isEmpty()) {
            return null;
        }
        RenderingTask renderingTask = null;
        synchronized (this.renderingTaskList) {
            if (!this.renderingTaskList.isEmpty()) {
                renderingTask = this.renderingTaskList.get(0);
                this.renderingTaskList.remove(renderingTask);
            }
        }
        logger.log("Starting to renderTask - " + renderingTask);
        return renderingTask;
    }

    private FutureTask<TestResults> getNextTestToClose() {
        RunningTest runningTest = null;
        synchronized (eyesToCloseList) {
            for (IRenderingEyes eyes : eyesToCloseList) {
                runningTest = eyes.getNextTestToClose();
                if (runningTest != null) {
                    return runningTest.getNextCloseTask();
                }
            }
        }
        return null;
    }

    public void open(IRenderingEyes eyes, RenderingInfo renderingInfo) {
        logger.verbose("RenderingGridMa nager.open()");
        if (this.renderingInfo == null) {
            this.renderingInfo = renderingInfo;
        }
        synchronized (eyesToOpenList) {
            eyesToOpenList.add(eyes);
            allEyes.add(eyes);
        }
        eyes.setListener(this.eyesListener);
        totalEyesCount++;
        eyesListener = new IRenderingEyes.EyesListener() {
            @Override
            public void onTaskComplete(Task task, IRenderingEyes eyes) {

            }

            @Override
            public void onRenderComplete() {
                logger.verbose("enter");
                notifyAllServices();
                logger.verbose("exit");
            }
        };
        eyes.setListener(eyesListener);
        notifyAllServices();
        logger.log("openerServiceLock.notify()");
    }

    private void startServices() {
        logger.verbose("RenderingGridManager.startServices");
        this.eyesOpenerService.start();
        this.eyesCloserService.start();
        this.renderingGridService.start();
        this.eyesCheckerService.start();
        this.servicesGroup.setDaemon(false);
    }

    private void stopServices() {
        logger.verbose("RenderingGridManager.stopServices");
        this.eyesOpenerService.stopService();
        this.eyesCloserService.stopService();
        this.servicesGroup.setDaemon(false);
    }


    private FutureTask<TestResults> getNextTestToOpen() {
        RunningTest bestTest = null;
        int bestMark = -1;
        synchronized (allEyes) {
            for (IRenderingEyes eyes : allEyes) {
                int currentTestMark = eyes.getBestScoreForOpen();
                if (bestMark < currentTestMark) {
                    bestTest = eyes.getNextTestToOpen();
                    bestMark = currentTestMark;
                }
            }
        }

        if (bestTest == null) {
            return null;
        }

        return new FutureTask<>(bestTest.getNextOpenTaskAndRemove());
    }

    public TestResultSummary getAllTestResults() {
        List<Future<TestResults>> allFutures = new ArrayList<>();
        for (IRenderingEyes eyes : allEyes) {
            allFutures.addAll(eyes.close());
            synchronized (this.eyesToCloseList) {
                this.eyesToCloseList.add(eyes);
            }
        }
        notifyAllServices();
        List<TestResults> allResults = new ArrayList<>();
        for (Future<TestResults> future : allFutures) {
            try {
                TestResults obj = future.get();
                allResults.add(obj);
            } catch (InterruptedException | ExecutionException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
        stopServices();
        notifyAllServices();
        return new TestResultSummary(allResults);
    }

    public void close(IRenderingEyes eyes) {
        synchronized (this.eyesToCloseList) {
            this.eyesToCloseList.add(eyes);
        }
        notifyAllServices();
    }

    public void check(ICheckRGSettings settings, String script, IEyesConnector connector, List<Task> taskList, final RenderListener listener) {

        RenderingTask renderingTask = new RenderingTask(connector, script, settings, taskList, this.renderingInfo, this.cachedResources, this.putResourceCache, logger, new RenderingTask.RenderTaskListener() {
            @Override
            public void onRenderSuccess() {
                listener.onRenderSuccess();
                synchronized (checkerServiceLock) {
                    notifyAllServices();
                }
            }

            @Override
            public void onRenderFailed(Exception e) {
                listener.onRenderFailed(e);
            }
        });
        this.renderingTaskList.add(renderingTask);
        notifyAllServices();
    }

    void notifyAllServices() {
        logger.verbose("enter");
        synchronized (openerServiceLock) {
            openerServiceLock.notify();
        }
        synchronized (closerServiceLock) {
            closerServiceLock.notify();
        }
        synchronized (checkerServiceLock) {
            checkerServiceLock.notify();
        }
        synchronized (renderingServiceLock) {
            renderingServiceLock.notify();
        }
        logger.verbose("exit");
    }
}
