package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.model.TestResultSummary;
import com.applitools.eyes.visualGridClient.services.RenderingGridManager;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.Future;

public class TestTopSites {
    private RenderingGridManager renderingManager;
    private WebDriver webDriver;

    @BeforeMethod
    public void Before(ITestContext testContext) {
        renderingManager = new RenderingGridManager(3);
        renderingManager.setLogHandler(new StdoutLogHandler(true));

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

        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(new BatchInfo(testedUrl));

        webDriver = new ChromeDriver();
        webDriver.get(testedUrl);

        try {
            RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
            renderingConfiguration.setTestName("Top 10 websites - " + testedUrl);
            renderingConfiguration.setAppName("RenderingGridIntegration");
            renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.CHROME);
            renderingConfiguration.addBrowser(700, 500, RenderingConfiguration.BrowserType.CHROME);
            renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.FIREFOX);
            renderingConfiguration.addBrowser(700, 500, RenderingConfiguration.BrowserType.FIREFOX);
            eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
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
            if (webDriver != null) {
                webDriver.quit();
            }
            TestResultSummary allTestResults = renderingManager.getAllTestResults();
            System.out.println(allTestResults);
            // End the test.
        }
    }

    @AfterMethod
    public void After(ITestContext testContext) {
        renderingManager.getLogger().log(renderingManager.getAllTestResults().toString());
        webDriver.quit();
    }
}
