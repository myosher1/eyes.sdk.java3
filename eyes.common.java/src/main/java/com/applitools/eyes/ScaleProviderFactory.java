package com.applitools.eyes;

import com.applitools.utils.PropertyHandler;

import java.awt.image.BufferedImage;

/**
 * Abstraction for instantiating scale providers.
 */
public abstract class ScaleProviderFactory {

    private final PropertyHandler<ScaleProvider> scaleProviderHandler;

    /**
     *
     * @param scaleProviderHandler A handler to update once a {@link ScaleProvider} instance is created.
     */
    public ScaleProviderFactory(PropertyHandler<ScaleProvider> scaleProviderHandler) {
        this.scaleProviderHandler = scaleProviderHandler;
    }

    /**
     * The main API for this factory.
     *
     * @param image An image which can be used to infer the scale ratio.
     * @return A {@link ScaleProvider} instance.
     */
    public ScaleProvider getScaleProvider(BufferedImage image) {
        ScaleProvider scaleProvider = getScaleProviderImpl(image);
        scaleProviderHandler.set(scaleProvider);
        return scaleProvider;
    }

    /**
     * The implementation of getting/creating the scale provider, should be implemented by child classes.
     *
     *
     * @param image An image which can be used to infer the scale ratio.
     * @return The scale provider to be used.
     */
    protected abstract ScaleProvider getScaleProviderImpl(BufferedImage image);
}
