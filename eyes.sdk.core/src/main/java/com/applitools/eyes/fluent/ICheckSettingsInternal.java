package com.applitools.eyes.fluent;

import com.applitools.eyes.visualgridclient.model.VisualGridSelector;

public interface ICheckSettingsInternal extends com.applitools.ICheckSettingsInternal {
    boolean getStitchContent();

    GetRegion[] getIgnoreRegions();

    GetRegion[] getStrictRegions();

    GetRegion[] getLayoutRegions();

    GetRegion[] getContentRegions();

    GetFloatingRegion[] getFloatingRegions();

    Boolean getIgnoreCaret();

    boolean isEnablePatterns();

    VisualGridSelector GetTargetSelector();

}
