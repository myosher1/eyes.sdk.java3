package com.applitools.eyes.selenium;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public final class TestDomCapture {
    public static void main(String[] args) throws Exception {

        // Open a Chrome browser.
        WebDriver driver = new ChromeDriver();

        // Initialize the VisualGridEyes SDK and set your private API key.
        Eyes eyes = new Eyes();

        eyes.setLogHandler(new StdoutLogHandler(true));
        eyes.setServerUrl("https://eyes.applitools.com/");
//        eyes.setProxy(new ProxySettings("http://127.0.0.1:8888"));

        // Switch sendDom flag on
        eyes.setSendDom(true);
        BatchInfo batchInfo = new BatchInfo("TTS - config batch");
        batchInfo.setId("RCA_Batch_ID");
        eyes.setBatch(batchInfo);
//        try {

        eyes.open(driver, "DOM Capture with config Test", "DOM Capture V2 - usa today",
                new RectangleSize(800, 600));

        // Navigate the browser to the "hello world!" web-site.
//        driver.get("https://www.usatoday.com");
        driver.get("http://applitools.com");
//        driver.get("https://nikita-andreev.github.io/applitools/dom_capture.html?aaa");
//        Thread.sleep(5000);

//        WebElement element = driver.findElement(By.className("video-container"));
//        eyes.checkElement(element, "Test DOM diffs");

        eyes.checkWindow();

        driver.quit();
        eyes.close(true);


    }
}