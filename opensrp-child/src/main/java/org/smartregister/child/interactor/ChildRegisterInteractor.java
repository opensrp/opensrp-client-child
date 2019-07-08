package org.smartregister.child.interactor;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.util.AppExecutors;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.UniqueId;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class ChildRegisterInteractor implements ChildRegisterContract.Interactor {

    public static final String TAG = ChildRegisterInteractor.class.getName();
    private AppExecutors appExecutors;


    public ChildRegisterInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    ChildRegisterInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        //TODO set presenter or model to null
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple,
                                final ChildRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UniqueId uniqueId = getUniqueIdRepository().getNextUniqueId();
                final String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (StringUtils.isBlank(entityId)) {
                            callBack.onNoUniqueId();
                        } else {
                            callBack.onUniqueIdFetched(triple, entityId);
                        }
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRegistration(final List<ChildEventClient> childEventClientList, final String jsonString,
                                 final UpdateRegisterParams updateRegisterParams,
                                 final ChildRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(childEventClientList, jsonString, updateRegisterParams);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onRegistrationSaved(updateRegisterParams.isEditMode());
                    }
                });
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void removeChildFromRegister(final String closeFormJsonString, final String providerId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //TODO add functionality to remove child from register
            }
        };

        appExecutors.diskIO().execute(runnable);
    }

    public void saveRegistration(List<ChildEventClient> childEventClientList, String jsonString,
                                 UpdateRegisterParams params) {
        try {
            List<String> currentFormSubmissionIds = new ArrayList<>();

            for (int i = 0; i < childEventClientList.size(); i++) {
                try {

                    ChildEventClient childEventClient = childEventClientList.get(i);
                    Client baseClient = childEventClient.getClient();
                    Event baseEvent = childEventClient.getEvent();

                    if (baseClient != null) {
                        JSONObject clientJson = new JSONObject(JsonFormUtils.gson.toJson(baseClient));
                        if (params.isEditMode()) {
                            try {
                                JsonFormUtils.mergeAndSaveClient(baseClient);
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        } else {
                            getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);

                            processWeight(jsonString, params, clientJson);
                            processHeight(jsonString, params, clientJson);
                        }
                    }

                    if (baseEvent != null) {
                        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
                        getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson, params.getStatus());
                        currentFormSubmissionIds
                                .add(eventJson.getString(EventClientRepository.event_column.formSubmissionId.toString()));
                    }

                    if (params.isEditMode()) {
                        // Unassign current OPENSRP ID
                        if (baseClient != null) {
                            try {
                                String newOpenSRPId = baseClient.getIdentifier(Constants.KEY.ZEIR_ID).replace("-", "");
                                String currentOpenSRPId = JsonFormUtils.getString(jsonString, JsonFormUtils.CURRENT_ZEIR_ID)
                                        .replace("-", "");
                                if (!newOpenSRPId.equals(currentOpenSRPId)) {
                                    //OPENSRP ID was changed
                                    getUniqueIdRepository().open(currentOpenSRPId);
                                }
                            } catch (Exception e) {//might crash if M_ZEIR
                                Log.d(TAG, e.getMessage());
                            }
                        }

                    } else {
                        if (baseClient != null) {
                            String opensrpId = baseClient.getIdentifier(Constants.KEY.ZEIR_ID);

                            //mark OPENSRP ID as used
                            getUniqueIdRepository().close(opensrpId);
                        }
                    }

                    if (baseClient != null || baseEvent != null) {
                        String imageLocation = null;
                        if (i == 0) {
                            imageLocation = JsonFormUtils.getFieldValue(jsonString, Constants.KEY.PHOTO);
                        } else if (i == 1) {
                            imageLocation = JsonFormUtils
                                    .getFieldValue(jsonString, JsonFormUtils.STEP2, Constants.KEY.PHOTO);
                        }

                        if (StringUtils.isNotBlank(imageLocation)) {
                            JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(currentFormSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public ECSyncHelper getSyncHelper() {
        return ChildLibrary.getInstance().getEcSyncHelper();
    }

    private void processWeight(String jsonString, UpdateRegisterParams params, JSONObject clientJson) throws JSONException {
        String weight = JsonFormUtils.getFieldValue(jsonString, JsonFormUtils.STEP1, Constants.KEY.BIRTH_WEIGHT);

        if (!TextUtils.isEmpty(weight)) {
            WeightWrapper weightWrapper = new WeightWrapper();
            weightWrapper.setGender(clientJson.getString(FormEntityConstants.Person.gender.name()));
            weightWrapper.setWeight(!TextUtils.isEmpty(weight) ? Float.valueOf(weight) : null);
            weightWrapper.setUpdatedWeightDate(new DateTime(), true);
            weightWrapper.setId(clientJson.getString(ClientProcessor.baseEntityIdJSONKey));

            Utils.recordWeight(GrowthMonitoringLibrary.getInstance().weightRepository(), weightWrapper,
                    getChildBirthDate(clientJson), params.getStatus());
        }
    }

    private void processHeight(String jsonString, UpdateRegisterParams params, JSONObject clientJson) throws JSONException {
        String height = JsonFormUtils.getFieldValue(jsonString, JsonFormUtils.STEP1, Constants.KEY.BIRTH_HEIGHT);

        if (!TextUtils.isEmpty(height)) {
            HeightWrapper heightWrapper = new HeightWrapper();
            heightWrapper.setGender(clientJson.getString(FormEntityConstants.Person.gender.name()));
            heightWrapper.setHeight(!TextUtils.isEmpty(height) ? Float.valueOf(height) : 0);
            heightWrapper.setUpdatedHeightDate(new DateTime(), true);
            heightWrapper.setId(clientJson.getString(ClientProcessor.baseEntityIdJSONKey));

            Utils.recordHeight(GrowthMonitoringLibrary.getInstance().heightRepository(), heightWrapper,
                    getChildBirthDate(clientJson), params.getStatus());
        }
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return Utils.context().allSharedPreferences();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return ChildLibrary.getInstance().getClientProcessorForJava();
    }

    private String getChildBirthDate(JSONObject jsonObject) {
        String childBirthDate = "";
        try {
            if (jsonObject != null && jsonObject.has(FormEntityConstants.Person.birthdate.toString())) {
                childBirthDate = jsonObject.getString(FormEntityConstants.Person.birthdate.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return childBirthDate;
    }

    public UniqueIdRepository getUniqueIdRepository() {
        return ChildLibrary.getInstance().getUniqueIdRepository();
    }

    public enum type {SAVED, UPDATED}

}
