package com.applitools.eyes.selenium;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestsDataProvider {
    @DataProvider(parallel = true)
    public static Object[][] dp() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("disable-infobars");

        FirefoxOptions firefoxOptions = new FirefoxOptions();

        DesiredCapabilities ie11Options = DesiredCapabilities.internetExplorer();
        ie11Options.setCapability(CapabilityType.BROWSER_VERSION, "11");

        SafariOptions safariOptions = new SafariOptions();

        if (!System.getenv("SELENIUM_SERVER_URL").contains("ondemand.saucelabs.com")) {
            chromeOptions.setHeadless(true);
            firefoxOptions.setHeadless(true);
        }

        String testPlatforms = System.getenv("APPLITOOLS_TEST_PLATFORMS");
        if (testPlatforms == null || testPlatforms.isEmpty()) {
            testPlatforms = System.getProperty("os.name");
        }

        Object[] platforms = testPlatforms.split(";");

        List<List<Object>> lists = new ArrayList<>();
        lists.add(Arrays.asList(new Object[]{chromeOptions, firefoxOptions, ie11Options, /*safariOptions*/}));
        lists.add(Arrays.asList(platforms));
        lists.add(Arrays.asList(new Object[]{false, true})); // force fps

        return TestUtils.generatePermutations(lists);
    }
}
