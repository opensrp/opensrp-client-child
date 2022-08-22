package org.smartregister.child.task;

import java.util.concurrent.Callable;

public interface TaskCallable<R> extends Callable<R> {

    void onTaskStarted();

    void onTaskResult(R result);
}
