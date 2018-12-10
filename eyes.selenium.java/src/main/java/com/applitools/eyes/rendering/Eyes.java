package com.applitools.eyes.rendering;

import com.applitools.ICheckRGSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.visualGridClient.services.*;
import com.applitools.eyes.visualGridClient.model.*;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class Eyes implements IRenderingEyes {

    private final Logger logger;
    private String serverUrl;
    private RenderingGridManager renderingGridManager;
    private List<RunningTest> testList = new ArrayList<>();
    private final List<RunningTest> testsInCloseProcess = new ArrayList<>();
    private AtomicBoolean isEyesClosed = new AtomicBoolean(false);
    private AtomicBoolean isEyesIssuedOpenTasks = new AtomicBoolean(false);
    private IRenderingEyes.EyesListener listener;
    private AbstractProxySettings proxy;

    private String PROCESS_RESOURCES;
    private JavascriptExecutor jsExecutor;
    private RenderingInfo renderingInfo;
    private IEyesConnector eyesConnector;

    {
        try {
            PROCESS_RESOURCES = GeneralUtils.readToEnd(Eyes.class.getResourceAsStream("/processResources.js"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RunningTest.RunningTestListener testListener = new RunningTest.RunningTestListener() {
        @Override
        public void onTaskComplete(Task task, RunningTest test) {
            switch (task.getType()) {
                case CLOSE:
                    boolean isEyesClosed = true;
                    for (RunningTest runningTest : testList) {
                        isEyesClosed &= runningTest.isTestClose();
                    }
                    Eyes.this.isEyesClosed.set(isEyesClosed);
                    break;
                case OPEN:

            }

            if (Eyes.this.listener != null) {
                Eyes.this.listener.onTaskComplete(task, Eyes.this);
            }
        }

        @Override
        public void onRenderComplete() {
            logger.verbose("enter");
            Eyes.this.listener.onRenderComplete();
            logger.verbose("exit");
        }
    };

    public Eyes(RenderingGridManager renderingGridManager) {
        ArgumentGuard.notNull(renderingGridManager, "renderingGridManager");
        this.renderingGridManager = renderingGridManager;
        this.logger = renderingGridManager.getLogger();
    }

    public void open(WebDriver webDriver, RenderingConfiguration renderingConfiguration) {
        logger.verbose("enter");

        ArgumentGuard.notNull(webDriver, "webDriver");
        ArgumentGuard.notNull(renderingConfiguration, "renderingConfiguration");

        initDriver(webDriver);

        logger.verbose("getting all browsers info...");
        List<RenderingConfiguration.RenderBrowserInfo> browserInfoList = renderingConfiguration.getBrowsersInfo();
        logger.verbose("creating test descriptors for each browser info...");
        for (RenderingConfiguration.RenderBrowserInfo browserInfo : browserInfoList) {
            logger.verbose("creating test descriptor");
            RunningTest test = new RunningTest(this.proxy, createEyesConnector(browserInfo), renderingConfiguration, browserInfo, logger, testListener);
            this.testList.add(test);
        }

        logger.verbose(String.format("opening %d tests...", testList.size()));
        this.renderingGridManager.open(this, renderingInfo);
        logger.verbose("done");
    }

    private IEyesConnector createEyesConnector(RenderingConfiguration.RenderBrowserInfo browserInfo) {
        logger.verbose("creating eyes server connector");
        IEyesConnector eyesConnector = new EyesConnector(browserInfo);

        if (this.renderingInfo == null) {
            logger.verbose("initializing rendering info...");
            this.renderingInfo = eyesConnector.getRenderingInfo();
        }
        eyesConnector.setRenderInfo(this.renderingInfo);


        eyesConnector.setProxy(this.proxy);
        eyesConnector.setLogHandler(this.logger.getLogHandler());
        if (this.serverUrl != null) {
            try {
                eyesConnector.setServerUrl(serverUrl);
            } catch (URISyntaxException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }

        this.eyesConnector = eyesConnector;
        return eyesConnector;
    }

    private void initDriver(WebDriver webDriver) {
        if (webDriver instanceof JavascriptExecutor) {
            this.jsExecutor = (JavascriptExecutor) webDriver;
        }
    }

    public RunningTest getNextTestToOpen() {
        RunningTest currentBestTest = null;
        int currentBestMark = -1;
        for (RunningTest test : testList) {
            if (test.isTestOpen() || !test.hasCheckTask()) continue;
            if (test.getMark() > currentBestMark) {
                currentBestTest = test;
            }
        }
        return currentBestTest;
    }

    public RunningTest getNextTestToClose() {
        RunningTest test = null;
        for (RunningTest runningTest : testList) {
            if (!runningTest.isTestClose() && runningTest.isTestIsReadyToClose() && !this.testsInCloseProcess.contains(runningTest)) {
                test = runningTest;

            }
        }
        synchronized (testsInCloseProcess) {
            this.testsInCloseProcess.add(test);
        }
        return test;
    }

    public List<Future<TestResults>> close() {
        List<Future<TestResults>> futureList = new ArrayList<>();
        for (RunningTest runningTest : testList) {
            if (!runningTest.isTestClose()) {
                futureList.add(runningTest.close());
            }
        }
        this.renderingGridManager.close(this);
        return futureList;
    }

    @Override
    public synchronized RunningTest getNextCheckTask() {

        int bestMark = -1;

        RunningTest bestTest = null;
        for (RunningTest runningTest : testList) {

            Task task = null;

            if (runningTest.getTaskList().isEmpty()) continue;

            task = runningTest.getTaskList().get(0);
            if (!runningTest.isTestOpen() || task.getType() != Task.TaskType.CHECK || !task.isTaskReadyToCheck())
                continue;

            if (bestMark < runningTest.getMark()) {
                bestTest = runningTest;
                bestMark = runningTest.getMark();
            }
        }
        if (bestTest == null) {
            return null;
        }
        return bestTest;
    }

    @Override
    public int getBestScoreForCheck() {
        int bestMark = -1;
        for (RunningTest runningTest : testList) {
            List<Task> taskList = runningTest.getTaskList();
            if (taskList == null || taskList.isEmpty()) {
                continue;
            }
            Task task = taskList.get(0);
            if (!runningTest.isTestOpen() || task.getType() != Task.TaskType.CHECK || !task.isTaskReadyToCheck())
                continue;
            int mark = runningTest.getMark();
            if (bestMark < mark) {
                bestMark = mark;
            }
        }
        return bestMark;
    }

    @Override
    public int getBestScoreForOpen() {
        int bestMark = -1;
        for (RunningTest runningTest : testList) {
            List<Task> taskList = runningTest.getTaskList();
            if (taskList == null || taskList.isEmpty()) {
                continue;
            }
            Task task = taskList.get(0);
            if (runningTest.isTestOpen() || task.getType() != Task.TaskType.OPEN)
                continue;
            int mark = runningTest.getMark();
            if (bestMark < mark) {
                bestMark = mark;
            }
        }
        return bestMark;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public boolean isEyesClosed() {
        boolean isEyesClosed = true;
        for (RunningTest runningTest : testList) {
            isEyesClosed = isEyesClosed && runningTest.isTestClose();
        }
        return isEyesClosed;
    }

    public void setListener(EyesListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the proxy settings to be used by the rest client.
     *
     * @param abstractProxySettings The proxy settings to be used by the rest client.
     *                              If {@code null} then no proxy is set.
     */
    public void setProxy(AbstractProxySettings abstractProxySettings) {
        this.proxy = abstractProxySettings;
    }

    public void check(ICheckRGSettings settings) {

        addOpenTaskToAllRunningTest();

        List<Task> taskList = new ArrayList<>();

        String domCaptureScript = "var callback = arguments[arguments.length - 1]; return (" + PROCESS_RESOURCES + ")().then(JSON.stringify).then(callback, function(err) {callback(err.stack || err.toString())})";

        String scriptResult = (String) this.jsExecutor.executeAsyncScript(domCaptureScript);

        for (final RunningTest test : testList) {
            Task checkTask = test.check(settings);
            taskList.add(checkTask);

        }
        this.renderingGridManager.check(settings, scriptResult, this.eyesConnector, taskList, new RenderingGridManager.RenderListener() {
            @Override
            public void onRenderSuccess() {

            }

            @Override
            public void onRenderFailed(Exception e) {

            }
        });
    }

    private synchronized void addOpenTaskToAllRunningTest() {
        if (!this.isEyesIssuedOpenTasks.get()) {
            for (RunningTest runningTest : testList) {
                runningTest.open();
            }
            logger.verbose("calling addOpenTaskToAllRunningTest.open");
            this.isEyesIssuedOpenTasks.set(true);
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
