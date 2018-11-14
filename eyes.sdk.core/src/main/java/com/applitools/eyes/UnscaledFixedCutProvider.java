package com.applitools.eyes;

import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;

/**
 * Cut provider based on fixed cut values, run BEFORE scaling.
 */
public class UnscaledFixedCutProvider implements CutProvider {
    private Logger logger;
    private final int header;
    private final int footer;
    private final int left;
    private final int right;

    /**
     * @param header The header to cut in pixels.
     * @param footer The footer to cut in pixels.
     * @param left   The left to cut in pixels.
     * @param right  The right to cut in pixels.
     */
    @SuppressWarnings("WeakerAccess")
    public UnscaledFixedCutProvider(int header, int footer, int left, int right) {
        this.header = header;
        this.footer = footer;
        this.left = left;
        this.right = right;
    }

    public void setLogger(Logger logger){
        this.logger = logger;
    }

    public BufferedImage cut(BufferedImage image) {
        if (header == 0 && footer == 0 && left == 0 && right == 0) return image;
        Region targetRegion = new Region(left, header,
                image.getWidth() - left - right,
                image.getHeight() - header - footer);

        return ImageUtils.cropImage(logger, image, targetRegion);
    }

    public CutProvider scale(double scaleRatio) {
        if (this instanceof NullCutProvider) {
            return this;
        }
        UnscaledFixedCutProvider cutProvider = new UnscaledFixedCutProvider(header, footer, left, right);
        cutProvider.logger = logger;
        return cutProvider;
    }
}
