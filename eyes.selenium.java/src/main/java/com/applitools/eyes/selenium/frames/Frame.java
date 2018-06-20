/*
 * Applitools software.
 */
package com.applitools.eyes.selenium.frames;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.EyesSeleniumUtils;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Encapsulates a frame/iframe. This is a generic type class,
 * and it's actual type is determined by the reference used by the user in
 * order to switch into the frame.
 */
public final class Frame {
    // A user can switch into a frame by either its name,
    // index or by passing the relevant web element.
    private final Logger logger;
    private final WebElement reference;
    private final Location location;
    private final RectangleSize size;
    private final RectangleSize innerSize;
    private final Location originalLocation;
    private String originalOverflow;
    private final JavascriptExecutor jsExecutor;
    private WebElement scrollRootElement;

    /**
     * @param logger           A Logger instance.
     * @param reference        The web element for the frame, used as a reference to switch into the frame.
     * @param location         The location of the frame within the current frame.
     * @param size             The frame element size (i.e., the size of the frame on the screen, not the internal document size).
     * @param innerSize        The frame element inner size (i.e., the size of the frame actual size, without borders).
     * @param originalLocation The scroll location of the frame.
     * @param jsExecutor       The Javascript Executor to use. Usually that will be the WebDriver.
     */
    public Frame(Logger logger, WebElement reference,
                 Location location, RectangleSize size, RectangleSize innerSize,
                 Location originalLocation,
                 JavascriptExecutor jsExecutor) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(reference, "reference");
        ArgumentGuard.notNull(location, "location");
        ArgumentGuard.notNull(size, "size");
        ArgumentGuard.notNull(innerSize, "innerSize");
        ArgumentGuard.notNull(originalLocation, "originalLocation");
        ArgumentGuard.notNull(jsExecutor, "jsExecutor");

        logger.verbose(String.format(
                "Frame(logger, reference, %s, %s, %s, %s)",
                location, size, innerSize, originalLocation));

        this.logger = logger;
        this.reference = reference;
        this.location = location;
        this.size = size;
        this.innerSize = innerSize;
        this.originalLocation = originalLocation;
        this.jsExecutor = jsExecutor;
    }

    public WebElement getReference() {
        return reference;
    }

    public Location getLocation() {
        return location;
    }

    public RectangleSize getSize() {
        return size;
    }

    public RectangleSize getInnerSize() {
        return innerSize;
    }

    public Location getOriginalLocation() {
        return originalLocation;
    }

    public String getOriginalOverflow() {
        return originalOverflow;
    }

    public void returnToOriginalOverflow(WebDriver driver) {
        WebElement scrollRootElement = this.scrollRootElement;
        if (scrollRootElement == null) {
            logger.verbose("no scroll root element. selecting default.");
            scrollRootElement = driver.findElement(By.tagName("html"));
        }
        try {
            EyesSeleniumUtils.setOverflow(originalOverflow, this.jsExecutor, scrollRootElement);
        } catch (InterruptedException e) {
            logger.log(e.toString());
        }
    }

    public void hideScrollbars(WebDriver driver) {
        WebElement scrollRootElement = this.scrollRootElement;
        if (scrollRootElement == null) {
            logger.verbose("no scroll root element. selecting default.");
            scrollRootElement = driver.findElement(By.tagName("html"));
        }
        try {
            this.originalOverflow = EyesSeleniumUtils.setOverflow("hidden", this.jsExecutor, scrollRootElement);
        } catch (InterruptedException e) {
            logger.log(e.toString());
        }
    }

    public void setScrollRootElement(WebElement scrollRootElement) {
        this.scrollRootElement = scrollRootElement;
    }

    public WebElement getScrollRootElement() {
        return this.scrollRootElement;
    }
}
