package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeClass;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

public abstract class TestSetup {

    protected Eyes eyes;
    protected WebDriver driver;
    protected WebDriver webDriver;

    protected String testSuitName;

    protected String testedPageUrl = "http://applitools.github.io/demo/TestPages/FramesTestPage/";
    protected RectangleSize testedPageSize = new RectangleSize(800, 600);

    protected Capabilities caps;
    private DesiredCapabilities desiredCaps = new DesiredCapabilities();

    private static BatchInfo batchInfo = new BatchInfo("Java3 Tests");

    protected HashSet<FloatingMatchSettings> expectedFloatingRegions = new HashSet<>();
    protected HashSet<Region> expectedIgnoreRegions = new HashSet<>();

    protected boolean compareExpectedRegions = false;

    protected String platform;
    protected boolean forceFPS;

    @BeforeClass(alwaysRun = true)
    public void OneTimeSetUp() {

        // Initialize the eyes SDK and set your private API key.
        eyes = new Eyes();

        LogHandler logHandler = new StdoutLogHandler(true);

        eyes.setLogHandler(logHandler);
        eyes.setStitchMode(StitchMode.CSS);

        eyes.setHideScrollbars(true);

//        if (System.getenv("CI") == null) {
//            eyes.setDebugScreenshotsPath("c:\\temp\\logs");
//            eyes.setSaveDebugScreenshots(true);
//        }

        String batchId = System.getenv("APPLITOOLS_BATCH_ID");
        if (batchId != null) {
            batchInfo.setId(batchId);
        }

        eyes.setBatch(batchInfo);
    }

    protected void setExpectedIgnoreRegions(Region... expectedIgnoreRegions) {
        this.expectedIgnoreRegions = new HashSet<>(Arrays.asList(expectedIgnoreRegions));
    }

    protected void setExpectedFloatingsRegions(FloatingMatchSettings... expectedFloatingsRegions) {
        this.expectedFloatingRegions = new HashSet<>(Arrays.asList(expectedFloatingsRegions));
    }

    public void beforeMethod(String methodName) {
        String seleniumServerUrl = System.getenv("SELENIUM_SERVER_URL");
        if (seleniumServerUrl.equalsIgnoreCase("http://ondemand.saucelabs.com/wd/hub")) {
            desiredCaps.setCapability("username", System.getenv("SAUCE_USERNAME"));
            desiredCaps.setCapability("accesskey", System.getenv("SAUCE_ACCESS_KEY"));
            desiredCaps.setCapability("platform", platform);

            caps.merge(desiredCaps);
        }

        try {
            webDriver = new RemoteWebDriver(new URL(seleniumServerUrl), caps);
        } catch (MalformedURLException ignored) {
        }

        String fps = forceFPS ? "_FPS" : "";
        String testName = methodName + fps;
        testName = testName.replace('[', '_')
                .replace(' ', '_')
                .replace("]", "");

        String logFilePath = "c:\\temp\\logs\\java_" + testName + "_" + platform + ".log";
        LogHandler logHandler = new FileLogger(logFilePath, false, true);

        eyes.setLogHandler(logHandler);
        eyes.addProperty("ForceFPS", forceFPS ? "true" : "false");

        driver = eyes.open(webDriver,
                testSuitName,
                testName,
                testedPageSize
        );

        driver.get(testedPageUrl);

        eyes.setForceFullPageScreenshot(forceFPS);
        eyes.setDebugScreenshotsPrefix("java_" + testName + "_");

        this.expectedIgnoreRegions.clear();
        this.expectedFloatingRegions.clear();
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s, force FPS: %s)",
                this.getClass().getSimpleName(),
                this.caps.getBrowserName(),
                this.platform,
                this.forceFPS);
    }
}
