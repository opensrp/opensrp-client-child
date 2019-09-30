package org.smartregister.child.task;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.util.Utils;
import org.smartregister.util.FormUtils;

import timber.log.Timber;

public class LaunchAdverseEventFormTask extends AsyncTask<Void, Void, String> {
    private BaseChildDetailTabbedActivity activity;

    public LaunchAdverseEventFormTask(BaseChildDetailTabbedActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            JSONObject form = FormUtils.getInstance(activity.getContext()).getFormJson("adverse_event");
            if (form != null) {
                JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
                for (int i = 0; i < fields.length(); i++) {
                    if (fields.getJSONObject(i).getString("key").equals("Reaction_Vaccine")) {
                        boolean result = activity.insertVaccinesGivenAsOptions(fields.getJSONObject(i));
                        if (!result) {
                            return null;
                        }
                    }
                }
                return form.toString();
            }

        } catch (Exception e) {
            Timber.e(e, "LaunchAdverseEventFormTask --> doInBackground");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String metaData) {
        super.onPostExecute(metaData);
        if (metaData != null) {
            activity.startFormActivity(metaData);
        } else {
            Utils.showToast(activity.getContext(), activity.getContext().getString(R.string.no_vaccine_record_found));
        }
    }
}
