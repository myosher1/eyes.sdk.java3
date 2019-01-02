package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.model.TestResultSummary;
import com.applitools.eyes.visualGridClient.services.VisualGridManager;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TestRenderingGridServiceVans {
    private VisualGridManager renderingManager;

    private String logsPath = System.getenv("APPLITOOLS_LOGS_PATH");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    private String dateTimeString = dateFormat.format(Calendar.getInstance().getTime());

    @BeforeClass
    public void beforeClass() {
        renderingManager = new VisualGridManager(40);
        renderingManager.setLogHandler(new StdoutLogHandler(true));
        FileLogger logHandler = new FileLogger("eyes.log", false, true);
        renderingManager.setLogHandler(logHandler);
        renderingManager.getLogger().log("enter");
        renderingManager.setServerUrl("https://eyes.applitools.com/");
    }


    @DataProvider(name = "Kids",parallel=true)
    public Object[][] Kids_urls() {
        BatchInfo batch = new BatchInfo("Kids Gallery Vans");
        return new Object[][]{
                {"https://www.vans.com/shop/infant-baby-shoes",batch},
                {"https://www.vans.com/shop/kids-toddler-baby-shoes",batch},
                {"https://www.vans.com/shop/kids-little-boys-shoes",batch}};

    }

    @DataProvider(name = "Women",parallel=true)
    public Object[][] Women_urls() {
        BatchInfo batch = new BatchInfo("Women Gallery Vans");
        return new Object[][]{
                {"https://www.vans.com/shop/womens-shoes-new-arrivals",batch},
                {"https://www.vans.com/shop/womens-shoes-sandals",batch},
                {"https://www.vans.com/shop/shoes-classics-womens-authentics?ref=womens-styles",batch},
                {"https://www.vans.com/shop/womens-clothes-new-arrivals",batch},
                {"https://www.vans.com/shop/womens-clothes-tees",batch},
                {"https://www.vans.com/shop/womens-clothes-pants",batch},
                {"https://www.vans.com/shop/womens-shoes-classics",batch}};
    }

    @DataProvider(name = "Men",parallel=true)
    public Object[][] Men_urls() {
        BatchInfo batch = new BatchInfo("Men Gallery Vans");

        return new Object[][]{
                {"https://www.vans.com/shop/mens-shoes-skate",batch},
                {"https://www.vans.com/shop/mens-shoes-shop-new-arrivals",batch},
                {"https://www.vans.com/shop/mens-shoes",batch},
                {"https://www.vans.com/shop/mens-shoes-sandals",batch}};
    }




    @DataProvider(name = "dp", parallel = true)
    public static Object[][] dp() {
        return new Object[][]{
                {"https://google.com"},
                {"https://facebook.com"},
//                {"https://youtube.com"},
//                {"https://amazon.com"},
//                {"https://yahoo.com"},
//                {"https://ebay.com"},
//                {"https://twitter.com"},
//                {"https://wikipedia.org"},
//                {"https://instagram.com"},
//                {"https://reddit.com"},
        };
    }

    @Test(dataProvider = "Men")
    public void test(String testedUrl, BatchInfo batch) {
        renderingManager.getLogger().log("entering with url " + testedUrl);
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(testedUrl);
        Eyes eyes = initEyes(webDriver, testedUrl, batch);

        Logger logger = eyes.getLogger();
        logger.log("navigated to " + testedUrl);


        try {
            //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
            logger.log("running check for url " + testedUrl);
            try {
                eyes.check(Target.window().withName("Step1 - " + testedUrl).sendDom(false));
//                eyes.check(Target.window().fully(false).withName("Step2 - " + testedUrl).sendDom(false));
            } catch (Exception e) {
                e.printStackTrace();
            }
            eyes.getLogger().log("calling eyes.close() for url " + testedUrl);

            TestResults close = eyes.close();
//            for (Future<TestResultContainer> future : closeAndR
//            eturnResults) {
//                logger.log("calling future.get() for url " + testedUrl);
//                future.get();
//            }
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

    private Eyes initEyes(WebDriver webDriver, String testedUrl, BatchInfo batch) {
        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(batch);
        initLogging(testedUrl, eyes);

        Logger logger = eyes.getLogger();
        logger.log("creating WebDriver: " + testedUrl);

        try {
            RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
            renderingConfiguration.setTestName("Vans Gallery page");
            renderingConfiguration.setAppName("RenderingGridIntegration");
            renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.CHROME);
            renderingConfiguration.addBrowser(700, 500, RenderingConfiguration.BrowserType.CHROME);
            renderingConfiguration.addBrowser(1200, 800, RenderingConfiguration.BrowserType.CHROME);
            renderingConfiguration.addBrowser(1600, 1200, RenderingConfiguration.BrowserType.CHROME);
            logger.log("created configurations for url " + testedUrl);
            eyes.open(webDriver, renderingConfiguration);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return eyes;
    }

    private void initLogging(String testedUrl, Eyes eyes) {
        String testName = testedUrl.substring(8);
        String path = logsPath + File.separator + "java" + File.separator + "TestTopSites_" + dateTimeString;
//        FileDebugResourceWriter fileDebugResourceWriter = new FileDebugResourceWriter(renderingManager.getLogger(), path, null, null);
//        eyes.setDebugResourceWriter(fileDebugResourceWriter);

        //FileLogger eyesLogger = new FileLogger("TopTenSites.log", true, true);
        //eyes.setLogHandler(eyesLogger);
    }

    @AfterMethod
    public void afterMethod(ITestContext testContext) {
        renderingManager.getLogger().log("enter");
    }

    @AfterClass
    public void afterTestRun(ITestContext testContext) {
        TestResultSummary allTestResults = renderingManager.getAllTestResults();
        renderingManager.getLogger().log(allTestResults.toString());
    }
}