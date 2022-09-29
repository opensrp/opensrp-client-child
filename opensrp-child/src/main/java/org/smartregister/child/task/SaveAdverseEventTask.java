package org.smartregister.child.task;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.util.AppExecutorService;

import java.util.Date;
import java.util.Iterator;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 05/03/2019.
 */
public class SaveAdverseEventTask implements OnTaskExecutedActions<TaskResult> {
    private final String jsonString;
    private final String locationId;
    private final String baseEntityId;
    private final String providerId;
    private final EventClientRepository eventClientRepository;
    private AppExecutorService appExecutors;

    public SaveAdverseEventTask(String jsonString, String locationId, String baseEntityId, String providerId,
                                EventClientRepository eventClientRepository) {
        this.jsonString = jsonString;
        this.locationId = locationId;
        this.baseEntityId = baseEntityId;
        this.providerId = providerId;
        this.eventClientRepository = eventClientRepository;
    }

    @Override
    public void onTaskStarted() {
        // notify on UI
    }

    @Override
    public void execute() {
        try {
            appExecutors = new AppExecutorService();
            appExecutors.executorService().execute(() -> {
                try {
                    processAdverseEvent();
                } catch (Exception e) {
                    Timber.e(e);
                }

                appExecutors.mainThread().execute(() -> onTaskResult(TaskResult.SUCCESS));
            });
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    private void processAdverseEvent() throws Exception {
        JSONObject jsonForm = new JSONObject(jsonString);

        JSONArray fields = ChildJsonFormUtils.fields(jsonForm);
        if (fields == null) {
            return;
        }

        String encounterDateField = ChildJsonFormUtils.getFieldValue(fields, Constants.DATE_REACTION);
        String encounterType = ChildJsonFormUtils.getString(jsonForm, ChildJsonFormUtils.ENCOUNTER_TYPE);
        JSONObject metadata = ChildJsonFormUtils.getJSONObject(jsonForm, ChildJsonFormUtils.METADATA);

        Date encounterDate = new Date();
        if (StringUtils.isNotBlank(encounterDateField)) {
            Date dateTime = ChildJsonFormUtils.formatDate(encounterDateField, false);
            if (dateTime != null) {
                encounterDate = dateTime;
            }
        }

        Event event = (Event) new Event()
                .withBaseEntityId(baseEntityId) //should be different for main and subform
                .withEventDate(encounterDate)
                .withEventType(encounterType)
                .withLocationId(locationId)
                .withProviderId(providerId).withEntityType(Constants.CHILD_TYPE)
                .withChildLocationId(ChildLibrary.getInstance().context().allSharedPreferences().fetchCurrentLocality())
                .withFormSubmissionId(ChildJsonFormUtils.generateRandomUUIDString()).withDateCreated(new Date());

        ChildJsonFormUtils.tagSyncMetadata(event);

        //add metadata
        addMetadata(fields, event, metadata);
    }

    private void addMetadata(JSONArray fields, Event event, JSONObject metadata) throws Exception {
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = ChildJsonFormUtils.getJSONObject(fields, i);
            String value = ChildJsonFormUtils.getString(jsonObject, ChildJsonFormUtils.VALUE);
            if (StringUtils.isNotBlank(value)) {
                ChildJsonFormUtils.addObservation(event, jsonObject);
            }
        }

        if (metadata != null) {
            Iterator<?> keys = metadata.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject jsonObject = ChildJsonFormUtils.getJSONObject(metadata, key);
                String value = ChildJsonFormUtils.getString(jsonObject, ChildJsonFormUtils.VALUE);
                if (StringUtils.isNotBlank(value)) {
                    String entityValue = ChildJsonFormUtils.getString(jsonObject, ChildJsonFormUtils.OPENMRS_ENTITY);
                    if (entityValue != null) {
                        if (entityValue.equals(ChildJsonFormUtils.CONCEPT)) {
                            ChildJsonFormUtils.addToJSONObject(jsonObject, Constants.KEY.KEY, key);
                            ChildJsonFormUtils.addObservation(event, jsonObject);
                        } else if ("encounter".equals(entityValue)) {
                            String entityIdValue = ChildJsonFormUtils.getString(jsonObject, ChildJsonFormUtils.OPENMRS_ENTITY_ID);
                            if (entityIdValue.equals(FormEntityConstants.Encounter.encounter_date.name())) {
                                Date eventDate = ChildJsonFormUtils.formatDate(value, false);
                                if (eventDate != null) {
                                    event.setEventDate(eventDate);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (event != null) {
            JSONObject eventJson = new JSONObject(ChildJsonFormUtils.gson.toJson(event));
            eventClientRepository.addEvent(event.getBaseEntityId(), eventJson);
        }
    }

    @Override
    public void onTaskResult(TaskResult result) {
        // notify on UI
    }
}
