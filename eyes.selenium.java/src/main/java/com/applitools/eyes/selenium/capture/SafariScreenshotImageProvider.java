package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.awt.image.BufferedImage;

public class SafariScreenshotImageProvider implements ImageProvider {

    private final Eyes eyes;
    private final Logger logger;
    private final TakesScreenshot tsInstance;

    public SafariScreenshotImageProvider(Eyes eyes, Logger logger, TakesScreenshot tsInstance) {
        this.eyes = eyes;
        this.logger = logger;
        this.tsInstance = tsInstance;
    }

    @Override
    public BufferedImage getImage() {
        logger.verbose("Getting screenshot as base64...");
        String screenshot64 = tsInstance.getScreenshotAs(OutputType.BASE64);
        logger.verbose("Done getting base64! Creating BufferedImage...");
        BufferedImage image = ImageUtils.imageFromBase64(screenshot64);

        eyes.getDebugScreenshotsProvider().save(image, "SAFARI");

        if (!eyes.getForceFullPageScreenshot()) {

            PositionProvider positionProvider = eyes.getElementPositionProvider();
            Location loc = positionProvider.getCurrentPosition();

            if (loc == null) {
                loc = new Location(0,0);
            }

            RectangleSize viewportSize = eyes.getViewportSize();
            double scaleRatio = eyes.getDevicePixelRatio();

            BufferedImage cutImage = new BufferedImage(
                    (int) Math.ceil(viewportSize.getWidth() * scaleRatio),
                    (int) Math.ceil(viewportSize.getHeight() * scaleRatio),
                    image.getType());

            cutImage.getRaster().setRect(
                    (int) Math.ceil(-loc.getX() * scaleRatio),
                    (int) Math.ceil(-loc.getY() * scaleRatio),
                    image.getData());

            return cutImage;
        }

        return image;
    }
}
