package com.applitools.eyes.fluent;

public interface ICheckSettingsInternal extends com.applitools.ICheckSettingsInternal {
    boolean getStitchContent();

    GetRegion[] getIgnoreRegions();

    GetRegion[] getStrictRegions();

    GetRegion[] getLayoutRegions();

    GetRegion[] getContentRegions();

    GetFloatingRegion[] getFloatingRegions();

    Boolean getIgnoreCaret();

    public boolean isEnablePatterns();

}
