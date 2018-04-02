package com.applitools.eyes.selenium;

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.CapabilityType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Test(alwaysRun = true)
@Listeners(TestListener.class)
public class TestFluentApi_IE11 extends TestFluentApi {

    @BeforeClass(alwaysRun = true)
    public void beforeClass() {
        DesiredCapabilities ieCaps = DesiredCapabilities.internetExplorer();
        ieCaps.setCapability(CapabilityType.BROWSER_VERSION, "11");
        caps = ieCaps;
    }
}
