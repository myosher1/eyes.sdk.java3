package com.applitools.eyes.renderingGrid;

import com.applitools.eyes.ProxySettings;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.services.EyesRunner;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class TestVisualGridRCA {
    @Test
    public void test_VG_RCA() {
        EyesRunner runner = new VisualGridRunner(10);
        Eyes eyes = new Eyes(runner);
        eyes.setProxy(new ProxySettings("http://127.0.0.1",8888));
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://applitools.github.io/demo/TestPages/VisualGridTestPage");
            eyes.open(driver, "Test Visual Grid", "Test RCA");
            eyes.setSendDom(true);
            eyes.check(Target.window());
            eyes.close();
            runner.getAllTestResults();
        }
        finally {
            driver.quit();
        }
    }
}
