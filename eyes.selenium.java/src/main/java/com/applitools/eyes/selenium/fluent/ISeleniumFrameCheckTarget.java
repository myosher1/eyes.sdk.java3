package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.selenium.IScrollRootElementContainer;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface ISeleniumFrameCheckTarget extends IScrollRootElementContainer {
    Integer getFrameIndex();
    String getFrameNameOrId();
    By getFrameSelector();
    WebElement getFrameReference();
}
