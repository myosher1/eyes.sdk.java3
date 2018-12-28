package com.applitools.eyes.visualGridClient.model;

import com.applitools.eyes.TestResults;

public class TestResultContainer {
    private TestResults testResults;
    private Exception exception;

    public TestResultContainer(TestResults testResults, Exception exception) {
        this.testResults = testResults;
        this.exception = exception;
    }

    public TestResults getTestResults() {
        return testResults;
    }

    public Exception getException() {
        return exception;
    }

    @Override
    public String toString() {
        return (testResults == null ? "" : testResults.toString()) + " - " + (exception == null ? "" : exception.toString());
    }
}
