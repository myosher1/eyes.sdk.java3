package com.applitools.eyes.selenium;

import com.applitools.eyes.FileLogger;
import com.applitools.eyes.LogHandler;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
import com.applitools.eyes.selenium.fluent.Target;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URI;

@RunWith(JUnit4.class)
public class TestFluentApi {

    private static Eyes eyes;
    private static WebDriver driver;
    private static LogHandler logHandler;

    @BeforeClass
    public static void OneTimeSetUp() {

        // Initialize the eyes SDK and set your private API key.
        eyes = new Eyes();
        eyes.setServerUrl(URI.create("https://localhost.applitools.com"));
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        logHandler = new FileLogger("c:\\temp\\logs\\Java\\TestElement.log", true, true);
        eyes.setLogHandler(logHandler);
        eyes.setForceFullPageScreenshot(true);
        eyes.setStitchMode(StitchMode.CSS);

        eyes.setHideScrollbars(true);

        eyes.setDebugScreenshotsPath("c:\\temp\\logs");
        eyes.setSaveDebugScreenshots(true);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("disable-infobars");
        ChromeDriver chromeDriver = new ChromeDriver(options);

        driver = eyes.open(chromeDriver, "Eyes Selenium SDK - Java", "FramesElementsTest",
                new RectangleSize(800, 599));

        //string userProfile = Environment.GetFolderPath(Environment.SpecialFolder.UserProfile);
        driver.navigate().to("http://applitools.github.io/demo/TestPages/FramesTestPage/");
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
        eyes.abortIfNotClosed();
    }

    @Test
    public void TestCheckWindowWithIgnoreRegion_Fluent() {
        eyes.check("Window with Ignore region", Target.window()
                .fully()
                .timeout(5000)
                .ignore(new Region(50, 50, 100, 100)));
    }


    @Test
    public void TestCheckRegionWithIgnoreRegion_Fluent() {
        eyes.check("Region with Ignore region", Target.region(By.id("overflowing-div"))
                .ignore(new Region(50, 50, 100, 100)));
    }

    @Test
    public void TestCheckFrame_Fully_Fluent() {
        eyes.check("Full Frame", Target.frame("frame1").fully());
    }

    @Test
    public void TestCheckFrame_Fluent() {
        eyes.check("Frame", Target.frame("frame1"));
    }

    @Test
    public void TestCheckFrameInFrame_Fully_Fluent() {
        eyes.check("Full Frame in Frame", Target.frame("frame1")
                .frame("frame1-1")
                .fully());
    }

    @Test
    public void TestCheckRegionInFrame_Fluent() {
        eyes.check("Region in Frame", Target.frame("frame1")
                .region(By.id("inner-frame-div"))
                .fully());
    }

    @Test
    public void TestCheckRegionInFrameInFrame_Fluent() {
        eyes.check("Region in Frame in Frame", Target.frame("frame1")
                .frame("frame1-1")
                .region(By.tagName("img"))
                .fully());
    }


    @Test
    public void TestCheckFrameInFrame_Fully_Fluent2() {
        eyes.check("Window with Ignore region", Target.window()
                .fully()
                .timeout(5000)
                .ignore(new Region(50, 50, 100, 100)));

        eyes.check("Full Frame in Frame", Target.frame("frame1")
                .frame("frame1-1")
                .fully());
    }
}
