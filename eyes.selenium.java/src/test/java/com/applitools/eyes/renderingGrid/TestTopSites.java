package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.model.FileDebugResourceWriter;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.model.TestResultContainer;
import com.applitools.eyes.visualGridClient.model.TestResultSummary;
import com.applitools.eyes.visualGridClient.services.RenderingGridManager;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;

public class TestTopSites {
    private RenderingGridManager renderingManager;

    private String logsPath = System.getenv("APPLITOOLS_LOGS_PATH");

    @BeforeClass
    public void beforeClass() {
        renderingManager = new RenderingGridManager(10);
        //renderingManager.setLogHandler(new StdoutLogHandler(true));
        FileLogger logHandler = new FileLogger("eyes.log", false, true);
        renderingManager.setLogHandler(logHandler);
        renderingManager.getLogger().log("enter");
//        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @DataProvider(name = "dp", parallel = true)
    public static Object[][] dp() {
        return new Object[][]{
//                {"https://google.com"},
                {"https://facebook.com"},
                {"https://youtube.com"},
//                {"https://amazon.com"},
//                {"https://yahoo.com"},
                {"https://ebay.com"},
                {"https://twitter.com"},
//                {"https://wikipedia.org"},
//                {"https://instagram.com"},
//                {"https://reddit.com"},
        };
    }

    @Test(dataProvider = "dp")
    public void test(String testedUrl) {
        renderingManager.getLogger().log("entering with url " + testedUrl);

        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo(testedUrl));

        initLogging(testedUrl, eyes);

        eyes.getLogger().log("creating WebDriver: " + testedUrl);
        WebDriver webDriver = new ChromeDriver();
        eyes.getLogger().log("navigating to " + testedUrl);
        webDriver.get(testedUrl);

        try {
            RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
            renderingConfiguration.setTestName("Top 10 websites - " + testedUrl);
            renderingConfiguration.setAppName("RenderingGridIntegration");
            renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.CHROME);
            renderingConfiguration.addBrowser(700, 500, RenderingConfiguration.BrowserType.CHROME);
            renderingConfiguration.addBrowser(1200, 800, RenderingConfiguration.BrowserType.CHROME);
            renderingConfiguration.addBrowser(1600, 1200, RenderingConfiguration.BrowserType.CHROME);
            eyes.getLogger().log("created configurations for url " + testedUrl);
            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            //eyes.setServerUrl("https://eyes.applitools.com/");
            eyes.open(webDriver, renderingConfiguration);
            //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
            eyes.getLogger().log("running check for url " + testedUrl);
            try {
                eyes.check(Target.window().withName("Step1 - "+testedUrl).sendDom(false));
                eyes.check(Target.window().fully(false).withName("Step2 - " + testedUrl).sendDom(false));
            } catch (Exception e) {
                e.printStackTrace();
            }
            eyes.getLogger().log("calling eyes.close() for url " + testedUrl);
            List<Future<TestResultContainer>> close = eyes.close();
//            for (Future<TestResultContainer> future : close) {
//                eyes.getLogger().log("calling future.get() for url " + testedUrl);
//                future.get();
//            }
            eyes.getLogger().log("end of `try` block for url " + testedUrl);

        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        } finally {
            eyes.getLogger().log("closing WebDriver for url " + testedUrl);
            webDriver.quit();

            // End the test.
        }
    }

    private void initLogging(String testedUrl, Eyes eyes) {
        String testName = testedUrl.substring(8);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
        String path = logsPath + File.separator + "java" + File.separator + "TestTopSites_" + testName + dateFormat.format(Calendar.getInstance().getTime());

        FileDebugResourceWriter fileDebugResourceWriter = new FileDebugResourceWriter(renderingManager.getLogger(), path, null, null);
        eyes.setDebugResourceWriter(fileDebugResourceWriter);

        //FileLogger eyesLogger = new FileLogger("TopTenSites.log", true, true);
        //eyes.setLogHandler(eyesLogger);
    }

    @AfterMethod
    public void afterMethod(ITestContext testContext) {
        renderingManager.getLogger().log("enter");
    }

    @AfterClass
    public void afterClass(ITestContext testContext){
        TestResultSummary allTestResults = renderingManager.getAllTestResults();
        renderingManager.getLogger().log(allTestResults.toString());
    }
}
