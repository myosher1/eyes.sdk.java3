package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.model.RenderBrowserInfo;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.model.TestResultContainer;
import com.applitools.eyes.visualGridClient.services.VisualGridManager;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class TestRenderingGridMultiThreadService {

    private VisualGridManager renderingManager;
    private WebDriver webDriver;

    @BeforeMethod
    public void Before(ITestContext testContext) {
        renderingManager = new VisualGridManager(1);
        renderingManager.setLogHandler(new StdoutLogHandler(true));

        webDriver = new ChromeDriver();
        webDriver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");
        //webDriver.get("http://applitools-vg-test.surge.sh/test.html");

        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @Test
    public void test() {

        final Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo("VG-2ThreadBatchThreads"));

        final String baselineEnvName = "";
        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {

                TestThreadMethod("VG-2ThreadBatchC11",
                        new RenderBrowserInfo(800, 600, RenderingConfiguration.BrowserType.CHROME, baselineEnvName),
                        new RenderBrowserInfo(700, 500, RenderingConfiguration.BrowserType.CHROME, baselineEnvName),
                        new RenderBrowserInfo(400, 300, RenderingConfiguration.BrowserType.CHROME, baselineEnvName));
            }
        });

        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                TestThreadMethod("VG-2ThreadBatchC22",
                        new RenderBrowserInfo(840, 680, RenderingConfiguration.BrowserType.CHROME, baselineEnvName),
                        new RenderBrowserInfo(750, 530, RenderingConfiguration.BrowserType.CHROME, baselineEnvName));
            }
        });

        threadA.start();
        threadB.start();

        try {
            threadA.join();
            threadB.join();
        } catch (InterruptedException e) {
            GeneralUtils.logExceptionStackTrace(renderingManager.getLogger(), e);
        }
    }

    private void TestThreadMethod(String batchName, RenderBrowserInfo... browsersInfo) {
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
            List<Future<TestResultContainer>> close = eyes.closeAndReturnResults();
            for (Future<TestResultContainer> future : close) {
                TestResultContainer testResultContainer = future.get();
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