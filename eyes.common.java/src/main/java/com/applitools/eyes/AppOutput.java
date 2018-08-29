package com.applitools.eyes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * An application output (title, image, etc).
 */
@JsonIgnoreProperties({"screenshot64"})
public class AppOutput {

    /**
     * The title of the screen of the application being captured.
     */
    private final String title;
    private final String screenshot64;
    private final String domUrl;

    /**
     * @param title        The title of the window.
     * @param screenshot64 Base64 encoding of the screenshot's bytes.
     * @param domUrl       The URL where the DOM snapshot is.
     */
    public AppOutput(String title, String screenshot64, String domUrl) {
        this.title = title;
        this.screenshot64 = screenshot64;
        this.domUrl = domUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getScreenshot64() {
        return screenshot64;
    }

    public String getDomUrl() {
        return domUrl;
    }
}