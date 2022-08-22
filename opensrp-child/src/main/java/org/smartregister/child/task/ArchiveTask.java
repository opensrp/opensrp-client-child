package org.smartregister.child.task;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ArchiveTask extends BaseTask<Object> {

    private Context context;

    public ArchiveTask(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void onTaskStarted() {
        Toast.makeText(context, "Busy doing nothing...", Toast.LENGTH_LONG).show();
    }

    @Override
    public Object call() {
        List<String> list = new ArrayList<>();
        list.add("Aa");
        list.add("Bb");
        list.add("Cc");

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }

        return list;
    }

    @Override
    public void onTaskResult(Object result) {
        Toast.makeText(context, "Task complete! " + result, Toast.LENGTH_SHORT).show();
    }
}
