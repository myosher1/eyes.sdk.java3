package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.selenium.rendering.Eyes;
import com.applitools.eyes.selenium.rendering.RenderingConfiguration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public final class TestRenderingGridService {
    public static void main(String[] args) throws Exception {

        // Open a Chrome browser.
        WebDriver driver = new ChromeDriver();


        RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
        renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.CHROME);
        renderingConfiguration.addBrowser(400, 300, RenderingConfiguration.BrowserType.FIREFOX);

        Eyes eyes = new Eyes(renderingConfiguration);

        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setServerUrl("https://eyes.applitools.com/");

        eyes.open();

        // End the test.
        eyes.close();


    }
}