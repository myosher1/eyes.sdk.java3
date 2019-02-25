package com.applitools.eyes.demo;

import com.applitools.ICheckSettings;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.Logger;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.config.SeleniumConfiguration;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class TestISeleniumVGEyesSelenium extends TestEyesBase {

    private final String testName;
    private static BatchInfo batchInfo = new BatchInfo("Top Sites - Selenium");
    private Eyes eyes;
    private Logger logger;

    @Factory(dataProvider = "dp")
    public TestISeleniumVGEyesSelenium(String testName) {
        this.testName = testName;
    }

    @BeforeClass
    public void before() {
        eyes = new Eyes();
        eyes.setServerUrl(SERVER_URL);
        eyes.setLogHandler(initLogHandler("selenium"));
        logger = eyes.getLogger();
    }

    @Override
    protected ICheckSettings getNewWindowCheckSettings() {
        return Target.window();
    }

    @Override
    protected Eyes initEyes(WebDriver webDriver, String testedUrl) {
        SeleniumConfiguration configuration = new SeleniumConfiguration();
        configuration.setTestName("Top Sites - " + testedUrl);
        configuration.setAppName("Top Sites");
        configuration.setBatch(batchInfo);
        eyes.open(webDriver, configuration);
        return eyes;
    }

    @Test
    public void test() {
        super.test(testName);
    }

    @AfterClass
    public void after() {
        try {
            TestResults results = eyes.close();
            logger.log(results.toString());
        } catch (Throwable e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }
}
