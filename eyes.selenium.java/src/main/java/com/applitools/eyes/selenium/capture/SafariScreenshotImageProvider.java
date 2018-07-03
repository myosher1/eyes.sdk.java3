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
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class SafariScreenshotImageProvider implements ImageProvider {

    private final Eyes eyes;
    private final Logger logger;
    private final TakesScreenshot tsInstance;
    private final IEyesJsExecutor jsExecutor;
    private final UserAgent userAgent;

    private static Map<DeviceData, Region> devicesRegions = null;

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

        logger.verbose("logical viewport size: " + originalViewportSize);

        if (userAgent.getOS().equals(OSNames.IOS)) {
            if (devicesRegions == null) {
                initDeviceRegionsTable();
            }

            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            DeviceData deviceData = new DeviceData(imageWidth, imageHeight, Integer.parseInt(userAgent.getBrowserMajorVersion()));
            if (devicesRegions.containsKey(deviceData)) {
                logger.verbose("device data found in hash table");
                Region crop = devicesRegions.get(deviceData);
                image = ImageUtils.cropImage(image, crop);
            } else {

                int topBarHeight = 20;
                int leftBarWidth = 0;
                int bottomBarHeight = 44;
                int rightBarWidth = 0;
                int urlBarHeight = 44;

                int extraPhysicalTopPixels = 0;
                int extraPhysicalBottomPixels = 0;

                int displayLogicalWidth = (int) Math.ceil(imageWidth / scaleRatio);
                int displayLogicalHeight = (int) Math.ceil(imageHeight / scaleRatio);

                logger.verbose("physical device pixel size: " + imageWidth + " x " + imageHeight);
                logger.verbose("physical device logical size: " + displayLogicalWidth + " x " + displayLogicalHeight);

                if (displayLogicalHeight == 736 && displayLogicalWidth == 414) { // iPhone 5.5 inch
                    logger.verbose("iPhone 5.5 inch detected");
                    topBarHeight = 18;
                } else if (displayLogicalHeight == 812 && displayLogicalWidth == 375) { // iPhone 5.8 inch p
                    logger.verbose("iPhone 5.8 inch portrait detected");
                    topBarHeight = 44;
                    bottomBarHeight = 83;
                } else if (displayLogicalWidth == 812 && displayLogicalHeight == 375) { // iPhone 5.8 inch l
                    logger.verbose("iPhone 5.8 inch landscape detected");
                    leftBarWidth = 44;
                    rightBarWidth = 44;
                }

                if (displayLogicalHeight < displayLogicalWidth) {
                    logger.verbose("landscape mode detected");
                    topBarHeight = 0;
                    if (displayLogicalWidth == 812 && displayLogicalHeight == 375) { // on iPhone X crop the home indicator.
                        bottomBarHeight = 15;
                    } else {
                        bottomBarHeight = 0;
                    }

                    // on iPhone 5.5 inch with Safari 10 in landscape mode crop the tabs bar.
                    if (displayLogicalWidth == 736 && displayLogicalHeight == 414 &&
                            Integer.parseInt(userAgent.getBrowserMajorVersion()) < 11) {
                        topBarHeight = 33;
                    }
                }

                if (Integer.parseInt(userAgent.getBrowserMajorVersion()) >= 11) { // Safari >= 11
                    logger.verbose("safari version 11 or higher detected");
                    urlBarHeight = 50;
                    if (displayLogicalHeight == 736 && displayLogicalWidth == 414) { // iPhone 5.5 inch
                        topBarHeight = 20;
                        extraPhysicalTopPixels = 1;
                        extraPhysicalBottomPixels = 1;
                    }
                }

                viewportSize = new RectangleSize(
                        (int) Math.ceil(imageWidth - ((leftBarWidth + rightBarWidth) * scaleRatio)),
                        (int) Math.ceil(imageHeight - ((topBarHeight + urlBarHeight + bottomBarHeight) * scaleRatio) - extraPhysicalTopPixels - extraPhysicalBottomPixels));

                logger.verbose("computed physical viewport size: " + viewportSize);

                logger.verbose("cropping IOS browser image");

                image = ImageUtils.cropImage(
                        image,
                        new Region(
                                (int) Math.ceil(leftBarWidth * scaleRatio),
                                (int) Math.ceil(((topBarHeight + urlBarHeight) * scaleRatio) + extraPhysicalTopPixels),
                                viewportSize.getWidth(),
                                viewportSize.getHeight()
                        )
                );
            }
        } else if (!eyes.getForceFullPageScreenshot()) {

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

    private void initDeviceRegionsTable() {
        devicesRegions = new HashMap<>();

        devicesRegions.put(new DeviceData(1125,2436,11), new Region(0, 283, 1125, 1903));
        devicesRegions.put(new DeviceData(2436,1125,11), new Region(0, 151, 2436, 930));

        devicesRegions.put(new DeviceData(1242,2208,11), new Region(0, 211, 1242, 1863));
        devicesRegions.put(new DeviceData(2208,1242,11), new Region(0, 151, 2208, 1090));

        devicesRegions.put(new DeviceData(1242,2208,10), new Region(0, 193, 1242, 1882));
        devicesRegions.put(new DeviceData(2208,1242,10), new Region(0, 231, 2208, 1010));

        devicesRegions.put(new DeviceData(750,1334,11), new Region(0, 141, 750, 1104));
        devicesRegions.put(new DeviceData(1334,750,11), new Region(0, 101, 1334, 648));

        devicesRegions.put(new DeviceData(750,1334,10), new Region(0, 129, 750, 1116));
        devicesRegions.put(new DeviceData(1334,750,10), new Region(0, 89, 1334, 660));

        devicesRegions.put(new DeviceData(640,1136,10), new Region(0, 129, 640, 918));
        devicesRegions.put(new DeviceData(1136,640,10), new Region(0, 89, 1136, 462));

        devicesRegions.put(new DeviceData(1536,2048,11), new Region(0, 141, 1536, 1907));
        devicesRegions.put(new DeviceData(2048,1536,11), new Region(0, 141, 2048, 1395));

        devicesRegions.put(new DeviceData(1536,2048,10), new Region(0, 194, 1536, 1854));
        devicesRegions.put(new DeviceData(2048,1536,10), new Region(0, 194, 2048, 1342));

        devicesRegions.put(new DeviceData(2048,2732,11), new Region(0, 141, 2048, 2591));
        devicesRegions.put(new DeviceData(2732,2048,11), new Region(0, 141, 2732, 1907));

        devicesRegions.put(new DeviceData(2048,2732,10), new Region(0, 129, 2048, 2603));
        devicesRegions.put(new DeviceData(2732,2048,10), new Region(0, 129, 2732, 1919));
    }

    private static class DeviceData {
        private int width;
        private int height;
        private int majorVersion;

        public DeviceData(int width, int height, int majorVersion) {

            this.width = width;
            this.height = height;
            this.majorVersion = majorVersion;
        }

        @Override
        public int hashCode() {
            return width * 100000 + height * 10 + majorVersion;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DeviceData)) return false;
            DeviceData other = (DeviceData)obj;
            return this.width == other.width && this.height == other.height && this.majorVersion == other.majorVersion;
        }
    }
}
