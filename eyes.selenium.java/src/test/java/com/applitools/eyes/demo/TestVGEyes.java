package com.applitools.eyes.demo;

import com.applitools.ICheckSettings;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.Logger;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgridclient.model.RenderingConfiguration;
import com.applitools.eyes.visualgridclient.model.TestResultSummary;
import com.applitools.eyes.visualgridclient.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestVGEyes extends TestEyesBase {

    private VisualGridRunner renderingManager;
    private Logger logger;
    private Eyes eyes;
    private BatchInfo batchInfo;

    @BeforeClass
    public void before() {
        renderingManager = new VisualGridRunner(40);
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
    protected Eyes initEyes(WebDriver webDriver, String testedUrl) {
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
        } catch (Throwable e) {
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
