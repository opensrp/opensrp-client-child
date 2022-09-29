package org.smartregister.child.task;

import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.util.AppExecutorService;

import timber.log.Timber;

public class SaveRegistrationDetailsTask implements OnTaskExecutedActions<TaskResult> {

    private String jsonString;
    private BaseChildDetailTabbedActivity activity;
    private AppExecutorService appExecutors;

    public SaveRegistrationDetailsTask(BaseChildDetailTabbedActivity activity) {
        this.activity = activity;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    @Override
    public void onTaskStarted() {
        // notify on UI
    }

    @Override
    public void execute() {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            try {
                UpdateRegisterParams updateRegisterParams = new UpdateRegisterParams();
                updateRegisterParams.setEditMode(true);

                activity.saveForm(jsonString, updateRegisterParams);
                activity.onRegistrationSaved(true);
            } catch (Exception e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> onTaskResult(TaskResult.SUCCESS));
        });
    }

    @Override
    public void onTaskResult(TaskResult result) {
        activity.showProgressDialog(activity.getString(R.string.updating_dialog_title), activity.getString(R.string.please_wait_message));
    }
}
