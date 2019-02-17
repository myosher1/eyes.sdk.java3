package com.applitools.eyes.demo;

import com.applitools.ICheckSettings;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.Logger;
import com.applitools.eyes.selenium.Eyes;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.DataProvider;

public abstract class TestIEyesBase {

    protected final String SERVER_URL = "https://VisualGridEyes.applitools.com/";

    @DataProvider(name = "dp", parallel = true)
    public static Object[][] dp() {
        return new Object[][]{
                {"https://google.com"},
                {"https://facebook.com"},
                {"https://youtube.com"},
                {"https://amazon.com"},
                {"https://yahoo.com"},
//                {"https://ebay.com"},
                {"https://twitter.com"},
                {"https://wikipedia.org"},
                {"https://instagram.com"},
                {"https://www.target.com/c/blankets-throws/-/N-d6wsb?lnk=ThrowsBlankets%E2%80%9C,tc"},
        };
    }

    public void test(String testedUrl) {
        WebDriver webDriver = new ChromeDriver();
        Eyes eyes = initEyes(webDriver, testedUrl);
        Logger logger = eyes.getLogger();
        webDriver.get(testedUrl);
        logger.log("navigated to " + testedUrl);
        try {
            logger.log("running check for url " + testedUrl);
            ICheckSettings windowCheckSettings = getNewWindowCheckSettings();
            eyes.check(windowCheckSettings.withName("Step1 - " + testedUrl));
            eyes.check(windowCheckSettings.fully(false).withName("Step2 - " + testedUrl));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webDriver.quit();
        }
    }

    protected abstract ICheckSettings getNewWindowCheckSettings();

    protected abstract Eyes initEyes(WebDriver webDriver, String testedUrl);

    protected LogHandler initLogHandler(String testName) {
        return new FileLogger("eyes_" + testName + ".log", false, false);
    }
}
