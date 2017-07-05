package com.applitools.eyes.selenium.fluent;

import com.applitools.eyes.Region;
import com.applitools.eyes.fluent.CheckSettings;
import com.applitools.eyes.fluent.ICheckSettings;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;

public class SeleniumCheckSettings extends CheckSettings implements ISeleniumCheckTarget {

    private By targetSelector;
    private List<FrameLocator> frameChain = new ArrayList<>();

    public SeleniumCheckSettings() { }

    public SeleniumCheckSettings(Region region) {
        super(region);
    }

    public SeleniumCheckSettings(By targetSelector)
    {
        this.targetSelector = targetSelector;
    }

    @Override
    public By getTargetSelector() {
        return this.targetSelector;
    }

    @Override
    public List<FrameLocator> getFrameChain() {
        return this.frameChain;
    }

    public SeleniumCheckSettings frame(By by)
    {
        FrameLocator fl = new FrameLocator();
        fl.setFrameSelector(by);
        this.frameChain.add(fl);
        return this;
    }

    public SeleniumCheckSettings frame(String frameNameOrId)
    {
        FrameLocator fl = new FrameLocator();
        fl.setFrameNameOrId(frameNameOrId);
        this.frameChain.add(fl);
        return this;
    }

    public SeleniumCheckSettings frame(int index)
    {
        FrameLocator fl = new FrameLocator();
        fl.setFrameIndex(index);
        this.frameChain.add(fl);
        return this;
    }

    public ICheckSettings region(Region region)
    {
        super.updateTargetRegion(region);
        return this;
    }

    public ICheckSettings region(By by)
    {
        this.targetSelector = by;
        return this;
    }
}
