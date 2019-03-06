package com.applitools.eyes.selenium;

import com.applitools.IDomCaptureListener;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.StdoutLogHandler;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.CountDownLatch;

public final class TestDomCapture {
    private static String domJson;
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
        driver.get("https://applitools.com/");
//        driver.get("https://nikita-andreev.github.io/applitools/dom_capture.html?aaa");
//        Thread.sleep(5000);

        final CountDownLatch latch = new CountDownLatch(1);
        eyes.setOnDomCapture(new IDomCaptureListener() {
            @Override
            public void onDomCaptureComplete(String dom) {
                domJson = dom;
                latch.countDown();
            }
        });

        eyes.checkWindow("Test DOM diffs");

        latch.await();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode targetJsonObj = mapper.readTree(domJson);

        String sourceJsonAsString = GeneralUtils.readToEnd(TestDomCapture.class.getResourceAsStream("/dom_fullpage.json"));
        JsonNode sourceJsonObj = mapper.readTree(sourceJsonAsString);

        driver.quit();
        eyes.close(false);

        if(!sourceJsonObj.equals(targetJsonObj)){
            throw new Exception("Dom capture json was not equal to target json");
        }

    }
}