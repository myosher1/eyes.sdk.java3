package com.applitools.eyes.demo;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.rendering.Target;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.model.TestResultSummary;
import com.applitools.eyes.visualGridClient.services.VisualGridManager;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestIEyesVG extends TestIEyesBase {

    private VisualGridManager renderingManager;
    private Logger logger;
    private Eyes eyes;
    private BatchInfo batchInfo;

    @BeforeClass
    public void before() {
        renderingManager = new VisualGridManager(40);
        renderingManager.setLogHandler(new StdoutLogHandler(true));
        renderingManager.setLogHandler(initLogHandler("visual_grid"));
        logger = renderingManager.getLogger();
        logger.log("enter");
        renderingManager.setServerUrl(SERVER_URL);
        batchInfo = new BatchInfo("Top Sites - Visual Grid");
    }

    @Override
    protected ICheckSettings getNewWindowCheckSettings() {
        return Target.window();
    }

    @Override
    protected IEyes initEyes(WebDriver webDriver, String testedUrl) {
        Eyes eyes = new Eyes(renderingManager);
        eyes.setBatch(batchInfo);
        Logger logger = eyes.getLogger();
        logger.log("creating WebDriver: " + testedUrl);
        try {
            RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
            renderingConfiguration.setTestName("Top Sites - " + testedUrl);
            renderingConfiguration.setAppName("Top Sites");
            String environment = "";
            renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.CHROME, environment);
            renderingConfiguration.addBrowser(700, 500, RenderingConfiguration.BrowserType.CHROME, environment);
            renderingConfiguration.addBrowser(1200, 800, RenderingConfiguration.BrowserType.CHROME, environment);
            renderingConfiguration.addBrowser(1600, 1200, RenderingConfiguration.BrowserType.CHROME, environment);
            logger.log("created configurations for url " + testedUrl);
            eyes.open(webDriver, renderingConfiguration);
            this.eyes = eyes;
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return this.eyes;
    }

    @Override
    @Test(dataProvider = "dp")
    public void test(String testName){
        super.test(testName);
    }

    @AfterMethod
    public void afterMethod() {
        eyes.close();
    }

    @AfterClass
    public void afterClass(ITestContext testContext) {
        TestResultSummary allTestResults = renderingManager.getAllTestResults();
        logger.log(allTestResults.toString());
    }
}
