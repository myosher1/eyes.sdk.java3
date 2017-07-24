package com.applitools;

import com.applitools.eyes.*;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import javafx.print.PageLayout;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Platform;
import java.io.IOException;
import java.net.URISyntaxException;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.util.ArrayList;
/**
 * Unit test for simple App.
 */
public class ElementsFramesTest {
    private static Eyes eyes;
    private static ArrayList<WebDriver> drivers;
    private static WebDriver activeDriver;

    @BeforeClass
    public static void setUp () throws URISyntaxException, java.net.MalformedURLException {
        eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setStitchMode(StitchMode.CSS);
        eyes.setForceFullPageScreenshot(true);
        ArrayList<Platform> platforms = new ArrayList<Platform>();
        platforms.add(Platform.WIN10);
        platforms.add(Platform.LINUX);
        platforms.add(Platform.MAC);
        ArrayList<String> browsers = new ArrayList<String>();
        browsers.add("chrome");
        browsers.add("firefox");
        drivers = new ArrayList<WebDriver>();
        for (Platform platform:platforms)
        {
            for (String browser:browsers)
            {
                WebDriver driver = createDriver(platform, browser);
                drivers.add(driver);
            }
        }



        WebDriver driver = createDriver(Platform.WIN10, "internet explorer");
        drivers.add(driver);
    }


    private static WebDriver createDriver(Platform platform, String browserName) throws java.net.MalformedURLException
    {
        final String URL = System.getenv("SAUCELABS_URL");
        WebDriver driver = null;

        if (URL != null) {
            DesiredCapabilities desired_cap = new DesiredCapabilities();
            desired_cap.setBrowserName(browserName);
            desired_cap.setPlatform(platform);
            if (browserName == "chrome")
            {
                desired_cap.setVersion("48.0");
            }

            DesiredCapabilities caps = DesiredCapabilities.chrome();
            driver = new RemoteWebDriver(new URL(URL), caps);
        }
        else
        {
            ChromeOptions co = new ChromeOptions();
            driver = new ChromeDriver(co);
        }
        return driver;
    }

    @AfterClass
    public static void tearDown () {
        for (WebDriver driver:drivers)
        {
            driver.quit();
        }

        eyes.abortIfNotClosed();
    }

    @org.junit.Test
    public void home1 () throws IOException {
        for (WebDriver driver:drivers) {
            activeDriver  = driver;
            activeDriver.get("https://astappev.github.io/test-html-pages/");
            eyes.checkWindow("Initial");
            eyes.checkRegion(By.id("overflowing-div"), "Initial", true);
            eyes.checkRegionInFrame("frame1", By.id("inner-frame-div"), "Inner frame div", true);
            eyes.checkRegion(By.id("overflowing-div-image"), "minions", true);
        }
    }

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            eyes.open(activeDriver, "Eyes Selenium SDK", description.getMethodName());
        }

        protected void finished(Description description) {
            eyes.close();
        }
    };

}
