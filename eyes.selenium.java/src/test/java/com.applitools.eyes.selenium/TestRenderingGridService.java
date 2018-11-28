package com.applitools.eyes.selenium;

import com.applitools.eyes.Logger;
import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.rendering.CheckRGSettings;
import com.applitools.eyes.rendering.Eyes;
import com.applitools.eyes.visualGridClient.data.RenderingConfiguration;
import com.applitools.eyes.visualGridClient.RenderingGridManager;
import com.applitools.eyes.visualGridClient.data.TestResultSummery;
import com.applitools.utils.GeneralUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.Future;

public final class TestRenderingGridService {
    public static void main(String[] args) {
        WebDriver webDriver = null;
        RenderingGridManager renderingManager = null;
        try {
            webDriver = new ChromeDriver();
            webDriver.get("https://nikita-andreev.github.io/applitools/dom_capture.html?aaa");
            RenderingConfiguration renderingConfiguration = new RenderingConfiguration();
            Logger logger = new Logger();
            logger.setLogHandler(new StdoutLogHandler());
            renderingConfiguration.setTestName("Open Concurrency");
            renderingConfiguration.setAppName("RenderingGridIntegration");
            renderingConfiguration.addBrowser(800, 600, RenderingConfiguration.BrowserType.CHROME);
            renderingConfiguration.addBrowser(400, 300, RenderingConfiguration.BrowserType.FIREFOX);
            renderingManager = new RenderingGridManager(3, logger);
            Eyes eyes = new Eyes(renderingManager, renderingConfiguration, logger);
            eyes.setProxy(new ProxySettings("http://127.0.0.1:8888"));
            eyes.setLogHandler(new StdoutLogHandler(true));
            eyes.setServerUrl("https://eyes.applitools.com/");

            eyes.open(webDriver);
            eyes.check(new CheckRGSettings());
            List<Future<TestResults>> close = eyes.close();
            for (Future<TestResults> future : close) {
                future.get();
            }

        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(e);
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
            // End the test.
            TestResultSummery allTestResults = renderingManager.getAllTestResults();
            System.out.println(allTestResults);
        }



    }
}