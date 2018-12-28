package com.applitools.eyes.visualGridClient.services;

public class ScoreTask {

    private Task task;
    private int score;

    ScoreTask(Task task, int score) {
        this.task = task;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    Task getTask(){
        task.setIsSent();
        return task;
    }
}
