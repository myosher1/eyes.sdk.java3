package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.config.SeleniumConfiguration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgridclient.model.EmulationDevice;
import com.applitools.eyes.visualgridclient.model.EmulationInfo;
import com.applitools.eyes.visualgridclient.model.ScreenOrientation;
import com.applitools.eyes.visualgridclient.model.TestResultSummary;
import com.applitools.eyes.visualgridclient.services.EyesRunner;
import com.applitools.eyes.visualgridclient.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestVG_EmulationDevices {
    private static final SeleniumConfiguration.BrowserType CHROME = SeleniumConfiguration.BrowserType.CHROME;
    private static final SeleniumConfiguration.BrowserType FIREFOX = SeleniumConfiguration.BrowserType.FIREFOX;
    private static final EmulationInfo.DeviceName IPHONE4 = EmulationInfo.DeviceName.IPHONE4;
    private static final ScreenOrientation PORTRAIT = ScreenOrientation.PORTRAIT;
    private EyesRunner visualGridRunner;

    private String logsPath = System.getenv("APPLITOOLS_LOGS_PATH");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    private String dateTimeString = dateFormat.format(Calendar.getInstance().getTime());
    private BatchInfo batchInfo;
    private FileLogger logHandler;

    @BeforeClass
    public void beforeClass() {
        visualGridRunner = new VisualGridRunner(10);
//        visualGridRunner.setLogHandler(new StdoutLogHandler(true));
        logHandler = new FileLogger("eyes3.log", false, true);
        visualGridRunner.setLogHandler(logHandler);
        visualGridRunner.getLogger().log("enter");
        batchInfo = new BatchInfo("hello world batch");
        batchInfo.setId("hw_VG_Batch_ID");
        visualGridRunner.setServerUrl("https://eyes.applitools.com/");
    }

    @DataProvider(name = "dp", parallel = true)
    public static Object[][] dp() {
        return new Object[][]{
//                {"https://todomvc-app-for-testing.surge.sh/"},
                {"https://google.com"},
                {"https://facebook.com"},
                {"https://youtube.com"},
                {"https://amazon.com"},
//                {"https://ebay.com"},
//                {"https://twitter.com"},
                {"https://wikipedia.org"},
                {"https://instagram.com"},
                {"https://www.target.com/c/blankets-throws/-/N-d6wsb?lnk=ThrowsBlankets%E2%80%9C,tc"},
        };
    }

    private Eyes initEyes(WebDriver webDriver, String testedUrl) {
        Eyes eyes = new Eyes(visualGridRunner);
//        Eyes eyes = new Eyes();
        eyes.setBatch(batchInfo);
        eyes.setLogHandler(logHandler);
        Logger logger = eyes.getLogger();
        logger.log("creating WebDriver: " + testedUrl);

        try {
            SeleniumConfiguration seleniumConfiguration = new SeleniumConfiguration();
            seleniumConfiguration.setTestName("VG hello world - " + testedUrl);
            seleniumConfiguration.setAppName("VG hello world");
            seleniumConfiguration.setBatch(batchInfo);
            seleniumConfiguration.setBaselineEnvName("michael");
            EmulationDevice emulationDevice = new EmulationDevice(300, 400, 1f, true, ScreenOrientation.LANDSCAPE);
            seleniumConfiguration.addDeviceEmulation(emulationDevice);
            seleniumConfiguration.addDeviceEmulation(new EmulationInfo(IPHONE4, PORTRAIT));
            EmulationInfo emulation = new EmulationInfo(EmulationInfo.DeviceName.IPHONE4, ScreenOrientation.PORTRAIT);
            seleniumConfiguration.addDeviceEmulation(emulation);
            seleniumConfiguration.addDeviceEmulation(new EmulationInfo(EmulationInfo.DeviceName.iPhone_X, ScreenOrientation.PORTRAIT));
            seleniumConfiguration.addDeviceEmulation(new EmulationInfo(EmulationInfo.DeviceName.Nexus_10, ScreenOrientation.LANDSCAPE));
            logger.log("created configurations for url " + testedUrl);
//            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            eyes.setConfiguration(seleniumConfiguration);
            eyes.open(webDriver, "Michael's App name", " michael test name");
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
            TestResults close = eyes.close(true);
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

    @AfterMethod
    public void afterMethod(ITestContext testContext) {
        visualGridRunner.getLogger().log("enter");
    }

    @AfterClass
    public void afterClass(ITestContext testContext) {
        TestResultSummary allTestResults = visualGridRunner.getAllTestResults();
        visualGridRunner.getLogger().log(allTestResults.toString());
    }
}
