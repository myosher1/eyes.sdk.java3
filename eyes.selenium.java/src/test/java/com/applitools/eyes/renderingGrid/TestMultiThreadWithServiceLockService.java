package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.model.CompletableTask;
import com.applitools.eyes.visualGridClient.model.RenderBrowserInfo;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.model.TestResultContainer;
import com.applitools.eyes.visualGridClient.services.VisualGridManager;
import com.applitools.eyes.visualGridClient.services.Task;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class TestMultiThreadWithServiceLockService {

    private VisualGridManager renderingManager;
    private WebDriver webDriver;
    private final Object openerLock = new Object();
    private final Object checkerLock = new Object();
    private final Object closerLock = new Object();
    private final Object renderLock = new Object();
    private final Object threadALock = new Object();
    private final Object threadBLock = new Object();
    private int concurrentOpenSessions;

    @BeforeMethod
    public void Before(ITestContext testContext) {
        concurrentOpenSessions = 3;
        renderingManager = new VisualGridManager(concurrentOpenSessions, openerLock, checkerLock, closerLock, renderLock);
        renderingManager.setLogHandler(new StdoutLogHandler(true));

        webDriver = new ChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");
        //webDriver.get("http://applitools-vg-test.surge.sh/test.html");

//        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception {

        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {
                TestThreadMethod("MichaelBatchC11", threadALock,
                        new RenderBrowserInfo(800, 600, RenderingConfiguration.BrowserType.CHROME),
                        new RenderBrowserInfo(700, 500, RenderingConfiguration.BrowserType.CHROME),
                        new RenderBrowserInfo(400, 300, RenderingConfiguration.BrowserType.CHROME));
            }
        }, "ThreadA");

        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                TestThreadMethod("MichaelBatchC22", threadBLock,
                        new RenderBrowserInfo(840, 680, RenderingConfiguration.BrowserType.CHROME),
                        new RenderBrowserInfo(750, 530, RenderingConfiguration.BrowserType.CHROME));
            }
        }, "ThreadB");

        //Start the thread and wait for all tasks to be created
        threadA.start();
        synchronized (threadALock) {
            threadALock.wait();
        }

        //Start the thread and wait for all tasks to be created
        threadB.start();
        synchronized (threadBLock) {
            threadBLock.wait();
        }


        //Get all tasks
        List<CompletableTask> allOpenTasks = renderingManager.getAllTasksByType(Task.TaskType.OPEN);
        List<CompletableTask> allCheckTasks = renderingManager.getAllTasksByType(Task.TaskType.CHECK);
        List<CompletableTask> allCloseTasks = renderingManager.getAllTasksByType(Task.TaskType.CLOSE);
        List<CompletableTask> allRenderingTasks = (List<CompletableTask>) renderingManager.getAllRenderingTasks();


        //Test that all tests are not opened yet.
        checkAllTaskAreNotComplete(allOpenTasks, "OPEN");

        //Test that all tests are not checked yet.
        checkAllTaskAreNotComplete(allCheckTasks, "CHECK");

        //Test that all tests are not closed yet.
        checkAllTaskAreNotComplete(allCloseTasks, "CLOSE");

        //Test that all tests are not rendered yet.
        checkAllTaskAreNotComplete(allRenderingTasks, "RENDER");

        //Start Rendering
        synchronized (renderLock) {
            renderLock.notify();
            //Wait for 2 renders to be submitted
            renderLock.wait();
            renderLock.wait();
        }

        int openedTaskCount = startServiceAndCountCompletedTasks(allOpenTasks, openerLock);

        Assert.assertEquals(openedTaskCount, concurrentOpenSessions, "Completed opened tasks are not equal to concurrency");

        int closeTasksCount = startServiceAndCountCompletedTasks(allCloseTasks, closerLock);

        Assert.assertEquals(closeTasksCount, 0, "Close tasks are completed before check tasks");

        int checkedTasksCount = startServiceAndCountCompletedTasks(allCheckTasks, checkerLock);

        Assert.assertEquals(checkedTasksCount, concurrentOpenSessions, "Completed checked tasks are not equal to concurrency");

        pauseAllServices();

        closeTasksCount = startServiceAndCountCompletedTasks(allCloseTasks, closerLock);

        Assert.assertEquals(closeTasksCount, concurrentOpenSessions);

        openedTaskCount = startServiceAndCountCompletedTasks(allOpenTasks, openerLock);

        Assert.assertEquals(openedTaskCount, 5, "Completed opened tasks are not equal to concurrency");

        checkedTasksCount = startServiceAndCountCompletedTasks(allCheckTasks, checkerLock);

        Assert.assertEquals(checkedTasksCount, 5, "Completed checked tasks are not equal to concurrency");

        closeTasksCount = startServiceAndCountCompletedTasks(allCloseTasks, closerLock);

        Assert.assertEquals(closeTasksCount, 5, "Close tasks are completed before check tasks");

    }

    private void pauseAllServices() {
        this.renderingManager.pauseAllService();
    }

    private int startServiceAndCountCompletedTasks(List<CompletableTask> allOpenTasks, Object debugLock) throws InterruptedException {

        //Start Opener and wait for 5 open tasks
        synchronized (debugLock) {
            debugLock.notify();
        }

        for (int i = 0; i < 5; i++) {
            synchronized (debugLock) {
                debugLock.wait(1000);
            }
        }

        int completedTasks = 0;
        for (CompletableTask openTask : allOpenTasks) {
            if (openTask.getIsTaskComplete()) completedTasks++;
        }

        return completedTasks;
    }

    private void checkAllTaskAreNotComplete(List<CompletableTask> allOpenTasks, String type) throws Exception {
        for (CompletableTask task : allOpenTasks) {
            Assert.assertFalse(task.getIsTaskComplete(), type + " Task is complete without notify openerService");
        }
    }

    private void TestThreadMethod(String batchName, Object lock, RenderBrowserInfo... browsersInfo) {
        try {
            Eyes eyes = new Eyes(renderingManager);
            eyes.setBatch(new BatchInfo(batchName));
            RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
            renderingConfiguration.setTestName("Open Concurrency with Batch 3");
            renderingConfiguration.setAppName("RenderingGridIntegration");
            renderingConfiguration.addBrowsers(browsersInfo);
            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888));
            eyes.open(webDriver, renderingConfiguration);
            eyes.check(Target.window().withName("test").sendDom(false));
            List<Future<TestResultContainer>> close = eyes.closeAndReturnResults();
            synchronized (lock) {
                lock.notify();
            }
            for (Future<TestResultContainer> future : close) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            GeneralUtils.logExceptionStackTrace(renderingManager.getLogger(), e);
        }
    }

    @AfterMethod
    public void After(ITestContext testContext) {
        renderingManager.getAllTestResults();
        renderingManager.getLogger().log(renderingManager.getAllTestResults().toString());
        webDriver.quit();
    }
}