package com.applitools.eyes.selenium;

import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Test(alwaysRun = true)
@Listeners(TestListener.class)
public class TestFluentApi_Chrome extends TestFluentApi {

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        ChromeOptions options = new ChromeOptions();
        if (!System.getenv("SELENIUM_SERVER_URL").contains("ondemand.saucelabs.com")) {
            options.setHeadless(true);
        }
        options.addArguments("disable-infobars");

        caps = options;
    }
}
