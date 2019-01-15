package com.applitools.eyes.demo;


import com.applitools.eyes.IEyes;
import com.applitools.eyes.Logger;
import com.applitools.eyes.rendering.Target;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public abstract class TestIEyesBase {

    protected IEyes eyes;

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

    @Test(dataProvider = "dp")
    public void test(String testedUrl) {
        WebDriver webDriver = new ChromeDriver();
        IEyes eyes = getEyes(webDriver, testedUrl);
        Logger logger = eyes.getLogger();
        webDriver.get(testedUrl);
        logger.log("navigated to " + testedUrl);
        try {
            logger.log("running check for url " + testedUrl);
            eyes.check(Target.window().withName("Step1 - " + testedUrl).sendDom(true));
            eyes.check(Target.window().fully(false).withName("Step2 - " + testedUrl).sendDom(true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract IEyes getEyes(WebDriver webDriver, String testedUrl);
}
