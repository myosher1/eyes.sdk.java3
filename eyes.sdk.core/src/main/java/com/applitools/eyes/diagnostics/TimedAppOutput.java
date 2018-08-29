package com.applitools.eyes.diagnostics;

import com.applitools.eyes.AppOutput;

/**
 * AppOutput with timing information.
 */
public class TimedAppOutput extends AppOutput {
    private final long elapsed;
    private final boolean isPrimary;

    /**
     * @param title        The title of the window.
     * @param screenshot64 Base64 encoding of the screenshot's bytes.
     * @param elapsed      The elapsed time from the first captured window
     *                     until this window was captured.
     * @param isPrimary    Whether this window is considered a "primary"
     *                     (e.g., if the user expected that up to this
     *                     window there should already have been a match in
     *                     a timing test).
     * @param domUrl       The URL where the DOM snapshot is.
     */
    public TimedAppOutput(String title, String screenshot64, long elapsed,
                          boolean isPrimary, String domUrl) {
        super(title, screenshot64, domUrl);
        this.elapsed = elapsed;
        this.isPrimary = isPrimary;
    }

    @SuppressWarnings("unused")
    public long getElapsed() {
        return elapsed;
    }

    @SuppressWarnings("unused")
    public boolean getIsPrimary() {
        return isPrimary;
    }
}
