package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;

/**
 * Scale provider based on a fixed scale ratio.
 */
public class FixedScaleProvider implements ScaleProvider {

    private final double scaleRatio;
    private final ScaleMethod scaleMethod;

    /**
     *
     * @param scaleRatio The scale ratio to use.
     * @param method The method used for scaling the image.
     */
    public FixedScaleProvider(double scaleRatio, ScaleMethod method) {
        ArgumentGuard.greaterThanZero(scaleRatio, "scaleRatio");
        ArgumentGuard.notNull(method, "method");
        this.scaleRatio = scaleRatio;
        this.scaleMethod = method;
    }

    /**
     *
     * @param scaleRatio The scale ratio to use.
     */
    public FixedScaleProvider(double scaleRatio) {
        this(scaleRatio, ScaleMethod.getDefault());
    }

    /**
     *
     * {@inheritDoc}
     */
    public double getScaleRatio() {
        return scaleRatio;
    }

    /**
     *
     * {@inheritDoc}
     */
    public ScaleMethod getScaleMethod() { return scaleMethod; }
}
