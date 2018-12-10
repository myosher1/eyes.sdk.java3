package com.applitools.eyes.visualGridClient.services;

import java.util.List;

public class ScoreTask {

    private Task task;
    private int score;
    private List<Task> taskList;

    public ScoreTask(Task task, int score, List<Task> taskList) {
        this.task = task;
        this.score = score;
        this.taskList = taskList;
    }

    public int getScore() {
        return score;
    }

    Task getTask(){
        taskList.remove(task);
        return task;
    }
}
