package com.applitools.eyes.selenium;

import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.utils.TestUtils;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.applitools.eyes.selenium.TestDataProvider.*;

public class Android2Test {

    @DataProvider(parallel = true)
    public static Object[][] data() {
        Object[][] googlePixelPermutations = TestUtils.generatePermutations(
                Arrays.asList(new Object[]{"Google Pixel GoogleAPI Emulator"}), // device
                Arrays.asList(new Object[]{"portrait", "landscape"}), // orientation
                Arrays.asList(new Object[]{"7.1"}), // OS Version
                Arrays.asList(new Object[]{false, true}) // fully
        );

        ArrayList<Object[]> returnValue = new ArrayList<>();
        returnValue.addAll(Arrays.asList(googlePixelPermutations));

        return returnValue.toArray(new Object[0][]);
    }

    @Test(dataProvider = "data")
    public void TestAndroidChromeCrop(String deviceName, String deviceOrientation, String platformVersion, boolean fully) throws MalformedURLException {
        Eyes eyes = new Eyes();

        eyes.setBatch(TestDataProvider.batchInfo);

        // This is your api key, make sure you use it in all your tests.
        DesiredCapabilities caps = DesiredCapabilities.iphone();
        caps.setCapability("deviceName", deviceName);
        caps.setCapability("deviceOrientation", deviceOrientation);
        caps.setCapability("platformVersion", platformVersion);
        caps.setCapability("platformName", "Android");
        caps.setCapability("browserName", "Chrome");

        caps.setCapability("username",  SAUCE_USERNAME);
        caps.setCapability("accesskey", SAUCE_ACCESS_KEY);


        WebDriver driver = new RemoteWebDriver(new URL(SAUCE_SELENIUM_URL), caps);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        eyes.setLogHandler(new StdoutLogHandler(TestUtils.verboseLogs));

        String testName = String.format("%s %s %s", deviceName, platformVersion, deviceOrientation);
        if (fully) {
            testName += " fully";
        }

        TestUtils.setupLogging(eyes, testName);

        eyes.setStitchMode(StitchMode.SCROLL);

        eyes.addProperty("Orientation", deviceOrientation);
        eyes.addProperty("Stitched", fully ? "True" : "False");

        try {
            driver.get("https://www.applitools.com/customers");
            eyes.open(driver, "Eyes Selenium SDK - Android Chrome Cropping", testName);
            eyes.check("Initial view", Target.region(By.cssSelector("body")).fully(fully));
            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
            driver.quit();
        }
    }

    @Test
    public void setUp() throws IOException {
        Eyes eyes = new Eyes();
        //eyes.setServerUrl(APPLITOOLS_SERVER_URL);
        // StitchMode SCROLL will fail as well (with nullpointer exception)
        eyes.setStitchMode(StitchMode.CSS);

        MutableCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", "Chrome");
        capabilities.setCapability("deviceName", "Android Emulator");
        capabilities.setCapability("deviceType", "tablet");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("platformVersion", "6.0");
        capabilities.setCapability("name", "Minimal test");
        //capabilities.setCapability("tunnelIdentifier", SAUCE_LABS_TUNNEL_IDENTIFIER);
        capabilities.setCapability("username", SAUCE_USERNAME);
        capabilities.setCapability("accesskey", SAUCE_ACCESS_KEY);

        WebDriver driver = new AppiumDriver<>(new URL(SAUCE_SELENIUM_URL), capabilities);

        //works
        //driver = new RemoteWebDriver(new URL(new URL(SAUCE_LABS_URL), capabilities);

        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

        try {
            driver = eyes.open(driver, "NS.nl", "Minimal test");

//            LOGGER.verbose("Open url {}.", URL);
//            driver.get(URL);

            // Will fail with "java.lang.IllegalArgumentException: scrollRootElement is null"
            // at com.applitools.utils.ArgumentGuard.notNull(ArgumentGuard.java:19)
            eyes.check(Target.window().fully());

            eyes.close();
        } finally {
            eyes.abortIfNotClosed();
            driver.quit();
        }
    }


}