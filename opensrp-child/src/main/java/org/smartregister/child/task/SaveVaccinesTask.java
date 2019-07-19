package org.smartregister.child.task;

import android.os.AsyncTask;
import android.view.View;

import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.immunization.domain.VaccineWrapper;

public class SaveVaccinesTask extends AsyncTask<VaccineWrapper, Void, Void> {
    private View view;
    private BaseChildDetailTabbedActivity activity;

    public SaveVaccinesTask(BaseChildDetailTabbedActivity activity) {
        this.activity = activity;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    protected Void doInBackground(VaccineWrapper... vaccineWrappers) {
        for (VaccineWrapper tag : vaccineWrappers) {
            activity.saveVaccine(tag);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        activity.showProgressDialog();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        activity.hideProgressDialog();
        activity.updateVaccineGroupViews(view);
    }
}
