package com.applitools.eyes.selenium;

import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;

public final class TestDomCapture {
    public static void main(String[] args) throws Exception {

        // Open a Chrome browser.
        WebDriver driver = new ChromeDriver();

        // Initialize the eyes SDK and set your private API key.
        Eyes eyes = new Eyes();

        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setServerUrl("https://test2eyes.applitools.com/");
        eyes.setProxy(new ProxySettings("http://127.0.0.1:8888"));

        eyes.setApiKey("OeBApoq9uPosO110ICV7AlL3ANe3bBAdYv5100MbHgpdAm8110");

        // Switch sendDom flag on
        eyes.setSendDom(true);
        try {

            eyes.open(driver, "DOM Capture Test", "This is a Smerf's test",
                    new RectangleSize(800, 600));

            // Navigate the browser to the "hello world!" web-site.
            driver.get("http://applitools.github.io/demo/TestPages/DomTest/dom_capture.html");
            eyes.checkWindow("Test DOM diffs");

            String domJson = eyes.tryCaptureDom();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode targetJsonObj = mapper.readTree(domJson);

            String sourceJsonAsString = GeneralUtils.readToEnd(TestDomCapture.class.getResourceAsStream("/domcapture.json"));
            JsonNode sourceJsonObj = mapper.readTree(sourceJsonAsString);

            if(!sourceJsonObj.equals(targetJsonObj)){
                throw new Exception("Dom capture json was not equal to target json");
            }

            // End the test.
            eyes.close();

        } catch (JsonProcessingException e) {

            eyes.abortIfNotClosed();
            eyes.getDriver().quit();

        } catch (IOException e) {
            eyes.abortIfNotClosed();
            eyes.getDriver().quit();
        } finally {

            // Close the browser.
            driver.quit();

            // If the test was aborted before eyes.close was called, ends the test as aborted.
            eyes.abortIfNotClosed();
        }

    }


}