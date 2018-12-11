package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.services.RenderingGridManager;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.model.TestResultSummary;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.Future;

public final class TestRenderingGridService {

    @Test
    public void test() {
        WebDriver webDriver = null;

        RenderingGridManager renderingManager = new RenderingGridManager(3);
        renderingManager.setLogHandler(new StdoutLogHandler(true));

        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo("MichaelBatch"));

        try {
            webDriver = new ChromeDriver();
            webDriver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");
//            webDriver.get("http://applitools-vg-test.surge.sh/test.html");
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
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

        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
            TestResultSummary allTestResults = renderingManager.getAllTestResults();
            System.out.println(allTestResults);
            // End the test.
        }
    }

    @BeforeMethod
    public void Before(ITestContext testContext){
        int x = 0;
    }

    @AfterMethod
    public void After(ITestContext testContext){
        int x = 0;
    }
}