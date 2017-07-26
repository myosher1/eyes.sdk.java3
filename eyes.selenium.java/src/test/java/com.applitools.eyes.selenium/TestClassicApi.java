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
public class TestClassicApi {

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

        driver = eyes.open(chromeDriver, "Eyes Selenium SDK - Classic API", "Eyes Selenium SDK - Classic API",
                new RectangleSize(800, 599));

        //string userProfile = Environment.GetFolderPath(Environment.SpecialFolder.UserProfile);
        driver.navigate().to("http://applitools.github.io/demo/TestPages/FramesTestPage/");
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
        eyes.abortIfNotClosed();
    }

}
