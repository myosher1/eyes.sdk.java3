package com.applitools.eyes.positioning;

import com.applitools.eyes.Location;

/**
 * A base class for position related memento instances. This is intentionally
 * not an interface, since the mementos might vary in their interfaces.
 */
public abstract class PositionMemento {
    private final Location position;

    /**
     *
     * @param l The current location to be saved.
     */
    public PositionMemento(Location l) {
        position = new Location(l);
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }
}

