package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.*;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestSVGSites {
    private VisualGridRunner visualGridRunner;

    private String logsPath = System.getenv("APPLITOOLS_LOGS_PATH");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    private String dateTimeString = dateFormat.format(Calendar.getInstance().getTime());

    @BeforeClass
    public void beforeClass() {
        visualGridRunner = new VisualGridRunner(10);
//        visualGridRunner.setLogHandler(new StdoutLogHandler(true));
        FileLogger logHandler = new FileLogger("eyes.log", false, true);
        visualGridRunner.setLogHandler(logHandler);
        visualGridRunner.getLogger().log("enter");
    }

    @DataProvider(name = "dp", parallel = true)
    public static Object[][] dp() {
        return new Object[][]{
//                {"https://carbon.sage.com/components/button-toggle-group"},
//                {"https://google.com"},
//                {"https://facebook.com"},
//                {"https://amazon.com"},
//                {"https://ebay.com"},
//                {"https://twitter.com"},
                {"https://wikipedia.org"},
//                {"https://instagram.com"},
//                {"https://www.target.com/c/blankets-throws/-/N-d6wsb?lnk=ThrowsBlankets%E2%80%9C,tc"},
        };
    }

    private Eyes initEyes(WebDriver webDriver, String testedUrl) {
        Eyes eyes = new Eyes(visualGridRunner);
        BatchInfo batchInfo = new BatchInfo("Top Ten Sites");
        batchInfo.setId("Target2");
        eyes.setBatch(batchInfo);
        eyes.setEnvName("TTS - migel");
        initLogging(testedUrl, eyes);

        Logger logger = eyes.getLogger();
        logger.log("creating WebDriver: " + testedUrl);

        try {
            Configuration configuration = new Configuration();
            configuration.setTestName("Top 10 websites - " + testedUrl);
            configuration.setAppName("Top Ten Sites");
            configuration.setBatch(new BatchInfo("TTS - config batch"));
            configuration.setBranchName("TTS - config branch");
            configuration.setBaselineEnvName("My Other Env Name");
            String environment = "My env name";
            ChromeEmulationInfo emulation = new ChromeEmulationInfo(DeviceName.iPhone_4, ScreenOrientation.PORTRAIT);
            configuration.addBrowser(800, 600, BrowserType.CHROME, environment);
            configuration.addBrowser(700, 500, BrowserType.FIREFOX, environment);
            configuration.addBrowser(700, 500, BrowserType.IE_10, environment);
            configuration.addBrowser(700, 500, BrowserType.IE_11, environment);
            configuration.addBrowser(1600, 1200, BrowserType.CHROME, environment);
            configuration.addBrowser(1200, 800, BrowserType.EDGE, environment);
//            configuration.addDeviceEmulation(emulation);
            logger.log("created configurations for url " + testedUrl);
            //VisualGridEyes.setServerUrl("https://eyes.applitools.com/");
            configuration.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            eyes.setConfiguration(configuration);
            eyes.open(webDriver, "Michael's App", "First Test", new RectangleSize(1200, 800));
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return eyes;
    }

    @Test(dataProvider = "dp")
    public void test(String testedUrl) {
        visualGridRunner.getLogger().log("entering with url " + testedUrl);
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
//            TestResults close = eyes.close(true);
//            Assert.assertNotNull(close);
            eyes.closeAsync();
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
        String path = "." + File.separator+ "sageDebug" + File.separator;
        visualGridRunner.setDebugResourceWriter(new FileDebugResourceWriter(eyes.getLogger(), path,"", ""));
//        FileDebugResourceWriter fileDebugResourceWriter = new FileDebugResourceWriter(visualGridRunner.getLogger(), path, null, null);
//        VisualGridEyes.setDebugResourceWriter(fileDebugResourceWriter);

//        FileLogger eyesLogger = new FileLogger("TopTenSites.log", true, true);
//        eyes.setLogHandler(eyesLogger);
    }

    @AfterMethod
    public void afterMethod(ITestContext testContext) {
        visualGridRunner.getLogger().log("enter");
    }

    @AfterClass
    public void afterClass(ITestContext testContext) {
        TestResultSummary allTestResults = visualGridRunner.getAllTestResults(false);
        for (TestResultContainer allTestResult : allTestResults) {
            System.out.println(allTestResult.toString());
        }
        visualGridRunner.getLogger().log(allTestResults.toString());
    }
}