package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a region.
 */
@JsonIgnoreProperties({"location", "empty", "middleOffset", "size", "sizeEmpty", "subRegions"})
public class RegionF {
    private static Logger logger = new Logger();
    @JsonProperty("left")
    private float left;
    @JsonProperty("top")
    private float top;
    @JsonProperty("width")
    private float width;
    @JsonProperty("height")
    private float height;
    @JsonProperty("coordinatesType")
    private CoordinatesType coordinatesType;

    public static final RegionF EMPTY = new RegionF(0, 0, 0, 0, CoordinatesType.SCREENSHOT_AS_IS);

    public static void initLogger(Logger logger) {
        RegionF.logger = logger;
    }

    protected void makeEmpty() {
        left = EMPTY.getLeft();
        top = EMPTY.getTop();
        width = EMPTY.getWidth();
        height = EMPTY.getHeight();
        this.coordinatesType = EMPTY.getCoordinatesType();
    }

    @JsonCreator()
    public RegionF(@JsonProperty("left") float left,
                   @JsonProperty("top") float top,
                   @JsonProperty("width") float width,
                   @JsonProperty("height") float height) {
        this(left, top, width, height, CoordinatesType.SCREENSHOT_AS_IS);
    }

    public RegionF(float left, float top, float width, float height, CoordinatesType coordinatesType) {
        ArgumentGuard.greaterThanOrEqualToZero(width, "width");
        ArgumentGuard.greaterThanOrEqualToZero(height, "height");

        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.coordinatesType = coordinatesType;
    }

    /**
     * @return true if the region is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.getLeft() == EMPTY.getLeft()
                && this.getTop() == EMPTY.getTop()
                && this.getWidth() == EMPTY.getWidth()
                && this.getHeight() == EMPTY.getHeight();
    }

    /**
     * @return true if the region's size is 0, false otherwise.
     */
    public boolean isSizeEmpty() {
        return this.getWidth() <= 0 || this.getHeight() <= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof RegionF)) {
            return false;
        }
        RegionF other = (RegionF) obj;

        return (this.getLeft() == other.getLeft())
                && (this.getTop() == other.getTop())
                && (this.getWidth() == other.getWidth())
                && (this.getHeight() == other.getHeight());
    }

    @Override
    public int hashCode() {
        return (int) (left + top + width + height);
    }

    public RegionF(Location location, RectangleSizeF size) {
        this(location, size, CoordinatesType.SCREENSHOT_AS_IS);
    }

    public RegionF(Location location, RectangleSize size) {
        this(location, new RectangleSizeF(size), CoordinatesType.SCREENSHOT_AS_IS);
    }

    public RegionF(Location location, RectangleSizeF size, CoordinatesType coordinatesType) {
        ArgumentGuard.notNull(location, "location");
        ArgumentGuard.notNull(size, "size");

        this.left = location.getX();
        this.top = location.getY();
        this.width = size.getWidth();
        this.height = size.getHeight();
        this.coordinatesType = coordinatesType;
    }

    public RegionF(RegionF other) {
        ArgumentGuard.notNull(other, "other");

        left = other.getLeft();
        top = other.getTop();
        width = other.getWidth();
        height = other.getHeight();
        coordinatesType = other.getCoordinatesType();
    }

    public RegionF(Region other) {
        ArgumentGuard.notNull(other, "other");

        left = other.getLeft();
        top = other.getTop();
        width = other.getWidth();
        height = other.getHeight();
        coordinatesType = other.getCoordinatesType();
    }

    /**
     * @return The (left, top) position of the current region.
     */
    public Location getLocation() {
        return new Location(left, top);
    }

    /**
     * @return The (-left, -top) position of the current region.
     */
    public Location getNegativeLocation() {
        return new Location(-left, -top);
    }

    /**
     * Get an offset region.
     * @param dx The X axis offset.
     * @param dy The Y axis offset.
     * @return A region with an offset location.
     */
    public RegionF offset(float dx, float dy) {
        return new RegionF(getLocation().offset(dx, dy), getSize(), getCoordinatesType());
    }

    /**
     * Get a region which is a scaled version of the current region.
     * IMPORTANT: This also scales the LOCATION(!!) of the region (not just its size).
     * @param scaleRatio The ratio by which to scale the region.
     * @return A new region which is a scaled version of the current region.
     */
    public RegionF scale(double scaleRatio) {
        return new RegionF(getLocation().scale(scaleRatio), getSize().scale(scaleRatio), getCoordinatesType());
    }

    /**
     * @return The size of the region.
     */
    public RectangleSizeF getSize() {
        return new RectangleSizeF(width, height);
    }

    /**
     * @return The region's coordinate type.
     */
    public CoordinatesType getCoordinatesType() {
        return this.coordinatesType;
    }

    public void setCoordinatesType(CoordinatesType value) {
        this.coordinatesType = value;
    }

    /**
     * @param size The updated size of the region.
     */
    public void setSize(RectangleSizeF size) {
        width = size.getWidth();
        height = size.getHeight();
    }

    /**
     * Set the (top,left) position of the current region
     * @param location The (top,left) position to set.
     */
    public void setLocation(Location location) {
        ArgumentGuard.notNull(location, "location");
        left = location.getX();
        top = location.getY();
    }

    /**
     * @param containerRegion The region to divide into sub-regions.
     * @param subRegionSize   The maximum size of each sub-region.
     * @return The sub-regions composing the current region. If subRegionSize
     * is equal or greater than the current region,  only a single region is
     * returned.
     */
    private static Iterable<RegionF> getSubRegionsWithFixedSize(
            RegionF containerRegion, RectangleSizeF subRegionSize) {
        ArgumentGuard.notNull(containerRegion, "containerRegion");
        ArgumentGuard.notNull(subRegionSize, "subRegionSize");

        List<RegionF> subRegions = new LinkedList<>();

        float subRegionWidth = subRegionSize.getWidth();
        float subRegionHeight = subRegionSize.getHeight();

        ArgumentGuard.greaterThanZero(subRegionWidth, "subRegionSize width");
        ArgumentGuard.greaterThanZero(subRegionHeight, "subRegionSize height");

        // Normalizing.
        if (subRegionWidth > containerRegion.width) {
            subRegionWidth = containerRegion.width;
        }
        if (subRegionHeight > containerRegion.height) {
            subRegionHeight = containerRegion.height;
        }

        // If the requested size is greater or equal to the entire region size,
        // we return a copy of the region.
        if (subRegionWidth == containerRegion.width &&
                subRegionHeight == containerRegion.height) {
            subRegions.add(new RegionF(containerRegion));
            return subRegions;
        }

        float currentTop = containerRegion.top;
        float bottom = containerRegion.top + containerRegion.height - 1;
        float right = containerRegion.left + containerRegion.width - 1;

        CoordinatesType currentType = containerRegion.getCoordinatesType();

        while (currentTop <= bottom) {

            if (currentTop + subRegionHeight > bottom) {
                currentTop = (bottom - subRegionHeight) + 1;
            }

            float currentLeft = containerRegion.left;
            while (currentLeft <= right) {
                if (currentLeft + subRegionWidth > right) {
                    currentLeft = (right - subRegionWidth) + 1;
                }

                subRegions.add(new RegionF(currentLeft, currentTop,
                        subRegionWidth, subRegionHeight, currentType));

                currentLeft += subRegionWidth;
            }
            currentTop += subRegionHeight;
        }
        return subRegions;
    }

    /**
     * @param containerRegion  The region to divide into sub-regions.
     * @param maxSubRegionSize The maximum size of each sub-region (some
     *                         regions might be smaller).
     * @return The sub-regions composing the current region. If
     * maxSubRegionSize is equal or greater than the current region,
     * only a single region is returned.
     */
    private static Iterable<RegionF> getSubRegionsWithVaryingSize(
            RegionF containerRegion, RectangleSizeF maxSubRegionSize) {
        ArgumentGuard.notNull(containerRegion, "containerRegion");
        ArgumentGuard.notNull(maxSubRegionSize, "maxSubRegionSize");
        ArgumentGuard.greaterThanZero(maxSubRegionSize.getWidth(),
                "maxSubRegionSize.getWidth()");
        ArgumentGuard.greaterThanZero(maxSubRegionSize.getHeight(),
                "maxSubRegionSize.getHeight()");

        List<RegionF> subRegions = new LinkedList<>();

        float currentTop = containerRegion.top;
        float bottom = containerRegion.top + containerRegion.height;
        float right = containerRegion.left + containerRegion.width;

        CoordinatesType currentType = containerRegion.getCoordinatesType();

        while (currentTop < bottom) {

            float currentBottom = currentTop + maxSubRegionSize.getHeight();
            if (currentBottom > bottom) {
                currentBottom = bottom;
            }

            float currentLeft = containerRegion.left;
            while (currentLeft < right) {
                float currentRight = currentLeft + maxSubRegionSize.getWidth();
                if (currentRight > right) {
                    currentRight = right;
                }

                float currentHeight = currentBottom - currentTop;
                float currentWidth = currentRight - currentLeft;

                subRegions.add(new RegionF(currentLeft, currentTop,
                        currentWidth, currentHeight, currentType));

                currentLeft += maxSubRegionSize.getWidth();
            }
            currentTop += maxSubRegionSize.getHeight();
        }
        return subRegions;
    }

    /**
     * Returns a list of sub-regions which compose the current region.
     * @param subRegionSize The default sub-region size to use.
     * @param isFixedSize   If {@code false}, then sub-regions might have a
     *                      size which is smaller then {@code subRegionSize}
     *                      (thus there will be no overlap of regions).
     *                      Otherwise, all sub-regions will have the same
     *                      size, but sub-regions might overlap.
     * @return The sub-regions composing the current region. If {@code
     * subRegionSize} is equal or greater than the current region,
     * only a single region is returned.
     */
    public Iterable<RegionF> getSubRegions(RectangleSizeF subRegionSize,
                                           boolean isFixedSize) {
        if (isFixedSize) {
            return getSubRegionsWithFixedSize(this, subRegionSize);
        }

        return getSubRegionsWithVaryingSize(this, subRegionSize);
    }

    /**
     * See {@link #getSubRegions(RectangleSizeF, boolean)}.
     * {@code isFixedSize} defaults to {@code false}.
     */
    public Iterable<RegionF> getSubRegions(RectangleSizeF subRegionSize) {
        return getSubRegions(subRegionSize, false);
    }

    /**
     * Check if a region is contained within the current region.
     * @param other The region to check if it is contained within the current
     *              region.
     * @return True if {@code other} is contained within the current region,
     * false otherwise.
     */
    @SuppressWarnings("UnusedDeclaration")
    public boolean contains(RegionF other) {
        float right = left + width;
        float otherRight = other.getLeft() + other.getWidth();

        float bottom = top + height;
        float otherBottom = other.getTop() + other.getHeight();

        return top <= other.getTop() && left <= other.getLeft()
                && bottom >= otherBottom && right >= otherRight;
    }

    /**
     * Check if a specified location is contained within this region.
     * <p>
     * @param location The location to test.
     * @return True if the location is contained within this region,
     * false otherwise.
     */
    public boolean contains(Location location) {
        return location.getX() >= left
                && location.getX() <= (left + width)
                && location.getY() >= top
                && location.getY() <= (top + height);
    }

    /**
     * Check if a region is intersected with the current region.
     * @param other The region to check intersection with.
     * @return True if the regions are intersected, false otherwise.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isIntersected(RegionF other) {
        float right = left + width;
        float bottom = top + height;

        float otherLeft = other.getLeft();
        float otherTop = other.getTop();
        float otherRight = otherLeft + other.getWidth();
        float otherBottom = otherTop + other.getHeight();

        return (((left <= otherLeft && otherLeft <= right)
                || (otherLeft <= left && left <= otherRight))
                && ((top <= otherTop && otherTop <= bottom)
                || (otherTop <= top && top <= otherBottom)));
    }

    /**
     * Replaces this region with the intersection of itself and {@code other}
     * @param other The region with which to intersect.
     */
    public void intersect(RegionF other) {

        logger.verbose(String.format("intersecting this region (%s) with %s ...", this, other));

        // If there's no intersection set this as the Empty region.
        if (!isIntersected(other)) {
            makeEmpty();
            return;
        }

        // The regions intersect. So let's first find the left & top values
        float otherLeft = other.getLeft();
        float otherTop = other.getTop();

        float intersectionLeft = (left >= otherLeft) ? left : otherLeft;
        float intersectionTop = (top >= otherTop) ? top : otherTop;

        // Now the width and height of the intersect
        float right = left + width;
        float otherRight = otherLeft + other.getWidth();
        float intersectionRight = (right <= otherRight) ? right : otherRight;
        float intersectionWidth = intersectionRight - intersectionLeft;

        float bottom = top + height;
        float otherBottom = otherTop + other.getHeight();
        float intersectionBottom = (bottom <= otherBottom) ? bottom : otherBottom;
        float intersectionHeight = intersectionBottom - intersectionTop;

        left = intersectionLeft;
        top = intersectionTop;
        width = intersectionWidth;
        height = intersectionHeight;

    }


    public float getLeft() {
        return left;
    }

    public float getTop() {
        return top;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }


    public void setLeft(float value) {
        left = value;
    }

    public void setTop(float value) {
        top = value;
    }

    public void setWidth(float value) {
        width = value;
    }

    public void setHeight(float value) {
        height = value;
    }

    public Location getMiddleOffset() {
        float middleX = width / 2;
        float middleY = height / 2;

        return new Location(middleX, middleY);
    }

    public RegionF expandToContain(RegionF region) {
        float left = Math.min(this.left, region.left);
        float top = Math.min(this.top, region.top);

        float thisRight = this.left + this.width;
        float otherRight = region.left + region.width;
        float maxRight = Math.max(thisRight, otherRight);
        float width = maxRight - left;

        float thisBottom = this.top + this.height;
        float otherBottom = region.top + region.height;
        float maxBottom = Math.max(thisBottom, otherBottom);
        float height = maxBottom - top;

        return new RegionF(left, top, width, height);
    }

    @Override
    public String toString() {
        return "(" + left + ", " + top + ") " + width + "x" + height + ", " + coordinatesType;
    }
}
