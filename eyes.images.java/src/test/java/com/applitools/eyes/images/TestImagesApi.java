package com.applitools.eyes.images;

import com.applitools.eyes.*;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestImagesApi {
    private Eyes eyes;
    private static final String TEST_SUITE_NAME = "Eyes Image SDK";
    private static BatchInfo batchInfo;

    @BeforeClass
    public static void setUpOnce() {
        batchInfo = new BatchInfo(TEST_SUITE_NAME);
    }

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {

            // Initialize the eyes SDK and set your private API key.
            eyes = new Eyes();

//            eyes.setServerUrl(URI.create("https://localhost.applitools.com"));

//            logHandler = new FileLogger("c:\\temp\\logs\\TestElement.log", true, true);
            LogHandler logHandler = new StdoutLogHandler(true);
            eyes.setLogHandler(logHandler);

            eyes.setBatch(batchInfo);

            String testName = description.getMethodName();
            eyes.open(TEST_SUITE_NAME, testName);

            eyes.setDebugScreenshotsPrefix("Java_Images_SDK_" + testName + "_" );
        }

        protected void finished(Description description) {
             eyes.close();
        }

        protected void failed(Throwable e, Description description) {
            eyes.abortIfNotClosed();
        }
    };

    @Test
    public void TestCheckImage() {
        eyes.checkImage("resources/minions-800x500.jpg");
    }

    @Test
    public void TestCheckImage_Fluent() {
        eyes.check("TestCheckImage_Fluent", Target.image("resources/minions-800x500.jpg"));
    }

    @Test
    public void TestCheckImage_WithIgnoreRegion_Fluent() {
        eyes.check("TestCheckImage_WithIgnoreRegion_Fluent", Target.image("resources/minions-800x500.jpg")
                .ignore(new Region(10,20,30,40)));
    }

    @Test
    public void TestCheckImage_Fluent_CutProvider() {
        eyes.setImageCut(new UnscaledFixedCutProvider(200, 100, 100, 50));
        eyes.check("TestCheckImage_Fluent", Target.image("resources/minions-800x500.jpg"));
    }
}
