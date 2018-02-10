package com.applitools.eyes.selenium;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TestClassicApi_IE11.class,
        TestClassicApi_IE11_ForceFullPageScreenshot.class,
        TestFluentApi_IE11.class,
        TestFluentApi_IE11_ForceFullPageScreenshot.class
})
public class SeleniumIE11TestsSuite {
}
