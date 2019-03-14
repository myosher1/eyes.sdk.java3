package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.EyesBase;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.config.SeleniumConfiguration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.visualgridclient.services.VisualGridRunner;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class TestVGServerConfigs {
    private final String server1;
    private final String server2;
    private final String key1;
    private final String key2;
    private final boolean isDisabled1;
    private final Boolean isDisabled2;
    private final String expectedServer;
    private final String expectedKey;
    private final boolean expectedDisabled;
    private final String tag;
    private VisualGridRunner renderingManager;


    @Factory(dataProvider = "dp")
    public TestVGServerConfigs(String server1, String server2,
                               String key1, String key2,
                               boolean isDisabled1,
                               Boolean isDisabled2,
                               String expectedServer,
                               String expectedKey,
                               boolean expectedDisabled, String tag) {
        this.server1 = server1;
        this.server2 = server2;
        this.key1 = key1;
        this.key2 = key2;
        this.isDisabled1 = isDisabled1;
        this.isDisabled2 = isDisabled2;
        this.expectedServer = expectedServer;
        this.expectedKey = expectedKey;
        this.expectedDisabled = expectedDisabled;
        this.tag = tag;
    }

    @BeforeClass
    public void beforeClass() {
        renderingManager = new VisualGridRunner(3);
        renderingManager.setLogHandler(new StdoutLogHandler(true));
        renderingManager.getLogger().log("enter");
        renderingManager.setServerUrl(server1);
        renderingManager.setApiKey(this.key1);
        renderingManager.setIsDisabled(isDisabled1);
    }

    @DataProvider(name = "dp")
    public static Object[][] dp() {
        String server1 = "https://eyesapi.applitools.com";
        String server2 = "https://fabricdemoeyes.applitools.com/";
        String key1 = System.getenv("APPLITOOLS_API_KEY");
        String key2 = System.getenv("FABRICAM_DEMO_EYES_API_KEY");
        return new Object[][]{
                {server1, server2, key1, key2, false, false, server2, key2, false, "tag1"},
                {null, server2, key1, key2, false, false, server2, key2, false, "tag2"},
                {null, null, key1, null, false, null, EyesBase.getDefaultServerUrl().toString(), key1, false, "tag3"},
                {null, server2, key1, key2, false, null, server2, key2, false, "tag4"},
                {null, server2, null, key2, false, null, server2, key2, false, "tag5"},
                {null, server2, null, key2, false, true, server2, key2, true, "tag6"},
                {server1, null, key1, null, true, null, server1, key1, true, "tag7"},
                {server1, null, key1, null, true, false, server1, key1, false, "tag8"},
        };
    }

    @Test
    public void test() {
        Eyes eyes = new Eyes(renderingManager);
        eyes.setServerUrl(server2);
        eyes.setApiKey(key2);
        if (isDisabled2 != null) {
            eyes.setIsDisabled(isDisabled2);
        }
        SeleniumConfiguration seleniumConfiguration = new SeleniumConfiguration(3, true, "");
        seleniumConfiguration.addBrowser(100, 100, SeleniumConfiguration.BrowserType.FIREFOX);
        ChromeDriver webDriver = new ChromeDriver();
        try {
            eyes.setConfiguration(seleniumConfiguration);
            eyes.open(webDriver);
            Assert.assertEquals(eyes.getServerUrl().toString(), this.expectedServer);
            Assert.assertEquals(eyes.getApiKey(), this.expectedKey);
            if (eyes.getIsDisabled() != expectedDisabled) {
                System.out.println("TestVGServerConfigs.test - "+ this.tag);
                eyes.getIsDisabled();
            }
            Assert.assertEquals(eyes.getIsDisabled(), this.expectedDisabled);
        } finally {
            webDriver.quit();
        }
    }

}
