package com.applitools.eyes.visualgrid.model;

import com.applitools.eyes.TestResults;

public class TestResultContainer {

    private TestResults testResults;
    private RenderBrowserInfo browserInfo;
    private Throwable exception;

    public TestResultContainer(TestResults testResults, RenderBrowserInfo browserInfo, Throwable exception) {
        this.testResults = testResults;
        this.browserInfo = browserInfo;
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
        String browserInfoStr = browserInfo != null ?  "\n browserInfo = " + browserInfo : "";
        return "TestResultContainer{" +
                "\n testResults=" + testResults +
                 browserInfoStr +
                "\n exception = " + exception +
                '}';
    }
}
