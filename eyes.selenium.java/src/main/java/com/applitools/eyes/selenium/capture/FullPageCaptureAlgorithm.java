package com.applitools.eyes.selenium.capture;

import com.applitools.eyes.*;
import com.applitools.eyes.capture.EyesScreenshotFactory;
import com.applitools.eyes.capture.ImageProvider;
import com.applitools.eyes.debug.DebugScreenshotsProvider;
import com.applitools.eyes.positioning.PositionMemento;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.exceptions.EyesDriverOperationException;
import com.applitools.eyes.selenium.positioning.NullRegionPositionCompensation;
import com.applitools.eyes.selenium.positioning.RegionPositionCompensation;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.applitools.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class FullPageCaptureAlgorithm {
    private static final int MIN_SCREENSHOT_PART_HEIGHT = 10;

    private final Logger logger;
    private final RegionPositionCompensation regionPositionCompensation;
    private final int waitBeforeScreenshots;
    private final DebugScreenshotsProvider debugScreenshotsProvider;
    private final EyesScreenshotFactory screenshotFactory;
    private final PositionProvider originProvider;
    private final ScaleProviderFactory scaleProviderFactory;
    private final CutProvider cutProvider;
    private final int stitchingOverlap;
    private final ImageProvider imageProvider;
    private final int minScreenshotPartSize = 10;

    public FullPageCaptureAlgorithm(Logger logger, RegionPositionCompensation regionPositionCompensation,
                                    int waitBeforeScreenshots, DebugScreenshotsProvider debugScreenshotsProvider,
                                    EyesScreenshotFactory screenshotFactory, PositionProvider originProvider,
                                    ScaleProviderFactory scaleProviderFactory, CutProvider cutProvider,
                                    int stitchingOverlap, ImageProvider imageProvider) {

        ArgumentGuard.notNull(logger, "logger");

        this.logger = logger;
        this.waitBeforeScreenshots = waitBeforeScreenshots;
        this.debugScreenshotsProvider = debugScreenshotsProvider;
        this.screenshotFactory = screenshotFactory;
        this.originProvider = originProvider;
        this.scaleProviderFactory = scaleProviderFactory;
        this.cutProvider = cutProvider;
        this.stitchingOverlap = stitchingOverlap;
        this.imageProvider = imageProvider;

        this.regionPositionCompensation =
                regionPositionCompensation != null
                        ? regionPositionCompensation
                        : new NullRegionPositionCompensation();
    }

    private void saveDebugScreenshotPart(BufferedImage image, Region region, String name) {

        String suffix = String.format("part-%s-%d_%d_%dx%d",
                name, region.getLeft(), region.getTop(), region.getWidth(), region.getHeight());

        debugScreenshotsProvider.save(image, suffix);
    }

    /**
     * Returns a stitching of a region.
     *
     * @param region           The region to stitch. If {@code Region.EMPTY}, the entire image will be stitched.
     * @param fullArea         The wanted area of the resulting image. If unknown, pass in {@code null} or {@code RectangleSize.EMPTY}.
     * @param positionProvider A provider of the scrolling implementation.
     * @return An image which represents the stitched region.
     */
    public BufferedImage getStitchedRegion(Region region, Region fullArea, PositionProvider positionProvider) {
        logger.verbose("getStitchedRegion()");

        ArgumentGuard.notNull(region, "region");
        ArgumentGuard.notNull(positionProvider, "positionProvider");

        logger.verbose(String.format("positionProvider: %s ; Region: %s", positionProvider.getClass(), region));

        PositionMemento originalPosition = originProvider.getState();
//        originProvider.setPosition(Location.ZERO); // first scroll to 0,0 so CSS stitching works.

        if (!positionProvider.equals(originProvider)) {
            originProvider.setPosition(Location.ZERO); // first scroll to 0,0 so CSS stitching works.
        }

        // Saving the original position (in case we were already in the outermost frame).
        PositionMemento originalStitchedState = positionProvider.getState();

        logger.verbose("Getting top/left image...");
        BufferedImage image = imageProvider.getImage();
        debugScreenshotsProvider.save(image, "original");

        // FIXME - scaling should be refactored
        ScaleProvider scaleProvider = scaleProviderFactory.getScaleProvider(image.getWidth());
        // Notice that we want to cut/crop an image before we scale it, we need to change
        double pixelRatio = 1 / scaleProvider.getScaleRatio();

        // FIXME - cropping should be overlaid, so a single cut provider will only handle a single part of the image.
        CutProvider scaledCutProvider = cutProvider.scale(pixelRatio);
        if (pixelRatio != 1 && !(scaledCutProvider instanceof NullCutProvider)) {
            image = scaledCutProvider.cut(image);
            debugScreenshotsProvider.save(image, "original-cut");
        }

        Region regionInScreenshot = getRegionInScreenshot(region, image, pixelRatio);

        if (!regionInScreenshot.isSizeEmpty()) {
            image = ImageUtils.getImagePart(image, regionInScreenshot);
            saveDebugScreenshotPart(image, region, "cropped");
        }

        if (pixelRatio != 1.0) {
            image = ImageUtils.scaleImage(image, 1.0 / pixelRatio);
            debugScreenshotsProvider.save(image, "scaled");
        }

        if (fullArea == null || fullArea.isEmpty()) {
            RectangleSize entireSize;
            try {
                entireSize = positionProvider.getEntireSize();
                logger.verbose("Entire size of region context: " + entireSize);
            } catch (EyesDriverOperationException e) {
                logger.log(" WARNING: Failed to extract entire size of region context" + e.getMessage());
                logger.log("Using image size instead: " + image.getWidth() + "x" + image.getHeight());
                entireSize = new RectangleSize(image.getWidth(), image.getHeight());
            }

            // Notice that this might still happen even if we used
            // "getImagePart", since "entirePageSize" might be that of a frame.
            if (image.getWidth() >= entireSize.getWidth() && image.getHeight() >= entireSize.getHeight()) {
                originProvider.restoreState(originalPosition);

                return image;
            }

            fullArea = new Region(Location.ZERO, entireSize);
        }

        RectangleSize scaledCropSize;
        Location scaledCropLocation = fullArea.getLocation();

        Region scaledCroppedSourceRect;

        RectangleSize initialSize = new RectangleSize(image.getWidth(), image.getHeight());

        Location physicalCropLocation = new Location(
                (int) Math.ceil(scaledCropLocation.getX() * pixelRatio),
                (int) Math.ceil(scaledCropLocation.getY() * pixelRatio));

        Region sourceRegion;
        if (regionInScreenshot.isSizeEmpty()) {
            RectangleSize physicalCropSize = new RectangleSize(initialSize.getWidth() - physicalCropLocation.getX(), initialSize.getHeight() - physicalCropLocation.getY());
            sourceRegion = new Region(physicalCropLocation, physicalCropSize);
        } else {
            // Starting with the screenshot we already captured at (0,0).
            sourceRegion = regionInScreenshot;
        }

        // These will be used for storing the actual stitched size (it is
        // sometimes less than the size extracted via "getEntireSize").
//        Location lastSuccessfulLocation;
//        RectangleSize lastSuccessfulPartSize;

        scaledCroppedSourceRect = cutProvider.toRegion(sourceRegion.getSize());
        scaledCroppedSourceRect.offset(sourceRegion.getLocation().getX(), sourceRegion.getLocation().getY());
        Region scaledCroppedSourceRegion = new Region(
                (int) Math.ceil(scaledCroppedSourceRect.getLeft() / pixelRatio),
                (int) Math.ceil(scaledCroppedSourceRect.getTop() / pixelRatio),
                (int) Math.ceil(scaledCroppedSourceRect.getWidth() / pixelRatio),
                (int) Math.ceil(scaledCroppedSourceRect.getHeight() / pixelRatio));

        scaledCropSize = new RectangleSize(scaledCroppedSourceRegion.getWidth(), scaledCroppedSourceRegion.getHeight());

        // The screenshot part is a bit smaller than the screenshot size, in order to eliminate
        // duplicate bottom/right-side scroll bars, as well as fixed position footers.
        RectangleSize screenshotPartSize = new RectangleSize(
                Math.max(scaledCropSize.getWidth(), minScreenshotPartSize),
                Math.max(scaledCropSize.getHeight(), minScreenshotPartSize)
        );


        // Getting the list of viewport regions composing the page (we'll take screenshot for each one).
        Region rectInScreenshot;
        if (regionInScreenshot.isSizeEmpty()) {
            int x = Math.max(0, fullArea.getLeft());
            int y = Math.max(0, fullArea.getTop());
            int w = Math.min(fullArea.getWidth(), scaledCropSize.getWidth());
            int h = Math.min(fullArea.getHeight(), scaledCropSize.getHeight());
            rectInScreenshot = new Region(
                    (int) Math.round(x * pixelRatio),
                    (int) Math.round(y * pixelRatio),
                    (int) Math.round(w * pixelRatio),
                    (int) Math.round(h * pixelRatio));
        } else {
            rectInScreenshot = new Region(regionInScreenshot.getLeft(), regionInScreenshot.getTop(), regionInScreenshot.getWidth(), regionInScreenshot.getHeight());
        }

        // Getting the list of sub-regions composing the whole region (we'll
        // take screenshot for each one).
        Iterable<SubregionForStitching> imageParts = fullArea.getSubRegions(screenshotPartSize, stitchingOverlap, pixelRatio, rectInScreenshot, logger);

        logger.verbose("Creating stitchedImage container.");
        //Notice stitchedImage uses the same type of image as the screenshots.
        BufferedImage stitchedImage = new BufferedImage(
                fullArea.getWidth(), fullArea.getHeight(), image.getType());


//        lastSuccessfulLocation = new Location(0, 0);
//        lastSuccessfulPartSize = new RectangleSize(initialPart.getWidth(), initialPart.getHeight());
        stitchScreenshot(positionProvider.getState(), positionProvider, imageParts, stitchedImage, scaleProvider.getScaleRatio(), scaledCutProvider);

        positionProvider.restoreState(originalStitchedState);
        originProvider.restoreState(originalPosition);

//        // If the actual image size is smaller than the extracted size, we crop the image.
//        int actualImageWidth = lastSuccessfulLocation.getX() + lastSuccessfulPartSize.getWidth();
//        int actualImageHeight = lastSuccessfulLocation.getY() + lastSuccessfulPartSize.getHeight();
//        logger.verbose("Extracted entire size: " + fullArea.getSize());
//        logger.verbose("Actual stitched size: " + actualImageWidth + "x" + actualImageHeight);
//
//        debugScreenshotsProvider.save(stitchedImage,"_stitched_before_trim");
//
//        if (actualImageWidth < stitchedImage.getWidth() || actualImageHeight < stitchedImage.getHeight()) {
//            logger.verbose("Trimming unnecessary margins...");
//            stitchedImage = ImageUtils.getImagePart(stitchedImage,
//                    new Region(0, 0,
//                            Math.min(actualImageWidth, stitchedImage.getWidth()),
//                            Math.min(actualImageHeight, stitchedImage.getHeight())));
//            logger.verbose("Done!");
//        }

        debugScreenshotsProvider.save(stitchedImage, "stitched");
        return stitchedImage;
    }

    private void stitchScreenshot(PositionMemento originalStitchedState, PositionProvider stitchProvider,
                                  Iterable<SubregionForStitching> screenshotParts, BufferedImage stitchedImage, double scaleRatio,
                                  CutProvider scaledCutProvider) {
        logger.verbose("originalStitchedState : " + originalStitchedState);
        logger.verbose("stitchProvider type : " + stitchProvider.getClass().getSimpleName());
        logger.verbose("scaleRatio : " + scaleRatio);
        logger.verbose("scaledCutProvider type : " + scaledCutProvider.getClass().getSimpleName());

        for (SubregionForStitching partRegion : screenshotParts) {
            logger.verbose("Part: " + partRegion);

            // Scroll to the part's top/left
            Location partRegionLocation = partRegion.getScrollTo();
            partRegionLocation = partRegionLocation.offset(originalStitchedState.getX(), originalStitchedState.getY());
            Location originPosition = stitchProvider.setPosition(partRegionLocation);

            int dx = partRegionLocation.getX() - originPosition.getX();
            int dy = partRegionLocation.getY() - originPosition.getY();

            Location targetPosition = partRegion.getPastePhysicalLocation();
            //targetPosition.Offset(-fullarea.Left, -fullarea.Top);
            targetPosition.offset(dx, dy);

            // Actually taking the screenshot.
            try {
                Thread.sleep(waitBeforeScreenshots);
            } catch (InterruptedException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
            BufferedImage partImage = imageProvider.getImage();
            BufferedImage cutPart = scaledCutProvider.cut(partImage);
            BufferedImage croppedPart;
            Region r = partRegion.getPhysicalCropArea();

            int width = r.getWidth();
            int height = r.getHeight();
            if ((width * height) != 0) {
                int minX = r.getLeft();
                int minY = r.getTop();
                croppedPart = cutPart.getSubimage(minX, minY, width, height);
            } else {
                croppedPart = cutPart;
            }
            Region r2 = partRegion.getLogicalCropArea();

            BufferedImage scaledPartImage = ImageUtils.scaleImage(croppedPart, scaleRatio);
            BufferedImage scaledCroppedPartImage = ImageUtils.cropImage(logger, scaledPartImage, r2);

            debugScreenshotsProvider.save(partImage, "partImage-" + originPosition.toStringForFilename());
            debugScreenshotsProvider.save(cutPart, "cutPart-" + originPosition.toStringForFilename());
            debugScreenshotsProvider.save(croppedPart, "croppedPart-" + originPosition.toStringForFilename());
            debugScreenshotsProvider.save(scaledPartImage, "scaledPartImage-" + originPosition.toStringForFilename());
            debugScreenshotsProvider.save(scaledCroppedPartImage, "scaledCroppedPartImage-" + targetPosition.toStringForFilename());
            logger.verbose("pasting part at :" + targetPosition);
            stitchedImage.getRaster().setRect(targetPosition.getX(), targetPosition.getY(), scaledCroppedPartImage.getData());
            debugScreenshotsProvider.save(stitchedImage, "stitched_" + targetPosition.toStringForFilename());
        }

        debugScreenshotsProvider.save(stitchedImage, "stitched");
    }

    private Region getRegionInScreenshot(Region region, BufferedImage image, double pixelRatio) {
        if (region.isSizeEmpty()) {
            return region;
        }

        logger.verbose("Creating screenshot object...");
        // We need the screenshot to be able to convert the region to screenshot coordinates.
        EyesScreenshot screenshot = screenshotFactory.makeScreenshot(image);
        logger.verbose("Getting region in screenshot...");

        // Region regionInScreenshot = screenshot.convertRegionLocation(regionProvider.getRegion(), regionProvider.getCoordinatesType(), CoordinatesType.SCREENSHOT_AS_IS);
        Region regionInScreenshot = screenshot.getIntersectedRegion(region, CoordinatesType.SCREENSHOT_AS_IS);

        logger.verbose("Region in screenshot: " + regionInScreenshot);
        regionInScreenshot = regionInScreenshot.scale(pixelRatio);
        logger.verbose("Scaled region: " + regionInScreenshot);

        regionInScreenshot = regionPositionCompensation.compensateRegionPosition(regionInScreenshot, pixelRatio);

        // Handling a specific case where the region is actually larger than
        // the screenshot (e.g., when body width/height are set to 100%, and
        // an internal div is set to value which is larger than the viewport).
        regionInScreenshot.intersect(new Region(0, 0, image.getWidth(), image.getHeight()));
        logger.verbose("Region after intersect: " + regionInScreenshot);
        return regionInScreenshot;
    }
}
