package com.applitools.eyes.selenium;

import com.applitools.eyes.FileLogger;
import com.applitools.eyes.FixedCutProvider;
import com.applitools.eyes.StdoutLogHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class IOSTest {

    public static void main(String[] args) throws MalformedURLException {
        Eyes eyes = new Eyes();
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
        // This is your api key, make sure you use it in all your tests.
        DesiredCapabilities caps = DesiredCapabilities.iphone();

        caps.setCapability("appiumVersion", "1.7.2");
        caps.setCapability("deviceName","iPhone X Simulator");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("platformVersion","11.0");
        caps.setCapability("platformName", "iOS");
        caps.setCapability("browserName", "Safari");
        String sauceUrl = String.format("http://%s:%s@ondemand.saucelabs.com:80/wd/hub", "matan", "ec79e940-078b-41d4-91a6-d7d6008cf1ea");
        WebDriver driver = new RemoteWebDriver(new URL(sauceUrl), caps);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        //eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setLogHandler(new FileLogger("c:\\temp\\logs\\iostest.log", false, true));
        eyes.setImageCut(new FixedCutProvider(30,12,8,5));
        //eyes.setForceFullPageScreenshot(true);
        eyes.setSaveDebugScreenshots(true);
        eyes.setDebugScreenshotsPath("C:\\temp\\logs");
        try {
            driver.get("https://www.applitools.com");
            // Start visual testing
            eyes.open(driver, "iOS test application", "testJer2");

            // Visual validation point #1
            eyes.checkWindow("Initial view");

            // End visual testing. Validate visual correctness.
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
            driver.quit();
        }
    }
}