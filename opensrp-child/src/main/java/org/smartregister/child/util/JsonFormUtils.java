package org.smartregister.child.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.common.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.ImageRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by ndegwamartin on 26/02/2019.
 */
public class JsonFormUtils extends org.smartregister.util.JsonFormUtils {
    private static final String TAG = JsonFormUtils.class.getCanonicalName();

    public static final String METADATA = "metadata";
    public static final String ENCOUNTER_TYPE = "encounter_type";
    public static final int REQUEST_CODE_GET_JSON = 2244;

    public static final String CURRENT_OPENSRP_ID = "current_opensrp_id";
    public static final String READ_ONLY = "read_only";

    public static final String STEP2 = "step2";
    public static final String MOTHER_DEFAULT_DOB = "01-01-1960";
    private static final String ENCOUNTER = "encounter";
    public static final String RELATIONAL_ID = "relational_id";
    public static final String CURRENT_ZEIR_ID = "current_zeir_id";
    public static final String ZEIR_ID = "ZEIR_ID";
    private static final String M_ZEIR_ID = "M_ZEIR_ID";
    public static final String encounterType = "Update Birth Registration";
    public static final String BCG_SCAR_EVENT = "Bcg Scar";


    public static JSONObject getFormAsJson(JSONObject form,
                                           String formName, String id,
                                           String currentLocationId) throws Exception {
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
                    Log.e(TAG, "UniqueIds are empty");
                    return null;
                }
            }

            if (StringUtils.isNotBlank(entityId)) {
                entityId = entityId.replace("-", "");
            }

            JsonFormUtils.addChildRegLocHierarchyQuestions(form);

            // Inject zeir id into the form
            JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
            JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString(JsonFormUtils.KEY)
                        .equalsIgnoreCase(JsonFormUtils.ZEIR_ID)) {
                    jsonObject.remove(JsonFormUtils.VALUE);
                    jsonObject.put(JsonFormUtils.VALUE, entityId);
                }
            }

        } else if (formName.equals(Utils.metadata().childRegister.outOfCatchmentFormName)) {
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
                if (jsonObject.getString(JsonFormUtils.KEY)
                        .equalsIgnoreCase(JsonFormUtils.ZEIR_ID)) {
                    jsonObject.remove(JsonFormUtils.VALUE);
                    jsonObject.put(JsonFormUtils.VALUE, entityId);
                }
            }

            JsonFormUtils.addAddAvailableVaccines(ChildLibrary.getInstance().context().applicationContext(), form);

        } else {
            Log.w(TAG, "Unsupported form requested for launch " + formName);
        }
        Log.d(TAG, "form is " + form.toString());
        return form;
    }

    public static void addChildRegLocHierarchyQuestions(JSONObject form) {
        try {
            JSONArray questions = form.getJSONObject("step1").getJSONArray("fields");
            ArrayList<String> allLevels = new ArrayList<>();
            allLevels.add("Country");
            allLevels.add("Province");
            allLevels.add("District");
            allLevels.add("Health Facility");
            allLevels.add("Zone");
            allLevels.add("Residential Area");

            ArrayList<String> healthFacilities = new ArrayList<>();
            healthFacilities.add("Country");
            healthFacilities.add("Province");
            healthFacilities.add("District");
            healthFacilities.add("Health Facility");

            List<String> defaultLocation = LocationHelper.getInstance().generateDefaultLocationHierarchy(allLevels);
            List<String> defaultFacility = LocationHelper.getInstance().generateDefaultLocationHierarchy(healthFacilities);
            List<FormLocation> upToFacilities = LocationHelper.getInstance().generateLocationHierarchyTree(false, healthFacilities);
            List<FormLocation> upToFacilitiesWithOther = LocationHelper.getInstance().generateLocationHierarchyTree(true, healthFacilities);
            List<FormLocation> entireTree = LocationHelper.getInstance().generateLocationHierarchyTree(true, allLevels);

            String defaultLocationString = AssetHandler.javaToJsonString(defaultLocation,
                    new TypeToken<List<String>>() {
                    }.getType());

            String defaultFacilityString = AssetHandler.javaToJsonString(defaultFacility,
                    new TypeToken<List<String>>() {
                    }.getType());

            String upToFacilitiesString = AssetHandler.javaToJsonString(upToFacilities,
                    new TypeToken<List<FormLocation>>() {
                    }.getType());

            String upToFacilitiesWithOtherString = AssetHandler.javaToJsonString(upToFacilitiesWithOther,
                    new TypeToken<List<FormLocation>>() {
                    }.getType());

            String entireTreeString = AssetHandler.javaToJsonString(entireTree,
                    new TypeToken<List<FormLocation>>() {
                    }.getType());

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
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    private static void addAddAvailableVaccines(Context context, JSONObject form) {
        List<VaccineGroup> supportedVaccines = VaccinatorUtils.getSupportedVaccines(context);
        if (supportedVaccines != null && !supportedVaccines.isEmpty() && form != null) {
            // For each of the vaccine groups, create a checkbox question
            try {
                JSONArray questionList = form.getJSONObject("step1").getJSONArray("fields");
                JSONObject vaccinationLabel = new JSONObject();
                vaccinationLabel.put("key", "Vaccines_Provided_Label");
                vaccinationLabel.put("type", "label");
                vaccinationLabel.put("text", "Which vaccinations were provided?");
                vaccinationLabel.put("openmrs_entity_parent", "-");
                vaccinationLabel.put("openmrs_entity", "-");
                vaccinationLabel.put("openmrs_entity_id", "-");
                questionList.put(vaccinationLabel);

                HashMap<String, ArrayList<JSONObject>> vaccineTypeConstraints = new HashMap<>();
                for (VaccineGroup curVaccineGroup : supportedVaccines) {
                    for (org.smartregister.immunization.domain.jsonmapping.Vaccine curVaccine : curVaccineGroup.vaccines) {
                        if (!vaccineTypeConstraints.containsKey(curVaccine.type)) {
                            vaccineTypeConstraints.put(curVaccine.type,
                                    new ArrayList<JSONObject>());
                        }
                        ArrayList<String> vaccineNamesDefined = new ArrayList<>();
                        if (curVaccine.vaccine_separator != null) {
                            String unsplitNames = curVaccine.name;
                            String separator = curVaccine.vaccine_separator;
                            String[] splitValues = unsplitNames.split(separator);
                            for (String splitValue : splitValues) {
                                vaccineNamesDefined.add(splitValue);
                            }
                        } else {
                            vaccineNamesDefined.add(curVaccine.name);
                        }

                        for (String curVaccineName : vaccineNamesDefined) {
                            JSONObject curConstraint = new JSONObject();
                            curConstraint.put("vaccine", curVaccineName);
                            curConstraint.put("type", "array");
                            curConstraint.put("ex",
                                    "notEqualTo(step1:" + curVaccineGroup.id + ", \"[\"" + curVaccineName + "\"]\")");
                            curConstraint.put("err", "Cannot be given with the other " + curVaccine.type + " dose");
                            vaccineTypeConstraints.get(curVaccine.type).add(curConstraint);
                        }
                    }
                }

                for (VaccineGroup curVaccineGroup : supportedVaccines) {
                    JSONObject curQuestion = new JSONObject();
                    curQuestion.put("key", curVaccineGroup.id);
                    curQuestion.put("type", "check_box");
                    curQuestion.put("is_vaccine_group", true);
                    curQuestion.put("label", curVaccineGroup.name);
                    curQuestion.put("openmrs_entity_parent", "-");
                    curQuestion.put("openmrs_entity", "-");
                    curQuestion.put("openmrs_entity_id", "-");

                    JSONArray options = new JSONArray();
                    for (org.smartregister.immunization.domain.jsonmapping.Vaccine curVaccine : curVaccineGroup.vaccines) {
                        ArrayList<String> definedVaccineNames = new ArrayList<>();
                        if (curVaccine.vaccine_separator != null) {
                            String rawNames = curVaccine.name;
                            String separator = curVaccine.vaccine_separator;
                            String[] split = rawNames.split(separator);
                            for (String aSplit : split) {
                                definedVaccineNames.add(aSplit);
                            }
                        } else {
                            definedVaccineNames.add(curVaccine.name);
                        }

                        for (String curVaccineName : definedVaccineNames) {
                            JSONObject curVaccines = new JSONObject();
                            curVaccines.put("key", curVaccineName);
                            curVaccines.put("text", curVaccineName);
                            curVaccines.put("value", "false");
                            JSONArray constraints = new JSONArray();

                            // Add the constraints
                            if (vaccineTypeConstraints.containsKey(curVaccine.type)) {
                                for (JSONObject curConstraint : vaccineTypeConstraints.get(curVaccine.type)) {
                                    if (!curConstraint.getString("vaccine")
                                            .equals(curVaccineName)) {
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
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
    }
/*
    public static void saveReportDeceased(Context context, org.smartregister.Context openSrpContext,
                                          String jsonString, String providerId, String locationId, String entityId) {

        try {
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

            Event event = (Event) new Event()
                    .withBaseEntityId(entityId) //should be different for main and subform
                    .withEventDate(encounterDate)
                    .withEventType(encounterType)
                    .withLocationId(locationId)
                    .withProviderId(providerId)
                    .withEntityType(Constants.EntityType.CHILD)
                    .withFormSubmissionId(generateRandomUUIDString())
                    .withDateCreated(new Date());

            for (int i = 0; i < fields.length(); i++) {
                JSONObject jsonObject = getJSONObject(fields, i);
                String value = getString(jsonObject, VALUE);
                if (StringUtils.isNotBlank(value)) {
                    addObservation(event, jsonObject);
                }
            }

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


            if (event != null) {
                JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));

                //After saving, Unsync(remove) this event's details
                //List<JSONObject> jsonEvents = new ArrayList<>();
                ///jsonEvents.add(eventJson);

                //Update client to deceased
                JSONObject client = db.getClientByBaseEntityId(eventJson.getString(ClientProcessor.baseEntityIdJSONKey));
                client.put("deathdate", encounterDateTimeString);
                client.put("deathdateApprox", false);
                db.addorUpdateClient(entityId, client);

                //Add Death Event for child to flag for Server delete
                db.addEvent(event.getBaseEntityId(), eventJson);

                //Update Child Entity to include death date
                Event updateChildDetailsEvent = (Event) new Event()
                        .withBaseEntityId(entityId) //should be different for main and subform
                        .withEventDate(encounterDate)
                        .withEventType(JsonFormUtils.encounterType)
                        .withLocationId(locationId)
                        .withProviderId(providerId)
                        .withEntityType(PathConstants.EntityType.CHILD)
                        .withFormSubmissionId(generateRandomUUIDString())
                        .withDateCreated(new Date());
                JsonFormUtils.addMetaData(context, updateChildDetailsEvent, new Date());
                JSONObject eventJsonUpdateChildEvent = new JSONObject(JsonFormUtils.gson.toJson(updateChildDetailsEvent));

                db.addEvent(entityId, eventJsonUpdateChildEvent); //Add event to flag server update

                //Update REGISTER and FTS Tables
                String tableName = PathConstants.CHILD_TABLE_NAME;
                AllCommonsRepository allCommonsRepository = openSrpContext.allCommonsRepositoryobjects(tableName);
                if (allCommonsRepository != null) {
                    ContentValues values = new ContentValues();
                    values.put(PathConstants.EC_CHILD_TABLE.DOD, PathConstants.DEFAULT_DATE_STRING);
                    allCommonsRepository.update(tableName, values, entityId);
                    allCommonsRepository.updateSearch(entityId);

                    // Update coverage reports
                    CoverageDropoutIntentService.unregister(context, entityId);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

*/

    public static ChildEventClient processChildUpdateForm(AllSharedPreferences allSharedPreferences, String jsonString) {

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

            lastInteractedWith(fields);

            dobUnknownUpdateFromAge(fields);

            Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag(allSharedPreferences), entityId);

            // Default child values
            baseClient.setLastName("Child");
            baseClient.setBirthdate(new Date(0));
            baseClient.setGender("Male");

            Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag(allSharedPreferences), entityId, Utils.metadata().childRegister.registerEventType, Utils.metadata().childRegister.tableName);

            JsonFormUtils.tagSyncMetadata(allSharedPreferences, baseEvent);// tag docs

            return new ChildEventClient(baseClient, baseEvent);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
    }

    /*
            public static ChildEventClient processChildHeadRegistrationForm(AllSharedPreferences allSharedPreferences, String jsonString, String childBaseEntityId) {

                try {
                    Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString, STEP2);

                    if (!registrationFormParams.getLeft()) {
                        return null;
                    }

                    JSONObject jsonForm = registrationFormParams.getMiddle();
                    JSONArray fields = registrationFormParams.getRight();

                    String entityId = getString(jsonForm, ENTITY_ID);
                    if (StringUtils.isBlank(entityId)) {
                        entityId = generateRandomUUIDString();
                    }

                    lastInteractedWith(fields);

                    dobUnknownUpdateFromAge(fields);

                    Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag(allSharedPreferences), entityId);
                    baseClient.addRelationship(Utils.metadata().childMemberRegister.childRelationKey, childBaseEntityId);

                    Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag(allSharedPreferences), entityId, Utils.metadata().childMemberRegister.registerEventType, Utils.metadata().childMemberRegister.tableName);

                    JsonFormUtils.tagSyncMetadata(allSharedPreferences, baseEvent);// tag docs

                    return new ChildEventClient(baseClient, baseEvent);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                    return null;
                }
            }

            public static ChildEventClient processChildUpdateForm(AllSharedPreferences allSharedPreferences, String jsonString, String childBaseEntityId) {
                return processChildForm(allSharedPreferences, jsonString, childBaseEntityId, Utils.metadata().childRegister.updateEventType);
            }

            public static ChildEventClient processChildMemberUpdateRegistrationForm(AllSharedPreferences allSharedPreferences, String jsonString, String childBaseEntityId) {
                return processChildForm(allSharedPreferences, jsonString, childBaseEntityId, Utils.metadata().childMemberRegister.updateEventType);
            }

            public static ChildEventClient processChildMemberRegistrationForm(AllSharedPreferences allSharedPreferences, String jsonString, String childBaseEntityId) {
                return processChildForm(allSharedPreferences, jsonString, childBaseEntityId, Utils.metadata().childMemberRegister.registerEventType);
            }

            private static ChildEventClient processChildForm(AllSharedPreferences allSharedPreferences, String jsonString, String childBaseEntityId, String encounterType) {
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

                    lastInteractedWith(fields);

                    dobUnknownUpdateFromAge(fields);

                    Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag(allSharedPreferences), entityId);
                    if (baseClient != null && !baseClient.getBaseEntityId().equals(childBaseEntityId)) {
                        baseClient.addRelationship(Utils.metadata().childMemberRegister.childRelationKey, childBaseEntityId);
                    }

                    Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, getJSONObject(jsonForm, METADATA), formTag(allSharedPreferences), entityId, encounterType, Utils.metadata().childMemberRegister.tableName);

                    JsonFormUtils.tagSyncMetadata(allSharedPreferences, baseEvent);// tag docs

                    return new ChildEventClient(baseClient, baseEvent);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                    return null;
                }
            }
    */
    public static void mergeAndSaveClient(ECSyncHelper ecUpdater, Client baseClient) throws Exception {
        JSONObject updatedClientJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(baseClient));

        JSONObject originalClientJsonObject = ecUpdater.getClient(baseClient.getBaseEntityId());

        JSONObject mergedJson = org.smartregister.util.JsonFormUtils.merge(originalClientJsonObject, updatedClientJson);

        //TODO Save edit log ?

        ecUpdater.addClient(baseClient.getBaseEntityId(), mergedJson);
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

    public static JSONObject getAutoPopulatedJsonEditFormString(Context context, CommonPersonObjectClient client) {
        try {
            JSONObject form = FormUtils.getInstance(context).getFormJson(Utils.metadata().childRegister.formName);
            LocationPickerView lpv = new LocationPickerView(context);
            lpv.init();
            // JsonFormUtils.addWomanRegisterHierarchyQuestions(form);
            Log.d(TAG, "Form is " + form.toString());
            if (form != null) {
                form.put(JsonFormUtils.ENTITY_ID, client.getCaseId());
                form.put(JsonFormUtils.ENCOUNTER_TYPE, Utils.metadata().childRegister.updateEventType);

                JSONObject metadata = form.getJSONObject(JsonFormUtils.METADATA);
                String lastLocationId = LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem());

                metadata.put(JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(JsonFormUtils.CURRENT_OPENSRP_ID, Utils.getValue(client.getColumnmaps(), Constants.JSON_FORM_KEY.UNIQUE_ID, false));

                //inject opensrp id into the form
                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    processPopulatableFields(client, jsonObject);

                }

                return form;
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return null;
    }

    protected static void processPopulatableFields(CommonPersonObjectClient client, JSONObject jsonObject) throws JSONException {


        if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.JSON_FORM_KEY.DOB)) {

            String dobString = Utils.getValue(client.getColumnmaps(), Constants.JSON_FORM_KEY.DOB, false);
            if (StringUtils.isNotBlank(dobString)) {
                Date dob = Utils.dobStringToDate(dobString);
                if (dob != null) {
                    jsonObject.put(JsonFormUtils.VALUE, dd_MM_yyyy.format(dob));
                }
            }

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.KEY.PHOTO)) {

            Photo photo = ImageUtils.profilePhotoByClientID(client.getCaseId(), Utils.getProfileImageResourceIDentifier());

            if (StringUtils.isNotBlank(photo.getFilePath())) {

                jsonObject.put(JsonFormUtils.VALUE, photo.getFilePath());

            }
        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.JSON_FORM_KEY.DOB_UNKNOWN)) {

            jsonObject.put(JsonFormUtils.READ_ONLY, false);
            JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
            optionsObject.put(JsonFormUtils.VALUE, Utils.getValue(client.getColumnmaps(), Constants.JSON_FORM_KEY.DOB_UNKNOWN, false));

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.JSON_FORM_KEY.AGE)) {

            jsonObject.put(JsonFormUtils.READ_ONLY, false);
            String dobString = Utils.getValue(client.getColumnmaps(), Constants.JSON_FORM_KEY.DOB, false);
            if (StringUtils.isNotBlank(dobString)) {
                jsonObject.put(JsonFormUtils.VALUE, Utils.getAgeFromDate(dobString));
            }

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.JSON_FORM_KEY.UNIQUE_ID)) {

            String uniqueId = Utils.getValue(client.getColumnmaps(), Constants.JSON_FORM_KEY.UNIQUE_ID, false);
            jsonObject.put(JsonFormUtils.VALUE, uniqueId.replace("-", ""));

        } else {
            Log.e(TAG, "ERROR:: Unprocessed Form Object Key " + jsonObject.getString(JsonFormUtils.KEY));
        }
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
                    throw new IllegalArgumentException("Failed to save static image, could not retrieve image compression format from name "
                            + absoluteFileName);
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
            Log.e(TAG, "Failed to save static image to disk");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close static images output stream after attempting to write image");
                }
            }
        }

    }

    protected static Triple<Boolean, JSONObject, JSONArray> validateParameters(String jsonString) {

        JSONObject jsonForm = toJSONObject(jsonString);
        JSONArray fields = fields(jsonForm);

        Triple<Boolean, JSONObject, JSONArray> registrationFormParams = Triple.of(jsonForm != null && fields != null, jsonForm, fields);
        return registrationFormParams;
    }

    protected static Triple<Boolean, JSONObject, JSONArray> validateParameters(String jsonString, String step) {

        JSONObject jsonForm = toJSONObject(jsonString);
        JSONArray fields = fields(jsonForm, step);

        Triple<Boolean, JSONObject, JSONArray> registrationFormParams = Triple.of(jsonForm != null && fields != null, jsonForm, fields);
        return registrationFormParams;
    }

    protected static Event tagSyncMetadata(AllSharedPreferences allSharedPreferences, Event event) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(locationId(allSharedPreferences));
        event.setChildLocationId(allSharedPreferences.fetchCurrentLocality());
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));

        event.setClientDatabaseVersion(ChildLibrary.getInstance().getDatabaseVersion());
        event.setClientApplicationVersion(ChildLibrary.getInstance().getApplicationVersion());
        return event;
    }

    protected static String locationId(AllSharedPreferences allSharedPreferences) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        String userLocationId = allSharedPreferences.fetchUserLocalityId(providerId);
        if (StringUtils.isBlank(userLocationId)) {
            userLocationId = allSharedPreferences.fetchDefaultLocalityId(providerId);
        }
        return userLocationId;
    }

    protected static void lastInteractedWith(JSONArray fields) {
        try {
            JSONObject lastInteractedWith = new JSONObject();
            lastInteractedWith.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.LAST_INTERACTED_WITH);
            lastInteractedWith.put(Constants.KEY.VALUE, Calendar.getInstance().getTimeInMillis());
            fields.put(lastInteractedWith);
        } catch (JSONException e) {
            Log.e(TAG, "", e);
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
                    isBirthdateApproximate.put(Constants.OPENMRS.ENTITY, Constants.ENTITY.PERSON);//Required for value to be processed
                    isBirthdateApproximate.put(Constants.OPENMRS.ENTITY_ID, FormEntityConstants.Person.birthdate_estimated);
                    fields.put(isBirthdateApproximate);

                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
    }

    protected static FormTag formTag(AllSharedPreferences allSharedPreferences) {
        FormTag formTag = new FormTag();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = ChildLibrary.getInstance().getApplicationVersion();
        formTag.databaseVersion = ChildLibrary.getInstance().getDatabaseVersion();
        return formTag;
    }

    public static JSONArray fields(JSONObject jsonForm, String step) {
        try {

            JSONObject step1 = jsonForm.has(step) ? jsonForm.getJSONObject(step) : null;
            if (step1 == null) {
                return null;
            }

            return step1.has(FIELDS) ? step1.getJSONArray(FIELDS) : null;

        } catch (JSONException e) {
            Log.e(TAG, "", e);
        }
        return null;
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
}
