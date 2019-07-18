package org.smartregister.child.task;

import android.os.AsyncTask;

import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.domain.UpdateRegisterParams;

public class SaveRegistrationDetailsTask extends AsyncTask<Void, Void, Void> {
    private String jsonString;
    private BaseChildDetailTabbedActivity activity;

    public SaveRegistrationDetailsTask(BaseChildDetailTabbedActivity activity) {
        this.activity = activity;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    @Override
    protected Void doInBackground(Void... params) {

        UpdateRegisterParams updateRegisterParams = new UpdateRegisterParams();
        updateRegisterParams.setEditMode(true);

        activity.saveForm(jsonString, updateRegisterParams);
        activity.onRegistrationSaved(true);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.showProgressDialog(activity.getString(R.string.updating_dialog_title), activity.getString(R.string.please_wait_message));
    }
}
