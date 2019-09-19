package org.smartregister.child.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.enums.LocationHierarchy;
import org.smartregister.child.task.SaveOutOfAreaServiceTask;
import org.smartregister.clientandeventmodel.Address;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.ImageRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessor;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 26/02/2019.
 */
public class JsonFormUtils extends org.smartregister.util.JsonFormUtils {
    public static final String METADATA = "metadata";
    public static final String ENCOUNTER_TYPE = "encounter_type";
    public static final int REQUEST_CODE_GET_JSON = 2244;
    public static final String CURRENT_OPENSRP_ID = "current_opensrp_id";
    public static final String READ_ONLY = "read_only";
    public static final String STEP2 = "step2";
    public static final String MOTHER_DEFAULT_DOB = "01-01-1960";
    public static final String RELATIONAL_ID = "relational_id";
    public static final String CURRENT_ZEIR_ID = "current_zeir_id";
    public static final String ZEIR_ID = "ZEIR_ID";
    public static final String updateBirthRegistrationDetailsEncounter = "Update Birth Registration";
    public static final String BCG_SCAR_EVENT = "Bcg Scar";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN, Locale.ENGLISH);
    public static final String GENDER = "gender";
    private static final String ENCOUNTER = "encounter";
    private static final String M_ZEIR_ID = "M_ZEIR_ID";
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    public static JSONObject getFormAsJson(JSONObject form, String formName, String id, String currentLocationId)
            throws Exception {
        if (form == null) {
            return null;
        }

        String entityId = id;
        form.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);

        if (Utils.metadata().childRegister.formName.equals(formName)) {
            if (StringUtils.isBlank(entityId)) {
                UniqueIdRepository uniqueIdRepo = ChildLibrary.getInstance().getUniqueIdRepository();
                entityId = uniqueIdRepo.getNextUniqueId() != null ? uniqueIdRepo.getNextUniqueId().getOpenmrsId() : "";
                if (entityId.isEmpty()) {
                    Timber.e("JsonFormUtils --> UniqueIds are empty");
                    return null;
                }
            }

            if (StringUtils.isNotBlank(entityId)) {
                entityId = entityId.replace("-", "");
            }

            JsonFormUtils.addChildRegLocHierarchyQuestions(form, "", LocationHierarchy.ENTIRE_TREE);

            // Inject zeir id into the form
            JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(JsonFormUtils.ZEIR_ID)) {
                    jsonObject.remove(JsonFormUtils.VALUE);
                    jsonObject.put(JsonFormUtils.VALUE, entityId);
                }
            }

        } else if (formName.equals(Utils.metadata().childRegister.outOfCatchmentFormName)) {
            if (StringUtils.isNotBlank(entityId)) {

                entityId = entityId.replace("-", "");
            } else {
                JSONArray fields = form.getJSONObject(JsonFormUtils.STEP1).getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < fields.length(); i++) {
                    if (fields.getJSONObject(i).getString(JsonFormUtils.KEY).equals(JsonFormUtils.ZEIR_ID)) {
                        fields.getJSONObject(i).put(READ_ONLY, false);
                        break;
                    }
                }
            }

            JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(JsonFormUtils.ZEIR_ID)) {
                    jsonObject.remove(JsonFormUtils.VALUE);
                    jsonObject.put(JsonFormUtils.VALUE, entityId);
                }
            }

            JsonFormUtils.addAddAvailableVaccines(ChildLibrary.getInstance().context().applicationContext(), form);

        } else {
            Timber.w("JsonFormUtils --> Unsupported form requested for launch %s", formName);
        }
        Timber.d("JsonFormUtils --> form is %s", form.toString());
        return form;
    }

    public static void addChildRegLocHierarchyQuestions(JSONObject form, String widgetKey, LocationHierarchy locationHierarchy) {
        try {
            JSONArray questions = form.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);
            ArrayList<String> allLevels = getLocationLevels();
            ArrayList<String> healthFacilities = getHealthFacilityLevels();

            List<String> defaultLocation = LocationHelper.getInstance().generateDefaultLocationHierarchy(allLevels);
            List<String> defaultFacility = LocationHelper.getInstance().generateDefaultLocationHierarchy(healthFacilities);
            List<FormLocation> upToFacilities = LocationHelper.getInstance().generateLocationHierarchyTree(false, healthFacilities);
            List<FormLocation> upToFacilitiesWithOther = LocationHelper.getInstance().generateLocationHierarchyTree(true, healthFacilities);
            List<FormLocation> entireTree = LocationHelper.getInstance().generateLocationHierarchyTree(true, allLevels);

            String defaultLocationString = AssetHandler.javaToJsonString(defaultLocation, new TypeToken<List<String>>() {
            }.getType());

            String defaultFacilityString = AssetHandler.javaToJsonString(defaultFacility, new TypeToken<List<String>>() {
            }.getType());

            String upToFacilitiesString = AssetHandler.javaToJsonString(upToFacilities, new TypeToken<List<FormLocation>>() {
            }.getType());

            String upToFacilitiesWithOtherString = AssetHandler.javaToJsonString(upToFacilitiesWithOther, new TypeToken<List<FormLocation>>() {
            }.getType());

            String entireTreeString = AssetHandler.javaToJsonString(entireTree, new TypeToken<List<FormLocation>>() {
            }.getType());

            updateLocationTree(widgetKey, locationHierarchy, questions, defaultLocationString, defaultFacilityString, upToFacilitiesString, upToFacilitiesWithOtherString, entireTreeString);

            //To Do Refactor to remove dependency on hardocded keys
            for (int i = 0; i < questions.length(); i++) {
                if (questions.getJSONObject(i).getString("key").equals("Home_Facility")) {
                    if (StringUtils.isNotBlank(upToFacilitiesString)) {
                        questions.getJSONObject(i).put("tree", new JSONArray(upToFacilitiesString));
                    }
                    if (StringUtils.isNotBlank(defaultFacilityString)) {
                        questions.getJSONObject(i).put("default", defaultFacilityString);
                    }
                } else if (questions.getJSONObject(i).getString("key").equals("Birth_Facility_Name")) {
                    if (StringUtils.isNotBlank(upToFacilitiesWithOtherString)) {
                        questions.getJSONObject(i).put("tree", new JSONArray(upToFacilitiesWithOtherString));
                    }
                    if (StringUtils.isNotBlank(defaultFacilityString)) {
                        questions.getJSONObject(i).put("default", defaultFacilityString);
                    }
                } else if (questions.getJSONObject(i).getString("key").equals("Residential_Area")) {
                    if (StringUtils.isNotBlank(entireTreeString)) {
                        questions.getJSONObject(i).put("tree", new JSONArray(entireTreeString));
                    }
                    if (StringUtils.isNotBlank(defaultLocationString)) {
                        questions.getJSONObject(i).put("default", defaultLocationString);
                    }
                }
            }

        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> addChildRegLocHierarchyQuestions");
        }
    }

    private static void addAddAvailableVaccines(Context context, JSONObject form) {
        List<VaccineGroup> supportedVaccines = VaccinatorUtils.getSupportedVaccines(context);
        if (supportedVaccines != null && !supportedVaccines.isEmpty() && form != null) {
            // For each of the vaccine groups, create a checkbox question
            try {
                JSONArray questionList = getQuestionList(form);

                HashMap<String, ArrayList<JSONObject>> vaccineTypeConstraints = new HashMap<>();
                for (VaccineGroup curVaccineGroup : supportedVaccines) {
                    for (Vaccine curVaccine : curVaccineGroup.vaccines) {
                        if (!vaccineTypeConstraints.containsKey(curVaccine.type)) {
                            vaccineTypeConstraints.put(curVaccine.type, new ArrayList<JSONObject>());
                        }
                        ArrayList<String> vaccineNamesDefined = new ArrayList<>();
                        if (curVaccine.vaccine_separator != null) {
                            String unSplitNames = curVaccine.name;
                            String separator = curVaccine.vaccine_separator;
                            String[] splitValues = unSplitNames.split(separator);
                            vaccineNamesDefined.addAll(Arrays.asList(splitValues));
                        } else {
                            vaccineNamesDefined.add(curVaccine.name);
                        }

                        for (String curVaccineName : vaccineNamesDefined) {
                            JSONObject curConstraint = getCurConstraint(curVaccineGroup, curVaccine, curVaccineName);
                            vaccineTypeConstraints.get(curVaccine.type).add(curConstraint);
                        }
                    }
                }

                for (VaccineGroup curVaccineGroup : supportedVaccines) {
                    JSONObject curQuestion = getCurQuestion(context, curVaccineGroup);

                    JSONArray options = new JSONArray();
                    for (Vaccine curVaccine : curVaccineGroup.vaccines) {
                        ArrayList<String> definedVaccineNames = new ArrayList<>();
                        if (curVaccine.vaccine_separator != null) {
                            String rawNames = curVaccine.name;
                            String separator = curVaccine.vaccine_separator;
                            String[] split = rawNames.split(separator);
                            definedVaccineNames.addAll(Arrays.asList(split));
                        } else {
                            definedVaccineNames.add(curVaccine.name);
                        }

                        for (String curVaccineName : definedVaccineNames) {
                            JSONObject curVaccines = new JSONObject();
                            curVaccines.put("key", curVaccineName);
                            curVaccines.put("text", VaccinatorUtils.getTranslatedVaccineName(context, curVaccineName));
                            curVaccines.put("value", "false");
                            JSONArray constraints = new JSONArray();

                            // Add the constraints
                            if (vaccineTypeConstraints.containsKey(curVaccine.type)) {
                                for (JSONObject curConstraint : vaccineTypeConstraints.get(curVaccine.type)) {
                                    if (!curConstraint.getString("vaccine").equals(curVaccineName)) {
                                        JSONObject constraintClone = new JSONObject(curConstraint.toString());
                                        constraintClone.remove("vaccine");
                                        constraints.put(constraintClone);
                                    }
                                }
                            }

                            if (constraints.length() > 0) {
                                curVaccines.put("constraints", constraints);
                            }

                            options.put(curVaccines);
                        }
                    }

                    curQuestion.put("options", options);
                    questionList.put(curQuestion);
                }
            } catch (JSONException e) {
                Timber.e(e, "JsonFormUtils --> addAddAvailableVaccines");
            }
        }
    }

    @NotNull
    private static JSONArray getQuestionList(JSONObject form) throws JSONException {
        JSONArray questionList = form.getJSONObject("step1").getJSONArray("fields");
        JSONObject vaccinationLabel = new JSONObject();
        vaccinationLabel.put("key", "Vaccines_Provided_Label");
        vaccinationLabel.put("type", "label");
        vaccinationLabel.put("text", "Which vaccinations were provided?");
        vaccinationLabel.put("openmrs_entity_parent", "-");
        vaccinationLabel.put("openmrs_entity", "-");
        vaccinationLabel.put("openmrs_entity_id", "-");
        questionList.put(vaccinationLabel);
        return questionList;
    }

    @NotNull
    private static JSONObject getCurConstraint(VaccineGroup curVaccineGroup, Vaccine curVaccine, String curVaccineName) throws JSONException {
        JSONObject curConstraint = new JSONObject();
        curConstraint.put("vaccine", curVaccineName);
        curConstraint.put("type", "array");
        curConstraint.put("ex",
                "notEqualTo(step1:" + curVaccineGroup.id + ", \"[\"" + curVaccineName + "\"]\")");
        curConstraint.put("err", "Cannot be given with the other " + curVaccine.type + " dose");
        return curConstraint;
    }

    private static JSONObject getCurQuestion(Context context, VaccineGroup curVaccineGroup) throws JSONException {
        JSONObject curQuestion = new JSONObject();
        curQuestion.put("key", curVaccineGroup.id);
        curQuestion.put("type", "check_box");
        curQuestion.put("is_vaccine_group", true);
        curQuestion.put("label", VaccinatorUtils.getTranslatedGroupName(context, curVaccineGroup));
        curQuestion.put("openmrs_entity_parent", "-");
        curQuestion.put("openmrs_entity", "-");
        curQuestion.put("openmrs_entity_id", "-");
        return curQuestion;
    }

    @NotNull
    private static ArrayList<String> getLocationLevels() {
        ArrayList<String> allLevels = new ArrayList<>();
        allLevels.add("Country");
        allLevels.add("Province");
        allLevels.add("Department");
        allLevels.add("Health Facility");
        allLevels.add("Zone");
        allLevels.add("Residential Area");
        allLevels.add("Facility");
        return allLevels;
    }

    @NotNull
    private static ArrayList<String> getHealthFacilityLevels() {
        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add("Country");
        healthFacilities.add("Province");
        healthFacilities.add("Department");
        healthFacilities.add("Health Facility");
        healthFacilities.add("Facility");
        return healthFacilities;
    }

    private static void updateLocationTree(String widgetKey, LocationHierarchy locationHierarchy, JSONArray questions,
                                           String defaultLocationString, String defaultFacilityString,
                                           String upToFacilitiesString, String upToFacilitiesWithOtherString,
                                           String entireTreeString) throws JSONException {
        for (int i = 0; i < questions.length(); i++) {
            JSONObject widgets = questions.getJSONObject(i);
            switch (locationHierarchy) {
                case FACILITY_ONLY:
                    if (StringUtils.isNotBlank(upToFacilitiesString)) {
                        addLocationTree(widgetKey, widgets, upToFacilitiesString);
                    }
                    if (StringUtils.isNotBlank(defaultFacilityString)) {
                        addLocationDefault(widgetKey, widgets, defaultFacilityString);
                    }
                    break;
                case FACILITY_WITH_OTHER_STRING:
                    if (StringUtils.isNotBlank(upToFacilitiesWithOtherString)) {
                        addLocationTree(widgetKey, widgets, upToFacilitiesWithOtherString);
                    }
                    if (StringUtils.isNotBlank(defaultFacilityString)) {
                        addLocationDefault(widgetKey, widgets, defaultFacilityString);
                    }
                    break;
                case ENTIRE_TREE:
                    if (StringUtils.isNotBlank(entireTreeString)) {
                        addLocationTree(widgetKey, widgets, entireTreeString);
                    }
                    if (StringUtils.isNotBlank(defaultFacilityString)) {
                        addLocationDefault(widgetKey, widgets, defaultLocationString);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static void addLocationTree(String widgetKey, JSONObject widget, String updateString) {
        try {
            if (widget.getString("key").equals(widgetKey)) {
                widget.put("tree", new JSONArray(updateString));
            }
        } catch (JSONException e) {
            Timber.e(e, "JsonFormUtils --> addLocationTree");
        }
    }

    private static void addLocationDefault(String widgetKey, JSONObject widget, String updateString) {
        try {
            if (widget.getString("key").equals(widgetKey)) {
                widget.put("default", new JSONArray(updateString));
            }
        } catch (JSONException e) {
            Timber.e(e, "JsonFormUtils --> addLocationDefault");
        }
    }

    public static void saveReportDeceased(Context context, String jsonString, String locationId, String entityId) {
        try {

            String providerId = ChildLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM();
            EventClientRepository db = ChildLibrary.getInstance().eventClientRepository();
            JSONObject jsonForm = new JSONObject(jsonString);

            JSONArray fields = fields(jsonForm);
            if (fields == null) {
                return;
            }

            String encounterDateField = getFieldValue(fields, "Date_of_Death");

            String encounterType = getString(jsonForm, ENCOUNTER_TYPE);
            JSONObject metadata = getJSONObject(jsonForm, METADATA);

            Date encounterDate = new Date();
            String encounterDateTimeString = null;
            if (StringUtils.isNotBlank(encounterDateField)) {
                encounterDateTimeString = formatDate(encounterDateField);
                Date dateTime = formatDate(encounterDateField, false);
                if (dateTime != null) {
                    encounterDate = dateTime;
                }
            }

            Event event = getEvent(providerId, locationId, entityId, encounterType, encounterDate, Constants.CHILD_TYPE);

            addSaveReportDeceasedObservations(fields, event);
            updateMetadata(metadata, event);


            if (event != null) {
                createEventObject(context, providerId, locationId, entityId, db, encounterDate, encounterDateTimeString, event);


                ContentValues values = new ContentValues();
                values.put(Constants.KEY.DOD, encounterDateField);
                values.put(Constants.KEY.DATE_REMOVED, Utils.getTodaysDate());
                updateChildFTSTables(values, entityId);

                updateDateOfRemoval(entityId, encounterDateTimeString);//TO DO Refactor  with better
            }

            processClients(context, Utils.getAllSharedPreferences(), ChildLibrary.getInstance().getEcSyncHelper());

        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> saveReportDeceased");
        }
    }

    private static void addSaveReportDeceasedObservations(JSONArray fields, Event event) {
        for (int i = 0; i < fields.length(); i++) {
            JSONObject jsonObject = getJSONObject(fields, i);
            String value = getString(jsonObject, VALUE);
            if (StringUtils.isNotBlank(value)) {
                addObservation(event, jsonObject);
            }
        }
    }

    private static void updateMetadata(JSONObject metadata, Event event) {
        if (metadata != null) {
            Iterator<?> keys = metadata.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                JSONObject jsonObject = getJSONObject(metadata, key);
                String value = getString(jsonObject, VALUE);
                if (StringUtils.isNotBlank(value)) {
                    String entityVal = getString(jsonObject, OPENMRS_ENTITY);
                    if (entityVal != null) {
                        if (entityVal.equals(CONCEPT)) {
                            addToJSONObject(jsonObject, KEY, key);
                            addObservation(event, jsonObject);
                        } else if (entityVal.equals(ENCOUNTER)) {
                            String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
                            if (entityIdVal.equals(FormEntityConstants.Encounter.encounter_date.name())) {
                                Date eDate = formatDate(value, false);
                                if (eDate != null) {
                                    event.setEventDate(eDate);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void processClients(Context context, AllSharedPreferences allSharedPreferences, ECSyncHelper ecSyncHelper) throws Exception {
        long lastSyncTimeStamp = allSharedPreferences.fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        ChildLibrary.getInstance().getClientProcessorForJava().getInstance(context).processClient(
                ecSyncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
        allSharedPreferences.saveLastUpdatedAtDate(lastSyncDate.getTime());
    }

    private static Event getEvent(String providerId, String locationId, String entityId, String encounterType, Date encounterDate, String childType) {
        return (Event) new Event().withBaseEntityId(entityId) //should be different for main and subform
                .withEventDate(encounterDate).withEventType(encounterType).withLocationId(locationId)
                .withProviderId(providerId).withEntityType(childType)
                .withFormSubmissionId(generateRandomUUIDString()).withDateCreated(new Date());
    }

    private static void createEventObject(Context context, String providerId, String locationId, String entityId, EventClientRepository db, Date encounterDate, String encounterDateTimeString, Event event) throws JSONException {
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));

        //After saving, Unsync(remove) this event's details
        //List<JSONObject> jsonEvents = new ArrayList<>();
        ///jsonEvents.add(eventJson);

        //Update client to deceased
        JSONObject client = db.getClientByBaseEntityId(eventJson.getString(ClientProcessor.baseEntityIdJSONKey));
        client.put(FormEntityConstants.Person.deathdate.name(), encounterDateTimeString);
        client.put(FormEntityConstants.Person.deathdate_estimated.name(), false);
        client.put(Constants.JSON_FORM_KEY.DEATH_DATE_APPROX, false);

        db.addorUpdateClient(entityId, client);

        //Add Death Event for child to flag for Server delete
        db.addEvent(event.getBaseEntityId(), eventJson);

        //Update Child Entity to include death date
        Event updateChildDetailsEvent = getEvent(providerId, locationId, entityId, JsonFormUtils.updateBirthRegistrationDetailsEncounter, encounterDate, Constants.CHILD_TYPE);

        addMetaData(context, updateChildDetailsEvent, new Date());

        JsonFormUtils.tagSyncMetadata(updateChildDetailsEvent);

        JSONObject eventJsonUpdateChildEvent = new JSONObject(JsonFormUtils.gson.toJson(updateChildDetailsEvent));

        db.addEvent(entityId, eventJsonUpdateChildEvent); //Add event to flag server update
    }

    public static void updateChildFTSTables(ContentValues values, String entityId) {
        //Update REGISTER and FTS Tables
        String tableName = Utils.metadata().childRegister.tableName;
        AllCommonsRepository allCommonsRepository = ChildLibrary.getInstance().context().allCommonsRepositoryobjects(tableName);
        if (allCommonsRepository != null) {
            allCommonsRepository.update(tableName, values, entityId);
            allCommonsRepository.updateSearch(entityId);

        }
    }

    public static Event addMetaData(Context context, Event event, Date start) throws JSONException {
        Map<String, String> metaFields = new HashMap<>();
        metaFields.put("deviceid", "163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        metaFields.put("end", "163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        metaFields.put("start", "163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        Calendar calendar = Calendar.getInstance();

        String end = DATE_TIME_FORMAT.format(calendar.getTime());

        Obs obs = new Obs();
        obs.setFieldCode("163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(DATE_TIME_FORMAT.format(start));
        obs.setFieldType("concept");
        obs.setFieldDataType("start");
        event.addObs(obs);


        obs.setFieldCode("163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(end);
        obs.setFieldDataType("end");
        event.addObs(obs);

        TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        @SuppressLint("MissingPermission") String deviceId =
                mTelephonyManager.getSimSerialNumber(); //Aready handded by native form

        obs.setFieldCode("163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        obs.setValue(deviceId);
        obs.setFieldDataType("deviceid");
        event.addObs(obs);
        return event;
    }

    protected static Event tagSyncMetadata(Event event) {
        AllSharedPreferences allSharedPreferences = Utils.getAllSharedPreferences();
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(locationId(allSharedPreferences));

        String childLocationId = getChildLocationId(event.getLocationId(), allSharedPreferences);
        event.setChildLocationId(childLocationId);

        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        event.setClientDatabaseVersion(ChildLibrary.getInstance().getDatabaseVersion());
        event.setClientApplicationVersion(ChildLibrary.getInstance().getApplicationVersion());
        return event;
    }

    @Nullable
    public static String getChildLocationId(@NonNull String defaultLocationId, @NonNull AllSharedPreferences allSharedPreferences) {
        String currentLocality = allSharedPreferences.fetchCurrentLocality();

        if (currentLocality != null) {
            String currentLocalityId = LocationHelper.getInstance().getOpenMrsLocationId(currentLocality);
            if (currentLocalityId != null && !defaultLocationId.equals(currentLocalityId)) {
                return currentLocalityId;
            }
        }

        return null;
    }

    public static void updateDateOfRemoval(String baseEntityId, String dateOfRemovalString) {

        ContentValues contentValues = new ContentValues();

        if (dateOfRemovalString != null) {
            contentValues.put(Constants.KEY.DATE_REMOVED, dateOfRemovalString);
        }

        ChildLibrary.getInstance().context().getEventClientRepository().getWritableDatabase()
                .update(Utils.metadata().childRegister.tableName, contentValues, Constants.KEY.BASE_ENTITY_ID + " = ?",
                        new String[]{baseEntityId});
    }

    public static String locationId(AllSharedPreferences allSharedPreferences) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        String userLocationId = allSharedPreferences.fetchUserLocalityId(providerId);
        if (StringUtils.isBlank(userLocationId)) {
            userLocationId = allSharedPreferences.fetchDefaultLocalityId(providerId);
        }
        return userLocationId;
    }

    public static ChildEventClient processChildDetailsForm(String jsonString, FormTag formTag) {

        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

            if (!registrationFormParams.getLeft()) {
                return null;
            }

            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();

            String entityId = getString(jsonForm, ENTITY_ID);
            if (StringUtils.isBlank(entityId)) {
                entityId = generateRandomUUIDString();
            }

            processGender(fields);//multi language to re visit

            processLocationFields(fields);

            lastInteractedWith(fields);

            dobUnknownUpdateFromAge(fields);

            JSONObject dobUnknownObject = getFieldJSONObject(fields, Constants.JSON_FORM_KEY.DATE_BIRTH);

            String date = dobUnknownObject.getString(Constants.KEY.VALUE);
            dobUnknownObject.put(Constants.KEY.VALUE, Utils.reverseHyphenatedString(date) + " 12:00:00");

            Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
            baseClient.setRelationalBaseEntityId(getString(jsonForm, Constants.KEY.RELATIONAL_ID));//mama

            Event baseEvent = org.smartregister.util.JsonFormUtils
                    .createEvent(fields, getJSONObject(jsonForm, METADATA), formTag, entityId,
                            Utils.metadata().childRegister.registerEventType, Utils.metadata().childRegister.tableName);

            JsonFormUtils.tagSyncMetadata(baseEvent);// tag docs

            return new ChildEventClient(baseClient, baseEvent);
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> processChildDetailsForm");
            return null;
        }
    }

    protected static Triple<Boolean, JSONObject, JSONArray> validateParameters(String jsonString) {

        JSONObject jsonForm = toJSONObject(jsonString);
        JSONArray fields = fields(jsonForm);

        return Triple.of(jsonForm != null && fields != null, jsonForm, fields);
    }

    protected static void processGender(JSONArray fields) {
        try {
            //TO DO Will need re-architecting later to support more languages, perhaps update the selector widget

            JSONObject genderObject = getFieldJSONObject(fields, Constants.SEX);
            String genderValue = "";

            String rawGender = genderObject.getString(JsonFormConstants.VALUE);
            char rawGenderChar = !TextUtils.isEmpty(rawGender) ? rawGender.charAt(0) : ' ';
            switch (rawGenderChar) {
                case 'm':
                case 'M':
                    genderValue = "Male";
                    break;

                case 'f':
                case 'F':
                    genderValue = "Female";
                    break;

                default:
                    break;

            }

            genderObject.put(Constants.KEY.VALUE, genderValue);
        } catch (JSONException e) {
            Timber.e(e, "JsonFormUtils --> processGender");
        }
    }

    protected static void processLocationFields(JSONArray fields) throws JSONException {
        for (int i = 0; i < fields.length(); i++) {
            if (fields.getJSONObject(i).has(JsonFormConstants.TYPE) &&
                    fields.getJSONObject(i).getString(JsonFormConstants.TYPE).equals(JsonFormConstants.TREE))
                try {
                    String rawValue = fields.getJSONObject(i).getString(JsonFormConstants.VALUE);
                    JSONArray valueArray = new JSONArray(rawValue);
                    if (valueArray.length() > 0) {
                        String lastLocationName = valueArray.getString(valueArray.length() - 1);
                        String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lastLocationName);
                        fields.getJSONObject(i).put(JsonFormConstants.VALUE, lastLocationId);
                    }
                } catch (Exception e) {
                    Timber.e(e, "JsonFormUitls --> processLocationFields");
                }
        }
    }

    protected static void lastInteractedWith(JSONArray fields) {
        try {
            JSONObject lastInteractedWith = new JSONObject();
            lastInteractedWith.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.LAST_INTERACTED_WITH);
            lastInteractedWith.put(Constants.KEY.VALUE, Calendar.getInstance().getTimeInMillis());
            fields.put(lastInteractedWith);
        } catch (JSONException e) {
            Timber.e(e, "JsonFormUtils --> lastInteractedWith");
        }
    }

    protected static void dobUnknownUpdateFromAge(JSONArray fields) {
        try {
            JSONObject dobUnknownObject = getFieldJSONObject(fields, Constants.JSON_FORM_KEY.DOB_UNKNOWN);
            JSONArray options = getJSONArray(dobUnknownObject, Constants.JSON_FORM_KEY.OPTIONS);
            JSONObject option = getJSONObject(options, 0);
            String dobUnKnownString = option != null ? option.getString(VALUE) : null;
            if (StringUtils.isNotBlank(dobUnKnownString) && Boolean.valueOf(dobUnKnownString)) {

                String ageString = getFieldValue(fields, Constants.JSON_FORM_KEY.AGE);
                if (StringUtils.isNotBlank(ageString) && NumberUtils.isNumber(ageString)) {
                    int age = Integer.valueOf(ageString);
                    JSONObject dobJSONObject = getFieldJSONObject(fields, Constants.JSON_FORM_KEY.DOB);
                    dobJSONObject.put(VALUE, Utils.getDob(age));

                    //Mark the birth date as an approximation
                    JSONObject isBirthdateApproximate = new JSONObject();
                    isBirthdateApproximate.put(Constants.KEY.KEY, FormEntityConstants.Person.birthdate_estimated);
                    isBirthdateApproximate.put(Constants.KEY.VALUE, Constants.BOOLEAN_INT.TRUE);
                    isBirthdateApproximate
                            .put(Constants.OPENMRS.ENTITY, Constants.ENTITY.PERSON);//Required for value to be processed
                    isBirthdateApproximate.put(Constants.OPENMRS.ENTITY_ID, FormEntityConstants.Person.birthdate_estimated);
                    fields.put(isBirthdateApproximate);

                }
            }
        } catch (JSONException e) {
            Timber.e(e, "JsonFormUtils --> dobUnknownUpdateFromAge");
        }
    }

    public static void mergeAndSaveClient(Client baseClient) throws Exception {
        JSONObject updatedClientJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(baseClient));
        JSONObject originalClientJsonObject =
                ChildLibrary.getInstance().getEcSyncHelper().getClient(baseClient.getBaseEntityId());
        JSONObject mergedJson = org.smartregister.util.JsonFormUtils.merge(originalClientJsonObject, updatedClientJson);
        //TODO Save edit log ?
        ChildLibrary.getInstance().getEcSyncHelper().addClient(baseClient.getBaseEntityId(), mergedJson);
    }

    public static void saveImage(String providerId, String entityId, String imageLocation) {
        if (StringUtils.isBlank(imageLocation)) {
            return;
        }

        File file = new File(imageLocation);
        if (!file.exists()) {
            return;
        }

        Bitmap compressedImageFile = ChildLibrary.getInstance().getCompressor().compressToBitmap(file);
        saveStaticImageToDisk(compressedImageFile, providerId, entityId);

    }

    private static void saveStaticImageToDisk(Bitmap image, String providerId, String entityId) {
        if (image == null || StringUtils.isBlank(providerId) || StringUtils.isBlank(entityId)) {
            return;
        }
        OutputStream os = null;
        try {

            if (entityId != null && !entityId.isEmpty()) {
                final String absoluteFileName = DrishtiApplication.getAppDir() + File.separator + entityId + ".JPEG";

                File outputFile = new File(absoluteFileName);
                os = new FileOutputStream(outputFile);
                Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                if (compressFormat != null) {
                    image.compress(compressFormat, 100, os);
                } else {
                    throw new IllegalArgumentException(
                            "Failed to save static image, could not retrieve image compression format from name " +
                                    absoluteFileName);
                }
                // insert into the db
                ProfileImage profileImage = new ProfileImage();
                profileImage.setImageid(UUID.randomUUID().toString());
                profileImage.setAnmId(providerId);
                profileImage.setEntityID(entityId);
                profileImage.setFilepath(absoluteFileName);
                profileImage.setFilecategory("profilepic");
                profileImage.setSyncStatus(ImageRepository.TYPE_Unsynced);
                ImageRepository imageRepo = Utils.context().imageRepository();
                imageRepo.add(profileImage);
            }

        } catch (FileNotFoundException e) {
            Timber.e(e, "JsonFormUtils --> Failed to save static image to disk");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Timber.e(e, "JsonFormUtils --> Failed to close static images output stream after attempting to write image");
                }
            }
        }

    }

    public static String getMetadataForEditForm(Context context, Map<String, String> childDetails) {
        return getMetadataForEditForm(context, childDetails, new ArrayList<String>());
    }

    public static String getMetadataForEditForm(Context context, Map<String, String> childDetails, List<String> nonEditableFields) {

        try {
            JSONObject form = new FormUtils(context).getFormJson(Utils.metadata().childRegister.formName);

            if (form != null) {

                JsonFormUtils.addChildRegLocHierarchyQuestions(form, "", LocationHierarchy.ENTIRE_TREE);
                Timber.d("Form is %s", form.toString());

                form.put(JsonFormUtils.ENTITY_ID, childDetails.get(Constants.KEY.BASE_ENTITY_ID));
                form.put(JsonFormUtils.ENCOUNTER_TYPE, Utils.metadata().childRegister.updateEventType);
                form.put(JsonFormUtils.RELATIONAL_ID, childDetails.get(RELATIONAL_ID));
                form.put(JsonFormUtils.CURRENT_ZEIR_ID,
                        Utils.getValue(childDetails, Constants.KEY.ZEIR_ID, true).replace("-", ""));
                form.put(JsonFormUtils.CURRENT_OPENSRP_ID,
                        Utils.getValue(childDetails, Constants.JSON_FORM_KEY.UNIQUE_ID, false));

                JSONObject metadata = form.getJSONObject(JsonFormUtils.METADATA);

                metadata.put(JsonFormUtils.ENCOUNTER_LOCATION,
                        ChildLibrary.getInstance().getLocationPickerView(context).getSelectedItem());


                //inject zeir id into the form
                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    setFormFieldValues(childDetails, nonEditableFields, jsonObject);
                }

                return form.toString();
            }
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> getMetadataForEditForm");
        }

        return "";
    }

    private static void setFormFieldValues(Map<String, String> childDetails, List<String> nonEditableFields, JSONObject jsonObject) throws JSONException {
        String prefix = jsonObject.has(JsonFormUtils.ENTITY_ID) && jsonObject.getString(JsonFormUtils.ENTITY_ID).equalsIgnoreCase("mother") ? "mother_" : "";

        if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.KEY.PHOTO)) {
            processPhoto(childDetails.get(Constants.KEY.BASE_ENTITY_ID), jsonObject);
        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.JSON_FORM_KEY.DOB_UNKNOWN)) {
            JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
            optionsObject.put(JsonFormUtils.VALUE, Utils.getValue(childDetails, Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));
        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.JSON_FORM_KEY.AGE)) {
            processAge(Utils.getValue(childDetails, Constants.JSON_FORM_KEY.DOB, false), jsonObject);
        } else if (jsonObject.getString(JsonFormConstants.TYPE).equalsIgnoreCase(JsonFormConstants.DATE_PICKER)) {
            processDate(childDetails, prefix, jsonObject);
        } else if (jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY).equalsIgnoreCase(JsonFormUtils.PERSON_INDENTIFIER)) {
            jsonObject.put(JsonFormUtils.VALUE, Utils.getValue(childDetails, jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY_ID).toLowerCase(), false).replace("-", ""));
        } else if (jsonObject.has(JsonFormConstants.TREE)) {
            processTree(jsonObject, Utils.getValue(childDetails, jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY).equalsIgnoreCase(JsonFormUtils.PERSON_ADDRESS) ? jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY_ID) : jsonObject.getString(JsonFormUtils.KEY), false));
        } else if (jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY).equalsIgnoreCase(JsonFormUtils.CONCEPT)) {
            jsonObject.put(JsonFormUtils.VALUE, getMappedValue(jsonObject.getString(JsonFormUtils.KEY), childDetails));
        } else if (jsonObject.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
            String val = getMappedValue(prefix + jsonObject.getString(JsonFormUtils.KEY), childDetails);
            String key = prefix + jsonObject.getString(JsonFormUtils.KEY);

            if (!TextUtils.isEmpty(val)) {
                JSONArray array = new JSONArray(val.charAt(0) == '[' ? val : "[" + key + "]");
                jsonObject.put(JsonFormConstants.VALUE, array);
            }
        } else {
            jsonObject.put(JsonFormUtils.VALUE, getMappedValue(prefix + jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY_ID), childDetails));
        }

        jsonObject.put(JsonFormUtils.READ_ONLY, nonEditableFields.contains(jsonObject.getString(JsonFormUtils.KEY)));
    }

    private static void processTree(JSONObject jsonObject, String entity) throws JSONException {
        List<String> entityHierarchy = null;


        if (entity != null) {
            if (entity.equalsIgnoreCase("other")) {
                entityHierarchy = new ArrayList<>();
                entityHierarchy.add(entity);
            } else {
                entityHierarchy = LocationHelper.getInstance().getOpenMrsLocationHierarchy(entity, true);
            }
        }

        String birthFacilityHierarchyString = AssetHandler.javaToJsonString(entityHierarchy, new TypeToken<List<String>>() {
        }.getType());
        if (StringUtils.isNotBlank(birthFacilityHierarchyString)) {
            jsonObject.put(JsonFormUtils.VALUE, birthFacilityHierarchyString);
        }

    }

    protected static void processPhoto(String baseEntityId, JSONObject jsonObject) throws JSONException {
        Photo photo = ImageUtils.profilePhotoByClientID(baseEntityId, Utils.getProfileImageResourceIDentifier());

        if (StringUtils.isNotBlank(photo.getFilePath())) {
            jsonObject.put(JsonFormUtils.VALUE, photo.getFilePath());

        }
    }

    protected static void processAge(String dobString, JSONObject jsonObject) throws JSONException {
        if (StringUtils.isNotBlank(dobString)) {
            jsonObject.put(JsonFormUtils.VALUE, Utils.getAgeFromDate(dobString));
        }
    }

    protected static void processDate(Map<String, String> childDetails, String prefix, JSONObject jsonObject)
            throws JSONException {
        String dateString = Utils.getValue(childDetails, jsonObject.getString(JsonFormUtils.OPENMRS_ENTITY_ID)
                .equalsIgnoreCase(FormEntityConstants.Person.birthdate.toString()) ? prefix + "dob" :
                jsonObject.getString(JsonFormUtils.KEY), false);
        Date date = Utils.dobStringToDate(dateString);
        if (StringUtils.isNotBlank(dateString) && date != null) {
            jsonObject.put(JsonFormUtils.VALUE, DATE_FORMAT.format(date));
        }
    }

    protected static String getMappedValue(String key, Map<String, String> childDetails) {

        String value = Utils.getValue(childDetails, key, false);
        return !TextUtils.isEmpty(value) ? value : Utils.getValue(childDetails, key.toLowerCase(), false);
    }

    protected static Triple<Boolean, JSONObject, JSONArray> validateParameters(String jsonString, String step) {

        JSONObject jsonForm = toJSONObject(jsonString);
        JSONArray fields = fields(jsonForm, step);

        return Triple.of(jsonForm != null && fields != null, jsonForm, fields);
    }

    public static JSONArray fields(JSONObject jsonForm, String step) {
        try {

            JSONObject step1 = jsonForm.has(step) ? jsonForm.getJSONObject(step) : null;
            if (step1 == null) {
                return null;
            }

            return step1.has(FIELDS) ? step1.getJSONArray(FIELDS) : null;

        } catch (JSONException e) {
            Timber.e(e, "JsonFormUtils --> fields");
        }
        return null;
    }

    public static FormTag formTag(AllSharedPreferences allSharedPreferences) {
        FormTag formTag = new FormTag();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = ChildLibrary.getInstance().getApplicationVersion();
        formTag.databaseVersion = ChildLibrary.getInstance().getDatabaseVersion();
        return formTag;
    }

    public static String getFieldValue(String jsonString, String step, String key) {
        JSONObject jsonForm = toJSONObject(jsonString);
        if (jsonForm == null) {
            return null;
        }

        JSONArray fields = fields(jsonForm, step);
        if (fields == null) {
            return null;
        }

        return getFieldValue(fields, key);

    }

    public static ChildEventClient processMotherRegistrationForm(String jsonString, String relationalId, ChildEventClient base) {
        try {
            android.content.Context context = CoreLibrary.getInstance().context().applicationContext();
            String subBindType = Constants.KEY.MOTHER;
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

            if (!registrationFormParams.getLeft()) {
                return null;
            }

            Client baseClient = base.getClient();
            Event baseEvent = base.getEvent();

            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();

            JSONObject metadata = getJSONObject(jsonForm, METADATA);

            JSONObject lookUpJSONObject = getJSONObject(metadata, Constants.KEY.LOOK_UP);
            String lookUpEntityId = null;
            String lookUpBaseEntityId = null;
            if (lookUpJSONObject != null) {
                lookUpEntityId = getString(lookUpJSONObject, JsonFormUtils.ENTITY_ID);
                lookUpBaseEntityId = getString(lookUpJSONObject, JsonFormConstants.VALUE);
            }

            Client subformClient = null;
            Event subformEvent = null;

            if (Constants.KEY.MOTHER.equals(lookUpEntityId) && StringUtils.isNotBlank(lookUpBaseEntityId)) {
                Client motherClient = new Client(lookUpBaseEntityId);
                addRelationship(context, motherClient, baseClient);
            } else {
                if (StringUtils.isNotBlank(subBindType)) {

                    String motherBaseEntityId = TextUtils.isEmpty(lookUpBaseEntityId) ? relationalId : lookUpBaseEntityId;
                    subformClient = createSubFormClient(context, fields, baseClient, subBindType, motherBaseEntityId);
                    subformClient.setGender(Constants.GENDER.FEMALE);
                }

                if (subformClient != null && baseEvent != null) {
                    JSONObject subBindTypeJson = getJSONObject(jsonForm, subBindType);
                    if (subBindTypeJson != null) {
                        String subBindTypeEncounter = getString(subBindTypeJson, ENCOUNTER_TYPE);
                        if (StringUtils.isNotBlank(subBindTypeEncounter)) {


                            subformEvent = JsonFormUtils.createSubFormEvent(getMotherFields(fields), metadata, baseEvent,
                                    subformClient.getBaseEntityId(), subBindTypeEncounter, subBindType);
                        }
                    }
                }
            }

            lastInteractedWith(fields);

            dobUnknownUpdateFromAge(fields);

            JsonFormUtils.tagSyncMetadata(subformEvent);// tag docs

            return new ChildEventClient(subformClient, subformEvent);
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> processMotherRegistrationForm");
            return null;
        }
    }

    private static void addRelationship(Context context, Client parent, Client child) {
        try {
            String relationships = AssetHandler.readFileFromAssetsFolder(FormUtils.ecClientRelationships, context);
            JSONArray jsonArray = null;

            jsonArray = new JSONArray(relationships);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject rObject = jsonArray.getJSONObject(i);
                if (rObject.has("field") && getString(rObject, "field").equals(ENTITY_ID)) {
                    child.addRelationship(rObject.getString("client_relationship"), parent.getBaseEntityId());
                } /* else {
                    //TODO how to add other kind of relationships
                  } */
            }
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> addRelationship");
        }
    }

    private static Client createSubFormClient(Context context, JSONArray fields, Client parent, String bindType,
                                              String relationalId) {

        if (StringUtils.isBlank(bindType)) {
            return null;
        }
        String stringBirthDate = getSubFormFieldValue(fields, FormEntityConstants.Person.birthdate, bindType);
        Map<String, String> identifierMap = getIdentifierMap(fields, parent, bindType);
        Date birthDate = formatDate(stringBirthDate, true);
        String stringDeathDate = getSubFormFieldValue(fields, FormEntityConstants.Person.deathdate, bindType);
        Date deathDate = formatDate(stringDeathDate, true);
        String approxBirthDate = getSubFormFieldValue(fields, FormEntityConstants.Person.birthdate_estimated, bindType);
        boolean birthDateApprox = isDateApprox(approxBirthDate);
        String approxDeathDate = getSubFormFieldValue(fields, FormEntityConstants.Person.deathdate_estimated, bindType);
        boolean deathDateApprox = isDateApprox(approxDeathDate);

        List<Address> addresses = new ArrayList<>(extractAddresses(fields, bindType).values());

        Map<String, String> clientMap = createClientMap(fields, bindType, relationalId);

        Client client = getClient(clientMap, birthDate, deathDate, birthDateApprox, deathDateApprox);
        client.withAddresses(addresses).withAttributes(extractAttributes(fields, clientMap.get(Constants.BIND_TYPE))).withIdentifiers(identifierMap);

        if (addresses.isEmpty()) {
            client.withAddresses(parent.getAddresses());
        }

        addRelationship(context, client, parent);
        return client;
    }

    @NotNull
    private static Client getClient(Map<String, String> clientMap, Date birthDate, Date deathDate, boolean birthDateApprox, boolean deathDateApprox) {

        Client client = (Client) new Client(clientMap.get(Constants.ENTITY_ID)).withFirstName(clientMap.get(Constants.FIRST_NAME)).withMiddleName(clientMap.get(Constants.MIDDLE_NAME)).withLastName(clientMap.get(Constants.LAST_NAME))
                .withBirthdate(birthDate, birthDateApprox).withDeathdate(deathDate, deathDateApprox).withGender(clientMap.get(GENDER))
                .withDateCreated(new Date());

        return client;
    }

    private static Map<String, String> createClientMap(JSONArray fields, String bindType, String relationalId) {
        String entityId = TextUtils.isEmpty(relationalId) ? generateRandomUUIDString() : relationalId;
        String firstName = getSubFormFieldValue(fields, FormEntityConstants.Person.first_name, bindType);
        String middleName = getSubFormFieldValue(fields, FormEntityConstants.Person.middle_name, bindType);
        String lastName = getSubFormFieldValue(fields, FormEntityConstants.Person.last_name, bindType);
        String gender = getSubFormFieldValue(fields, FormEntityConstants.Person.gender, bindType);

        Map<String, String> client = new HashMap<>();
        client.put(Constants.ENTITY_ID, entityId);
        client.put(Constants.FIRST_NAME, firstName);
        client.put(Constants.MIDDLE_NAME, middleName);
        client.put(Constants.LAST_NAME, lastName);
        client.put(GENDER, gender);
        client.put(Constants.BIND_TYPE, bindType);
        return client;
    }

    @NotNull
    private static Map<String, String> getIdentifierMap(JSONArray fields, Client parent, String bindType) {
        Map<String, String> identifiers = extractIdentifiers(fields, bindType);
        String parentIdentifier = parent.getIdentifier(ZEIR_ID);
        if (StringUtils.isNotBlank(parentIdentifier)) {
            String identifier = parentIdentifier.concat("_").concat(bindType);
            identifiers.put(M_ZEIR_ID, identifier);
        }
        return identifiers;
    }

    private static boolean isDateApprox(String approxDate) {
        boolean dateApprox = false;
        if (!StringUtils.isEmpty(approxDate) && NumberUtils.isNumber(approxDate)) {
            int date = 0;
            try {
                date = Integer.parseInt(approxDate);
            } catch (Exception e) {
                Timber.e(e);
            }
            dateApprox = date > 0;
        }
        return dateApprox;
    }


    private static Event createSubFormEvent(JSONArray fields, JSONObject metadata, Event parent, String entityId,
                                            String encounterType, String bindType) {

        List<EventClient> eventClients = ChildLibrary.getInstance().eventClientRepository()
                .getEventsByBaseEntityIdsAndSyncStatus(BaseRepository.TYPE_Unsynced, Arrays.asList(entityId));

        boolean alreadyExists = eventClients.size() > 0;
        org.smartregister.domain.db.Event domainEvent = alreadyExists ? eventClients.get(0).getEvent() : null;

        Event event = getEvent(parent, entityId, encounterType, bindType, alreadyExists, domainEvent);

        addSubFormEventObservations(fields, event);
        updateMetadata(metadata, event);

        return event;
    }

    private static void addSubFormEventObservations(JSONArray fields, Event event) {
        if (fields != null && fields.length() != 0)
            addSaveReportDeceasedObservations(fields, event);
    }

    private static Event getEvent(Event parent, String entityId, String encounterType, String bindType, boolean alreadyExists, org.smartregister.domain.db.Event domainEvent) {
        return (Event) new Event().withBaseEntityId(
                alreadyExists ? domainEvent.getBaseEntityId() : entityId)//should be different for main and subform
                .withEventDate(parent.getEventDate()).withEventType(encounterType).withEntityType(bindType)
                .withFormSubmissionId(alreadyExists ? domainEvent.getFormSubmissionId() : generateRandomUUIDString())
                .withDateCreated(new Date());
    }

    private static JSONArray getMotherFields(JSONArray fields) throws JSONException {
        JSONArray array = new JSONArray();

        for (int i = 0; i < fields.length(); i++) {
            if (fields.getJSONObject(i).has(ENTITY_ID) && fields.getJSONObject(i).getString(ENTITY_ID).equals("mother")) {
                array.put(fields.getJSONObject(i));
            }
        }
        return array;
    }

    public static void processOutOfAreaService(String jsonString, ChildRegisterContract.ProgressDialogCallback progressDialogCallback) {
        SaveOutOfAreaServiceTask saveOutOfAreaServiceTask = new SaveOutOfAreaServiceTask(ChildLibrary.getInstance().context().applicationContext(), jsonString, progressDialogCallback);
        Utils.startAsyncTask(saveOutOfAreaServiceTask, null);
    }

    public static boolean processMoveToCatchment(Context context, AllSharedPreferences allSharedPreferences,
                                                 JSONObject jsonObject) {

        try {
            int eventsCount = jsonObject.has(Constants.NO_OF_EVENTS) ? jsonObject.getInt(Constants.NO_OF_EVENTS) : 0;
            if (eventsCount == 0) {
                return false;
            }

            JSONArray events = getOutOFCatchmentJsonArray(jsonObject, Constants.EVENTS);
            JSONArray clients = getOutOFCatchmentJsonArray(jsonObject, Constants.CLIENTS);

            ChildLibrary.getInstance().getEcSyncHelper().batchSave(events, clients);
            addProcessMoveToCatchment(context, allSharedPreferences, createEventList(events));
            processClients(context, allSharedPreferences, ChildLibrary.getInstance().getEcSyncHelper());

            return true;
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> processMoveToCatchment");
        }

        return false;
    }

    private static JSONArray getOutOFCatchmentJsonArray(JSONObject jsonObject, String clients) throws JSONException {
        return jsonObject.has(clients) ? jsonObject.getJSONArray(clients) : new JSONArray();
    }

    private static List<Pair<Event, JSONObject>> createEventList(JSONArray events) throws JSONException {
        List<Pair<Event, JSONObject>> eventList = new ArrayList<>();
        for (int i = 0; i < events.length(); i++) {
            JSONObject jsonEvent = events.getJSONObject(i);
            Event event = ChildLibrary.getInstance().getEcSyncHelper().convert(jsonEvent, Event.class);
            if (event == null) {
                continue;
            }

            // Skip previous move to catchment events
            if (MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT.equals(event.getEventType())) {
                continue;
            }

            if (Constants.EventType.BITRH_REGISTRATION.equals(event.getEventType())) {
                eventList.add(0, Pair.create(event, jsonEvent));
            } else if (!eventList.isEmpty() && Constants.EventType.NEW_WOMAN_REGISTRATION.equals(event.getEventType())) {
                eventList.add(1, Pair.create(event, jsonEvent));
            } else {
                eventList.add(Pair.create(event, jsonEvent));
            }

        }

        return eventList;
    }

    private static void addProcessMoveToCatchment(Context context, AllSharedPreferences allSharedPreferences, List<Pair<Event, JSONObject>> eventList) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        String locationId = allSharedPreferences.fetchDefaultLocalityId(providerId);
        for (Pair<Event, JSONObject> pair : eventList) {
            Event event = pair.first;
            JSONObject jsonEvent = pair.second;

            String fromLocationId = null;
            if (Utils.metadata().childRegister.registerEventType.equals(event.getEventType())) {
                fromLocationId = updateHomeFacility(locationId, event, fromLocationId);

            }


            if (Constants.EventType.BITRH_REGISTRATION.equals(event.getEventType()) ||
                    Constants.EventType.NEW_WOMAN_REGISTRATION.equals(event.getEventType())) {
                createMoveToCatchmentEvent(context, providerId, locationId, event, fromLocationId);

            }

            // Update providerId, locationId and Save unsynced event
            event.setProviderId(providerId);
            event.setLocationId(locationId);
            event.setVersion(System.currentTimeMillis());
            JSONObject updatedJsonEvent = ChildLibrary.getInstance().getEcSyncHelper().convertToJson(event);
            jsonEvent = JsonFormUtils.merge(jsonEvent, updatedJsonEvent);

            ChildLibrary.getInstance().getEcSyncHelper().addEvent(event.getBaseEntityId(), jsonEvent);
        }
    }

    private static void createMoveToCatchmentEvent(Context context, String toProviderId, String toLocationId, Event event, String fromLocationId) {
        //Create move to catchment event;
        Event moveToCatchmentEvent = JsonFormUtils
                .createMoveToCatchmentEvent(context, event, fromLocationId, toProviderId, toLocationId);
        if (moveToCatchmentEvent != null) {
            JSONObject moveToCatchmentJsonEvent =
                    ChildLibrary.getInstance().getEcSyncHelper().convertToJson(moveToCatchmentEvent);
            if (moveToCatchmentJsonEvent != null) {
                ChildLibrary.getInstance().getEcSyncHelper()
                        .addEvent(moveToCatchmentEvent.getBaseEntityId(), moveToCatchmentJsonEvent);
            }
        }
    }

    private static String updateHomeFacility(String toLocationId, Event event, String fromLocationId) {
        // Update home facility
        String locationId = fromLocationId;
        for (Obs obs : event.getObs()) {
            if (obs.getFormSubmissionField().equals(Constants.HOME_FACILITY)) {
                locationId = obs.getValue().toString();
                List<Object> values = new ArrayList<>();
                values.add(toLocationId);
                obs.setValues(values);
            }
        }
        return locationId;
    }

    public static Event createMoveToCatchmentEvent(Context context, Event referenceEvent, String fromLocationId,
                                                   String toProviderId, String toLocationId) {

        try {

            //Same location/provider, no need to move
            if (toLocationId.equals(fromLocationId) || referenceEvent.getProviderId().equals(toProviderId)) {
                return null;
            }

            final String FORM_SUBMISSION_FIELD = "formsubmissionField";
            final String DATA_TYPE = "text";

            Event event = getEvent(referenceEvent.getProviderId(), fromLocationId, referenceEvent.getBaseEntityId(), MoveToMyCatchmentUtils.MOVE_TO_CATCHMENT_EVENT, new Date(), "child");


            String formSubmissionField = "From_ProviderId";
            List<Object> vall = new ArrayList<>();
            vall.add(referenceEvent.getProviderId());
            event.addObs(new Obs(FORM_SUBMISSION_FIELD, DATA_TYPE, formSubmissionField, "", vall, new ArrayList<>(), null,
                    formSubmissionField));

            formSubmissionField = "From_LocationId";
            vall = new ArrayList<>();
            vall.add(fromLocationId);
            event.addObs(new Obs(FORM_SUBMISSION_FIELD, DATA_TYPE, formSubmissionField, "", vall, new ArrayList<>(), null,
                    formSubmissionField));

            formSubmissionField = "To_ProviderId";
            vall = new ArrayList<>();
            vall.add(toProviderId);
            event.addObs(new Obs(FORM_SUBMISSION_FIELD, DATA_TYPE, formSubmissionField, "", vall, new ArrayList<>(), null,
                    formSubmissionField));

            formSubmissionField = "To_LocationId";
            vall = new ArrayList<>();
            vall.add(toLocationId);
            event.addObs(new Obs(FORM_SUBMISSION_FIELD, DATA_TYPE, formSubmissionField, "", vall, new ArrayList<>(), null,
                    formSubmissionField));

            addMetaData(context, event, new Date());

            JsonFormUtils.tagSyncMetadata(event);
            return event;

        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> createMoveToCatchmentEvent");
            return null;
        }
    }


    //TO DO Remove
    //DEPRECATED

    /**
     * Starts an instance of JsonFormActivity with the provided form details
     *
     * @param context                     The activity form is being launched from
     * @param jsonFormActivityRequestCode The request code to be used to launch {@link BaseChildFormActivity}
     * @param formName                    The name of the form to launch
     * @param uniqueId                    The unique entity id for the form (e.g child's ZEIR id)
     * @param currentLocationId           OpenMRS id for the current device's location
     * @throws Exception
     */
    public static void startForm(Activity context, int jsonFormActivityRequestCode, String formName, String uniqueId,
                                 String currentLocationId) throws Exception {
        Intent intent = new Intent(context, Utils.metadata().childFormActivity);

        Form formParam = new Form();
        // formParam.setName("Rules engine demo");
        formParam.setWizard(false);
        formParam.setHideSaveLabel(true);
        formParam.setNextLabel("");

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, formParam);


        String entityId = uniqueId;
        JSONObject form = new FormUtils(context).getFormJson(formName);
        if (form != null) {
            form.getJSONObject(JsonFormUtils.METADATA).put(JsonFormUtils.ENCOUNTER_LOCATION, currentLocationId);

            if (Utils.metadata().childRegister.formName.equals(formName)) {
                if (StringUtils.isBlank(entityId)) {
                    UniqueIdRepository uniqueIdRepo = CoreLibrary.getInstance().context().getUniqueIdRepository();
                    entityId = uniqueIdRepo.getNextUniqueId() != null ? uniqueIdRepo.getNextUniqueId().getOpenmrsId() : "";
                    if (entityId.isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.no_openmrs_id), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (StringUtils.isNotBlank(entityId)) {
                    entityId = entityId.replace("-", "");
                }

                JsonFormUtils.addChildRegLocHierarchyQuestions(form, "", LocationHierarchy.ENTIRE_TREE);

                // Inject zeir id into the form
                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(JsonFormUtils.ZEIR_ID)) {
                        jsonObject.remove(JsonFormUtils.VALUE);
                        jsonObject.put(JsonFormUtils.VALUE, entityId);
                    }
                }
            } else if ("out_of_catchment_service".equals(formName)) {
                if (StringUtils.isNotBlank(entityId)) {
                    entityId = entityId.replace("-", "");
                } else {
                    JSONArray fields = form.getJSONObject("step1").getJSONArray("fields");
                    for (int i = 0; i < fields.length(); i++) {
                        if (fields.getJSONObject(i).getString("key").equals("ZEIR_ID")) {
                            fields.getJSONObject(i).put(READ_ONLY, false);
                            break;
                        }
                    }
                }

                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(JsonFormUtils.ZEIR_ID)) {
                        jsonObject.remove(JsonFormUtils.VALUE);
                        jsonObject.put(JsonFormUtils.VALUE, entityId);
                    }
                }

                JsonFormUtils.addAddAvailableVaccines(context, form);
            } else {
                Timber.w("Unsupported form requested for launch %s", formName);
            }

            intent.putExtra("json", form.toString());
            Timber.d("JsonFormUtils --> form is %s", form.toString());
            context.startActivityForResult(intent, jsonFormActivityRequestCode);
        }
    }

    public static void createBCGScarEvent(Context context, String baseEntityId, String providerId, String locationId) {

        try {

            Event event = getEvent(providerId, locationId, baseEntityId, BCG_SCAR_EVENT, new Date(), "child");


            final String BCG_SCAR_CONCEPT = "160265AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
            final String YES_CONCEPT = "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

            List<Object> values = new ArrayList<>();
            values.add(YES_CONCEPT);

            List<Object> humanReadableValues = new ArrayList<>();
            humanReadableValues.add("Yes");

            event.addObs(
                    new Obs(CONCEPT, "select one", BCG_SCAR_CONCEPT, "", values, humanReadableValues, null, "bcg_scar"));

            JSONObject eventJson = new JSONObject(gson.toJson(event));
            if (eventJson != null) {
                ECSyncHelper.getInstance(context).addEvent(baseEntityId, eventJson);
            }

        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> createBCGScarEvent");
        }
    }


    public static Map<String, String> updateClientAttribute(Context context,
                                                            CommonPersonObjectClient childDetails, String attributeName,
                                                            Object attributeValue) throws Exception {

        org.smartregister.Context openSRPContext = CoreLibrary.getInstance().context();

        Date date = new Date();
        EventClientRepository db = openSRPContext.getEventClientRepository();

        JSONObject client = db.getClientByBaseEntityId(childDetails.entityId());
        JSONObject attributes = client.getJSONObject(JsonFormUtils.attributes);
        attributes.put(attributeName, attributeValue);
        client.remove(JsonFormUtils.attributes);
        client.put(JsonFormUtils.attributes, attributes);
        db.addorUpdateClient(childDetails.entityId(), client);


        DetailsRepository detailsRepository = openSRPContext.detailsRepository();
        detailsRepository.add(childDetails.entityId(), attributeName, attributeValue.toString(), new Date().getTime());
        ContentValues contentValues = new ContentValues();
        //Add the base_entity_id
        contentValues.put(attributeName.toLowerCase(), attributeValue.toString());
        db.getWritableDatabase()
                .update(Utils.metadata().childRegister.tableName, contentValues, Constants.KEY.BASE_ENTITY_ID + "=?",
                        new String[]{childDetails.entityId()});

        AllSharedPreferences allSharedPreferences = openSRPContext.allSharedPreferences();
        String locationName = allSharedPreferences.fetchCurrentLocality();
        if (StringUtils.isBlank(locationName)) {
            locationName = LocationHelper.getInstance().getDefaultLocation();
        }

        Event event = getEvent(allSharedPreferences.fetchRegisteredANM(), LocationHelper.getInstance().getOpenMrsLocationId(locationName), childDetails.entityId(), JsonFormUtils.updateBirthRegistrationDetailsEncounter, new Date(), Constants.CHILD_TYPE);

        JsonFormUtils.addMetaData(context, event, date);
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
        db.addEvent(childDetails.entityId(), eventJson);
        processClients(context, allSharedPreferences, ECSyncHelper.getInstance(context));

        //update details
        Map<String, String> detailsMap = detailsRepository.getAllDetailsForClient(childDetails.entityId());
        if (childDetails.getColumnmaps().containsKey(attributeName)) {
            childDetails.getColumnmaps().put(attributeName, attributeValue.toString());
        }
        Utils.putAll(detailsMap, childDetails.getColumnmaps());

        return detailsMap;
    }
}