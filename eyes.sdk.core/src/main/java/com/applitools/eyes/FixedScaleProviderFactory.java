package com.applitools.eyes;

import com.applitools.utils.PropertyHandler;

import java.awt.image.BufferedImage;

/**
 * Factory implementation for creating {@link FixedScaleProvider} instances.
 */
public class FixedScaleProviderFactory extends ScaleProviderFactory {

    private final double scaleRatio;
    private final ScaleMethod scaleMethod;

    /**
     *
     * @param scaleRatio The ratio to scale by.
     * @param scaleMethod The scale method to use for scaling.
     */
    public FixedScaleProviderFactory(double scaleRatio, ScaleMethod scaleMethod,
                                     PropertyHandler<ScaleProvider> scaleProviderHandler) {
        super(scaleProviderHandler);
        this.scaleRatio = scaleRatio;
        this.scaleMethod = scaleMethod;
    }

    @Override
    protected ScaleProvider getScaleProviderImpl(int imageToScaleWidth) {
        return new FixedScaleProvider(scaleRatio, scaleMethod);
    }
}
