package com.applitools.eyes.visualGridClient.model;

import com.applitools.eyes.TestResults;

import java.util.List;

public class TestResultSummary {
    private List<TestResultContainer> allResults;
    private int passed = 0;
    private int unresolved = 0;
    private int failed = 0;
    private int exceptions = 0;
    private int mismatches = 0;
    private int missing = 0;
    private int matches = 0;

    public TestResultSummary(List<TestResultContainer> allResults) {
        this.allResults = allResults;
        for (TestResultContainer resultContainer : allResults) {
            if (resultContainer.getException() != null){
                this.exceptions++;
            }
            TestResults result = resultContainer.getTestResults();
            if (result == null) continue;
            if (result.getStatus() != null) {
                switch (result.getStatus()) {
                    case Failed:
                        this.failed++;
                        break;
                    case Passed:
                        this.passed++;
                        break;
                    case Unresolved:
                        this.unresolved++;
                        break;
                }
            }
            matches += result.getMatches();
            missing += result.getMissing();
            mismatches += result.getMismatches();
        }
    }

    public TestResultContainer[] getAllResults() {
        return allResults.toArray(new TestResultContainer[0]);
    }

    @Override
    public String toString() {
        return "result summary{" +
                "all results=" + allResults +
                ", passed=" + passed +
                ", unresolved=" + unresolved +
                ", failed=" + failed +
                ", exceptions=" + exceptions +
                ", mismatches=" + mismatches +
                ", missing=" + missing +
                ", matches=" + matches +
                '}';
    }
}
