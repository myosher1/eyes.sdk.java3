package com.applitools.eyes.selenium.rendering;

import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.config.Configuration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public final class TestBasicRenderingConfiguration {
    public static void main(String[] args) throws Exception {

        // Open a Chrome browser.
        WebDriver driver = new ChromeDriver();

        // Initialize the eyes SDK and set your private API key.
        Eyes eyes = new Eyes();

        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setServerUrl("https://test2eyes.applitools.com/");


        Configuration config = new Configuration();
        config.setAppName("DOM Capture RunningTest");
        config.setTestName("This is a Smerf's test");
        RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
        renderingConfiguration.addBrowser(renderingConfiguration.new RenderBrowserInfo(800, 600,  RenderingConfiguration.BrowserType.CHROME));
        renderingConfiguration.addBrowser(renderingConfiguration.new RenderBrowserInfo(800, 600,  RenderingConfiguration.BrowserType.FIREFOX));
        config.setRenderingConfiguration(renderingConfiguration);
        eyes.open(driver, config);

        driver.get("https://www.booking.com/searchresults.en-gb.html?label=gen173nr-1FCAEoggJCAlhYSDNYBGhqiAEBmAEuwgEKd2luZG93cyAxMMgBDNgBAegBAfgBC5ICAXmoAgM;sid=ce4701a88873eed9fbb22893b9c6eae4;city=-2600941;from_idr=1&;ilp=1;d_dcp=1");
        eyes.checkWindow("RunningTest DOM diffs");

        eyes.close();


    }
}