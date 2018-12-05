package com.applitools;

import com.applitools.eyes.Region;

public interface ICheckRGSettings {
    Region getRegion();

    Object getScriptHooks();

    boolean isSendDom();
}
