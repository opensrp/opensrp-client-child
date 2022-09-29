package org.smartregister.child.task;

public interface PostTaskExecutionListener<R> {

    void onTaskResult(R result);
}
