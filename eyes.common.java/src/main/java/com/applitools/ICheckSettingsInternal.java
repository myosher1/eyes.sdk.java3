package com.applitools;

import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.Region;

public interface ICheckSettingsInternal {
    Region getTargetRegion();

    int getTimeout();

    MatchLevel getMatchLevel();

    String getName();
}
