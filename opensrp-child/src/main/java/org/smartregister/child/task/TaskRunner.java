package org.smartregister.child.task;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class TaskRunner {

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public <R> void executeAsync(TaskCallable<R> callable) {
        try {
            callable.onTaskStarted();
            executor.execute(new RunnableTask<R>(handler, callable));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static class RunnableTask<R> implements Runnable {

        private final Handler handler;
        private final TaskCallable<R> callable;

        public RunnableTask(Handler handler, TaskCallable<R> callable) {
            this.handler = handler;
            this.callable = callable;
        }

        @Override
        public void run() {
            try {
                final R result = callable.call();
                handler.post(new RunnableTaskForHandler<>(callable, result));
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    public static class RunnableTaskForHandler<R> implements Runnable {

        private TaskCallable<R> callable;
        private R result;

        public RunnableTaskForHandler(TaskCallable<R> callable, R result) {
            this.callable = callable;
            this.result = result;
        }

        @Override
        public void run() {
            callable.onTaskResult(result);
        }
    }
}
