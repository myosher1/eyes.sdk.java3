package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a 2D size.
 */
public class RectangleSizeF {
    private float width;
    private float height;

    public static RectangleSizeF EMPTY = new RectangleSizeF(0,0);

    /**
     * Creates a new RectangleSize instance.
     * @param width The width of the rectangle.
     * @param height The height of the rectangle.
     */
    @JsonCreator
    public RectangleSizeF(@JsonProperty("width") float width, @JsonProperty("height") float height) {
        ArgumentGuard.greaterThanOrEqualToZero(width, "width");
        ArgumentGuard.greaterThanOrEqualToZero(height, "height");

        this.width = width;
        this.height = height;
    }

    public RectangleSizeF(RectangleSize size) {
        ArgumentGuard.notNull(size,"size");
        this.width = size.getWidth();
        this.height = size.getHeight();
    }

    public boolean isEmpty() {
        return this.width == 0 && this.height == 0;
    }

    /**
     * @return The rectangle's width.
     */
    public float getWidth() {
        return width;
    }

    /**
     * @return The rectangle's height.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Parses a string into a {link RectangleSize} instance.
     * @param size A string representing width and height separated by "x".
     * @return An instance representing the input size.
     */
    public static RectangleSizeF parse(String size) {
        ArgumentGuard.notNull(size, "size");
        String[] parts = size.split("x");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Not a valid size string: " + size);
        }

        return new RectangleSizeF(
                Float.parseFloat(parts[0]), Float.parseFloat(parts[1]));
    }

    /**
     * Get a scaled version of the current size.
     *
     * @param scaleRatio The ratio by which to scale.
     * @return A scaled version of the current size.
     */
    public RectangleSizeF scale(double scaleRatio) {
        return new RectangleSizeF(width * (float)scaleRatio, height * (float)scaleRatio);
    }

    /**
     * @param obj A {@link RectangleSizeF} instance to be
     *            checked for equality with the current instance.
     * @return {@code true} if and only if the input objects are equal by
     *          value, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof RectangleSizeF)) {
            return false;
        }

        RectangleSizeF other = (RectangleSizeF) obj;
        return width == other.width && height == other.height;
    }

    @Override
    public int hashCode() {
        return (int)width ^ (int)height;
    }


    @Override
    public String toString() {
        return width + "x" + height;
    }
}
