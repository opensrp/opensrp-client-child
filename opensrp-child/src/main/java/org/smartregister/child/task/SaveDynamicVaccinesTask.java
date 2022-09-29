package org.smartregister.child.task;

import static org.smartregister.util.JsonFormUtils.getJSONObject;
import static org.smartregister.util.Utils.getAllSharedPreferences;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.event.DynamicVaccineType;
import org.smartregister.child.listener.OnSaveDynamicVaccinesListener;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AppExecutorService;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class SaveDynamicVaccinesTask implements OnTaskExecutedActions<TaskResult> {

    private final OnSaveDynamicVaccinesListener onSaveDynamicVaccinesListener;
    private final String jsonString;
    private final String baseEntityId;
    private final DynamicVaccineType dynamicVaccineType;
    private AppExecutorService appExecutors;

    public SaveDynamicVaccinesTask(OnSaveDynamicVaccinesListener onSaveDynamicVaccinesListener,
                                   String jsonString, String entityId, DynamicVaccineType dynamicVaccineType) {
        this.onSaveDynamicVaccinesListener = onSaveDynamicVaccinesListener;
        this.jsonString = jsonString;
        this.baseEntityId = entityId;
        this.dynamicVaccineType = dynamicVaccineType;
    }

    @Override
    public void onTaskStarted() {

    }

    @Override
    public void execute() {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            try {
                JSONObject jsonForm = new JSONObject(jsonString);
                JSONArray fields = ChildJsonFormUtils.fields(jsonForm);
                if (fields == null) {
                    return;
                }

                FormTag formTag = ChildJsonFormUtils.formTag(Utils.context().allSharedPreferences());
                Event baseEvent = ChildJsonFormUtils.createEvent(
                        fields,
                        getJSONObject(jsonForm, ChildJsonFormUtils.METADATA),
                        formTag,
                        baseEntityId,
                        jsonForm.getString(ChildJsonFormUtils.ENCOUNTER_TYPE),
                        Constants.CHILD_TYPE
                );

                String vaccineField = jsonForm.getString(Constants.KEY.DYNAMIC_FIELD);

                if (baseEvent != null && StringUtils.isNoneBlank(vaccineField)) {
                    Utils.processExtraVaccinesEventObs(baseEvent, vaccineField);
                    JSONObject eventJson = new JSONObject(ChildJsonFormUtils.gson.toJson(baseEvent));
                    ECSyncHelper syncHelper = ChildLibrary.getInstance().getEcSyncHelper();
                    syncHelper.addEvent(baseEvent.getBaseEntityId(), eventJson, BaseRepository.TYPE_Unsynced);
                    List<String> currentFormSubmissionIds = Collections.singletonList(
                            eventJson.getString(EventClientRepository.event_column.formSubmissionId.toString())
                    );
                    Date lastSyncDate = new Date(getAllSharedPreferences().fetchLastUpdatedAtDate(0));
                    ChildLibrary.getInstance().getClientProcessorForJava().processClient(syncHelper.getEvents(currentFormSubmissionIds));
                    getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
                }

            } catch (Exception e) {
                Timber.e(Log.getStackTraceString(e));
            }

            appExecutors.mainThread().execute(() -> onTaskResult(TaskResult.SUCCESS));
        });
    }

    @Override
    public void onTaskResult(TaskResult result) {
        onSaveDynamicVaccinesListener.onSaveDynamicVaccine(dynamicVaccineType);
    }
}
