package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.services.RenderingGridManager;
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

    private RenderingGridManager renderingManager;
    private WebDriver webDriver;

    @Test
    public void test() {
        webDriver = new ChromeDriver();

        renderingManager = new RenderingGridManager(1);
        renderingManager.setLogHandler(new StdoutLogHandler(true));

        final Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo("MichaelBatch"));

        webDriver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");
//            webDriver.get("http://applitools-vg-test.surge.sh/test.html");
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Eyes eyes = new Eyes(renderingManager);
                    eyes.setBatch(new BatchInfo("MichaelBatchC1"));
                    RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
                    renderingConfiguration.setTestName("Open Concurrency with Batch 2");
                    renderingConfiguration.setAppName("RenderingGridIntegration");
                    renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.CHROME);
                    renderingConfiguration.addBrowser(700, 500, RenderingConfiguration.BrowserType.CHROME);
                    renderingConfiguration.addBrowser(400, 300, RenderingConfiguration.BrowserType.CHROME);
                    eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
                    //eyes.setServerUrl("https://eyes.applitools.com/");
                    eyes.open(webDriver, renderingConfiguration);
                    //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
                    eyes.check(Target.window().withName("test").sendDom(false));
                    List<Future<TestResults>> close = eyes.close();
                    for (Future<TestResults> future : close) {
                        future.get();
                    }

                } catch (
                        InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread threadB = new Thread(new Runnable() {
            @Override
            public void run() {
                Eyes eyes = new Eyes(renderingManager);
                eyes.setBatch(new BatchInfo("MichaelBatchC2"));
                RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
                renderingConfiguration.setTestName("Open Concurrency with Batch 2");
                renderingConfiguration.setAppName("RenderingGridIntegration");
                renderingConfiguration.addBrowser(840, 680, RenderingConfiguration.BrowserType.CHROME);
                renderingConfiguration.addBrowser(750, 530, RenderingConfiguration.BrowserType.CHROME);
                eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
                //eyes.setServerUrl("https://eyes.applitools.com/");
                eyes.open(webDriver, renderingConfiguration);
                //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
                eyes.check(Target.window().withName("test").sendDom(false));
                eyes.close();

            }
        });
        threadA.start();
        threadB.start();

        try {
            threadA.join();
            threadB.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @BeforeMethod
    public void Before(ITestContext testContext) {
        int x = 0;
    }

    @AfterMethod
    public void After(ITestContext testContext) {
        System.out.println(renderingManager.getAllTestResults());
        webDriver.quit();
    }
}