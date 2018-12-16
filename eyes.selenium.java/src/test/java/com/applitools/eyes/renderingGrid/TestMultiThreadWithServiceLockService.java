package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.model.CompletableTask;
import com.applitools.eyes.visualGridClient.model.RenderBrowserInfo;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.services.RenderingGridManager;
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

    private RenderingGridManager renderingManager;
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
        renderingManager = new RenderingGridManager(concurrentOpenSessions, openerLock, checkerLock, closerLock, renderLock);
        renderingManager.setLogHandler(new StdoutLogHandler(true));

        webDriver = new ChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");
        //webDriver.get("http://applitools-vg-test.surge.sh/test.html");

        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception {

        final Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo("MichaelBatchThreads"));

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
        List<CompletableTask> allOpenTasks = renderingManager.getAllTaksByType(Task.TaskType.OPEN);
        List<CompletableTask> allCheckTasks = renderingManager.getAllTaksByType(Task.TaskType.CHECK);
        List<CompletableTask> allCloseTasks = renderingManager.getAllTaksByType(Task.TaskType.CLOSE);
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
        int openedTask = startServiceAndCountCompletedTasks(allOpenTasks, openerLock);

        Assert.assertEquals(openedTask, concurrentOpenSessions, "Completed opened tasks are not equal to concurrency");

        int checkedTasks = startServiceAndCountCompletedTasks(allCheckTasks, checkerLock);

        Assert.assertEquals(checkedTasks, concurrentOpenSessions, "Completed checked tasks are not equal to concurrency");

        closeAllServices();

        if (openedTask > this.concurrentOpenSessions) throw new Exception("More Open sessions then concurrency");


        try {
            threadA.join();
            threadB.join();
        } catch (InterruptedException e) {
            GeneralUtils.logExceptionStackTrace(renderingManager.getLogger(), e);
        }


    }

    private void closeAllServices() {
        this.renderingManager.pauseAllService();
    }

    private int startServiceAndCountCompletedTasks(List<CompletableTask> allOpenTasks, Object debugLock) throws InterruptedException {
        //Start Opener and wait for 5 open tasks
        synchronized (debugLock) {
            debugLock.notify();
            debugLock.wait(1000);
            debugLock.wait(1000);
            debugLock.wait(1000);
            debugLock.wait(1000);
            debugLock.wait(1000);
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
            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            eyes.open(webDriver, renderingConfiguration);
            eyes.check(Target.window().withName("test").sendDom(false));
            List<Future<TestResults>> close = eyes.close();
            synchronized (lock) {
                lock.notify();
            }
            for (Future<TestResults> future : close) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            GeneralUtils.logExceptionStackTrace(renderingManager.getLogger(), e);
        }
    }

    @AfterMethod
    public void After(ITestContext testContext) {
        renderingManager.getLogger().log(renderingManager.getAllTestResults().toString());
        webDriver.quit();
    }
}