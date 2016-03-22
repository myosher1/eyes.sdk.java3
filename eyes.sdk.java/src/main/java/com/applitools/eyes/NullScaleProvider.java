package com.applitools.eyes;

/**
 * A scale provider which does not do scaling.
 */
public class NullScaleProvider extends FixedScaleProvider {

    public NullScaleProvider() {
        super(1);
    }
}
