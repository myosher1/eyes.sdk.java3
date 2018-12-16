package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.model.FileDebugResourceWriter;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
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
        renderingManager = new RenderingGridManager(3);
        renderingManager.setLogHandler(new StdoutLogHandler(true));
        renderingManager.getLogger().log("enter");
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @DataProvider(name = "dp", parallel = true)
    public static Object[][] dp() {
        return new Object[][]{
                {"https://google.com"},
                {"https://facebook.com"},
                {"https://youtube.com"},
                {"https://amazon.com"},
                {"https://yahoo.com"},
                {"https://ebay.com"},
                {"https://twitter.com"},
                {"https://wikipedia.org"},
                {"https://instagram.com"},
                {"https://reddit.com"},
        };
    }

    @Test(dataProvider = "dp")
    public void test(String testedUrl) {
        renderingManager.getLogger().log("enter");

        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo(testedUrl));

        String testName = testedUrl.substring(8);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
        String path = logsPath + File.separator + "java" + File.separator + "TestTopSites_" + testName + dateFormat.format(Calendar.getInstance().getTime());
        FileDebugResourceWriter fileDebugResourceWriter = new FileDebugResourceWriter(renderingManager.getLogger(), path, null, null);
        eyes.setDebugResourceWriter(fileDebugResourceWriter);

        WebDriver webDriver = new ChromeDriver();
        webDriver.get(testedUrl);

        try {
            RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
            renderingConfiguration.setTestName("Top 10 websites - " + testedUrl);
            renderingConfiguration.setAppName("RenderingGridIntegration");
            renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.CHROME);
            renderingConfiguration.addBrowser(700, 500, RenderingConfiguration.BrowserType.CHROME);
            renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.FIREFOX);
            renderingConfiguration.addBrowser(700, 500, RenderingConfiguration.BrowserType.FIREFOX);
            //eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            //eyes.setServerUrl("https://eyes.applitools.com/");
            eyes.open(webDriver, renderingConfiguration);
            //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
            eyes.check(Target.window().withName(testedUrl).sendDom(false));
            List<Future<TestResults>> close = eyes.close();
            for (Future<TestResults> future : close) {
                future.get();
            }

        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        } finally {
            webDriver.quit();
            TestResultSummary allTestResults = renderingManager.getAllTestResults();
            System.out.println(allTestResults);
            // End the test.
        }
    }

    @AfterMethod
    public void afterMethod(ITestContext testContext) {
        renderingManager.getLogger().log("enter");
    }

    @AfterClass
    public void afterClass(){
        renderingManager.getLogger().log(renderingManager.getAllTestResults().toString());
    }
}
