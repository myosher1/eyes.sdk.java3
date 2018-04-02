package com.applitools.eyes.selenium;

import org.openqa.selenium.safari.SafariOptions;
import org.testng.annotations.BeforeClass;

public class TestFluentApi_Safari extends TestFluentApi {

    @BeforeClass
    public void beforeClass() {
        caps = new SafariOptions();
    }
}
