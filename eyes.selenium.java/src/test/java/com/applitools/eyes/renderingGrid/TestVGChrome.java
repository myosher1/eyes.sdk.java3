package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.config.SeleniumConfiguration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgridclient.model.EmulationInfo;
import com.applitools.eyes.visualgridclient.model.ScreenOrientation;
import com.applitools.eyes.visualgridclient.model.TestResultSummary;
import com.applitools.eyes.visualgridclient.services.EyesRunner;
import com.applitools.eyes.visualgridclient.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestVGChrome {

    private EyesRunner visualGridRunner;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    private String dateTimeString = dateFormat.format(Calendar.getInstance().getTime());

    @SuppressWarnings("Duplicates")
    @BeforeClass
    public void before() {
        visualGridRunner = new VisualGridRunner(1);
        FileLogger logHandler = new FileLogger("eyes.log", false, true);
        visualGridRunner.setLogHandler(logHandler);
        visualGridRunner.getLogger().log("enter");
        visualGridRunner.setServerUrl("https://eyes.applitools.com/");
    }

    @DataProvider(name = "dp", parallel = true)
    public static Object[][] dp() {
        return new Object[][]{
                {"https://www.applitools.com"}
        };
    }

    private Eyes initEyes(WebDriver webDriver, String testedUrl) {
        Eyes eyes = new Eyes(visualGridRunner);
        BatchInfo batchInfo = new BatchInfo("Test VG with Chrome");
        batchInfo.setId("Target");
        eyes.setBatch(batchInfo);

        Logger logger = eyes.getLogger();
        logger.log("creating WebDriver: " + testedUrl);

        try {
            SeleniumConfiguration seleniumConfiguration = new SeleniumConfiguration();
            seleniumConfiguration.setTestName("Chrome - " + testedUrl);
            seleniumConfiguration.setAppName("Test VG Chrome");
            seleniumConfiguration.setBatch(new BatchInfo("Test"));
            String environment = "";
            EmulationInfo emulation = new EmulationInfo(EmulationInfo.DeviceName.Nexus_6, ScreenOrientation.PORTRAIT);
            seleniumConfiguration.addBrowser(800, 600, SeleniumConfiguration.BrowserType.CHROME, environment);
            seleniumConfiguration.addBrowser(1024, 768, SeleniumConfiguration.BrowserType.CHROME, environment);
            seleniumConfiguration.addBrowser(1368, 768, SeleniumConfiguration.BrowserType.CHROME, environment);
            seleniumConfiguration.addBrowser(1920, 1080, SeleniumConfiguration.BrowserType.CHROME, environment);
            seleniumConfiguration.addDeviceEmulation(emulation);
            logger.log("created configurations for url " + testedUrl);
            eyes.setConfiguration(seleniumConfiguration);
            eyes.open(webDriver);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return eyes;
    }

    @Test(dataProvider = "dp")
    public void testFullPage(String testedUrl) {
        visualGridRunner.getLogger().log("entering with url " + testedUrl);
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(testedUrl);
        Eyes eyes = initEyes(webDriver, testedUrl);
        Logger logger = eyes.getLogger();
        logger.log("navigated to " + testedUrl);

        try {
            logger.log("running check for url " + testedUrl);
            try {
                eyes.check(Target.window().fully(false).withName("Visible viewport - " + testedUrl).sendDom(false).setUseDom(false));
                eyes.check(Target.window().fully().withName("Fully viewport - " + testedUrl).sendDom(false).setUseDom(false));

                eyes.setForceFullPageScreenshot(true);
                eyes.setSendDom(false);
                eyes.checkWindow("ForceFullPageScreenshot = true - " + testedUrl);

                eyes.setForceFullPageScreenshot(false);
                eyes.setSendDom(false);
                eyes.checkWindow("ForceFullPageScreenshot = false - " + testedUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            eyes.getLogger().log("calling VisualGridEyes.close() for url " + testedUrl);
            TestResults close = eyes.close(true);
            Assert.assertNotNull(close);
            logger.log("end of `try` block for url " + testedUrl);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        } finally {
            logger.log("closing WebDriver for url " + testedUrl);
            webDriver.quit();
            logger.log("url " + testedUrl + " - done with browser.");
        }
    }

    @Test(dataProvider = "dp")
    public void testStitchMode(String testedUrl) {
        visualGridRunner.getLogger().log("entering with url " + testedUrl);
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(testedUrl);
        Eyes eyes = initEyes(webDriver, testedUrl);
        Logger logger = eyes.getLogger();
        logger.log("navigated to " + testedUrl);

        try {
            logger.log("running check for url " + testedUrl);
            try {
                eyes.setStitchMode(StitchMode.CSS);
                eyes.check(Target.window().fully().withName("Fully with StitchMode.CSS - " + testedUrl));

                eyes.setStitchMode(StitchMode.SCROLL);
                eyes.check(Target.window().fully().withName("Fully with StitchMode.SCROLL - " + testedUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
            eyes.getLogger().log("calling VisualGridEyes.close() for url " + testedUrl);
            TestResults close = eyes.close(true);
            Assert.assertNotNull(close);
            logger.log("end of `try` block for url " + testedUrl);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        } finally {
            logger.log("closing WebDriver for url " + testedUrl);
            webDriver.quit();
            logger.log("url " + testedUrl + " - done with browser.");
        }
    }

    @Test(dataProvider = "dp")
    public void testRegions(String testedUrl) {
        visualGridRunner.getLogger().log("entering with url " + testedUrl);
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(testedUrl);
        Eyes eyes = initEyes(webDriver, testedUrl);
        Logger logger = eyes.getLogger();
        logger.log("navigated to " + testedUrl);

        try {
            logger.log("running check for url " + testedUrl);
            try {
                eyes.checkRegion(new Region(100, 200, 400, 800), 15_000, "checkRegion()");

                eyes.check(Target.region(new Region(100, 200, 400, 800)).withName("Target.region()"));

                eyes.checkRegion(By.className("cover-button"));

                eyes.check(Target.region(By.id("tns1-ow")));

                eyes.check(Target.window().ignore(By.id("tns1-ow")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            eyes.getLogger().log("calling VisualGridEyes.close() for url " + testedUrl);
            TestResults close = eyes.close(true);
            Assert.assertNotNull(close);
            logger.log("end of `try` block for url " + testedUrl);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        } finally {
            logger.log("closing WebDriver for url " + testedUrl);
            webDriver.quit();
            logger.log("url " + testedUrl + " - done with browser.");
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
