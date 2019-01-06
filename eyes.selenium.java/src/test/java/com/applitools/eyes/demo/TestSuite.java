package com.applitools.eyes.demo;

import com.applitools.ICheckSettings;
import com.applitools.eyes.*;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.eyes.selenium.config.Configuration;
import com.applitools.eyes.visualGridClient.model.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.services.VisualGridManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestSuite {

    private IEyes eyes;
    private WebDriver webDriver;
    private ICheckSettings checkSettings;
    private VisualGridManager visualGridManager;

    public TestSuite() {
        this.webDriver = new ChromeDriver();
    }

    @BeforeClass
    public void before_VisualGrid() {
        visualGridManager = new VisualGridManager(4);
        visualGridManager.setLogHandler(new StdoutLogHandler(true));

        visualGridManager.setServerUrl("https://fabricdemoeyes.applitools.com/");
        visualGridManager.setApiKey(System.getenv("FABRICAM_DEMO_EYES_API_KEY"));

        com.applitools.eyes.rendering.Eyes eyes = new com.applitools.eyes.rendering.Eyes(visualGridManager);
        eyes.setBatch(new BatchInfo("Applitools Visual Grid Demo"));

        //eyes.setProxy(new ProxySettings("http://127.0.0.1", 8888));

        RenderingConfiguration configuration = new RenderingConfiguration();
        initConfig(configuration);
        configuration.setTestName("Applitools Visual Grid Demo");

        configuration.addBrowser(800, 600, RenderingConfiguration.BrowserType.CHROME);
        configuration.addBrowser(1200, 800, RenderingConfiguration.BrowserType.CHROME);
        configuration.addBrowser(1920, 1000, RenderingConfiguration.BrowserType.CHROME);

        eyes.open(webDriver, configuration);
        this.eyes = eyes;

        this.checkSettings = com.applitools.eyes.rendering.Target.window();
    }

//    @BeforeClass
//    public void before_LocalRendering() {
//        com.applitools.eyes.selenium.Eyes eyes = new com.applitools.eyes.selenium.Eyes();
//        eyes.setLogHandler(new StdoutLogHandler(true));
//        eyes.setBatch(new BatchInfo("Applitools Local Rendering Demo"));
//
//        Configuration configuration = new Configuration();
//        initConfig(configuration);
//
//        configuration.setTestName("Applitools Local Rendering Demo");
//        configuration.setStitchMode(StitchMode.CSS);
//        configuration.setViewportSize(new RectangleSize(1200, 800));
//
//        eyes.open(webDriver, configuration);
//        this.eyes = eyes;
//
//        this.checkSettings = com.applitools.eyes.selenium.fluent.Target.window();
//    }

    private void initConfig(com.applitools.eyes.config.Configuration configuration) {
        configuration.setAppName("Applitools Java3 Demo");
    }

    @Test
    public void test() {
        webDriver.get("https://applitools.github.io/demo/TestPages/FramesTestPage");
        eyes.check("check window", checkSettings);
    }

    @AfterMethod
    public void close() {
        try {
            eyes.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            eyes.abortIfNotClosed();
            webDriver.quit();
        }
    }

    @AfterClass
    public void collectResults() {
        if (visualGridManager != null) {
            eyes.getLogger().log(visualGridManager.getAllTestResults().toString());
        }
    }
}

