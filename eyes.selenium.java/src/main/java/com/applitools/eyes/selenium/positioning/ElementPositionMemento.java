package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Location;
import com.applitools.eyes.positioning.PositionMemento;

/**
 * Encapsulates state for {@link ElementPositionProvider} instances.
 */
public class ElementPositionMemento extends PositionMemento {
    /**
     * @param l The current location to be saved.
     */
    public ElementPositionMemento(Location l) {
        super(l);
    }
}
