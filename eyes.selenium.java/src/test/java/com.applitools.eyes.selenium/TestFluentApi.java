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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

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
        eyes.setApiKey (System.getenv("APPLITOOLS_API_KEY"));

        //logHandler = new FileLogger("c:\\temp\\logs\\Java\\TestElement.log", true, true);
        //eyes.setLogHandler(logHandler);
        eyes.setForceFullPageScreenshot(true);
        eyes.setStitchMode(StitchMode.CSS);

        driver = eyes.open(new ChromeDriver(), "Eyes Selenium SDK - .Net", "FramesElementsTest",
                new RectangleSize(1024, 635));

        //string userProfile = Environment.GetFolderPath(Environment.SpecialFolder.UserProfile);
        driver.navigate().to("http://applitools.github.io/demo/TestPages/FramesTestPage/");
    }

    @AfterClass
    public static void tearDown () {
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
}
