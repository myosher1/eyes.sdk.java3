package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.TestResults;

public class TestResultContainer {
    private TestResults testResults;
    private Throwable exception;

    public TestResultContainer(TestResults testResults, Throwable exception) {
        this.testResults = testResults;
        this.exception = exception;
    }

    public TestResults getTestResults() {
        return testResults;
    }

    public Throwable getException() {
        return exception;
    }

    @Override
    public String toString() {
        return (testResults == null ? "" : testResults.toString()) + " - " + (exception == null ? "" : exception.toString());
    }
}
