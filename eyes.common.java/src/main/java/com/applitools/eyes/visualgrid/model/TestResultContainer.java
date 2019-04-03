package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.TestResults;

public class TestResultContainer {
    private TestResults testResults;
    private Error exception;

    public TestResultContainer(TestResults testResults, Error exception) {
        this.testResults = testResults;
        this.exception = exception;
    }

    public TestResults getTestResults() {
        return testResults;
    }

    public Error getException() {
        return exception;
    }

    @Override
    public String toString() {
        return (testResults == null ? "" : testResults.toString()) + " - " + (exception == null ? "" : exception.toString());
    }
}
