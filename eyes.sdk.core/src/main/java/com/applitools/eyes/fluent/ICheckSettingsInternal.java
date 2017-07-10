package com.applitools.eyes.fluent;

import com.applitools.eyes.FloatingMatchSettings;
import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.Region;

public interface ICheckSettingsInternal {
    Region getTargetRegion();
    int getTimeout();
    boolean getStitchContent();
    MatchLevel getMatchLevel();
    Region[] getIgnoreRegions();
    FloatingMatchSettings[] getFloatingRegions();
    Boolean getIgnoreCaret();
}
