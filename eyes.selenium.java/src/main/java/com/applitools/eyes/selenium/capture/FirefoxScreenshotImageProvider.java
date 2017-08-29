package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.frames.Frame;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.awt.image.BufferedImage;

public class FirefoxScreenshotImageProvider implements ImageProvider {

    private final Eyes eyes;
    private final Logger logger;
    private final TakesScreenshot tsInstance;

    public FirefoxScreenshotImageProvider(Eyes eyes, Logger logger, TakesScreenshot tsInstance) {
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

        eyes.getDebugScreenshotsProvider().save(image, "FIREFOX_FRAME");

        EyesWebDriver eyesWebDriver = (EyesWebDriver) eyes.getDriver();
        FrameChain frameChain = eyesWebDriver.getFrameChain();
        if (frameChain.size() > 0) {
            //Frame frame = frameChain.peek();
            Location loc = eyes.getRegionToCheck().getLocation();
            logger.verbose("");
            logger.verbose("");
            logger.verbose("frame.getLocation(): " + loc);
            logger.verbose("");
            logger.verbose("");

            RectangleSize viewportSize = eyes.getViewportSize();
            double scaleRatio = eyes.getDevicePixelRatio();

            BufferedImage fullImage;
            fullImage = new BufferedImage(
                    (int) Math.ceil(viewportSize.getWidth() * scaleRatio),
                    (int) Math.ceil(viewportSize.getHeight() * scaleRatio),
                    image.getType());

            fullImage.getRaster().setRect(
                    (int) Math.ceil(loc.getX() * scaleRatio),
                    (int) Math.ceil(loc.getY() * scaleRatio),
                    image.getData());

            return fullImage;
        }

        return image;
    }
}
