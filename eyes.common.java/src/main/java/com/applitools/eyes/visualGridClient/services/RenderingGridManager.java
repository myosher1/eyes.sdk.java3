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
    private Map<String, Future<Boolean>> putResourceCache = Collections.synchronizedMap(new HashMap<String, Future<Boolean>>());

    private final Logger logger = new Logger();

    private final Object openerServiceConcurrencyLock = new Object();
    private final Object openerServiceLock = new Object();
    private final Object checkerServiceLock = new Object();
    private final Object closerServiceLock = new Object();
    private final Object renderingServiceLock = new Object();
    private final List<RenderingTask> renderingTaskList = Collections.synchronizedList(new ArrayList<RenderingTask>());

    private RenderingInfo renderingInfo;
    private IDebugResourceWriter debugResourceWriter;

    public void pauseAllService() {
        eyesOpenerService.debugPauseService();
        eyesCloserService.debugPauseService();
        eyesCheckerService.debugPauseService();
        renderingGridService.debugPauseService();
    }

    public interface RenderListener {

        void onRenderSuccess();

        void onRenderFailed(Exception e);

    }

    private IRenderingEyes.EyesListener eyesListener = new IRenderingEyes.EyesListener() {
        @Override
        public void onTaskComplete(Task task, IRenderingEyes eyes) {
            Task.TaskType type = task.getType();
            switch (type) {

                case OPEN:
                    synchronized (eyesToOpenList) {
                        logger.verbose("removing task " + task.toString());
                        eyesToOpenList.remove(eyes);
                    }
                    break;
                case CLOSE:
                    logger.verbose("Task Close.");
                    eyesOpenerService.decrementConcurrency();
                    synchronized (eyesToCloseList) {
                        if (eyes.isEyesClosed()) {
                            eyesToCloseList.remove(eyes);
                        }
                    }
                    synchronized (openerServiceConcurrencyLock) {
                        openerServiceConcurrencyLock.notify();
                    }
                    break;
                case ABORT:
                    logger.verbose("Task Abort.");
                    synchronized (eyesToCloseList) {
                        if (eyes.isEyesClosed()) {
                            eyesToCloseList.remove(eyes);
                        }
                    }
                    synchronized (openerServiceConcurrencyLock) {
                        openerServiceConcurrencyLock.notify();
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

    public RenderingGridManager(int concurrentOpenSessions) {
        this(concurrentOpenSessions, null, null, null, null);

    }

    public RenderingGridManager(int concurrentOpenSessions,
                                Object openerServiceDebugLock,
                                Object checkerServiceDebugLock,
                                Object closerServiceDebugLock,
                                Object renderServiceDebugLock) {

        this.concurrentOpenSessions = concurrentOpenSessions;
        this.openerServiceDebugLock = openerServiceDebugLock;
        this.checkerServiceDebugLock = checkerServiceDebugLock;
        this.closerServiceDebugLock = closerServiceDebugLock;
        this.renderServiceDebugLock = renderServiceDebugLock;
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
    }

    public Logger getLogger() {
        return logger;
    }

    public Map<String, IResourceFuture> getCachedResources() {
        return cachedResources;
    }

    public Map<String, Future<Boolean>> getPutResourceCache() {
        return putResourceCache;
    }

    public RenderingInfo getRenderingInfo() {
        return renderingInfo;
    }

    private void init() {
        this.eyesOpenerService = new OpenerService("eyesOpenerService", servicesGroup, logger, this.concurrentOpenSessions, openerServiceConcurrencyLock, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResults> getNextTask(@SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker, Task.TaskListener taskListener) {

                return getOrWaitForTask(openerServiceLock, tasker, taskListener, "eyesOpenerService");
            }

        }, openerServiceDebugLock, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResults> getOrWaitForNextTask(Task.TaskListener taskListener) {
                return getNextTestToOpen();
            }
        });

        this.eyesCloserService = new EyesService("eyesCloserService", servicesGroup, logger, concurrentOpenSessions, closerServiceDebugLock, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResults> getNextTask(@SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker, Task.TaskListener taskListener) {

                return getOrWaitForTask(closerServiceLock, tasker, taskListener, "eyesCloserService");
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResults> getOrWaitForNextTask(Task.TaskListener taskListener) {
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
                                renderingServiceLock.wait(2000);
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

        this.eyesCheckerService = new EyesService("eyesCheckerService", servicesGroup, logger, this.concurrentOpenSessions, checkerServiceDebugLock, new EyesService.EyesServiceListener() {
            @Override
            public FutureTask<TestResults> getNextTask(@SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker, Task.TaskListener taskListener) {

                return getOrWaitForTask(checkerServiceLock, tasker, taskListener, "eyesCheckerService");
            }

        }, new EyesService.Tasker() {
            @Override
            public FutureTask<TestResults> getOrWaitForNextTask(Task.TaskListener taskListener) {
                return getNextCheckTask(taskListener);
            }
        });
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    private FutureTask<TestResults> getOrWaitForTask(Object lock, @SuppressWarnings("SpellCheckingInspection") EyesService.Tasker tasker, Task.TaskListener taskListener, String serviceName) {
        FutureTask<TestResults> nextTestToOpen = tasker.getOrWaitForNextTask(taskListener);
        if (nextTestToOpen == null) {
            synchronized (lock) {
                try {
                    nextTestToOpen = tasker.getOrWaitForNextTask(taskListener);
                    if (nextTestToOpen == null) {

                        logger.log(serviceName + " going to sleep");
                        lock.wait(2000);
                        logger.log(serviceName + " woke up");
                        nextTestToOpen = tasker.getOrWaitForNextTask(taskListener);
                    }

                } catch (InterruptedException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }
        }
        return nextTestToOpen;
    }

    private FutureTask<TestResults> getNextCheckTask(Task.TaskListener listener) {
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

        //Service listener
        task.addListener(listener);
        return new FutureTask<>(task);
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
        logger.verbose("RenderingGridManager.open()");
        if (this.renderingInfo == null) {
            this.renderingInfo = renderingInfo;
        }
        synchronized (eyesToOpenList) {
            eyesToOpenList.add(eyes);
            allEyes.add(eyes);
        }
        eyes.setListener(this.eyesListener);
        eyes.setListener(eyesListener);
        notifyAllServices();
        logger.log("concurrencyLock.notify()");
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
        this.renderingGridService.stopService();
        this.eyesCheckerService.stopService();
    }

    private synchronized FutureTask<TestResults> getNextTestToOpen() {
        ScoreTask bestScoreTask = null;
        int bestMark = -1;
        logger.verbose("looking for best test in a list of " + allEyes.size());
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
            logger.verbose("no test found.");
            return null;
        }

        logger.verbose("found test with mark " + bestMark);
        logger.verbose("calling getNextOpenTaskAndRemove on " + bestScoreTask.toString());
        Task nextOpenTask = bestScoreTask.getTask();
        return new FutureTask<>(nextOpenTask);
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

    public synchronized void check(ICheckRGSettings settings, String script, IEyesConnector connector, List<Task> taskList, List<Task> openTasks, final RenderListener listener) {

        RenderingTask renderingTask = new RenderingTask(connector, script, settings, taskList, openTasks, this, new RenderingTask.RenderTaskListener() {
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
        synchronized (renderingTaskList) {
            this.renderingTaskList.add(renderingTask);
        }
        notifyAllServices();
    }

    private void notifyAllServices() {
        logger.verbose("enter");
        synchronized (openerServiceLock) {
            openerServiceLock.notify();
        }
        logger.verbose("openerLockFree");
        synchronized (closerServiceLock) {
            closerServiceLock.notify();
        }
        logger.verbose("closerLockFree");
        synchronized (checkerServiceLock) {
            checkerServiceLock.notify();
        }
        logger.verbose("checkerLockFree");
        synchronized (renderingServiceLock) {
            renderingServiceLock.notify();
        }
        logger.verbose("renderingLockFree");
        logger.verbose("exit");
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

}
