package org.smartregister.child.task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static org.smartregister.util.JsonFormUtils.getJSONObject;
import static org.smartregister.util.Utils.getAllSharedPreferences;

public class SaveDynamicVaccinesTask extends AsyncTask<Void, Void, Void> {
    private String jsonString;
    private String baseEntityId;

    public SaveDynamicVaccinesTask(String jsonString, String entityId) {
        this.jsonString = jsonString;
        this.baseEntityId = entityId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            JSONObject jsonForm = new JSONObject(jsonString);

            JSONArray fields = JsonFormUtils.fields(jsonForm);
            if (fields == null) {
                return null;
            }

            FormTag formTag = JsonFormUtils.formTag(Utils.context().allSharedPreferences());
            Event baseEvent = JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, JsonFormUtils.METADATA),
                    formTag, baseEntityId, jsonForm.getString(JsonFormUtils.ENCOUNTER_TYPE), Constants.CHILD_TYPE);

            if (baseEvent != null) {
                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
                ECSyncHelper syncHelper = ChildLibrary.getInstance().getEcSyncHelper();
                syncHelper.addEvent(baseEvent.getBaseEntityId(), eventJson, BaseRepository.TYPE_Unsynced);
                List<String> currentFormSubmissionIds = Collections.singletonList(eventJson
                        .getString(EventClientRepository.event_column.formSubmissionId.toString()));
                Date lastSyncDate = new Date(getAllSharedPreferences().fetchLastUpdatedAtDate(0));
                ChildLibrary.getInstance().getClientProcessorForJava().processClient(syncHelper.getEvents(currentFormSubmissionIds));
                getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
            }

        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }
}
