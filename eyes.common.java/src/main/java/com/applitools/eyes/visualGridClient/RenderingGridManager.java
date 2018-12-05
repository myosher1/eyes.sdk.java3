package com.applitools.eyes.visualGridClient;

import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.visualGridClient.data.*;
import com.applitools.eyes.visualGridClient.services.EyesBaseService;
import com.applitools.eyes.visualGridClient.services.EyesCheckerService;
import com.applitools.eyes.visualGridClient.services.EyesCloserService;
import com.applitools.eyes.visualGridClient.services.EyesOpenerService;
import com.applitools.utils.GeneralUtils;

import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class RenderingGridManager {

    private int concurrentOpenSessions;
    private EyesOpenerService eyesOpenerService;
    private EyesCloserService eyesCloserService;
    private EyesCheckerService eyesCheckerService;
    private RenderingGridService renderingGridService;
    private ThreadGroup servicesGroup = new ThreadGroup("Services Group");
    private ArrayList<IRenderingEyes> eyesToOpenList = new ArrayList<>(200);
    private final ArrayList<IRenderingEyes> eyesToCloseList = new ArrayList<>(200);
    private ArrayList<IRenderingEyes> allEyes = new ArrayList<>(200);
    private Map<String, IResourceFuture> cachedResources = Collections.synchronizedMap(new HashMap<String, IResourceFuture>());
    private Map<String, Future<Boolean>> putResourceCache = Collections.synchronizedMap(new HashMap<String, Future<Boolean>>());

    private Logger logger;
    private int totalEyesCount = 0;
    private int eyesClosedCount = 0;

    public interface RenderListener{
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
        }
    };
    private List<RenderingTask> renderingTaskList = new ArrayList<>();
    private RenderingInfo renderingInfo;

    public RenderingGridManager(int concurrentOpenSessions, Logger logger) {
        this.concurrentOpenSessions = concurrentOpenSessions;
        this.logger = logger;

        this.eyesOpenerService = new EyesOpenerService("EyesOpenerService", logger, servicesGroup,
                this.concurrentOpenSessions, new EyesBaseService.EyesServiceListener() {
            @Override
            public FutureTask<TestResults> getNextTask() {
                return getNextTestToOpen();
            }

        });

        this.eyesCloserService = new EyesCloserService("EyesCloserService", logger, servicesGroup, this.concurrentOpenSessions, new EyesBaseService.EyesServiceListener() {
            @Override
            public FutureTask<TestResults> getNextTask() {
                return getNextTestToClose();
            }

        });

        this.renderingGridService = new RenderingGridService("RenderGridService", logger, servicesGroup,
                this.concurrentOpenSessions, new RenderingGridService.RenderGridServiceListener() {
            @Override
            public RenderingTask getNextTask() {
                return RenderingGridManager.this.getNextRenderingTask();
            }
        });

        this.eyesCheckerService = new EyesCheckerService("EyesCheckerService", logger, servicesGroup, this.concurrentOpenSessions, new EyesBaseService.EyesServiceListener() {
            @Override
            public FutureTask<TestResults> getNextTask() {
                return getNextTestToCheck();
            }

        });
    }

    private FutureTask<TestResults> getNextTestToCheck() {
        Task bestTask = null;
        int bestMark = -1;
        synchronized (allEyes) {
            for (IRenderingEyes eye : allEyes) {
                int bestTestMark = eye.getBestMarkForCheck();
                if (bestMark < bestTestMark) {
                    bestTask = eye.getNextTaskToCheck();
                }
            }
        }

        if (bestTask == null) {
            return null;
        }

        FutureTask<TestResults> futureTask = new FutureTask<>(bestTask);
        return futureTask;
    }

    private RenderingTask getNextRenderingTask() {
        if (this.renderingTaskList.isEmpty()) {
            return null;
        }
        RenderingTask renderingTask = this.renderingTaskList.get(0);
        this.renderingTaskList.remove(renderingTask);
        return renderingTask;
    }


    private FutureTask<TestResults> getNextTestToClose() {
        RunningTest runningTest = null;
        synchronized (eyesToCloseList) {
            for (IRenderingEyes eyes : eyesToCloseList) {
                runningTest = eyes.getNextTestToClose();
                if (runningTest != null) {
                    return runningTest.getNextTask();
                }
            }
        }
        return null;
    }

    public void open(IRenderingEyes eyes, RenderingInfo rendringInfo) {
        logger.verbose("RenderingGridManager.open()");
        if (this.renderingInfo == null) {
            this.renderingInfo = rendringInfo;
        }
        synchronized (eyesToOpenList) {
            eyesToOpenList.add(eyes);
            allEyes.add(eyes);
        }
        eyes.setListener(this.eyesListener);
        totalEyesCount++;

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
        logger.verbose("RenderingGridManager.startServices");
        this.eyesOpenerService.stopService();
        this.eyesCloserService.stopService();
        this.servicesGroup.setDaemon(false);
    }


    private FutureTask<TestResults> getNextTestToOpen() {
        int mark = -1;
        RunningTest bestTest = null;
        for (IRenderingEyes eyes : eyesToOpenList) {
            RunningTest test = eyes.getNextTestToOpen();
            if (test != null && mark < test.getMark()) {
                mark = test.getMark();
                bestTest = test;
            }
        }
        if (bestTest != null) {
            return bestTest.getNextTask();
        }
        return null;
    }

    public TestResultSummery getAllTestResults() {
        List<Future<TestResults>> allFutures = new ArrayList<>();
        for (IRenderingEyes eyes : allEyes) {
            allFutures.addAll(eyes.close());
            synchronized (this.eyesToCloseList) {
                this.eyesToCloseList.add(eyes);
            }
        }
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
        return new TestResultSummery(allResults);
    }

    public void close(IRenderingEyes eyes) {
        synchronized (this.eyesToCloseList) {
            this.eyesToCloseList.add(eyes);
        }
    }

    public void check(CheckRGSettings settings, String script, IEyesConnector connector, List<Task> taskList, final RenderListener listener) {
        if(!this.eyesCheckerService.isAlive()){
            startServices();
        }
        RenderingTask renderingTask = new RenderingTask(connector, script, settings, taskList, this.renderingInfo, this.cachedResources, this.putResourceCache, logger, new RenderingTask.RenderTaskListener() {
            @Override
            public void onRenderSuccess() {
                listener.onRenderSuccess();
            }

            @Override
            public void onRenderFailed(Exception e) {
                listener.onRenderFailed(e);
            }
        });
        this.renderingTaskList.add(renderingTask);
    }
}
