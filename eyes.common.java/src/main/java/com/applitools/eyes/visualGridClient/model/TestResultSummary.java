package com.applitools.eyes.visualGridClient.model;

import com.applitools.eyes.TestResults;

import java.util.List;

public class TestResultSummary {
    private List<Object> allResults;
    private int passed = 0;
    private int unresolved = 0;
    private int failed = 0;
    private int exceptions = 0;
    private int missmatches = 0;
    private int missing = 0;
    private int matches = 0;

    public TestResultSummary(List<TestResults> allResults) {
        for (TestResults result : allResults) {
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
            } 

    }

    @Override
    public String toString() {
        return "resultummery{" +
                "allResults=" + allResults +
                ", passed=" + passed +
                ", unresolved=" + unresolved +
                ", failed=" + failed +
                ", exceptions=" + exceptions +
                ", missmatches=" + missmatches +
                ", missing=" + missing +
                ", matches=" + matches +
                '}';
    }
}
