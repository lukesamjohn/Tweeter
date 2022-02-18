package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecution <T extends Runnable>{

    T task;

    public TaskExecution(T task) {
        this.task = task;
    }

    public void executeTask() {
        BackgroundTaskUtils.runTask(task);
    }

}
