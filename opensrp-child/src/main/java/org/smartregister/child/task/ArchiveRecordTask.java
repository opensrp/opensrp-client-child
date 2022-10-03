package org.smartregister.child.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;

import org.smartregister.child.R;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.util.AppExecutorService;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;

import timber.log.Timber;

public class ArchiveRecordTask implements OnTaskExecutedActions<TaskResult> {

    private final WeakReference<Activity> activityWeakReference;
    private final Map<String, String> childDetails;
    private ProgressDialog progressDialog;
    private AppExecutorService appExecutors;

    public ArchiveRecordTask(@NonNull Activity activity, @NonNull Map<String, String> childDetails) {
        this.activityWeakReference = new WeakReference<>(activity);
        this.childDetails = childDetails;
    }

    @Override
    public void onTaskStarted() {
        progressDialog = new ProgressDialog(activityWeakReference.get());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(activityWeakReference.get().getString(R.string.child_archive_record_task_dialog_message));
        progressDialog.show();
    }

    @Override
    public void execute() {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            try {
                String baseEntityId = childDetails.get(Constants.KEY.BASE_ENTITY_ID);
                Event archiveRecordEvent;
                if (baseEntityId != null) {
                    archiveRecordEvent = Utils.createArchiveRecordEvent(baseEntityId);
                    Utils.initiateEventProcessing(Collections.singletonList(archiveRecordEvent.getFormSubmissionId()));
                }
            } catch (Exception e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> onTaskResult(TaskResult.SUCCESS));
        });
    }

    @Override
    public void onTaskResult(TaskResult result) {
        progressDialog.dismiss();
        Activity activity = activityWeakReference.get();
        Intent intent = new Intent(activity, Utils.metadata().getChildRegisterActivity()); // update with the register
        activity.startActivity(intent);
        activity.finish();
    }
}
