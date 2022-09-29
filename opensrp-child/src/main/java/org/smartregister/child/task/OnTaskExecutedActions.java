package org.smartregister.child.task;

public interface OnTaskExecutedActions<R> {

    void onTaskStarted();

    void execute();

    void onTaskResult(R result);
}
