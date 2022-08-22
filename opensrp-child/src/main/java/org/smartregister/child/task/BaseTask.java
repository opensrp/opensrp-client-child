package org.smartregister.child.task;

public abstract class BaseTask<R> implements TaskCallable<R> {

    @Override
    public void onTaskStarted() {
        // do nothing
    }

    @Override
    public R call() throws Exception {
        return null;
    }

    @Override
    public void onTaskResult(R result) {
        // do nothing
    }
}
