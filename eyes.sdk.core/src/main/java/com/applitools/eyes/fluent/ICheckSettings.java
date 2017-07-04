package com.applitools.eyes.fluent;

import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.Region;

/**
 * The interface of the match settings object.
 */
public interface ICheckSettings {
    /**
     * Adds one or more ignore regions.
     * @param regions One or more regions to ignore when validating the screenshot.
     * @return This instance of the settings object.
     */
    ICheckSettings Ignore(Region... regions);

    /**
     * Defines that the screenshot will contain the entire element or region, even if it's outside the view.
     * @return This instance of the settings object.
     */
    ICheckSettings Fully();

    /**
     * Adds a floating region. A floating region is a a region that can be placed within the boundaries of a bigger region.
     * @param maxOffset How much each of the content rectangles can move in any direction.
     * @param regions One or more content rectangles.
     * @return This instance of the settings object.
     */
    ICheckSettings Floating(int maxOffset, Region... regions);

    /**
     * Adds a floating region. A floating region is a a region that can be placed within the boundaries of a bigger region.
     * @param region The content rectangle.
     * @param maxUpOffset How much the content can move up.
     * @param maxDownOffset How much the content can move down.
     * @param maxLeftOffset How much the content can move to the left.
     * @param maxRightOffset How much the content can move to the right.
     * @return This instance of the settings object.
     */
    ICheckSettings Floating(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset);

    /**
     * Defines the timeout to use when acquiring and comparing screenshots.
     * @param timeoutMilliseconds The timeout to use in milliseconds.
     * @return This instance of the settings object.
     */
    ICheckSettings Timeout(int timeoutMilliseconds);

    /**
     * Shortcut to set the match level to {@code MatchLevel.LAYOUT}.
     * @return This instance of the settings object.
     */
    ICheckSettings Layout();

    /**
     * Shortcut to set the match level to {@code MatchLevel.EXACT}.
     * @return This instance of the settings object.
     */
    ICheckSettings Exact();

    /**
     * Shortcut to set the match level to {@code MatchLevel.STRICT}.
     * @return This instance of the settings object.
     */
    ICheckSettings Strict();

    /**
     * Shortcut to set the match level to {@code MatchLevel.CONTENT}.
     * @return This instance of the settings object.
     */
    ICheckSettings Content();

    /**
     * Set the match level by which to compare the screenshot.
     * @param matchLevel The match level to use.
     * @return This instance of the settings object.
     */
    ICheckSettings MatchLevel(MatchLevel matchLevel);

    /**
     * Defines if to detect and ignore a blinking caret in the screenshot.
     * @param ignoreCaret Whether or not to detect and ignore a blinking caret in the screenshot.
     * @return This instance of the settings object.
     */
    ICheckSettings IgnoreCaret(boolean ignoreCaret);

    /**
     * Defines to ignore a blinking caret in the screenshot.
     * @return This instance of the settings object.
     */
    ICheckSettings IgnoreCaret();
}
