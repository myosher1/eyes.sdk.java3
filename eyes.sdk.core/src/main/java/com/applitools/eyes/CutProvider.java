package com.applitools.eyes;

import java.awt.image.BufferedImage;

/**
 * Encapsulates cutting logic.
 */
public interface CutProvider {

    /**
     *
     * @param image The image to cut.
     * @return A new cut image.
     */
    BufferedImage cut(BufferedImage image);
}
