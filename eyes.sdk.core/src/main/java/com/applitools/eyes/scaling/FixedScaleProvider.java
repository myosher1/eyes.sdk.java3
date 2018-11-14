package com.applitools.eyes.scaling;

import com.applitools.eyes.Logger;
import com.applitools.eyes.ScaleProvider;
import com.applitools.utils.ArgumentGuard;

/**
 * Scale provider based on a fixed scale ratio.
 */
public class FixedScaleProvider implements ScaleProvider {

    private final double scaleRatio;
    private Logger logger;

    /**
     *
     * @param scaleRatio The scale ratio to use.
     */
    public FixedScaleProvider(double scaleRatio) {
        ArgumentGuard.greaterThanZero(scaleRatio, "scaleRatio");
        this.scaleRatio = scaleRatio;
    }

    /**
     *
     * {@inheritDoc}
     */
    public double getScaleRatio() {
        return scaleRatio;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
