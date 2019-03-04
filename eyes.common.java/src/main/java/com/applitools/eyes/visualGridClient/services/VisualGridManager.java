package com.applitools.eyes.visualGridClient.services;

import com.applitools.ICheckRGSettings;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.Logger;
import com.applitools.eyes.visualGridClient.model.*;
import com.applitools.utils.GeneralUtils;

import java.util.*;
import java.util.concurrent.*;

public class VisualGridManager {

    private int concurrentOpenSessions;

    //For Testing...
    private final Object openerServiceDebugLock;
    private final Object checkerServiceDebugLock;
    private final Object closerServiceDebugLock;
    private final Object renderServiceDebugLock;

    private OpenerService eyesOpenerService;
    private EyesService eyesCloserService;
    private EyesService eyesCheckerService;
    private RenderingGridService renderingGridService;
    private ThreadGroup servicesGroup = new ThreadGroup("Services Group");
    private final List<IRenderingEyes> eyesToOpenList = Collections.synchronizedList(new ArrayList<IRenderingEyes>(200));
    private final List<IRenderingEyes> eyesToCloseList = Collections.synchronizedList(new ArrayList<IRenderingEyes>(200));
    private final List<IRenderingEyes> allEyes = Collections.synchronizedList(new ArrayList<IRenderingEyes>(200));
    private Map<String, IResourceFuture> cachedResources = Collections.synchronizedMap(new HashMap<String, IResourceFuture>());
    private Map<String, PutFuture> putResourceCache = Collections.synchronizedMap(new HashMap<String, PutFuture>());

    private final Logger logger = new Logger();

    private final Object openerServiceConcurrencyLock = new Object();
    private final Object openerServiceLock = new Object();
    private final Object checkerServiceLock = new Object();
    private final Object closerServiceLock = new Object();
    private final Object renderingServiceLock = new Object();
    private final List<RenderingTask> renderingTaskList = Collections.synchronizedList(new ArrayList<RenderingTask>());

    private RenderingInfo renderingInfo;
    private IDebugResourceWriter debugResourceWriter;

    private RateLimiter rateLimiter;
    private String serverUrl;
    private static final String DEFAULT_API_KEY = System.getenv("APPLITOOLS_API_KEY");
    private String apiKey = DEFAULT_API_KEY;
    private boolean isDisabled;

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private FutureTask<TestResultContainer> getOrWaitForTask(Object lock, @SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker,
                                                             String serviceName) {
        FutureTask<TestResultContainer> nextTestToOpen = tasker.getNextTask();
        if (nextTestToOpen == null) {
            try {
//                logger.verbose("locking " + serviceName);
                synchronized (lock) {
                    lock.wait(500);
                }
//                logger.verbose("releasing " + serviceName);
                nextTestToOpen = tasker.getNextTask();
//                logger.verbose(serviceName + " tasker returned " + nextTestToOpen);
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
        return nextTestToOpen;
    }

    public void pauseAllService() {
        eyesOpenerService.debugPauseService();
        eyesCloserService.debugPauseService();
        eyesCheckerService.debugPauseService();
        renderingGridService.debugPauseService();
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey != null ? apiKey : DEFAULT_API_KEY;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public boolean getIsDisabled() {
        return this.isDisabled;
    }

    public interface RenderListener {

        void onRenderSuccess();

        void onRenderFailed(Exception e);

    }

    private IRenderingEyes.EyesListener eyesListener = new IRenderingEyes.EyesListener() {
        @Override
        public void onTaskComplete(Task task, IRenderingEyes eyes) {
            logger.verbose("Enter with: " + task.getType());
            Task.TaskType type = task.getType();
            try {
                switch (type) {

                    case OPEN:
                        logger.verbose("locking eyesToOpenList");
                        synchronized (eyesToOpenList) {
                            logger.verbose("removing task " + task.toString());
                            eyesToOpenList.remove(eyes);
                        }
                        logger.verbose("releasing eyesToOpenList");
                        break;
                    case CLOSE:
                        logger.verbose("Task Close.");
                        eyesOpenerService.decrementConcurrency();
                        logger.verbose("locking eyesToCloseList");
                        synchronized (eyesToCloseList) {
                            if (eyes.isEyesClosed()) {
                                eyesToCloseList.remove(eyes);
//                                allEyes.remove(eyes);
                            }
                        }
                        logger.verbose("releasing eyesToCloseList");
                        logger.verbose("locking openerServiceConcurrencyLock");
                        synchronized (openerServiceConcurrencyLock) {
                            openerServiceConcurrencyLock.notify();
                        }
                        logger.verbose("releasing openerServiceConcurrencyLock");
                        break;
                    case ABORT:
                        logger.verbose("Task Abort.");
                        logger.verbose("locking eyesToCloseList");
                        synchronized (eyesToCloseList) {
                            if (eyes.isEyesClosed()) {
                                eyesToCloseList.remove(eyes);
//                                allEyes.remove(eyes);
                            }
                        }
                        logger.verbose("releasing eyesToCloseList");
                        logger.verbose("locking openerServiceConcurrencyLock");
                        synchronized (openerServiceConcurrencyLock) {
                            openerServiceConcurrencyLock.notify();
                        }
                        logger.verbose("releasing openerServiceConcurrencyLock");
                        break;
                    case CHECK:
                        logger.verbose("Check complete.");
                }
            } catch (Exception e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
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

    public VisualGridManager(int concurrentOpenSessions) {
        this(concurrentOpenSessions, null, null, null, null);

    }

    public VisualGridManager(int concurrentOpenSessions,
                             Object openerServiceDebugLock,
                             Object checkerServiceDebugLock,
                             Object closerServiceDebugLock,
                             Object renderServiceDebugLock) {

        this.concurrentOpenSessions = concurrentOpenSessions;
        this.openerServiceDebugLock = openerServiceDebugLock;
        this.checkerServiceDebugLock = checkerServiceDebugLock;
        this.closerServiceDebugLock = closerServiceDebugLock;
        this.renderServiceDebugLock = renderServiceDebugLock;
        this.rateLimiter = new RateLimiter(logger, 20);
        init();
        startServices();
        logger.verbose("rendering grid manager is built");
    }

    /**
     * Sets a handler of log messages generated by this API.
     *
     * @param logHandler Handles log messages generated by this API.
     */
    public void setLogHandler(LogHandler logHandler) {
        logger.setLogHandler(logHandler);
        if (!logHandler.isOpen()) {
            logHandler.open();
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public Map<String, IResourceFuture> getCachedResources() {
        return cachedResources;
    }

    public Map<String, PutFuture> getPutResourceCache() {
        return putResourceCache;
    }

    public RenderingInfo getRenderingInfo() {
        return renderingInfo;
    }

    private void init() {
        this.eyesOpenerService = new OpenerService("eyesOpenerService", servicesGroup,
                logger, this.concurrentOpenSessions, openerServiceConcurrencyLock, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResultContainer> getNextTask(@SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker) {
                return getOrWaitForTask(openerServiceLock, tasker, "eyesOpenerService");
            }

        }, openerServiceDebugLock, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResultContainer> getNextTask() {
                return getNextTestToOpen();
            }
        });

        this.eyesCloserService = new EyesService("eyesCloserService", servicesGroup, logger, concurrentOpenSessions, closerServiceDebugLock, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResultContainer> getNextTask(@SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker) {

                return getOrWaitForTask(closerServiceLock, tasker, "eyesCloserService");
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResultContainer> getNextTask() {
                return getNextTestToClose();
            }
        });

        this.renderingGridService = new RenderingGridService("renderingGridService", servicesGroup, logger, this.concurrentOpenSessions, renderServiceDebugLock, new RenderingGridService.RGServiceListener() {
            @Override
            public RenderingTask getNextTask() {
                RenderingTask nextTestToRender = getNextRenderingTask();
                if (nextTestToRender == null) {
                    synchronized (renderingServiceLock) {
                        try {
                            nextTestToRender = getNextRenderingTask();
                            if (nextTestToRender == null) {
                                renderingServiceLock.wait(300);
//                                logger.log("Rendering service woke up");
                                nextTestToRender = getNextRenderingTask();
                            }

                        } catch (Exception e) {
                            GeneralUtils.logExceptionStackTrace(logger, e);
                        }
                    }
                }
                return nextTestToRender;
            }
        });

        this.eyesCheckerService = new EyesService("eyesCheckerService", servicesGroup, logger, this.concurrentOpenSessions, checkerServiceDebugLock, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResultContainer> getNextTask(@SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker) {

                return getOrWaitForTask(checkerServiceLock, tasker, "eyesCheckerService");
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResultContainer> getNextTask() {
                return getNextCheckTask();
            }
        });
    }

    private FutureTask<TestResultContainer> getNextCheckTask() {
        ScoreTask bestScoreTask = null;
        int bestScore = -1;
        synchronized (allEyes) {
            for (IRenderingEyes eyes : allEyes) {
                ScoreTask currentScoreTask = eyes.getBestScoreTaskForCheck();
                if (currentScoreTask == null) continue;
                int currentTestMark = currentScoreTask.getScore();
                if (bestScore < currentTestMark) {
                    bestScoreTask = currentScoreTask;
                    bestScore = currentTestMark;
                }
            }
        }

        if (bestScoreTask == null) {
            return null;
        }
        Task task = bestScoreTask.getTask();
        return new FutureTask<>(task);
    }

    private RenderingTask getNextRenderingTask() {
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
//        logger.log("Starting to renderTask - " + renderingTask);
        return renderingTask;
    }

    private FutureTask<TestResultContainer> getNextTestToClose() {
        RunningTest runningTest;
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
        logger.verbose("enter");
        if (this.renderingInfo == null) {
            this.renderingInfo = renderingInfo;
        }
//        logger.verbose("locking eyesToOpenList");
        synchronized (eyesToOpenList) {
            eyesToOpenList.add(eyes);
        }
//        logger.verbose("releasing eyesToOpenList");
//        logger.verbose("locking allEyes");
        synchronized (allEyes) {
            allEyes.add(eyes);
        }
        logger.verbose("releasing allEyes");
        eyes.setListener(eyesListener);
        logger.log("concurrencyLock.notify()");
    }

    private void startServices() {
        logger.verbose("enter");
        this.eyesOpenerService.start();
        this.eyesCloserService.start();
        this.renderingGridService.start();
        this.eyesCheckerService.start();
        this.servicesGroup.setDaemon(false);
        logger.verbose("exit");
    }

    private void stopServices() {
        logger.verbose("enter");
        this.eyesOpenerService.stopService();
        this.eyesCloserService.stopService();
        this.renderingGridService.stopService();
        this.eyesCheckerService.stopService();
        logger.verbose("exit");
    }

    private synchronized FutureTask<TestResultContainer> getNextTestToOpen() {
        ScoreTask bestScoreTask = null;
        int bestMark = -1;
//        logger.verbose("looking for best test in a list of " + allEyes.size());
        synchronized (allEyes) {
            for (IRenderingEyes eyes : allEyes) {
                ScoreTask currentTestMark = eyes.getBestScoreTaskForOpen();
                if (currentTestMark == null) continue;
                int currentScore = currentTestMark.getScore();
                if (bestMark < currentScore) {
                    bestMark = currentScore;
                    bestScoreTask = currentTestMark;
                }
            }
        }

        if (bestScoreTask == null) {
//            logger.verbose("no test found.");
            return null;
        }

        logger.verbose("found test with mark " + bestMark);
        logger.verbose("calling getNextOpenTaskAndRemove on " + bestScoreTask.toString());
        Task nextOpenTask = bestScoreTask.getTask();
        return new FutureTask<>(nextOpenTask);
    }

    public TestResultSummary getAllTestResults() {
        return getAllTestResults(false);
    }

    public TestResultSummary getAllTestResults(boolean shouldThrowException) {
        logger.verbose("enter");
        Map<IRenderingEyes, List<Future<TestResultContainer>>> allFutures = new HashMap<>();
        for (IRenderingEyes eyes : allEyes) {
            List<Future<TestResultContainer>> value = eyes.close();
            allFutures.put(eyes, value);
            synchronized (this.eyesToCloseList) {
                if (!this.eyesToCloseList.contains(eyes)) {
                    this.eyesToCloseList.add(eyes);
                }
            }
        }

        logger.verbose("eyesToCloseList.size: " + eyesToCloseList.size());

        notifyAllServices();
        List<TestResultContainer> allResults = new ArrayList<>();
        logger.verbose("trying to call future.get on " + allFutures.size() + " future lists.");
        for (Map.Entry<IRenderingEyes, List<Future<TestResultContainer>>> entry : allFutures.entrySet()) {

            List<Future<TestResultContainer>> value = entry.getValue();
            IRenderingEyes key = entry.getKey();
            logger.verbose("trying to call future.get on " + value.size() + " futures of " + key);
            for (Future<TestResultContainer> future : value) {
                logger.verbose("calling future.get on " + key);
                TestResultContainer obj = null;
                try {
                    obj = future.get(10, TimeUnit.MINUTES);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                    if (shouldThrowException) {
                        throw new Error(e);
                    }
                }
                logger.verbose("got TestResultContainer: " + obj);
                allResults.add(obj);
            }

        }
        stopServices();
        notifyAllServices();
        logger.verbose("exit");
        return new TestResultSummary(allResults);
    }

    public void close(IRenderingEyes eyes) {
        logger.verbose("adding eyes to close list: " + eyes);
        synchronized (this.eyesToCloseList) {
            if (!this.eyesToCloseList.contains(eyes)) {
                this.eyesToCloseList.add(eyes);
            }
        }
        notifyAllServices();
    }

    public synchronized void check(ICheckRGSettings settings, IDebugResourceWriter debugResourceWriter, String script,
                                   IEyesConnector connector, List<Task> taskList, List<Task> openTasks, final RenderListener listener) {

        if (debugResourceWriter == null) {
            debugResourceWriter = this.debugResourceWriter;
        }
        if (debugResourceWriter == null) {
            debugResourceWriter = new NullDebugResourceWriter();
        }
        RenderingTask renderingTask = new RenderingTask(connector, script, settings, taskList, openTasks, this, debugResourceWriter, new RenderingTask.RenderTaskListener() {
            @Override
            public void onRenderSuccess() {
                logger.verbose("enter");
                listener.onRenderSuccess();
                notifyAllServices();
            }

            @Override
            public void onRenderFailed(Exception e) {
                listener.onRenderFailed(e);
            }
        });
        logger.verbose("locking renderingTaskList");
        synchronized (renderingTaskList) {
            this.renderingTaskList.add(renderingTask);
        }
        logger.verbose("releasing renderingTaskList");
        notifyAllServices();
//        logger.verbose("exit");
    }

    private void notifyAllServices() {
//        logger.verbose("enter");
        notifyOpenerService();
        notifyCloserService();
        notifyCheckerService();
        notifyRenderingService();
//        logger.verbose("exit");
    }

    private void notifyRenderingService() {
//        logger.log("trying to notify rendering service");
        synchronized (renderingServiceLock) {
            renderingServiceLock.notify();
        }
//        logger.verbose("renderingLockFree");
    }

    private void notifyCloserService() {
//        logger.log("trying to notify closer service");
        synchronized (closerServiceLock) {
            closerServiceLock.notifyAll();
        }
//        logger.verbose("closerLockFree");
    }

    private void notifyOpenerService() {
//        logger.log("trying to notify opener service");
        synchronized (openerServiceLock) {
            openerServiceLock.notifyAll();
//            logger.verbose("openerLockFree");
        }
    }

    private void notifyCheckerService() {
//        logger.log("trying to notify checker service");
        synchronized (checkerServiceLock) {
            checkerServiceLock.notifyAll();
//            logger.verbose("checkerLockFree");
        }
    }

    public List<CompletableTask> getAllTasksByType(Task.TaskType type) {
        List<CompletableTask> allTasks = new ArrayList<>();
        for (IRenderingEyes eyes : allEyes) {
            for (RunningTest runningTest : eyes.getAllRunningTests()) {
                for (Task task : runningTest.getTaskList()) {
                    if (task.getType() == type) {
                        allTasks.add(task);
                    }
                }
            }
        }
        return allTasks;
    }

    public List<? extends CompletableTask> getAllRenderingTasks() {
        return this.renderingTaskList;
    }

    public void setDebugResourceWriter(IDebugResourceWriter debugResourceWriter) {
        this.debugResourceWriter = debugResourceWriter;
    }

    public IDebugResourceWriter getDebugResourceWriter() {
        return this.debugResourceWriter;
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }
}
