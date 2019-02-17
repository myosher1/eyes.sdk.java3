package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.*;
import com.applitools.eyes.fluent.GetFloatingRegion;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class FloatingRegionByElement implements GetFloatingRegion {

    private WebElement element;
    private int maxUpOffset;
    private int maxDownOffset;
    private int maxLeftOffset;
    private int maxRightOffset;

    public FloatingRegionByElement(WebElement element, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {

        this.element = element;
        this.maxUpOffset = maxUpOffset;
        this.maxDownOffset = maxDownOffset;
        this.maxLeftOffset = maxLeftOffset;
        this.maxRightOffset = maxRightOffset;
    }

    @Override
    public List<FloatingMatchSettings> getRegions(EyesBase eyesBase, EyesScreenshot screenshot) {
        Point locationAsPoint = element.getLocation();
        Dimension size = element.getSize();

        Location adjustedLocation;
        if (screenshot != null) {
            // Element's coordinates are context relative, so we need to convert them first.
            adjustedLocation = screenshot.getLocationInScreenshot(new Location(locationAsPoint.getX(), locationAsPoint.getY()),
                    CoordinatesType.CONTEXT_RELATIVE);
        } else {
            adjustedLocation = new Location(locationAsPoint.getX(), locationAsPoint.getY());
        }

        List<FloatingMatchSettings> value = new ArrayList<>();

        value.add(new FloatingMatchSettings(adjustedLocation.getX(), adjustedLocation.getY(), size.getWidth(),
                size.getHeight(), maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset));

        return value;
    }
}
