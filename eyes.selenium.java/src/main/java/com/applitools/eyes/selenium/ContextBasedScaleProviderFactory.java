package com.applitools.eyes.selenium;

import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.ScaleMethod;
import com.applitools.eyes.ScaleProvider;
import com.applitools.eyes.ScaleProviderFactory;
import com.applitools.utils.PropertyHandler;

import java.awt.image.BufferedImage;

/**
 * Factory implementation for creating {@link ContextBasedScaleProvider} instances.
 */
public class ContextBasedScaleProviderFactory extends ScaleProviderFactory {

    private final RectangleSize topLevelContextEntireSize;
    private final RectangleSize viewportSize;
    private final ScaleMethod scaleMethod;
    private final double devicePixelRatio;

    /**
     *
     *
     * @param topLevelContextEntireSize The total size of the top level
     *                                  context. E.g., for selenium this
     *                                  would be the document size of the top
     *                                  level frame.
     * @param viewportSize              The viewport size.
     *
     * @param scaleMethod               The method used for scaling.
     * @param devicePixelRatio          The device pixel ratio of the
     *                                  platform on which the application is
     *                                  running.
     */
    public ContextBasedScaleProviderFactory(RectangleSize topLevelContextEntireSize, RectangleSize viewportSize,
                                            ScaleMethod scaleMethod, double devicePixelRatio,
                                            PropertyHandler<ScaleProvider> scaleProviderHandler) {
        super(scaleProviderHandler);
        this.topLevelContextEntireSize = topLevelContextEntireSize;
        this.viewportSize = viewportSize;
        this.scaleMethod = scaleMethod;
        this.devicePixelRatio = devicePixelRatio;
    }

    @Override
    protected ScaleProvider getScaleProviderImpl(int imageToScaleWidth) {
        ContextBasedScaleProvider scaleProvider = new ContextBasedScaleProvider(topLevelContextEntireSize, viewportSize,
                scaleMethod, devicePixelRatio);
        scaleProvider.updateScaleRatio(imageToScaleWidth);
        return scaleProvider;
    }
}
