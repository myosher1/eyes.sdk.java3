package com.applitools.eyes.fluent;

import com.applitools.ICheckSettings;
import com.applitools.eyes.visualgrid.model.VisualGridSelector;

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

    ICheckSettings scriptHook(String hook);

}
