package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.EyesBase;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.services.VisualGridRunner;
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
    private VisualGridRunner renderingManager;


    @Factory(dataProvider = "dp")
    public TestVGServerConfigs(String server1, String server2,
                               String key1, String key2,
                               boolean isDisabled1,
                               Boolean isDisabled2,
                               String expectedServer,
                               String expectedKey,
                               boolean expectedDisabled) {
        this.server1 = server1;
        this.server2 = server2;
        this.key1 = key1;
        this.key2 = key2;
        this.isDisabled1 = isDisabled1;
        this.isDisabled2 = isDisabled2;
        this.expectedServer = expectedServer;
        this.expectedKey = expectedKey;
        this.expectedDisabled = expectedDisabled;
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
                {server1, server2, key1, key2, false, false, server2, key2, false},
                {null, server2, key1, key2, false, false, server2, key2, false},
                {null, null, key1, null, false, null, EyesBase.getDefaultServerUrl().toString(), key1, false},
                {null, server2, key1, key2, false, null, server2, key2, false},
                {null, server2, null, key2, false, null, server2, key2, false},
                {null, server2, null, key2, false, true, server2, key2, true},
                {server1, null, key1, null, true, null, server1, key1, true},
                {server1, null, key1, null, true, false, server1, key1, false},
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
        RenderingConfiguration renderingConfiguration = new RenderingConfiguration(3, true, "");
        renderingConfiguration.addBrowser(100, 100, RenderingConfiguration.BrowserType.FIREFOX);
        ChromeDriver webDriver = new ChromeDriver();
        try {
            eyes.open(webDriver, renderingConfiguration);
            Assert.assertEquals(eyes.getServerUrl().toString(), this.expectedServer);
            Assert.assertEquals(eyes.getApiKey(), this.expectedKey);
            Assert.assertEquals(eyes.getIsDisabled(), this.expectedDisabled);
        } finally {
            webDriver.quit();
        }
    }

}
