package com.applitools;

import com.applitools.eyes.Region;

public interface ICheckRGSettingsInternal {
    Region getRegion();

    Object getScriptHooks();

    String getSizeMode();

    boolean isSendDom();
}
