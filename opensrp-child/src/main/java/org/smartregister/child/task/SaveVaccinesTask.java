package org.smartregister.child.task;

import android.view.View;

import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.util.AppExecutorService;

import timber.log.Timber;

public class SaveVaccinesTask implements OnTaskExecutedActions<TaskResult> {

    private View view;
    private BaseChildDetailTabbedActivity activity;
    private AppExecutorService appExecutors;
    private VaccineWrapper[] vaccineWrappers;

    public SaveVaccinesTask(BaseChildDetailTabbedActivity activity, VaccineWrapper... vaccineWrappers) {
        this.activity = activity;
        this.vaccineWrappers = vaccineWrappers;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void onTaskStarted() {
        activity.showProgressDialog();
    }

    @Override
    public void execute() {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            try {
                for (VaccineWrapper tag : this.vaccineWrappers) {
                    activity.saveVaccine(tag);
                }
            } catch (Exception e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> onTaskResult(TaskResult.SUCCESS));
        });
    }

    @Override
    public void onTaskResult(TaskResult result) {
        activity.hideProgressDialog();
        activity.updateVaccineGroupViews(view);
    }
}
