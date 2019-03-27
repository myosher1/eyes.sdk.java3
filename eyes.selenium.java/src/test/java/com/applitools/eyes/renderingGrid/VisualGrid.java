package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.FileLogger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgridclient.model.TestResultSummary;
import com.applitools.eyes.visualgridclient.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

public class VisualGrid {

    private VisualGridRunner VisualGrid = new VisualGridRunner(10);

    private Eyes eyes = new Eyes(VisualGrid);

    private WebDriver driver;


    @BeforeClass
    protected void init(){
        FileLogger logHandler = new FileLogger("sophiLog.log", false, true);
        eyes.setLogHandler(logHandler);
        eyes.setBranchName("Child Branch");
        VisualGrid.setLogger(eyes.getLogger());
        Configuration configuration = eyes.getConfiguration();
        configuration.setBatch(new BatchInfo("example vg"));
    }

    @BeforeMethod
    public void setUp(){
        driver = new ChromeDriver();
        Configuration configuration = eyes.getConfiguration();
        configuration.addBrowser(800, 600, Configuration.BrowserType.CHROME);
//        configuration.addBrowser(700, 500, Configuration.BrowserType.CHROME);
//        configuration.addBrowser(1200, 800, Configuration.BrowserType.CHROME);
//        configuration.addBrowser(1600, 1200, Configuration.BrowserType.CHROME);
        eyes.setConfiguration(configuration);
    }

    @Test
    public void HelloWorld() {

        try {
            driver.get("https://applitools.com/helloworld");

            eyes.open(driver, "app", "test", new RectangleSize(600, 600));
            eyes.check("first check", Target.window().withName("Step 1 A"));
            eyes.check("second check", Target.window().fully(false).withName("Step 1 B"));
            driver.findElement(By.tagName("button")).click();
            eyes.check("third check", Target.window().withName("Step 2 A"));
            eyes.check("forth check", Target.window().fully(false).withName("Step 2 B"));

            eyes.check(Target.window());

            eyes.close();


            TestResultSummary allTestResults = VisualGrid.getAllTestResults();
            System.out.println("Results: " + allTestResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Github(){


        driver.get("https://github.com/");
        eyes.open(driver, "app", "test", new RectangleSize(600, 600));

        eyes.check(Target.window().fully());

        eyes.close();

//        TestResultSummary allTestResults = VisualGrid.getAllTestResults();
//        System.out.println("Results: " + allTestResults);
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @AfterClass
    public void closeRunner() {
        TestResultSummary allTestResults = this.VisualGrid.getAllTestResults();
        String message = allTestResults.toString();
        eyes.getLogger().verbose(message);
    }
}