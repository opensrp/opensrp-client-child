package org.smartregister.child.task;

import android.os.AsyncTask;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.util.Utils;
import org.smartregister.util.FormUtils;

import timber.log.Timber;

/**
 * This Task launches the Adverse Event Form
 * <p>
 * For the form to load the Vaccines in the Reaction_Vaccine field/widget it needs to dynamically check from the DB which vaccines have already been provided for the client.
 * It dynamically creates the spinner elements that eventually render. i.e.The elements/options hardcoded on the form are ignored
 * <p>
 * Note However: for it to work, a field with the key "openmrs_choice_ids" needs to be defined within the Reaction_Vaccine json object,
 * the value for which is a json object with key-value mapping of the vaccine name e.g. BCG as key and the concept id as value e.g. 149310AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
 * <p>
 * example
 * <p>
 * "openmrs_choice_ids": {
 * "BCG": "149310AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
 * "HepB": "159666AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
 * "OPV": "129578AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
 * }
 */
public class LaunchAdverseEventFormTask extends AsyncTask<Void, Void, String> {
    private BaseChildDetailTabbedActivity activity;

    public LaunchAdverseEventFormTask(BaseChildDetailTabbedActivity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            JSONObject form = new FormUtils(activity.getContext()).getFormJson("adverse_event");
            if (form != null) {
                JSONArray fields = form.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
                for (int i = 0; i < fields.length(); i++) {
                    if (fields.getJSONObject(i).getString(JsonFormConstants.KEY).equals("Reaction_Vaccine")) {
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
