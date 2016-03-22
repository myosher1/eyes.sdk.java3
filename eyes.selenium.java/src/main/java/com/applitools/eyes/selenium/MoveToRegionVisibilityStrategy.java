package com.applitools.eyes.selenium;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.PositionMemento;
import com.applitools.eyes.PositionProvider;

/**
 * An implementation of {@link RegionVisibilityStrategy}, which tries to move
 * to the region.
 */
public class MoveToRegionVisibilityStrategy implements
        RegionVisibilityStrategy {

    private final Logger logger;
    private PositionMemento originalPosition;

    public MoveToRegionVisibilityStrategy(Logger logger) {
        this.logger = logger;
    }

    public void moveToRegion(PositionProvider positionProvider,
                             Location location) {
        logger.verbose("Getting current position state..");
        originalPosition = positionProvider.getState();
        logger.verbose("Done! Setting position..");
        positionProvider.setPosition(location);
        logger.verbose("Done!");
    }

    public void returnToOriginalPosition(PositionProvider positionProvider) {
        logger.verbose("Returning to original position...");
        positionProvider.restoreState(originalPosition);
        logger.verbose("Done!");
    }
}
