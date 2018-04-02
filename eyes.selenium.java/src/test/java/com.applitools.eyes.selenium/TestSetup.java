package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


@Test(dataProvider = "data", alwaysRun = true)
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

    @DataProvider(parallel = false)
    public static Object[][] data() {
        String testPlatforms = System.getenv("APPLITOOLS_TEST_PLATFORMS");
        if (testPlatforms == null || testPlatforms.isEmpty()) {
            testPlatforms = System.getProperty("os.name");
        }

        Object[] platforms = testPlatforms.split(";");

        List<List<Object>> lists = new ArrayList<>();
        lists.add(Arrays.asList(platforms));
        lists.add(Arrays.asList(new Object[]{false, true})); // force fps

        return TestUtils.generatePermutations(lists);
    }

    @BeforeClass(alwaysRun = true)
    public void OneTimeSetUp() {

        // Initialize the eyes SDK and set your private API key.
        eyes = new Eyes();

        LogHandler logHandler = new StdoutLogHandler(true);

        eyes.setLogHandler(logHandler);
        eyes.setStitchMode(StitchMode.CSS);

        eyes.setHideScrollbars(true);

        if (System.getenv("CI") == null) {
            eyes.setDebugScreenshotsPath("c:\\temp\\logs");
            //eyes.setSaveDebugScreenshots(true);
        }

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

    //@BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method, Object[] parameters) {
        assert parameters != null;
        String platform = (String) parameters[0];

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

        boolean forceFPS = (boolean) parameters[1];

        String fps = forceFPS ? "_FPS" : "";
        String testName = method.getName() + fps;
        testName = testName.replace('[', '_')
                .replace(' ', '_')
                .replace("]", "");

        LogHandler logHandler = new FileLogger("c:\\temp\\logs\\java_" + testName + "_" + platform + ".log", true, true);
        eyes.setLogHandler(logHandler);

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
}
