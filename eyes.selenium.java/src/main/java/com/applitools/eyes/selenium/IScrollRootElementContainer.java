package com.applitools.eyes.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface IScrollRootElementContainer {
    By getScrollRootSelector();
    WebElement getScrollRootElement();
}
