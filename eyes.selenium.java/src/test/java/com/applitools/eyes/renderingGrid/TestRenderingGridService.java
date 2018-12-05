package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.Logger;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.fluent.CheckRGSettings;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.data.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.RenderingGridManager;
import com.applitools.eyes.visualGridClient.data.TestResultSummery;
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
        RenderingGridManager renderingManager = null;
        Logger logger = null;
        try {
            webDriver = new ChromeDriver();
            webDriver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");
            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
//            webDriver.get("http://applitools-vg-test.surge.sh/test.html");
            RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
            logger = new Logger();
            logger.setLogHandler(new StdoutLogHandler(true));
            renderingConfiguration.setTestName("Open Concurrency");
            renderingConfiguration.setAppName("RenderingGridIntegration");
            renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.CHROME);
//            renderingConfiguration.addBrowser(400, 300, RenderingConfiguration.BrowserType.FIREFOX);
            renderingManager = new RenderingGridManager(3, logger);
            Eyes eyes = new Eyes(renderingManager, renderingConfiguration, logger);
//            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            eyes.setLogHandler(new StdoutLogHandler(true));
            eyes.setServerUrl("https://eyes.applitools.com/");
            eyes.open(webDriver);
            //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
            eyes.check(Target.window().fully(true).sendDom(false));
            List<Future<TestResults>> close = eyes.close();
            for (Future<TestResults> future : close) {
                future.get();
            }

        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
            TestResultSummery allTestResults = renderingManager.getAllTestResults();
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