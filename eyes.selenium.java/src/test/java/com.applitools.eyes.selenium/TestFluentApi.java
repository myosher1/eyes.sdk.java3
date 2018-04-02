package com.applitools.eyes.selenium;

import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.ICheckSettings;
import com.applitools.eyes.selenium.fluent.Target;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public abstract class TestFluentApi extends TestSetup {

    @BeforeClass(alwaysRun = true)
    public void beforeTest(){
        testSuitName = "Eyes Selenium SDK - Fluent API";
        testedPageUrl = "http://applitools.github.io/demo/TestPages/FramesTestPage/";
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckWindowWithIgnoreRegion_Fluent(String platform, boolean forceFPS) {
        webDriver.findElement(By.tagName("input")).sendKeys("My Input");
        eyes.check("Fluent - Window with Ignore region", Target.window()
                .fully()
                .timeout(5000)
                .ignore(new Region(50, 50, 100, 100)));
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckRegionWithIgnoreRegion_Fluent(String platform, boolean forceFPS) {
        eyes.check("Fluent - Region with Ignore region", Target.region(By.id("overflowing-div"))
                .ignore(new Region(50, 50, 100, 100)));
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckFrame_Fully_Fluent(String platform, boolean forceFPS) {
        eyes.check("Fluent - Full Frame", Target.frame("frame1").fully());
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckFrame_Fluent(String platform, boolean forceFPS) {
        eyes.check("Fluent - Frame", Target.frame("frame1"));
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckFrameInFrame_Fully_Fluent(String platform, boolean forceFPS) {
        eyes.check("Fluent - Full Frame in Frame", Target.frame("frame1")
                .frame("frame1-1")
                .fully());
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckRegionInFrame_Fluent(String platform, boolean forceFPS) {
        eyes.check("Fluent - Region in Frame", Target.frame("frame1")
                .region(By.id("inner-frame-div"))
                .fully());
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckRegionInFrameInFrame_Fluent(String platform, boolean forceFPS) {
        eyes.check("Fluent - Region in Frame in Frame", Target.frame("frame1")
                .frame("frame1-1")
                .region(By.tagName("img"))
                .fully());
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestScrollbarsHiddenAndReturned_Fluent(String platform, boolean forceFPS){
        eyes.check("Fluent - Window (Before)", Target.window().fully());
        eyes.check("Fluent - Inner frame div",
                Target.frame("frame1")
                      .region(By.id("inner-frame-div"))
                      .fully());
        eyes.check("Fluent - Window (After)", Target.window().fully());
    }
    /*
    @Test
    public void TestCheckRegionInFrame2_Fluent() {
        eyes.check("Fluent - Inner frame div 1", Target.frame("frame1")
                .region(By.id("inner-frame-div"))
                .fully()
                .timeout(5000)
                .ignore(new Region(50, 50, 100, 100)));

        eyes.check("Fluent - Inner frame div 2", Target.frame("frame1")
                .region(By.id("inner-frame-div"))
                .fully()
                .ignore(new Region(50, 50, 100, 100))
                .ignore(new Region(70, 170, 90, 90)));

        eyes.check("Fluent - Inner frame div 3", Target.frame("frame1")
                .region(By.id("inner-frame-div"))
                .fully()
                .timeout(5000));

        eyes.check("Fluent - Inner frame div 4", Target.frame("frame1")
                .region(By.id("inner-frame-div"))
                .fully());

        eyes.check("Fluent - Full frame with floating region", Target.frame("frame1")
                .fully()
                .layout()
                .floating(25, new Region(200, 200, 150, 150)));
    }
    */

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckFrameInFrame_Fully_Fluent2(String platform, boolean forceFPS) {
        eyes.check("Fluent - Window", Target.window()
                .fully()
        );

        eyes.check("Fluent - Full Frame in Frame 2", Target.frame("frame1")
                .frame("frame1-1")
                .fully());
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckWindowWithIgnoreBySelector_Fluent(String platform, boolean forceFPS) {
        eyes.check("Fluent - Window with ignore region by selector", Target.window()
                .ignore(By.id("overflowing-div")));
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckWindowWithFloatingBySelector_Fluent(String platform, boolean forceFPS)
    {
        eyes.check("Fluent - Window with floating region by selector", Target.window()
                .floating(By.id("overflowing-div"), 3, 3, 20, 30));
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckWindowWithFloatingByRegion_Fluent(String platform, boolean forceFPS) {
        ICheckSettings settings = Target.window()
                .floating(new Region(10, 10, 20, 20), 3, 3, 20, 30);
        eyes.check("Fluent - Window with floating region by region", settings);

        setExpectedFloatingsRegions(new FloatingMatchSettings(10, 10, 20, 20, 3, 3, 20, 30));
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckElementFully_Fluent(String platform, boolean forceFPS)
    {
        //Assert.fail("failing test");
        WebElement element = webDriver.findElement(By.id("overflowing-div-image"));
        eyes.check("Fluent - Region by element - fully", Target.region(element).fully());
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckElementWithIgnoreRegionByElementOutsideTheViewport_Fluent(String platform, boolean forceFPS)
    {
        WebElement element = webDriver.findElement(By.id("overflowing-div-image"));
        WebElement ignoreElement = webDriver.findElement(By.id("overflowing-div"));
        eyes.check("Fluent - Region by element", Target.region(element).ignore(ignoreElement));
    }

    @Test(dataProvider = "data", alwaysRun = true)
    public void TestCheckElementWithIgnoreRegionBySameElement_Fluent(String platform, boolean forceFPS)
    {
        WebElement element = webDriver.findElement(By.id("overflowing-div-image"));
        eyes.check("Fluent - Region by element", Target.region(element).ignore(element));
        setExpectedIgnoreRegions(new Region(0,0,304,184));
    }
}
