package com.applitools.eyes.visualgridclient.services;

import com.applitools.eyes.visualgridclient.model.TestResultSummary;

public interface EyesRunner {

    TestResultSummary getAllTestResults();

    TestResultSummary getAllTestResults(boolean shouldThrowException);
}
