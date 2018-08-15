package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Location;
import com.applitools.eyes.positioning.PositionMemento;

/**
 * Encapsulates state for {@link ElementPositionProvider} instances.
 */
public class ElementPositionMemento extends PositionMemento {
    private final Location position;

    /**
     *
     * @param l The current location to be saved.
     */
    public ElementPositionMemento(Location l) {
        position = new Location(l);
    }

    public float getX() {
        return position.getX();
    }

    public float getY() {
        return position.getY();
    }
}
