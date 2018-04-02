package com.applitools.eyes.selenium;

import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.Region;
import com.applitools.eyes.TestResults;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.ImageMatchSettings;
import com.applitools.eyes.metadata.SessionResults;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;

public class TestListener implements ITestListener {

    private TestSetup testSetup;

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("onTestStart");
        this.testSetup = ((TestSetup) result.getInstance());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("onTestSuccess");
        afterMethod();
    }

    @Override
    public void onTestFailure(ITestResult testResult) {
        System.out.println("onTestFailure");
        afterMethod();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("onTestSkipped");
        afterMethod();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        System.out.println("onTestFailedButWithinSuccessPercentage");
        afterMethod();
    }

    private void afterMethod() {
        Eyes eyes = testSetup.eyes;
        try {
            TestResults results = eyes.close();
            String apiSessionUrl = results.getApiUrls().getSession();
            URI apiSessionUri = UriBuilder.fromUri(apiSessionUrl)
                    .queryParam("format", "json")
                    .queryParam("AccessToken", results.getSecretToken())
                    .queryParam("apiKey", eyes.getApiKey())
                    .build();

            Client client = ClientBuilder.newClient();
            String srStr = client.target(apiSessionUri)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class);

            ObjectMapper jsonMapper = new ObjectMapper();
            jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            SessionResults resultObject = jsonMapper.readValue(srStr, SessionResults.class);

            ActualAppOutput[] actualAppOutput = resultObject.getActualAppOutput();

            ImageMatchSettings imageMatchSettings = actualAppOutput[0].getImageMatchSettings();
            FloatingMatchSettings[] floating = imageMatchSettings.getFloating();
            Region[] ignoreRegions = imageMatchSettings.getIgnore();

            if (testSetup.expectedFloatingRegions.size() > 0) {
                HashSet<FloatingMatchSettings> floatingRegionsSet = new HashSet<>(Arrays.asList(floating));
                Assert.assertEquals(floatingRegionsSet, testSetup.expectedFloatingRegions, "Floating regions lists differ");
            }

            if (testSetup.expectedIgnoreRegions.size() > 0) {
                HashSet<Region> ignoreRegionsSet = new HashSet<>(Arrays.asList(ignoreRegions));
                Assert.assertEquals(ignoreRegionsSet, testSetup.expectedIgnoreRegions, "Ignore regions lists differ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            eyes.abortIfNotClosed();
            testSetup.driver.quit();
        }
    }


    @Override
    public void onStart(ITestContext context) {
        System.out.println("onStart");
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("onFinish");
    }

}