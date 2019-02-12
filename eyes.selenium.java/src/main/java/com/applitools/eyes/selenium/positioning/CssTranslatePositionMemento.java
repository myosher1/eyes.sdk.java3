package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Location;
import com.applitools.eyes.positioning.PositionMemento;

import java.util.Map;

/**
 * Encapsulates state for {@link CssTranslatePositionProvider} instances.
 */
public class CssTranslatePositionMemento extends PositionMemento {
    private final String transform;
    private final Location position;

    /**
     *
     * @param transform The current transform.
     */
    public CssTranslatePositionMemento(String transform, Location position) {
        super(position);
        this.transform = transform;
        this.position = position;
    }

    /**
     *
     * @return The current transforms. The keys are the style keys from
     * which each of the transforms were taken.
     */
    public String getTransform() {
        return transform;
    }

    public Location getPosition() { return position; }
}
