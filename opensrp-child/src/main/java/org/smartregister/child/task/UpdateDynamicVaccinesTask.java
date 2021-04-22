package org.smartregister.child.task;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.ExtraVaccineUpdateEvent;
import org.smartregister.child.listener.OnSaveDynamicVaccinesListener;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import timber.log.Timber;

import static com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN;
import static org.smartregister.util.Utils.getAllSharedPreferences;

public class UpdateDynamicVaccinesTask extends AsyncTask<Void, Void, Void> {

    private final OnSaveDynamicVaccinesListener onSaveDynamicVaccinesListener;
    private final List<ExtraVaccineUpdateEvent> vaccineEvents;
    private final SimpleDateFormat nativeFormDateFormat = new SimpleDateFormat(NATIIVE_FORM_DATE_FORMAT_PATTERN, Locale.ENGLISH);

    public UpdateDynamicVaccinesTask(OnSaveDynamicVaccinesListener onSaveDynamicVaccinesListener,
                                     List<ExtraVaccineUpdateEvent> vaccineEvents) {
        this.onSaveDynamicVaccinesListener = onSaveDynamicVaccinesListener;
        this.vaccineEvents = vaccineEvents;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            ECSyncHelper syncHelper = ChildLibrary.getInstance().getEcSyncHelper();
            List<String> submissionIds = new ArrayList<>();

            for (ExtraVaccineUpdateEvent vaccineEvent : vaccineEvents) {
                FormTag formTag = ChildJsonFormUtils.formTag(getAllSharedPreferences());
                String eventType;

                if (vaccineEvent.isRemoved()) {
                    eventType = Constants.EventType.DELETE_DYNAMIC_VACCINES;
                } else {
                    eventType = Constants.EventType.UPDATE_DYNAMIC_VACCINES;
                }

                if (StringUtils.isNotBlank(eventType)) {

                    Event baseEvent = ChildJsonFormUtils.createEvent(new JSONArray(), new JSONObject(),
                            formTag, vaccineEvent.getEntityId(), eventType, eventType);

                    baseEvent.setFormSubmissionId(UUID.randomUUID().toString());
                    baseEvent.addDetails(Constants.KEY.VACCINE_DATE, vaccineEvent.getVaccineDate());
                    baseEvent.addDetails(Constants.KEY.VACCINE, vaccineEvent.getVaccine());
                    baseEvent.addDetails(Constants.KEY.BASE_ENTITY_ID, vaccineEvent.getEntityId());

                    ChildJsonFormUtils.tagSyncMetadata(baseEvent);
                    JSONObject eventJson = new JSONObject(ChildJsonFormUtils.gson.toJson(baseEvent));
                    submissionIds.add(eventJson.getString(EventClientRepository.event_column.formSubmissionId.toString()));
                    ChildLibrary.getInstance().getEcSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
                    syncHelper.addEvent(baseEvent.getBaseEntityId(), eventJson, BaseRepository.TYPE_Unsynced);
                }

                Date lastSyncDate = new Date(getAllSharedPreferences().fetchLastUpdatedAtDate(0));
                ChildLibrary.getInstance().getClientProcessorForJava().processClient(syncHelper.getEvents(submissionIds));
                getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
            }
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onSaveDynamicVaccinesListener.onUpdateDynamicVaccine();
    }
}
