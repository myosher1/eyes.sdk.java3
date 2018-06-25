package com.applitools.eyes.selenium.positioning;

import com.applitools.eyes.Logger;
import com.applitools.eyes.positioning.PositionProvider;
import com.applitools.eyes.selenium.SeleniumJavaScriptExecutor;
import com.applitools.eyes.selenium.StitchMode;
import com.applitools.utils.ArgumentGuard;
import org.openqa.selenium.WebElement;

public class SeleniumScrollPositionProviderFactory {
    public static PositionProvider getPositionProvider(Logger logger, StitchMode stitchMode, SeleniumJavaScriptExecutor jsExecutor, WebElement scrollRootElement) {
        ArgumentGuard.notNull(logger, "logger");
        ArgumentGuard.notNull(jsExecutor, "jsExecutor");

        switch (stitchMode)
        {
            case SCROLL: return ScrollPositionProvider.getInstance(logger, jsExecutor);
            case CSS: return new CssTranslatePositionProvider(logger, jsExecutor, scrollRootElement);
            default:
                return null;
        }
    }
}
