package org.smartregister.child.task;

import android.content.Context;

import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.contract.IChildStatus;
import org.smartregister.util.AppExecutorService;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 01/09/2020.
 */
public class SaveChildStatusTask implements OnTaskExecutedActions<TaskResult> {

    private IChildStatus presenter;
    private Context context;
    private AppExecutorService appExecutors;

    public SaveChildStatusTask(Context context, IChildStatus presenter) {
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    public void onTaskStarted() {
        presenter.getView().showProgressDialog(context.getResources().getString(R.string.updating_dialog_title), "");
    }

    @Override
    public void execute() {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            try {
                presenter.activateChildStatus(ChildLibrary.getInstance().context(), presenter.getView().getChildDetails());
            } catch (Exception e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> onTaskResult(TaskResult.SUCCESS));
        });
    }

    @Override
    public void onTaskResult(TaskResult result) {
        presenter.getView().hideProgressDialog();
        presenter.getView().updateViews();
    }
}
