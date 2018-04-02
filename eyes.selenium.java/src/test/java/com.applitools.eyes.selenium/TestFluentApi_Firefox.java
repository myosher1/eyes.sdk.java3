package com.applitools.eyes.selenium;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Test(alwaysRun = true)
@Listeners(TestListener.class)
public class TestFluentApi_Firefox extends TestFluentApi {

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        FirefoxOptions options = new FirefoxOptions();
        if (!System.getenv("SELENIUM_SERVER_URL").contains("ondemand.saucelabs.com")) {
            //options.setHeadless(true);
        }
        caps = options;
    }
}
