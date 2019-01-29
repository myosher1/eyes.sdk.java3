package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.*;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.model.TestResultContainer;
import com.applitools.eyes.visualGridClient.model.TestResultSummary;
import com.applitools.eyes.visualGridClient.services.VisualGridManager;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;

public final class TestRenderingGridServiceWithJsHook {

    private VisualGridManager renderingManager;
    private WebDriver webDriver;

    private String logsPath = System.getenv("APPLITOOLS_LOGS_PATH");

    @BeforeMethod
    public void Before(ITestContext testContext){
        renderingManager = new VisualGridManager(3);
        renderingManager.setLogHandler(new StdoutLogHandler(true));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
        String path = logsPath + File.separator + "java" + File.separator + "TestRenderingGridService" + dateFormat.format(Calendar.getInstance().getTime());
//        FileDebugResourceWriter fileDebugResourceWriter = new FileDebugResourceWriter(renderingManager.getLogger(), path, null, null);
//        renderingManager.setDebugResourceWriter(fileDebugResourceWriter);

        webDriver = new ChromeDriver();
        webDriver.get("http://applitools-vg-test.surge.sh/test.html");

        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }

    @Test
    public void test() {

        IEyes eyes = initEyes(webDriver, new BatchInfo("WebHookBatch") );

        try {
            RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
            renderingConfiguration.setTestName("Visual Grid With Web Hook");
            renderingConfiguration.setAppName("RenderingGridIntegration");
            renderingConfiguration.addBrowser(400, 300, RenderingConfiguration.BrowserType.CHROME);
            //eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888, null, null));
            eyes.setServerUrl("https://eyes.applitools.com/");
            ((Eyes) eyes).open(webDriver, renderingConfiguration);
            //CheckRGSettings setting = new CheckRGSettings(CheckRGSettings.SizeMode.FULL_PAGE, null, null, false);
            String jshook = "document.body.style='background-color: red'";
            eyes.check(Target.window().withName("test").fully().sendDom(false).webHook(jshook));
            List<Future<TestResultContainer>> close = ((Eyes) eyes).close();
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(eyes.getLogger(), e);
        } finally {
            if (webDriver != null) {
//                webDriver.quit();
            }
        }
    }

    private Eyes initEyes(WebDriver webDriver, BatchInfo batch) {
        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(batch);

        Logger logger = eyes.getLogger();

        try {
            RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
            renderingConfiguration.setTestName("Vans Gallery page");
            renderingConfiguration.setAppName("RenderingGridIntegration");
            renderingConfiguration.addBrowser(1200, 800, RenderingConfiguration.BrowserType.CHROME, null, null);
            eyes.open(webDriver, renderingConfiguration);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return eyes;
    }

    @AfterMethod
    public void After(ITestContext testContext) {
        renderingManager.getLogger().log(renderingManager.getAllTestResults().toString());
        webDriver.quit();
    }
}