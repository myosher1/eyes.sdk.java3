package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualGridClient.model.*;
import com.applitools.eyes.visualGridClient.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestTopSites {
    private VisualGridRunner renderingManager;

    private String logsPath = System.getenv("APPLITOOLS_LOGS_PATH");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    private String dateTimeString = dateFormat.format(Calendar.getInstance().getTime());

    @BeforeClass
    public void beforeClass() {
        renderingManager = new VisualGridRunner(40);
        renderingManager.setLogHandler(new StdoutLogHandler(true));
        FileLogger logHandler = new FileLogger("VisualGridEyes.log", false, true);
        renderingManager.setLogHandler(logHandler);
        renderingManager.getLogger().log("enter");
        renderingManager.setServerUrl("https://VisualGridEyes.applitools.com/");
    }

    @DataProvider(name = "dp", parallel = true)
    public static Object[][] dp() {
        return new Object[][]{
                {"https://google.com"},
                {"https://facebook.com"},
                {"https://youtube.com"},
                {"https://amazon.com"},
                {"https://yahoo.com"},
//                {"https://ebay.com"},
                {"https://twitter.com"},
                {"https://wikipedia.org"},
                {"https://instagram.com"},
                {"https://www.target.com/c/blankets-throws/-/N-d6wsb?lnk=ThrowsBlankets%E2%80%9C,tc"},
        };
    }

    private Eyes initEyes(WebDriver webDriver, String testedUrl) {
        Eyes eyes = new Eyes(renderingManager);
        BatchInfo batchInfo = new BatchInfo("Top Ten Sites");
        batchInfo.setId("Target");
        eyes.setBatch(batchInfo);

        initLogging(testedUrl, eyes);

        Logger logger = eyes.getLogger();
        logger.log("creating WebDriver: " + testedUrl);

        try {
            RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
            renderingConfiguration.setTestName("Top 10 websites - " + testedUrl);
            renderingConfiguration.setAppName("Top Ten Sites");
            String environment = "";
            EmulationBaseInfo emulation = new EmulationInfo(EmulationInfo.DeviceName.IPHONE4, ScreenOrientation.PORTRAIT);
            renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.CHROME, environment, emulation);
            renderingConfiguration.addBrowser(700, 500, RenderingConfiguration.BrowserType.CHROME, environment);
            renderingConfiguration.addBrowser(1200, 800, RenderingConfiguration.BrowserType.CHROME, environment);
            renderingConfiguration.addBrowser(1600, 1200, RenderingConfiguration.BrowserType.CHROME, environment);
            logger.log("created configurations for url " + testedUrl);
//            VisualGridEyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            //VisualGridEyes.setServerUrl("https://eyes.applitools.com/");
            eyes.open(webDriver, renderingConfiguration);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return eyes;
    }

    @Test(dataProvider = "dp")
    public void test(String testedUrl) {
        renderingManager.getLogger().log("entering with url " + testedUrl);
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(testedUrl);
        Eyes eyes = (Eyes) initEyes(webDriver, testedUrl);
        Logger logger = eyes.getLogger();
        logger.log("navigated to " + testedUrl);

        try {
            //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
            logger.log("running check for url " + testedUrl);
            try {
                eyes.check(Target.window().withName("Step1 - " + testedUrl).sendDom(true));
                eyes.check(Target.window().fully(false).withName("Step2 - " + testedUrl).sendDom(true));
            } catch (Exception e) {
                e.printStackTrace();
            }
            eyes.getLogger().log("calling VisualGridEyes.close() for url " + testedUrl);
//            VisualGridEyes.close();
            TestResults close = eyes.close();
            Assert.assertNotNull(close);
            logger.log("end of `try` block for url " + testedUrl);

        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        } finally {
            logger.log("closing WebDriver for url " + testedUrl);
            webDriver.quit();
            logger.log("url " + testedUrl + " - done with browser.");
            // End the test.
        }
    }

    private void initLogging(String testedUrl, Eyes eyes) {
        String testName = testedUrl.substring(8);
        String path = logsPath + File.separator + "java" + File.separator + "TestTopSites_" + dateTimeString;
//        FileDebugResourceWriter fileDebugResourceWriter = new FileDebugResourceWriter(renderingManager.getLogger(), path, null, null);
//        VisualGridEyes.setDebugResourceWriter(fileDebugResourceWriter);

        //FileLogger eyesLogger = new FileLogger("TopTenSites.log", true, true);
        //VisualGridEyes.setLogHandler(eyesLogger);
    }

    @AfterMethod
    public void afterMethod(ITestContext testContext) {
        renderingManager.getLogger().log("enter");
    }

    @AfterClass
    public void afterClass(ITestContext testContext) {
        TestResultSummary allTestResults = renderingManager.getAllTestResults();
        renderingManager.getLogger().log(allTestResults.toString());
    }
}
