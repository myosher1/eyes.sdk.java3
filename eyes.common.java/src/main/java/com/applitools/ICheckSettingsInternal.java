package com.applitools;

import com.applitools.eyes.MatchLevel;
import com.applitools.eyes.Region;

import java.util.Map;

public interface ICheckSettingsInternal {
    Region getTargetRegion();

    int getTimeout();

    MatchLevel getMatchLevel();

    String getName();

    Map<String, String> getScriptHooks();

    String getSizeMode();

    Region getRegion();

    boolean isSendDom();
}
