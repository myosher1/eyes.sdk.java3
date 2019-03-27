package com.applitools.eyes.demo;

import com.applitools.ICheckSettings;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgridclient.model.*;
import com.applitools.eyes.visualgridclient.services.VisualGridRunner;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
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
        renderingManager.setLogHandler(new FileLogger(true));
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
        eyes.setBaselineEnvName("applitools environment");
        Logger logger = eyes.getLogger();
        logger.log("creating WebDriver: " + testedUrl);
        try {
            Configuration configuration = new Configuration();
            configuration.setTestName("Top Sites - " + testedUrl);
            configuration.setAppName("Top Sites");

            String environment = "VeryCoolEnvironment";
            configuration.addBrowser(800, 600, Configuration.BrowserType.CHROME, environment);
            configuration.addBrowser(700, 500, Configuration.BrowserType.CHROME, environment);
            configuration.addBrowser(1200, 800, Configuration.BrowserType.CHROME, environment);
            configuration.addBrowser(1600, 1200, Configuration.BrowserType.CHROME, environment);

            EmulationDevice emulationDevice = new EmulationDevice(300, 400, 0.5f, ScreenOrientation.LANDSCAPE);
            EmulationInfo emulationInfo = new EmulationInfo(EmulationInfo.DeviceName.Galaxy_Note_2, ScreenOrientation.PORTRAIT);

            configuration.addDeviceEmulation(emulationDevice, environment);
            configuration.addDeviceEmulation(emulationInfo);

            logger.log("created configurations for url " + testedUrl);
            eyes.setConfiguration(configuration);
            eyes.open(webDriver);
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


    @AfterClass
    public void afterClass(ITestContext testContext) {
        TestResultSummary allTestResults = renderingManager.getAllTestResults();
        logger.log(allTestResults.toString());
    }
}
