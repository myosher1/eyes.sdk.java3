package com.applitools.eyes.demo;

import com.applitools.eyes.TestResults;
import com.applitools.eyes.config.SeleniumConfiguration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgridclient.model.EmulationDevice;
import com.applitools.eyes.visualgridclient.model.EmulationInfo;
import com.applitools.eyes.visualgridclient.model.ScreenOrientation;
import com.applitools.eyes.visualgridclient.model.TestResultSummary;
import com.applitools.eyes.visualgridclient.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class MinimalVisualGridDemo {

    public static void main(String[] args){
        MinimalVisualGridDemo program = new MinimalVisualGridDemo();
        program.run();
    }

    private void run()
    {
        // Create a new webdriver
        WebDriver webDriver = new ChromeDriver();

        // Navigate to the url we want to test
        webDriver.get("https://applitools.com/helloworld");

        // Create a runner with concurrency of 10
        VisualGridRunner runner = new VisualGridRunner(10);

        // Create Eyes object with the runner, meaning it'll be a Visual Grid eyes.
        Eyes eyes = new Eyes(runner);

        // Create SeleniumConfiguration.
        SeleniumConfiguration sconf = new SeleniumConfiguration();

        // Set test name
        sconf.setTestName("Visual Grid Demo");

        // Set app name
        sconf.setAppName("Visual Grid Demo");

        // Add browsers
        sconf.addBrowser(800, 600, SeleniumConfiguration.BrowserType.CHROME);
        sconf.addBrowser(700, 500, SeleniumConfiguration.BrowserType.CHROME);
        sconf.addBrowser(1200, 800, SeleniumConfiguration.BrowserType.CHROME);
        sconf.addBrowser(1600, 1200, SeleniumConfiguration.BrowserType.CHROME);

        // Add iPhone 4 device emulation
        EmulationInfo iphone4 = new EmulationInfo(EmulationInfo.DeviceName.iPhone_4, ScreenOrientation.PORTRAIT);
        sconf.addDeviceEmulation(iphone4);

        // Add custom mobile device emulation
        EmulationDevice customMobile = new EmulationDevice(1024, 768, 2, true, ScreenOrientation.LANDSCAPE);
        sconf.addDeviceEmulation(customMobile);

        // Set the configuration object to eyes
        eyes.setConfiguration(sconf);

        // Call Open on eyes to initialize a test session
        eyes.open(webDriver);

        // Add 2 checks
        eyes.check(Target.window().withName("Step 1 - Viewport"));
        eyes.check(Target.window().fully().withName("Step 1 - Full Page"));

        webDriver.findElement(By.tagName("button")).click();

        // Add 2 checks
        eyes.check(Target.window().withName("Step 2 - Viewport"));
        eyes.check(Target.window().fully().withName("Step 2 - Full Page"));

        // Close the browser
        webDriver.quit();

        // Close eyes and collect results
        TestResults results = eyes.close();
        TestResultSummary allTestResults = runner.getAllTestResults();
    }
}
