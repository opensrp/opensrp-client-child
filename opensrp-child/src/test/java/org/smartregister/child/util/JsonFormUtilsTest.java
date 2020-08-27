package org.smartregister.child.util;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.FormLocationTree;
import org.smartregister.child.model.ChildMotherDetailsModel;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.BaseProfileActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, ChildLibrary.class, ECSyncHelper.class, CoreLibrary.class, LocationHelper.class})
public class JsonFormUtilsTest {

    private JSONObject jsonObject;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private CoreLibrary coreLibrary;

    @Captor
    private ArgumentCaptor addClientCaptor;

    @Captor
    private ArgumentCaptor ecSyncHelperAddEventCaptor;

    @Mock
    private ECSyncHelper ecSyncHelper;

    @Mock
    private org.smartregister.Context opensrpContext;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    private String registrationForm = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Birth Registration\",\"fields\":[{\"key\":\"Child_Photo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\"},{\"key\":\"Home_Facility\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"hint\":\"Child's home health facility \",\"tree\":[],\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home facility\"}},{\"key\":\"ZEIR_ID\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"ZEIR_ID\",\"type\":\"barcode\",\"render_type\":\"ID\",\"barcode_type\":\"qrcode\",\"hint\":\"Child's ZEIR ID \",\"scanButtonText\":\"Scan QR Code\",\"value\":\"0\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Child's ZEIR ID\"}},{\"key\":\"First_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First name\",\"edit_type\":\"name\"},{\"key\":\"Last_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Last name \",\"edit_type\":\"name\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the last name\"}},{\"key\":\"Sex\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"spinner\",\"hint\":\"Sex \",\"values\":[\"Male\",\"Female\"],\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the sex\"}},{\"key\":\"Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"date_picker\",\"hint\":\"Child's DOB \",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"}},{\"key\":\"First_Health_Facility_Contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"date\",\"type\":\"date_picker\",\"hint\":\"Date first seen \",\"expanded\":false,\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date that the child was first seen at a health facility for immunization services\"},\"constraints\":[{\"type\":\"date\",\"ex\":\"greaterThanEqualTo(., step1:Date_Birth)\",\"err\":\"Date first seen can't occur before date of birth\"}]},{\"key\":\"Birth_Weight\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth weight (kg) \",\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth weight\"}},{\"key\":\"Birth_Height\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth Height (cm)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Height must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid height\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth height\"}},{\"key\":\"Mother_Guardian_First_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian first name \",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's first name\"}},{\"key\":\"Mother_Guardian_Last_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian last name \",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's last name\"}},{\"key\":\"Mother_Guardian_Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"entity_id\":\"mother\",\"type\":\"date_picker\",\"hint\":\"Mother/Guardian DOB\",\"look_up\":\"true\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"01-01-1900\",\"max_date\":\"today-10y\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's DOB\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child-enrollment-relevance.yml\"}}}},{\"key\":\"Mother_Guardian_Date_Birth_Unknown\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdateApprox\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"check_box\",\"label\":\"\",\"options\":[{\"key\":\"Mother_Guardian_Date_Birth_Unknown\",\"text\":\"DOB unknown?\",\"text_size\":\"18px\",\"value\":\"false\"}]},{\"key\":\"Mother_Guardian_Age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"mother_age\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/Guardian Age\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a number\"},\"v_min\":{\"value\":\"0\",\"err\":\"Age must be equal or greater than 0\"},\"v_max\":{\"value\":\"99\",\"err\":\"Age must be equal or less than 99\"},\"v_regex\":{\"value\":\"^$|([0-9]+)\",\"err\":\"The number must be valid\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child-enrollment-relevance.yml\"}}},\"v_required\":{\"value\":true,\"err\":\"Please enter the age\"}},{\"key\":\"Mother_Guardian_NRC\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"NRC_Number\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian NRC number\"},{\"key\":\"Mother_Guardian_Phone_Number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian phone number\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length\"}},{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Place of birth \",\"values\":[\"Health facility\",\"Home\"],\"openmrs_choice_ids\":{\"Health facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":true,\"err\":\"Please enter the place of birth\"}},{\"key\":\"Birth_Facility_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Which health facility was the child born in? \",\"tree\":[],\"v_required\":{\"value\":true,\"err\":\"Please enter the birth facility name\"},\"relevance\":{\"step1:Place_Birth\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"Health facility\\\")\"}}},{\"key\":\"Birth_Facility_Name_Other\",\"openmrs_entity_parent\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Other health facility \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please specify the health facility the child was born in\"},\"relevance\":{\"step1:Birth_Facility_Name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}}},{\"key\":\"Residential_Area_Other\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address5\",\"type\":\"edit_text\",\"hint\":\"Other residential area \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please specify the residential area\"},\"relevance\":{\"step1:Residential_Area\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}}},{\"key\":\"Residential_Address\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address2\",\"type\":\"edit_text\",\"hint\":\"Home address \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please enter the home address\"}}]}}";

    @Mock
    private LocationHelper locationHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jsonObject = new JSONObject();
    }

    @Test
    public void isDateApproxWithNonNumberTest() throws Exception {
        boolean isApproximate = Whitebox.invokeMethod(JsonFormUtils.class, "isDateApprox", "1500208620000L");
        Assert.assertFalse(isApproximate);
    }

    @Test
    public void isDateApproxWithNumberTest() throws Exception {
        boolean isApproximate = Whitebox.invokeMethod(JsonFormUtils.class, "isDateApprox", "1");
        Assert.assertTrue(isApproximate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void processAgeWithWrongDateFormatTest() throws Exception {
        JSONObject result = Whitebox.invokeMethod(JsonFormUtils.class, "processAge", "7/19/19", jsonObject);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.has(JsonFormUtils.VALUE));
        Assert.assertNotNull(result.get(JsonFormUtils.VALUE));
    }

    @Test
    public void processAgeTest() throws Exception {
        String TEST_DOB = "2017-01-01";
        LocalDate date = LocalDate.parse(TEST_DOB);
        Integer expectedDifferenceInYears = Period.between(date, LocalDate.now()).getYears();

        Whitebox.invokeMethod(JsonFormUtils.class, "processAge", TEST_DOB, jsonObject);
        Assert.assertTrue(jsonObject.has(JsonFormUtils.VALUE));
        Assert.assertNotNull(jsonObject.get(JsonFormUtils.VALUE));
        Assert.assertEquals(expectedDifferenceInYears, jsonObject.get(JsonFormUtils.VALUE));
    }

    @Test
    public void getChildLocationIdShouldReturnNullWhenCurrentLocalityIsNull() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        Assert.assertNull(JsonFormUtils.getChildLocationId("98349797-489834", allSharedPreferences));
    }

    @Test
    public void getChildLocationIdShouldReturnCurrentLocalityIdWhenCurrentLocalityIsDifferentFromDefaultLocality() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        String currentLocality = "Kilimani";
        String currentLocalityId = "9943-43534-2dsfs";

        Mockito.doReturn(currentLocality)
                .when(allSharedPreferences)
                .fetchCurrentLocality();

        LocationHelper locationHelper = Mockito.mock(LocationHelper.class);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        Mockito.doReturn(currentLocalityId).when(locationHelper).getOpenMrsLocationId(Mockito.eq(currentLocality));

        Assert.assertEquals(currentLocalityId, JsonFormUtils.getChildLocationId("98349797-489834", allSharedPreferences));
    }

    @Test
    public void mergeAndSaveClient() throws Exception {
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("first_name", "John");
        PowerMockito.when(ecSyncHelper.getClient("234")).thenReturn(jsonObject);
        PowerMockito.when(childLibrary.getEcSyncHelper()).thenReturn(ecSyncHelper);

        Client client = new Client("234");
        JsonFormUtils.mergeAndSaveClient(client);
        Mockito.verify(ecSyncHelper, Mockito.times(1))
                .addClient((String) addClientCaptor.capture(), (JSONObject) addClientCaptor.capture());

        JSONObject expected = new JSONObject();
        expected.put("baseEntityId", "234");
        expected.put("type", "Client");
        expected.put("first_name", "John");
        Assert.assertEquals("234", addClientCaptor.getAllValues().get(0));
        Assert.assertEquals(expected.toString(), addClientCaptor.getAllValues().get(1).toString());
    }

    @Test
    public void createBCGScarEvent() throws Exception {
        PowerMockito.mockStatic(ECSyncHelper.class);
        Context context = Mockito.mock(Context.class);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        PowerMockito.when(opensrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        String providerId = "providerId";
        String teamName = "teamA";
        String teamId = "24234-234";
        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(providerId);
        PowerMockito.when(allSharedPreferences.fetchDefaultTeam(providerId)).thenReturn(teamName);
        PowerMockito.when(allSharedPreferences.fetchDefaultTeamId(providerId)).thenReturn(teamId);
        PowerMockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn(null);

        PowerMockito.when(ECSyncHelper.getInstance(context)).thenReturn(ecSyncHelper);

        JsonFormUtils jsonFormUtils = new JsonFormUtils();
        Whitebox.invokeMethod(jsonFormUtils,
                "createBCGScarEvent", context,
                "3434-234", providerId, "locationId");
        Mockito.verify(ecSyncHelper).addEvent((String) ecSyncHelperAddEventCaptor.capture(),
                (JSONObject) ecSyncHelperAddEventCaptor.capture());

        String baseEntityId = (String) ecSyncHelperAddEventCaptor.getAllValues().get(0);
        JSONObject eventJson = (JSONObject) ecSyncHelperAddEventCaptor.getAllValues().get(1);
        JSONObject obsJsonObject = eventJson.optJSONArray("obs").optJSONObject(0);
        Assert.assertEquals("3434-234", baseEntityId);
        Assert.assertEquals("bcg_scar", obsJsonObject.optString("formSubmissionField"));
        Assert.assertEquals("concept", obsJsonObject.optString("fieldType"));

        Assert.assertEquals("160265AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", obsJsonObject.optString("fieldCode"));
        Assert.assertEquals("select one", obsJsonObject.optString("fieldDataType"));
        Assert.assertEquals("Yes", obsJsonObject.optJSONArray("humanReadableValues").optString(0));
        Assert.assertEquals("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", obsJsonObject.optJSONArray("values").optString(0));

        Assert.assertEquals(JsonFormUtils.BCG_SCAR_EVENT, eventJson.optString("eventType"));
        Assert.assertEquals(teamName, eventJson.optString("team"));
        Assert.assertEquals("child", eventJson.optString("entityType"));
        Assert.assertEquals(teamId, eventJson.optString("teamId"));
    }

    @Test
    public void testMotherDobUnknownUpdateFromAgeCalculatesDOBCorrectly() throws JSONException {

        JSONArray array = new JSONArray();

        JSONObject isBirthdateApproximate = new JSONObject();
        isBirthdateApproximate.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH_UNKNOWN);
        isBirthdateApproximate.put(Constants.OPENMRS.ENTITY, Constants.ENTITY.PERSON);
        isBirthdateApproximate.put(Constants.OPENMRS.ENTITY_ID, FormEntityConstants.Person.birthdate_estimated);

        JSONObject dobOptions = new JSONObject();
        dobOptions.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH_UNKNOWN);
        dobOptions.put(Constants.KEY.VALUE, "true");

        JSONArray optArray = new JSONArray();
        optArray.put(dobOptions);

        isBirthdateApproximate.put(Constants.JSON_FORM_KEY.OPTIONS, optArray);
        array.put(isBirthdateApproximate);

        JSONObject ageJson = new JSONObject();
        ageJson.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_AGE);
        ageJson.put(Constants.KEY.VALUE, "21");
        ageJson.put(Constants.OPENMRS.ENTITY, "person_attribute");
        ageJson.put(Constants.OPENMRS.ENTITY_ID, JsonFormConstants.EDIT_TEXT);
        array.put(ageJson);

        JSONObject dobJson = new JSONObject();
        dobJson.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH);
        dobJson.put(Constants.KEY.VALUE, "");
        dobJson.put(Constants.OPENMRS.ENTITY, Constants.ENTITY.PERSON);
        dobJson.put(Constants.OPENMRS.ENTITY_ID, FormEntityConstants.Person.birthdate);
        array.put(dobJson);

        JSONObject dobDateObject = JsonFormUtils.getFieldJSONObject(array, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH);

        JsonFormUtils.dobUnknownUpdateFromAge(array, Constants.KEY.MOTHER);

        Assert.assertNotNull(dobDateObject.getString(Constants.KEY.VALUE));

        Calendar cal = Calendar.getInstance();
        cal.setTime(Calendar.getInstance().getTime());
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.YEAR, -21);
        String expectedDate = new SimpleDateFormat(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN).format(cal.getTime());

        Assert.assertEquals(expectedDate, dobDateObject.get(Constants.KEY.VALUE));
    }

    @Test
    public void testAddChildRegLocHierarchyQuestionsShouldFillFieldsWithLocationHierarchy() throws Exception {
        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add("Country");
        healthFacilities.add("Province");
        String entity = "232-432-3232";
        List<String> entityHierarchy = new ArrayList<>();
        entityHierarchy.add("Kenya");
        entityHierarchy.add("Central");
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.mockStatic(LocationHelper.class);
        Mockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
        Mockito.doReturn("locationA").when(locationHelper).getOpenMrsLocationId(entity);
        Mockito.doReturn(entityHierarchy).when(locationHelper).getOpenMrsLocationHierarchy("locationA", false);
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, null,
                true);
        childMetadata.setFieldsWithLocationHierarchy(new HashSet<String>(Arrays.asList("Home_Facility")));
        Mockito.when(Utils.metadata()).thenReturn(childMetadata);
        childMetadata.setHealthFacilityLevels(healthFacilities);
        List<FormLocation> entireTree = new ArrayList<>();
        FormLocation formLocationCountry = new FormLocation();
        formLocationCountry.level = "Country";
        formLocationCountry.name = "Kenya";
        formLocationCountry.key = "0";
        FormLocation formLocationProvince = new FormLocation();
        formLocationProvince.level = "Province";
        formLocationProvince.name = "Central";
        formLocationProvince.key = "1";
        List<FormLocation> entireTreeCountryNode = new ArrayList<>();
        entireTreeCountryNode.add(formLocationProvince);
        formLocationCountry.nodes = entireTreeCountryNode;
        entireTree.add(formLocationCountry);
        ArrayList<String> allLevels = Utils.metadata().getLocationLevels();
        Mockito.doReturn(entireTree).when(locationHelper).generateLocationHierarchyTree(ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(healthFacilities));
        Mockito.doReturn(entireTree).when(locationHelper).generateLocationHierarchyTree(ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(allLevels));
        Mockito.doReturn(healthFacilities).when(locationHelper).generateDefaultLocationHierarchy(ArgumentMatchers.eq(healthFacilities));
        Mockito.doReturn(allLevels).when(locationHelper).generateDefaultLocationHierarchy(ArgumentMatchers.eq(allLevels));
        JSONObject form = new JSONObject(registrationForm);
        JsonFormUtils.addChildRegLocHierarchyQuestions(form);
        String expectedTree = "[{\"nodes\":[{\"level\":\"Province\",\"name\":\"Central\",\"key\":\"1\"}],\"level\":\"Country\",\"name\":\"Kenya\",\"key\":\"0\"}]";
        JSONArray fields = FormUtils.getMultiStepFormFields(form);
        JSONObject homeFacility = JsonFormUtils.getFieldJSONObject(fields, Constants.HOME_FACILITY);

        Assert.assertEquals(expectedTree, homeFacility.optString(JsonFormConstants.TREE));
    }

    @Test
    public void testUpdateLocationStringShouldPopulateTreeAndDefaultAttributeUsingLocationHierarchyTree() throws Exception {

        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);

        PowerMockito.mockStatic(Utils.class);
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, null, true);
        Mockito.when(Utils.metadata()).thenReturn(childMetadata);
        childMetadata.setFieldsWithLocationHierarchy(new HashSet<>(Arrays.asList("village")));

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.KEY, "village");
        jsonObject.put(JsonFormConstants.TYPE, JsonFormConstants.TREE);
        jsonArray.put(jsonObject);
        String hierarchyString = "[\"Kenya\",\"Central\"]";
        String entireTreeString = "[{\"nodes\":[{\"level\":\"Province\",\"name\":\"Central\",\"key\":\"1\"}],\"level\":\"Country\",\"name\":\"Kenya\",\"key\":\"0\"}]";
        ArrayList<String> allLevels = Utils.metadata().getLocationLevels();
        ArrayList<String> healthFacilities = new ArrayList<>();
        healthFacilities.add("Country");
        healthFacilities.add("Province");

        List<FormLocation> entireTree = new ArrayList<>();
        FormLocation formLocationCountry = new FormLocation();
        formLocationCountry.level = "Country";
        formLocationCountry.name = "Kenya";
        formLocationCountry.key = "0";
        FormLocation formLocationProvince = new FormLocation();
        formLocationProvince.level = "Province";
        formLocationProvince.name = "Central";
        formLocationProvince.key = "1";

        List<FormLocation> entireTreeCountryNode = new ArrayList<>();
        entireTreeCountryNode.add(formLocationProvince);
        formLocationCountry.nodes = entireTreeCountryNode;
        entireTree.add(formLocationCountry);

        PowerMockito.mockStatic(LocationHelper.class);
        Mockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);

        Mockito.doReturn(entireTree).when(locationHelper).generateLocationHierarchyTree(ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(healthFacilities));

        WhiteboxImpl.invokeMethod(JsonFormUtils.class, "updateLocationTree", jsonArray, hierarchyString, entireTreeString, healthFacilities, allLevels);
        Assert.assertTrue(jsonObject.has(JsonFormConstants.TREE));
        Assert.assertTrue(jsonObject.has(JsonFormConstants.DEFAULT));
        Assert.assertEquals(hierarchyString, jsonObject.optString(JsonFormConstants.DEFAULT));
        Assert.assertEquals(entireTreeString, jsonObject.optString(JsonFormConstants.TREE));
    }

    @Test
    public void testGenerateFormLocationTreeGeneratesCorrectFormTree() throws Exception {

        PowerMockito.mockStatic(LocationHelper.class);
        Mockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);

        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);

        ArrayList<String> locations = new ArrayList<>();
        locations.add("Country");
        locations.add("County");
        locations.add("Village");

        ArrayList<String> filteredLocations = new ArrayList<>();
        filteredLocations.add("Country");
        filteredLocations.add("County");

        Mockito.doReturn(filteredLocations).when(locationHelper).generateLocationHierarchyTree(ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(filteredLocations));

        FormLocationTree formLocationTree = WhiteboxImpl.invokeMethod(JsonFormUtils.class, "generateFormLocationTree", locations, false, "County");

        Assert.assertNotNull(formLocationTree);
        Assert.assertEquals("[\"Country\",\"County\"]", formLocationTree.getFormLocationString());
        List<FormLocation> formLocations = formLocationTree.getFormLocations();

        Assert.assertNotNull(formLocations);
        Assert.assertEquals(2, formLocations.size());
        Assert.assertEquals("Country", formLocations.get(0));
        Assert.assertEquals("County", formLocations.get(1));
    }

    @Test
    public void testProcessReturnedAdvanceSearchResults() {
        Response<String> response = new Response<>(ResponseStatus.success, "[{\"type\":\"Client\",\"dateCreated\":\"2020-07-24T14:05:02.389+01:00\",\"serverVersion\":1595595902378,\"baseEntityId\":\"e027f793-ce2d-4bd4-86b1-ae0a3c56c230\",\"identifiers\":{\"M_ZEIR_ID\":\"130902\"},\"addresses\":[{\"addressType\":\"\",\"addressFields\":{\"address1\":\"Kituoni Malenga\",\"address2\":\"Victoria Falls\"}}],\"attributes\":{\"mother_rubella\":\"No\",\"mother_tdv_doses\":\"1 dose of TDV during pregnancy\",\"mother_nationality\":\"Other\",\"mother_nationality_other\":\"Ghanaian\"},\"firstName\":\"Linda\",\"lastName\":\"Linet\",\"birthdate\":\"1975-01-01T01:00:00.000+01:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"_id\":\"a0aa244a-9e3d-493e-8b0a-f39d8ebc10cd\",\"_rev\":\"v1\"},{\"type\":\"Client\",\"dateCreated\":\"2020-07-24T14:07:05.266+01:00\",\"serverVersion\":1595596025265,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":11,\"baseEntityId\":\"bb740c35-9a59-4c31-ac36-7ed870fc9fc1\",\"identifiers\":{\"zeir_id\":\"130905\"},\"addresses\":[{\"addressType\":\"\",\"addressFields\":{\"address1\":\"Keno Kobi\",\"address2\":\"Kimboi\"}}],\"attributes\":{\"age\":\"0.17\",\"child_reg\":\"65656232212\",\"ga_at_birth\":\"36\",\"place_of_birth\":\"On the way to the hospital\",\"Birth_Certificate\":\"2018/8655\"},\"firstName\":\"Melvis\",\"lastName\":\"Aurelia\",\"birthdate\":\"2020-05-24T13:00:00.000+01:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{\"mother\":[\"e027f793-ce2d-4bd4-86b1-ae0a3c56c230\"]},\"_id\":\"b509bb6c-1eb8-4fad-95fa-e6d4d784e2f9\",\"_rev\":\"v1\"},{\"type\":\"Client\",\"dateCreated\":\"2020-07-24T14:05:02.381+01:00\",\"serverVersion\":1595595902378,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":11,\"baseEntityId\":\"5ce1e428-dbfa-4ad6-9de2-246fc9f3ffa2\",\"identifiers\":{\"zeir_id\":\"130900\"},\"addresses\":[{\"addressType\":\"\",\"addressFields\":{\"address1\":\"Kituoni Malenga\",\"address2\":\"Victoria Falls\"}}],\"attributes\":{\"age\":\"0.41\",\"child_reg\":\"75665652323\",\"ga_at_birth\":\"40\",\"place_of_birth\":\"Hospital\",\"Birth_Certificate\":\"2020/52333\"},\"firstName\":\"Anto\",\"lastName\":\"Rosalina\",\"birthdate\":\"2020-02-24T13:00:00.000+01:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{\"father\":[\"b54ca41b-5b20-4a58-a761-0748289a73cf\"],\"mother\":[\"e027f793-ce2d-4bd4-86b1-ae0a3c56c230\"]},\"_id\":\"e989ee39-d7d2-43a8-ac8f-a62d93682595\",\"_rev\":\"v1\"}]");
        List<ChildMotherDetailsModel> childMotherDetailsModels = JsonFormUtils.processReturnedAdvanceSearchResults(response);
        Assert.assertEquals(2, childMotherDetailsModels.size());
        ChildMotherDetailsModel firstChild = childMotherDetailsModels.get(0);
        ChildMotherDetailsModel secondChild = childMotherDetailsModels.get(1);
        Assert.assertEquals("Melvis", firstChild.getFirstName());
        Assert.assertEquals("Anto", secondChild.getFirstName());
        Assert.assertEquals("Linda", firstChild.getMotherFirstName());
        Assert.assertEquals("Linet", secondChild.getMotherLastName());
    }
}