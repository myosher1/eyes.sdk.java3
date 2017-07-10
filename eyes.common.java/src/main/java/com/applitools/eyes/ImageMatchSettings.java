package com.applitools.eyes;

/**
 * Encapsulates match settings for the a session.
 */
public class ImageMatchSettings {
    private MatchLevel matchLevel;
    private ExactMatchSettings exact;
    private Boolean ignoreCaret;
    private Region ignoreRegions[];
    private FloatingMatchSettings floatingMatchSettings[];

    public ImageMatchSettings(MatchLevel matchLevel, ExactMatchSettings exact) {
        this.matchLevel = matchLevel;
        this.exact = exact;
        this.ignoreCaret = null;
    }

    /**
     * See {@link #ImageMatchSettings(MatchLevel, ExactMatchSettings)}.
     * {@code matchLevel} defaults to {@code STRICT},
     * {@code exact} defaults to {@code null}.
     */
    public ImageMatchSettings() {
        this(MatchLevel.STRICT, null);
    }

    /**
     *
     * @return The "strictness" level of the match.
     */
    public MatchLevel getMatchLevel() {
        return matchLevel;
    }

    /**
     *
     * @param matchLevel The "strictness" level of the match.
     */
    public void setMatchLevel(MatchLevel matchLevel) {
        this.matchLevel = matchLevel;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @return The parameters for the "Exact" match settings.
     */
    public ExactMatchSettings getExact() {
        return exact;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @param exact The parameters for the "exact" match settings.
     */
    public void setExact(ExactMatchSettings exact) {
        this.exact = exact;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @return The parameters for the "IgnoreCaret" match settings.
     */
    public Boolean getIgnoreCaret() {
        return ignoreCaret;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Sets an array of regions to ignore.
     * @param ignoreRegions The array of regions to ignore.
     */
    public void setIgnoreRegions(Region[] ignoreRegions) {
        this.ignoreRegions = ignoreRegions;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Returns the array of regions to ignore.
     * @return the array of regions to ignore.
     */
    public Region[] getIgnoreRegion() {
        return this.ignoreRegions;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Sets an array of floating regions.
     * @param floatingRegions The array of floating regions.
     */
    public void setFloatingRegions(FloatingMatchSettings[] floatingRegions) {
        this.floatingMatchSettings = floatingRegions;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     * Returns an array of floating regions.
     * @return an array of floating regions.
     */
    public FloatingMatchSettings[] getFloatingRegions() {
        return this.floatingMatchSettings;
    }

    @SuppressWarnings("UnusedDeclaration")
    /**
     *
     * @param ignoreCaret The parameters for the "ignoreCaret" match settings.
     */
    public void setIgnoreCaret(Boolean ignoreCaret) {
        this.ignoreCaret = ignoreCaret;
    }


    public String toString() {
        return String.format("Match level: %s, Exact match settings: %s",
                matchLevel, exact);
    }
}
