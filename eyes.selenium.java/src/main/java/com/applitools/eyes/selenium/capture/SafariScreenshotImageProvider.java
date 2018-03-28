package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.*;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.SeleniumJavaScriptExecutor;
import com.applitools.eyes.selenium.frames.FrameChain;
import com.applitools.eyes.selenium.positioning.ScrollPositionProvider;
import com.applitools.eyes.selenium.wrappers.EyesWebDriver;
import com.applitools.utils.ImageUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.awt.image.BufferedImage;

public class SafariScreenshotImageProvider implements ImageProvider {

    private final Eyes eyes;
    private final Logger logger;
    private final TakesScreenshot tsInstance;
    private final IEyesJsExecutor jsExecutor;
    private final UserAgent userAgent;

    public SafariScreenshotImageProvider(Eyes eyes, Logger logger, TakesScreenshot tsInstance, UserAgent userAgent) {
        this.eyes = eyes;
        this.logger = logger;
        this.tsInstance = tsInstance;
        this.jsExecutor = new SeleniumJavaScriptExecutor((EyesWebDriver) eyes.getDriver());
        this.userAgent = userAgent;
    }

    @Override
    public BufferedImage getImage() {
        logger.verbose("Getting screenshot as base64...");
        String screenshot64 = tsInstance.getScreenshotAs(OutputType.BASE64);
        logger.verbose("Done getting base64! Creating BufferedImage...");
        BufferedImage image = ImageUtils.imageFromBase64(screenshot64);

        eyes.getDebugScreenshotsProvider().save(image, "SAFARI");

        if (eyes.getIsCutProviderExplicitlySet()) {
            return image;
        }

        double scaleRatio = eyes.getDevicePixelRatio();
        RectangleSize originalViewportSize = eyes.getViewportSize();
        RectangleSize viewportSize = originalViewportSize.scale(scaleRatio);

        if (userAgent.getOS().equals(OSNames.IOS)) {
            int leftBarHeight = 0;
            int topBarHeight = 20;
            int urlBarHeight = 44;

            logger.verbose("logical viewport size: " + originalViewportSize);

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            int displayLogicalWidth = (int) Math.ceil(imageWidth / scaleRatio);
            int displayLogicalHeight = (int) Math.ceil(imageHeight / scaleRatio);

            logger.verbose("physical device pixel size: " + imageWidth + " x " + imageHeight);
            logger.verbose("physical device logical size: " + displayLogicalWidth + " x " + displayLogicalHeight);

            if (displayLogicalHeight == 736 && displayLogicalWidth == 414) { // iPhone 5.5 inch
                logger.verbose("iPhone 5.5 inch detected");
                topBarHeight = 18;
            } else if (displayLogicalHeight == 812 && displayLogicalWidth == 375) { // iPhone 5.8 inch
                logger.verbose("iPhone 5.8 inch detected");
                topBarHeight = 44;
            } else if (displayLogicalWidth == 812 && displayLogicalHeight == 375) { // iPhone 5.8 inch
                logger.verbose("iPhone 5.8 inch detected");
                leftBarHeight = 44;
            }

            if (displayLogicalHeight < displayLogicalWidth) {
                logger.verbose("landscape mode detected");
                topBarHeight = 0;
            }

            if (Integer.parseInt(userAgent.getBrowserMajorVersion()) >= 11) { // Safari >= 11
                logger.verbose("safari version 11 or higher detected");
                urlBarHeight = 50;
            }

            logger.verbose("cropping IOS browser image");
            image = ImageUtils.cropImage(
                    image,
                    new Region(
                            (int) Math.ceil(leftBarHeight * scaleRatio),
                            (int) Math.ceil((topBarHeight + urlBarHeight) * scaleRatio),
                            viewportSize.getWidth(),
                            viewportSize.getHeight()
                    )
            );
        }

        if (!eyes.getForceFullPageScreenshot()) {

            Location loc;
            FrameChain currentFrameChain = ((EyesWebDriver) eyes.getDriver()).getFrameChain();

            if (currentFrameChain.size() == 0) {
                PositionProvider positionProvider = new ScrollPositionProvider(logger, jsExecutor);
                loc = positionProvider.getCurrentPosition();
            } else {
                loc = currentFrameChain.getDefaultContentScrollPosition();
            }

            loc = loc.scale(scaleRatio);

            image = ImageUtils.cropImage(image, new Region(loc, viewportSize));
        }

        return image;
    }
}
