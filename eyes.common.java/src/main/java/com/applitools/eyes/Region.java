package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a region.
 */
@SuppressWarnings("WeakerAccess")
@JsonIgnoreProperties({"location", "empty", "middleOffset", "size", "sizeEmpty", "subRegions"})
public class Region {
    private static Logger logger = new Logger();
    @JsonProperty("left")
    private int left;
    @JsonProperty("top")
    private int top;
    @JsonProperty("width")
    private int width;
    @JsonProperty("height")
    private int height;
    @JsonProperty("coordinatesType")
    private CoordinatesType coordinatesType;

    public static final Region EMPTY = new Region(0, 0, 0, 0, CoordinatesType.SCREENSHOT_AS_IS);

    public static void initLogger(Logger logger) {
        Region.logger = logger;
    }

    protected void makeEmpty() {
        left = EMPTY.getLeft();
        top = EMPTY.getTop();
        width = EMPTY.getWidth();
        height = EMPTY.getHeight();
        this.coordinatesType = EMPTY.getCoordinatesType();
    }

    @JsonCreator()
    public Region(@JsonProperty("left") int left,
                  @JsonProperty("top") int top,
                  @JsonProperty("width") int width,
                  @JsonProperty("height") int height) {
        this(left, top, width, height, CoordinatesType.SCREENSHOT_AS_IS);
    }

    public Region(int left, int top, int width, int height, CoordinatesType coordinatesType) {
        ArgumentGuard.greaterThanOrEqualToZero(width, "width");
        ArgumentGuard.greaterThanOrEqualToZero(height, "height");

        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.coordinatesType = coordinatesType;
    }


    public Region(RegionF region) {
        ArgumentGuard.notNull(region, "region");

        this.left = Math.round(region.getLeft());
        this.top = Math.round(region.getTop());
        this.width = Math.round(region.getWidth());
        this.height = Math.round(region.getHeight());
        this.coordinatesType = region.getCoordinatesType();
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

        if (!(obj instanceof Region)) {
            return false;
        }
        Region other = (Region) obj;

        return (this.getLeft() == other.getLeft())
                && (this.getTop() == other.getTop())
                && (this.getWidth() == other.getWidth())
                && (this.getHeight() == other.getHeight());
    }

    @Override
    public int hashCode() {
        return left + top + width + height;
    }

    public Region(Region other) {
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
     * Check if a region is contained within the current region.
     * @param other The region to check if it is contained within the current
     *              region.
     * @return True if {@code other} is contained within the current region,
     * false otherwise.
     */
    @SuppressWarnings("UnusedDeclaration")
    public boolean contains(Region other) {
        int right = left + width;
        int otherRight = other.getLeft() + other.getWidth();

        int bottom = top + height;
        int otherBottom = other.getTop() + other.getHeight();

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
    public boolean isIntersected(Region other) {
        int right = left + width;
        int bottom = top + height;

        int otherLeft = other.getLeft();
        int otherTop = other.getTop();
        int otherRight = otherLeft + other.getWidth();
        int otherBottom = otherTop + other.getHeight();

        return (((left <= otherLeft && otherLeft <= right)
                || (otherLeft <= left && left <= otherRight))
                && ((top <= otherTop && otherTop <= bottom)
                || (otherTop <= top && top <= otherBottom)));
    }

    /**
     * Replaces this region with the intersection of itself and {@code other}
     * @param other The region with which to intersect.
     */
    public void intersect(Region other) {

        logger.verbose(String.format("intersecting this region (%s) with %s ...", this, other));

        // If there's no intersection set this as the Empty region.
        if (!isIntersected(other)) {
            makeEmpty();
            return;
        }

        // The regions intersect. So let's first find the left & top values
        int otherLeft = other.getLeft();
        int otherTop = other.getTop();

        int intersectionLeft = (left >= otherLeft) ? left : otherLeft;
        int intersectionTop = (top >= otherTop) ? top : otherTop;

        // Now the width and height of the intersect
        int right = left + width;
        int otherRight = otherLeft + other.getWidth();
        int intersectionRight = (right <= otherRight) ? right : otherRight;
        int intersectionWidth = intersectionRight - intersectionLeft;

        int bottom = top + height;
        int otherBottom = otherTop + other.getHeight();
        int intersectionBottom = (bottom <= otherBottom) ? bottom : otherBottom;
        int intersectionHeight = intersectionBottom - intersectionTop;

        left = intersectionLeft;
        top = intersectionTop;
        width = intersectionWidth;
        height = intersectionHeight;

    }


    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public void setLeft(int value) {
        left = value;
    }

    public void setTop(int value) {
        top = value;
    }

    public void setWidth(int value) {
        width = value;
    }

    public void setHeight(int value) {
        height = value;
    }

    public Location getMiddleOffset() {
        int middleX = width / 2;
        int middleY = height / 2;

        return new Location(middleX, middleY);
    }

    public Region expandToContain(Region region) {
        int left = Math.min(this.left, region.left);
        int top = Math.min(this.top, region.top);

        int thisRight = this.left + this.width;
        int otherRight = region.left + region.width;
        int maxRight = Math.max(thisRight, otherRight);
        int width = maxRight - left;

        int thisBottom = this.top + this.height;
        int otherBottom = region.top + region.height;
        int maxBottom = Math.max(thisBottom, otherBottom);
        int height = maxBottom - top;

        return new Region(left, top, width, height);
    }

    @Override
    public String toString() {
        return "(" + left + ", " + top + ") " + width + "x" + height + ", " + coordinatesType;
    }
}
