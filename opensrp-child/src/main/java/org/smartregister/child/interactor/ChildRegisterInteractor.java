package org.smartregister.child.interactor;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.util.AppExecutors;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.UniqueId;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.Date;
import java.util.List;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class ChildRegisterInteractor implements ChildRegisterContract.Interactor {

    public static final String TAG = ChildRegisterInteractor.class.getName();

    public enum type {SAVED, UPDATED}


    private AppExecutors appExecutors;

    @VisibleForTesting
    ChildRegisterInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public ChildRegisterInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final ChildRegisterContract.InteractorCallBack callBack) {

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
    public void saveRegistration(final List<ChildEventClient> childEventClientList, final String jsonString, final boolean isEditMode, final ChildRegisterContract.InteractorCallBack callBack) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                saveRegistration(childEventClientList, jsonString, isEditMode);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onRegistrationSaved(isEditMode);
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

    private void saveRegistration(List<ChildEventClient> childEventClientList, String jsonString, boolean isEditMode) {
        try {

            for (int i = 0; i < childEventClientList.size(); i++) {
                ChildEventClient childEventClient = childEventClientList.get(i);
                Client baseClient = childEventClient.getClient();
                Event baseEvent = childEventClient.getEvent();

                if (baseClient != null) {
                    JSONObject clientJson = new JSONObject(JsonFormUtils.gson.toJson(baseClient));
                    if (isEditMode) {
                        try {
                            JsonFormUtils.mergeAndSaveClient(getSyncHelper(), baseClient);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    } else {
                        getSyncHelper().addClient(baseClient.getBaseEntityId(), clientJson);

                        WeightWrapper weightParams = new WeightWrapper();
                        weightParams.setGender(clientJson.getString(FormEntityConstants.Person.gender.name()));
                        String weight = JsonFormUtils.getFieldValue(jsonString, JsonFormUtils.STEP1, DBConstants.KEY.BIRTH_WEIGHT);
                        weightParams.setWeight(!TextUtils.isEmpty(weight) ? Float.valueOf(weight) : null);
                        weightParams.setUpdatedWeightDate(new DateTime(), true);
                        weightParams.setId(clientJson.getString(ClientProcessor.baseEntityIdJSONKey));

                        Utils.recordWeight(GrowthMonitoringLibrary.getInstance().weightRepository(), weightParams, clientJson.getString(FormEntityConstants.Person.birthdate.name()));

                    }
                }


                if (baseEvent != null) {
                    JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(baseEvent));
                    getSyncHelper().addEvent(baseEvent.getBaseEntityId(), eventJson);
                }

                if (isEditMode) {
                    // Unassign current OPENSRP ID
                    if (baseClient != null) {
                        try {
                            String newOpenSRPId = baseClient.getIdentifier(DBConstants.KEY.ZEIR_ID).replace("-", "");
                            String currentOpenSRPId = JsonFormUtils.getString(jsonString, JsonFormUtils.CURRENT_ZEIR_ID).replace("-", "");
                            if (!newOpenSRPId.equals(currentOpenSRPId)) {
                                //OPENSRP ID was changed
                                getUniqueIdRepository().open(currentOpenSRPId);
                            }
                        } catch (Exception e) {//might crash if M_ZEIR
                            Log.e(TAG, e.getMessage());
                        }
                    }

                } else {
                    if (baseClient != null) {
                        String opensrpId = baseClient.getIdentifier(DBConstants.KEY.ZEIR_ID);

                        //mark OPENSRP ID as used
                        getUniqueIdRepository().close(opensrpId);
                    }
                }

                if (baseClient != null || baseEvent != null) {
                    String imageLocation = null;
                    if (i == 0) {
                        imageLocation = JsonFormUtils.getFieldValue(jsonString, Constants.KEY.PHOTO);
                    } else if (i == 1) {
                        imageLocation = JsonFormUtils.getFieldValue(jsonString, JsonFormUtils.STEP2, Constants.KEY.PHOTO);
                    }

                    if (StringUtils.isNotBlank(imageLocation)) {
                        JsonFormUtils.saveImage(baseEvent.getProviderId(), baseClient.getBaseEntityId(), imageLocation);
                    }
                }
            }

            long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            getClientProcessorForJava().processClient(getSyncHelper().getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        //TODO set presenter or model to null
    }

    public AllSharedPreferences getAllSharedPreferences() {
        return Utils.context().allSharedPreferences();
    }

    public UniqueIdRepository getUniqueIdRepository() {
        return ChildLibrary.getInstance().getUniqueIdRepository();
    }


    public ECSyncHelper getSyncHelper() {
        return ChildLibrary.getInstance().getEcSyncHelper();
    }

    public ClientProcessorForJava getClientProcessorForJava() {
        return ChildLibrary.getInstance().getClientProcessorForJava();
    }

}
