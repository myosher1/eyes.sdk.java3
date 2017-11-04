package com.applitools.eyes.exceptions;

import com.applitools.eyes.TestResults;

/**
 * Indicates that an existing test ended, and that differences where found from the baseline.
 */
public class DiffsFoundException extends TestFailedException {
    public DiffsFoundException(TestResults testResults, String test, String app, String url) {
        super(testResults, String.format("Test '%s' of '%s' detected differences! See details at: %s", test, app, url));
    }
}
