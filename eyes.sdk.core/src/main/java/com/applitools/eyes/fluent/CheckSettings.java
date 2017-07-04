package com.applitools.eyes.fluent;

import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * The Match settings object to use in the various Eyes.Check methods.
 */
public class CheckSettings implements ICheckSettings {

    private Region targetRegion;
    private List<Region> ignoreRegions = new ArrayList<>();
    private MatchLevel matchLevel;
    private Boolean ignoreCaret;
    private boolean stitchContent = false;
    private List<FloatingMatchSettings> floatingRegions = new ArrayList<>();
    private int timeout = -1;

    protected CheckSettings() { }

    protected CheckSettings(Region region)
    {
        this.targetRegion = region;
    }

    protected void Ignore(Region region) {
        ignoreRegions.add(region);
    }

    protected void Floating_(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset){
        this.floatingRegions.add(
                new FloatingMatchSettings(
                        region.getLeft(),
                        region.getTop(),
                        region.getLeft()+region.getWidth(),
                        region.getTop()+region.getHeight(),
                        maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings Ignore(Region... regions) {
        for (Region r : regions) {
            Ignore(r);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings Fully() {
        this.stitchContent = true;
        return this;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings Floating(int maxOffset, Region... regions) {
        for (Region r:regions) {
            this.Floating_(r, maxOffset, maxOffset, maxOffset, maxOffset);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings Floating(Region region, int maxUpOffset, int maxDownOffset, int maxLeftOffset, int maxRightOffset) {
        this.Floating_(region, maxUpOffset, maxDownOffset, maxLeftOffset, maxRightOffset);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings Timeout(int timeoutMilliseconds) {
        this.timeout = timeoutMilliseconds;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings Layout() {
        this.matchLevel = MatchLevel.LAYOUT;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings Exact() {
        this.matchLevel = MatchLevel.EXACT;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings Strict() {
        this.matchLevel = MatchLevel.STRICT;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings Content() {
        this.matchLevel = MatchLevel.CONTENT;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings MatchLevel(MatchLevel matchLevel) {
        this.matchLevel = matchLevel;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings IgnoreCaret(boolean ignoreCaret) {
        this.ignoreCaret = ignoreCaret;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICheckSettings IgnoreCaret() {
        this.ignoreCaret = true;
        return this;
    }
}
