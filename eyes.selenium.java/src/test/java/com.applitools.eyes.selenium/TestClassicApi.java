package com.applitools.eyes.selenium;

import com.applitools.eyes.IEyesJsExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Factory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class TestClassicApi extends TestSetup {

    private Eyes eyes;

    @Override
    public void setEyes(Eyes eyes) {
        this.eyes = eyes;
    }

    @Factory(dataProvider = "dp", dataProviderClass = TestsDataProvider.class)
    public TestClassicApi(Capabilities caps, String platform) {
        super.caps = caps;
        super.platform = platform;
        super.forceFPS = false;

        testSuitName = "Eyes Selenium SDK - Classic API";
        testedPageUrl = "https://applitools.github.io/demo/TestPages/FramesTestPage/";
    }

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

    @Test
    public void TestCheckInnerFrame() {
        eyes.setHideScrollbars(false);
        driver.switchTo().defaultContent();
        driver.switchTo().frame(webDriver.findElement(By.name("frame1")));
        eyes.checkFrame("frame1-1", "inner-frame");
        eyes.getLogger().log("Validating (1) ...");
        eyes.checkWindow("window after check frame");
        eyes.getLogger().log("Validating (2) ...");
        WebElement innerFrameBody = driver.findElement(By.tagName("body"));
        ((IEyesJsExecutor) driver).executeScript("arguments[0].style.background='red';", innerFrameBody);
        eyes.checkWindow("window after change background color of inner frame");
    }
}
