package com.applitools.eyes.demo;

import com.applitools.eyes.*;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.model.TestResultSummary;
import com.applitools.eyes.visualGridClient.services.VisualGridManager;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

public class TestIEyesVG extends TestIEyesBase {

    private VisualGridManager renderingManager;
    private Logger logger;

    @BeforeClass
    public void before(){
        renderingManager = new VisualGridManager(40);
        renderingManager.setLogHandler(new StdoutLogHandler(true));
        FileLogger logHandler = new FileLogger("eyes.log", false, true);
        renderingManager.setLogHandler(logHandler);
        logger = renderingManager.getLogger();
        logger.log("enter");
        renderingManager.setServerUrl("https://eyes.applitools.com/");
    }


    @Override
    protected IEyes getEyes(WebDriver webDriver, String testedUrl) {
        if (this.eyes == null) {
            Eyes eyes = new Eyes(renderingManager);
            BatchInfo batchInfo = new BatchInfo("Top Ten Sites");
            batchInfo.setId("Target");
            eyes.setBatch(batchInfo);
            Logger logger = eyes.getLogger();
            logger.log("creating WebDriver: " + testedUrl);
            try {
                RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
                renderingConfiguration.setTestName("Top 10 websites - " + testedUrl);
                renderingConfiguration.setAppName("Top Ten Sites");
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
        }
        return this.eyes;
    }

    @AfterMethod
    public void afterMethod(){
        Eyes eyes = getEyes();
    }

    @AfterClass
    public void afterClass(ITestContext testContext) {
        TestResultSummary allTestResults = renderingManager.getAllTestResults();
        logger.log(allTestResults.toString());
    }
}
