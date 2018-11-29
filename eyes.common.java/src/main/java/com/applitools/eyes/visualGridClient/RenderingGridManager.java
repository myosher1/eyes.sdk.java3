package com.applitools.eyes.visualGridClient;

import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.visualGridClient.data.*;
import com.applitools.eyes.visualGridClient.services.EyesBaseService;
import com.applitools.eyes.visualGridClient.services.EyesCheckerService;
import com.applitools.eyes.visualGridClient.services.EyesCloserService;
import com.applitools.eyes.visualGridClient.services.EyesOpenerService;
import com.applitools.utils.GeneralUtils;

import java.util.*;
import java.util.concurrent.*;

public class RenderingGridManager {

    private int concurrentOpenSessions;
    private ExecutorService executor;
    private EyesOpenerService eyesOpenerService;
    private EyesCloserService eyesCloserService;
    private EyesCheckerService eyesCheckerService;
    private RenderingGridService renderingGridService;
    private HashMap<String, String> resourceCache = new HashMap<>();
    private ThreadGroup servicesGroup = new ThreadGroup("Services Group");
    private Logger logger;
    private int totalEyesCount = 0;
    private int eyesClosedCount = 0;
    private ArrayList<IRenderingEyes> eyesToOpenList = new ArrayList<>(200);
    private final ArrayList<IRenderingEyes> eyesToCloseList = new ArrayList<>(200);
    private ArrayList<IRenderingEyes> allEyes = new ArrayList<>(200);
    private Set<Map.Entry<RunningTest, RenderRequest>> renderRequestsAsList;
    private Map<String, IResourceFuture> cachedResources = Collections.synchronizedMap(new HashMap<String, IResourceFuture>());

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

        this.eyesCheckerService = new EyesCheckerService("EyesCloserService", logger, servicesGroup, this.concurrentOpenSessions, new EyesBaseService.EyesServiceListener() {
            @Override
            public FutureTask<TestResults> getNextTask() {
                return getNextTestToCheck();
            }

        });
        this.executor = Executors.newFixedThreadPool(4);

    }

    private FutureTask<TestResults> getNextTestToCheck() {
        int mark = -1;
        RunningTest bestTest = null;
        for (IRenderingEyes eyes : allEyes) {
            RunningTest test = eyes.getNextTestToCheck();
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

    private RenderingTask getNextRenderingTask() {
        if (this.renderingTaskList.isEmpty()) {
            return null;
        }
        RenderingTask renderingTask = this.renderingTaskList.get(0);
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
                GeneralUtils.logExceptionStackTrace(e);
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

    public void render(Map<RunningTest, RenderRequest> testToRenderRequestMapping) {
        this.renderRequestsAsList = testToRenderRequestMapping.entrySet();
    }

    public void check(CheckRGSettings settings, String script, List<RenderingConfiguration.RenderBrowserInfo> browsersInfo, IEyesConnector connector, List<Task> taskList) {
        if(!this.eyesCheckerService.isAlive()){
            startServices();
        }
        this.renderingTaskList.add(new RenderingTask(connector, script, settings, taskList, this.renderingInfo, null, this.cachedResources));
    }
}
