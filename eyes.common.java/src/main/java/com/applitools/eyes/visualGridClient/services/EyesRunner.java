package com.applitools.eyes.visualGridClient.services;

import com.applitools.eyes.visualGridClient.model.TestResultSummary;

public interface EyesRunner {

    TestResultSummary getAllTestResults();

    TestResultSummary getAllTestResults(boolean shouldThrowException);
}
