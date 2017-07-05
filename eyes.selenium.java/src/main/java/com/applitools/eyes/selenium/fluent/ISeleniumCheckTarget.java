package com.applitools.eyes.selenium.fluent;

import org.openqa.selenium.By;
import java.util.List;

public interface ISeleniumCheckTarget {
    By getTargetSelector();
    List<FrameLocator> getFrameChain();
}
