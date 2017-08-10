package com.applitools.eyes.selenium;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.By;

@RunWith(JUnit4.class)
public class TestClassicApi extends TestSetup {

    @ClassRule
    public static final TestRule setTestSuitName = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            testSuitName = "Eyes Selenium SDK - Classic API - Java";
        }
    };

    @Test
    public void TestCheckWindow() {
        eyes.checkWindow("Window");
    }

    @Test
    public void TestCheckRegion() {
        eyes.checkRegion(By.id("overflowing-div"), "Region", true);
    }

    @Test
    public void TestCheckFrame() {
        eyes.checkFrame("frame1", "frame1");
    }

    @Test
    public void TestCheckRegionInFrame() {
        eyes.checkRegionInFrame("frame1", By.id("inner-frame-div"), "Inner frame div", true);
    }

    @Test
    public void TestCheckRegion2() {
        eyes.checkRegion(By.id("overflowing-div-image"), "minions", true);
    }
}
