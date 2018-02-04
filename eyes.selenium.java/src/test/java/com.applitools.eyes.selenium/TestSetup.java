package com.applitools.eyes.selenium;

import com.applitools.eyes.*;
import com.applitools.eyes.metadata.ActualAppOutput;
import com.applitools.eyes.metadata.SessionResults;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

@RunWith(JUnit4.class)
public abstract class TestSetup {

    private static LogHandler logHandler;

    protected static Eyes eyes;
    protected static WebDriver driver;
    protected static WebDriver webDriver;

    protected static String testSuitName;

    protected static String testedPageUrl = "http://applitools.github.io/demo/TestPages/FramesTestPage/";

    protected static boolean forceFullPageScreenshot = false;
    protected static boolean runRemotely = true;
    protected static boolean hideScrollbars = true;
    protected static DesiredCapabilities caps;

    private HashSet<FloatingMatchSettings> expectedFloatingsSet = new HashSet<>();

    @BeforeClass
    public static void OneTimeSetUp() {

        // Initialize the eyes SDK and set your private API key.
        eyes = new Eyes();
//        eyes.setServerUrl(URI.create("https://localhost.applitools.com"));
        eyes.setApiKey(System.getenv("APPLITOOLS_API_KEY"));

        //logHandler = new FileLogger("c:\\temp\\logs\\TestElement.log", true, true);
        logHandler = new StdoutLogHandler(true);
        //eyes.setLogHandler(logHandler);
        eyes.setForceFullPageScreenshot(forceFullPageScreenshot);
        eyes.setStitchMode(StitchMode.CSS);

        eyes.setHideScrollbars(true);

//        eyes.setDebugScreenshotsPath("c:\\temp\\logs");
//        eyes.setSaveDebugScreenshots(true);
        eyes.setBatch(new BatchInfo(testSuitName));
    }

    protected void setExpectedFloatingsRegions(FloatingMatchSettings... floatingMatchSettings){
        this.expectedFloatingsSet = new HashSet<>(Arrays.asList(floatingMatchSettings));
    }

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {

            if (runRemotely) {
                try {
                    caps.setCapability("username", System.getenv("SAUCE_USERNAME"));
                    caps.setCapability("accesskey", System.getenv("SAUCE_ACCESS_KEY"));

                    webDriver = new RemoteWebDriver(new URL(System.getenv("SELENIUM_SERVER_URL")), caps);
                } catch (MalformedURLException ex) { }
            }

            driver = eyes.open(webDriver,
                    testSuitName,
                    description.getMethodName(),
                    new RectangleSize(800, 600)
            );

            driver.navigate().to(testedPageUrl);
            //eyes.getPositionProvider().setPosition(new Location(100,200));

            eyes.setDebugScreenshotsPrefix("Java_" + description.getMethodName() + "_" );
        }

        protected void finished(Description description) {
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
                FloatingMatchSettings[] floating = actualAppOutput[0].getImageMatchSettings().getFloating();
                if (expectedFloatingsSet.size() > 0) {
                    HashSet<FloatingMatchSettings> floatingsSet = new HashSet<>(Arrays.asList(floating));
                    Assert.assertTrue("Floating regions lists differ", CollectionUtils.isEqualCollection(expectedFloatingsSet, floatingsSet));
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                eyes.abortIfNotClosed();
                driver.quit();
            }
        }
    };
}
