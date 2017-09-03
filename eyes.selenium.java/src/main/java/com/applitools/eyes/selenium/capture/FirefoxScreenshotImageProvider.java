package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.Location;
import com.applitools.eyes.Logger;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.Region;
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
            //Region region = eyes.getRegionToCheck();

            EyesWebDriverScreenshot screenshot = new EyesWebDriverScreenshot(logger, eyesWebDriver, image);

            Location loc = screenshot.getFrameWindow().getLocation();
            logger.verbose("");
            logger.verbose("frame.getLocation(): " + loc);
            logger.verbose("");

            double scaleRatio = eyes.getDevicePixelRatio();
            RectangleSize viewportSize = eyes.getViewportSize();
            viewportSize = viewportSize.scale(scaleRatio);
            loc = loc.scale(scaleRatio);

            BufferedImage fullImage;
            fullImage = new BufferedImage(
                    viewportSize.getWidth(),
                    viewportSize.getHeight(),
                    image.getType());

            fullImage.getRaster().setRect(
                    loc.getX(),
                    loc.getY(),
                    image.getData());

            return fullImage;
        }

        return image;
    }
}
