package org.smartregister.child.util;

import android.content.ContentValues;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.FormLocationTree;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.view.activity.BaseProfileActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChildJsonFormUtilsTest extends BaseUnitTest {

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private org.smartregister.Context opensrpContext;

    @Mock
    private Context context;

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

    private String registrationForm = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Birth Registration\",\"fields\":[{\"key\":\"Child_Photo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\"},{\"key\":\"Home_Facility\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"hint\":\"Child's home health facility \",\"tree\":[],\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home facility\"}},{\"key\":\"ZEIR_ID\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"ZEIR_ID\",\"type\":\"barcode\",\"render_type\":\"ID\",\"barcode_type\":\"qrcode\",\"hint\":\"Child's ZEIR ID \",\"scanButtonText\":\"Scan QR Code\",\"value\":\"0\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Child's ZEIR ID\"}},{\"key\":\"First_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First name\",\"edit_type\":\"name\"},{\"key\":\"Last_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Last name \",\"edit_type\":\"name\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the last name\"}},{\"key\":\"Sex\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"spinner\",\"hint\":\"Sex \",\"values\":[\"Male\",\"Female\"],\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the sex\"}},{\"key\":\"Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"date_picker\",\"hint\":\"Child's DOB \",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"}},{\"key\":\"First_Health_Facility_Contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"date\",\"type\":\"date_picker\",\"hint\":\"Date first seen \",\"expanded\":false,\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date that the child was first seen at a health facility for immunization services\"},\"constraints\":[{\"type\":\"date\",\"ex\":\"greaterThanEqualTo(., step1:Date_Birth)\",\"err\":\"Date first seen can't occur before date of birth\"}]},{\"key\":\"Birth_Weight\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth weight (kg) \",\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth weight\"}},{\"key\":\"Birth_Height\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth Height (cm)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Height must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid height\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth height\"}},{\"key\":\"Mother_Guardian_First_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian first name \",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's first name\"}},{\"key\":\"Mother_Guardian_Last_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian last name \",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's last name\"}},{\"key\":\"Mother_Guardian_Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"entity_id\":\"mother\",\"type\":\"date_picker\",\"hint\":\"Mother/Guardian DOB\",\"look_up\":\"true\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"01-01-1900\",\"max_date\":\"today-10y\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's DOB\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child-enrollment-relevance.yml\"}}}},{\"key\":\"Mother_Guardian_Date_Birth_Unknown\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdateApprox\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"check_box\",\"label\":\"\",\"options\":[{\"key\":\"Mother_Guardian_Date_Birth_Unknown\",\"text\":\"DOB unknown?\",\"text_size\":\"18px\",\"value\":\"false\"}]},{\"key\":\"Mother_Guardian_Age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"mother_age\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/Guardian Age\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a number\"},\"v_min\":{\"value\":\"0\",\"err\":\"Age must be equal or greater than 0\"},\"v_max\":{\"value\":\"99\",\"err\":\"Age must be equal or less than 99\"},\"v_regex\":{\"value\":\"^$|([0-9]+)\",\"err\":\"The number must be valid\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child-enrollment-relevance.yml\"}}},\"v_required\":{\"value\":true,\"err\":\"Please enter the age\"}},{\"key\":\"Mother_Guardian_NRC\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"NRC_Number\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian NRC number\"},{\"key\":\"Mother_Guardian_Phone_Number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian phone number\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length\"}},{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Place of birth \",\"values\":[\"Health facility\",\"Home\"],\"openmrs_choice_ids\":{\"Health facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":true,\"err\":\"Please enter the place of birth\"}},{\"key\":\"Birth_Facility_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Which health facility was the child born in? \",\"tree\":[],\"v_required\":{\"value\":true,\"err\":\"Please enter the birth facility name\"},\"relevance\":{\"step1:Place_Birth\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"Health facility\\\")\"}}},{\"key\":\"Birth_Facility_Name_Other\",\"openmrs_entity_parent\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Other health facility \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please specify the health facility the child was born in\"},\"relevance\":{\"step1:Birth_Facility_Name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}}},{\"key\":\"Residential_Area_Other\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address5\",\"type\":\"edit_text\",\"hint\":\"Other residential area \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please specify the residential area\"},\"relevance\":{\"step1:Residential_Area\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}}},{\"key\":\"Residential_Address\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address2\",\"type\":\"edit_text\",\"hint\":\"Home address \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please enter the home address\"}}]}}";

    @Mock
    private EventClientRepository eventClientRepository;

    private String reportDeceasedForm = "{\"count\":\"1\",\"encounter_type\":\"Death\",\"entity_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-05-19 10:26:41\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-05-19 10:27:18\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"19-05-2020\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Report Deceased\",\"fields\":[{\"key\":\"Date_of_Death\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1543AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"date\",\"type\":\"date_picker\",\"hint\":\"Date of death \",\"expanded\":false,\"min_date\":\"19-05-2020\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Date cannot be past today's date\"},\"constraints\":[{\"type\":\"date\",\"ex\":\"greaterThanEqualTo(., step1:Date_Birth)\",\"err\":\"Date of death can't occur before date of birth\"}],\"is-rule-check\":false,\"value\":\"19-05-2020\"},{\"key\":\"Cause_Death\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160218AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Suspected cause of death\",\"edit_type\":\"name\",\"value\":\"something terrible\"},{\"key\":\"Place_Death\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1541AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Where did the death occur? \",\"values\":[\"Health facility\",\"Home\"],\"openmrs_choice_ids\":{\"Health facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please select one option\"},\"value\":\"Home\"},{\"key\":\"Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"date_picker\",\"hint\":\"Child's DOB\",\"read_only\":true,\"hidden\":true,\"is_visible\":false,\"is-rule-check\":false,\"value\":\"19-05-2020\"}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.8.1-SNAPSHOT\",\"formVersion\":\"\"}}";

    private String childRegistrationClient = "{\"firstName\":\"Doe\",\"middleName\":\"Jane\",\"lastName\":\"Jane\",\"birthdate\":\"2019-07-02T02:00:00.000+02:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{\"mother\":[\"bdf50ebc-c352-421c-985d-9e9880d9ec58\",\"bdf50ebc-c352-421c-985d-9e9880d9ec58\"]},\"baseEntityId\":\"c4badbf0-89d4-40b9-8c37-68b0371797ed\",\"identifiers\":{\"zeir_id\":\"14750004\"},\"addresses\":[{\"addressType\":\"usual_residence\",\"addressFields\":{\"address5\":\"Not sure\"}}],\"attributes\":{\"age\":\"0.0\",\"Birth_Certificate\":\"ADG\\/23652432\\/1234\",\"second_phone_number\":\"0972343243\"},\"dateCreated\":\"2019-07-02T15:42:57.838+02:00\",\"serverVersion\":1562074977828,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":1,\"type\":\"Client\",\"id\":\"b8798571-dee6-43b5-a289-fc75ab703792\",\"revision\":\"v1\"}";

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private Repository repository;

    @Captor
    private ArgumentCaptor<JSONObject> eventClientAddOrUpdateClient;

    @Captor
    private ArgumentCaptor<ContentValues> dbUpdateDateOfRemoval;

    @Captor
    private ArgumentCaptor<ContentValues> allCommonsRepoUpdate;

    @Mock
    private AllCommonsRepository allCommonsRepository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private TelephonyManager telephonyManager;

    @Mock
    private ClientProcessorForJava clientProcessorForJava;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jsonObject = new JSONObject();
    }

    @Test
    public void isDateApproxWithNonNumberTest() throws Exception {
        boolean isApproximate = Whitebox.invokeMethod(ChildJsonFormUtils.class, "isDateApprox", "1500208620000L");
        Assert.assertFalse(isApproximate);
    }

    @Test
    public void isDateApproxWithNumberTest() throws Exception {
        boolean isApproximate = Whitebox.invokeMethod(ChildJsonFormUtils.class, "isDateApprox", "1");
        Assert.assertTrue(isApproximate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void processAgeWithWrongDateFormatTest() throws Exception {
        JSONObject result = Whitebox.invokeMethod(ChildJsonFormUtils.class, "processAge", "7/19/19", jsonObject);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.has(ChildJsonFormUtils.VALUE));
        Assert.assertNotNull(result.get(ChildJsonFormUtils.VALUE));
    }

    @Test
    public void processAgeTest() throws Exception {
        String TEST_DOB = "2017-01-01";
        LocalDate date = LocalDate.parse(TEST_DOB);
        Integer expectedDifferenceInYears = Period.between(date, LocalDate.now()).getYears();

        Whitebox.invokeMethod(ChildJsonFormUtils.class, "processAge", TEST_DOB, jsonObject);
        Assert.assertTrue(jsonObject.has(ChildJsonFormUtils.VALUE));
        Assert.assertNotNull(jsonObject.get(ChildJsonFormUtils.VALUE));
        Assert.assertEquals(expectedDifferenceInYears, jsonObject.get(ChildJsonFormUtils.VALUE));
    }

    @Test
    public void getChildLocationIdShouldReturnNullWhenCurrentLocalityIsNull() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        Assert.assertNull(ChildJsonFormUtils.getChildLocationId("98349797-489834", allSharedPreferences));
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

        Assert.assertEquals(currentLocalityId, ChildJsonFormUtils.getChildLocationId("98349797-489834", allSharedPreferences));
    }

    @Test
    public void mergeAndSaveClient() throws Exception {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("first_name", "John");
        Mockito.when(ecSyncHelper.getClient("234")).thenReturn(jsonObject);
        Mockito.when(childLibrary.getEcSyncHelper()).thenReturn(ecSyncHelper);

        Client client = new Client("234");
        ChildJsonFormUtils.mergeAndSaveClient(client);
        Mockito.verify(ecSyncHelper, Mockito.times(1))
                .addClient((String) addClientCaptor.capture(), (JSONObject) addClientCaptor.capture());

        JSONObject expected = new JSONObject();
        expected.put("baseEntityId", "234");
        expected.put("type", "Client");
        expected.put("first_name", "John");
        Assert.assertEquals("234", addClientCaptor.getAllValues().get(0));
        Assert.assertEquals(expected.optString("first_name"), ((JSONObject) addClientCaptor.getAllValues().get(1)).optString("first_name"));
    }

    @Test
    public void createBCGScarEvent() throws Exception {
        Context context = Mockito.mock(Context.class);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        String providerId = "providerId";
        String teamName = "teamA";
        String teamId = "24234-234";
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(providerId);
        Mockito.when(allSharedPreferences.fetchDefaultTeam(providerId)).thenReturn(teamName);
        Mockito.when(allSharedPreferences.fetchDefaultTeamId(providerId)).thenReturn(teamId);
        Mockito.when(allSharedPreferences.fetchCurrentLocality()).thenReturn(null);

        ReflectionHelpers.setStaticField(ECSyncHelper.class, "instance", ecSyncHelper);
        ChildJsonFormUtils jsonFormUtils = new ChildJsonFormUtils();
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

        Assert.assertEquals(ChildJsonFormUtils.BCG_SCAR_EVENT, eventJson.optString("eventType"));
        Assert.assertEquals(teamName, eventJson.optString("team"));
        Assert.assertEquals("child", eventJson.optString("entityType"));
        Assert.assertEquals(teamId, eventJson.optString("teamId"));
    }

    @Test
    public void testMotherDobUnknownUpdateFromAgeCalculatesDOBCorrectly() throws JSONException {

        JSONArray array = new JSONArray();

        JSONObject isBirthdateApproximate = new JSONObject();
        isBirthdateApproximate.put(Constants.KEY.KEY, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH_UNKNOWN);
        isBirthdateApproximate.put(Constants.OPENMRS.ENTITY, Constants.OPENMRS_ENTITY.PERSON);
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
        dobJson.put(Constants.OPENMRS.ENTITY, Constants.OPENMRS_ENTITY.PERSON);
        dobJson.put(Constants.OPENMRS.ENTITY_ID, FormEntityConstants.Person.birthdate);
        array.put(dobJson);

        JSONObject dobDateObject = ChildJsonFormUtils.getFieldJSONObject(array, Constants.JSON_FORM_KEY.MOTHER_GUARDIAN_DATE_BIRTH);

        ChildJsonFormUtils.dobUnknownUpdateFromAge(array, Constants.KEY.MOTHER);

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
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, null, true, new RegisterQueryProvider());
        Mockito.when(childLibrary.metadata()).thenReturn(metadata);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);
        Mockito.doReturn("locationA").when(locationHelper).getOpenMrsLocationId(entity);
        Mockito.doReturn(entityHierarchy).when(locationHelper).getOpenMrsLocationHierarchy("locationA", false);
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, BaseChildRegisterActivity.class,
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
        ChildJsonFormUtils.addRegistrationFormLocationHierarchyQuestions(form);
        JSONArray fields = FormUtils.getMultiStepFormFields(form);
        JSONObject homeFacility = ChildJsonFormUtils.getFieldJSONObject(fields, Constants.HOME_FACILITY);
        JSONArray resultTreeObject = new JSONArray(homeFacility.optString(JsonFormConstants.TREE));
        Assert.assertTrue(resultTreeObject.optJSONObject(0).has("nodes"));
        Assert.assertEquals("Kenya", resultTreeObject.optJSONObject(0).optString("name"));
        Assert.assertEquals("Country", resultTreeObject.optJSONObject(0).optString("level"));
        Assert.assertEquals("0", resultTreeObject.optJSONObject(0).optString("key"));
        Assert.assertEquals("Central", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("name"));
        Assert.assertEquals("1", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("key"));
        Assert.assertEquals("Province", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("level"));
    }

    @Test
    public void testUpdateLocationStringShouldPopulateTreeAndDefaultAttributeUsingLocationHierarchyTree() throws Exception {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
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

        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        Mockito.doReturn(entireTree).when(locationHelper).generateLocationHierarchyTree(ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(healthFacilities));

        WhiteboxImpl.invokeMethod(ChildJsonFormUtils.class, "updateLocationTree", jsonArray, hierarchyString, entireTreeString, healthFacilities, allLevels);
        Assert.assertTrue(jsonObject.has(JsonFormConstants.TREE));
        Assert.assertTrue(jsonObject.has(JsonFormConstants.DEFAULT));
        Assert.assertEquals(hierarchyString, jsonObject.optString(JsonFormConstants.DEFAULT));
        JSONArray resultTreeObject = new JSONArray(jsonObject.optString(JsonFormConstants.TREE));
        Assert.assertTrue(resultTreeObject.optJSONObject(0).has("nodes"));
        Assert.assertEquals("Kenya", resultTreeObject.optJSONObject(0).optString("name"));
        Assert.assertEquals("Country", resultTreeObject.optJSONObject(0).optString("level"));
        Assert.assertEquals("0", resultTreeObject.optJSONObject(0).optString("key"));
        Assert.assertEquals("Central", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("name"));
        Assert.assertEquals("1", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("key"));
        Assert.assertEquals("Province", resultTreeObject.optJSONObject(0).optJSONArray("nodes").optJSONObject(0).optString("level"));
    }

    @Test
    public void testGenerateFormLocationTreeGeneratesCorrectFormTree() throws Exception {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        ArrayList<String> locations = new ArrayList<>();
        locations.add("Country");
        locations.add("County");
        locations.add("Village");

        ArrayList<String> filteredLocations = new ArrayList<>();
        filteredLocations.add("Country");
        filteredLocations.add("County");

        Mockito.doReturn(filteredLocations).when(locationHelper).generateLocationHierarchyTree(ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(filteredLocations));

        FormLocationTree formLocationTree = WhiteboxImpl.invokeMethod(ChildJsonFormUtils.class, "generateFormLocationTree", locations, false, "County");

        Assert.assertNotNull(formLocationTree);
        Assert.assertEquals("[\"Country\",\"County\"]", formLocationTree.getFormLocationString());
        List<FormLocation> formLocations = formLocationTree.getFormLocations();

        Assert.assertNotNull(formLocations);
        Assert.assertEquals(2, formLocations.size());
        Assert.assertEquals("Country", formLocations.get(0));
        Assert.assertEquals("County", formLocations.get(1));
    }

    @Test
    public void testSaveReportDeceasedShouldPassCorrectArguments() throws JSONException {
        String entityId = "b8798571-dee6-43b5-a289-fc75ab703792";
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, null, true, new RegisterQueryProvider());
        Mockito.when(childLibrary.metadata()).thenReturn(metadata);

        JSONObject jsonClientObject = new JSONObject(childRegistrationClient);
        Mockito.when(eventClientRepository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Mockito.when(opensrpContext.allCommonsRepositoryobjects(metadata.getRegisterQueryProvider().getDemographicTable())).thenReturn(allCommonsRepository);
        Mockito.when(eventClientRepository.getClientByBaseEntityId(entityId)).thenReturn(jsonClientObject);
        Mockito.when(childLibrary.eventClientRepository()).thenReturn(eventClientRepository);
        Mockito.when(childLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("demo");
        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(telephonyManager.getSimSerialNumber()).thenReturn("234234-234");
        Mockito.when(context.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ChildJsonFormUtils.saveReportDeceased(context, reportDeceasedForm, "434-2342", entityId);

        Mockito.verify(eventClientRepository).addorUpdateClient(Mockito.eq(entityId), eventClientAddOrUpdateClient.capture());

        Mockito.verify(eventClientRepository, Mockito.times(2))
                .addEvent(Mockito.eq(entityId), Mockito.any(JSONObject.class));

        Mockito.verify(allCommonsRepository)
                .update(Mockito.eq(metadata.getRegisterQueryProvider().getDemographicTable()), allCommonsRepoUpdate.capture(), Mockito.eq(entityId));

        Mockito.verify(sqLiteDatabase).update(Mockito.eq(metadata.getRegisterQueryProvider().getDemographicTable()),
                dbUpdateDateOfRemoval.capture(), Mockito.eq(Constants.KEY.BASE_ENTITY_ID + " = ?"), Mockito.eq(new String[]{entityId}));

        JSONObject resultEventAddOrUpdate = eventClientAddOrUpdateClient.getValue();
        Assert.assertNotNull(resultEventAddOrUpdate);
        Assert.assertEquals("2020-05-19T00:00:00.000Z", resultEventAddOrUpdate.optString("deathdate"));
        Assert.assertFalse(resultEventAddOrUpdate.optBoolean("deathdate_estimated"));

        ContentValues contentValues = allCommonsRepoUpdate.getValue();
        Assert.assertNotNull(contentValues);

        Assert.assertEquals(contentValues.get(Constants.KEY.DOD), "19-05-2020");
        Assert.assertEquals(contentValues.get(Constants.KEY.DATE_REMOVED), Utils.getTodaysDate());


        ContentValues contentValues1 = dbUpdateDateOfRemoval.getValue();
        Assert.assertNotNull(contentValues1);
        Assert.assertEquals(contentValues1.get(Constants.KEY.DATE_REMOVED), "2020-05-19T00:00:00.000Z");

    }

    @Test
    public void testAddAvailableVaccinesShouldPopulateForm() throws Exception {
        JSONObject formObject = new JSONObject(registrationForm);
        int initialSize = formObject.optJSONObject(JsonFormConstants.STEP1).optJSONArray(JsonFormConstants.FIELDS).length();
        Whitebox.invokeMethod(ChildJsonFormUtils.class, "addAvailableVaccines",
                RuntimeEnvironment.application, formObject);
        JSONObject step1 = formObject.optJSONObject(JsonFormConstants.STEP1);
        JSONArray fields = step1.optJSONArray(JsonFormConstants.FIELDS);
        Assert.assertEquals((initialSize + 7), fields.length()); // 6 vaccine groups + a label
        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.optJSONObject(i);
            if (field.has(JsonFormConstants.OPTIONS_FIELD_NAME)) {
                JSONArray options = field.optJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                Set<String> keySet = getOptionKeys(options);
                if ("Birth".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("OPV 0"));
                    Assert.assertTrue(keySet.contains("BCG"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(2, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                } else if ("Six_Wks".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("OPV 1"));
                    Assert.assertTrue(keySet.contains("Penta 1"));
                    Assert.assertTrue(keySet.contains("PCV 1"));
                    Assert.assertTrue(keySet.contains("Rota 1"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(4, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                } else if ("Ten_Wks".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("OPV 2"));
                    Assert.assertTrue(keySet.contains("Penta 2"));
                    Assert.assertTrue(keySet.contains("Rota 2"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(4, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                } else if ("Fourteen_Weeks".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("OPV 3"));
                    Assert.assertTrue(keySet.contains("Penta 3"));
                    Assert.assertTrue(keySet.contains("PCV 3"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(3, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                } else if ("Nine_Months".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("Measles 1"));
                    Assert.assertTrue(keySet.contains("MR 1"));
                    Assert.assertTrue(keySet.contains("OPV 4"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(3, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                } else if ("Eighteen_Months".equals(field.optString(JsonFormConstants.KEY))) {
                    Assert.assertTrue(keySet.contains("Measles 2"));
                    Assert.assertTrue(keySet.contains("MR 2"));
                    Assert.assertTrue(field.optBoolean(Constants.IS_VACCINE_GROUP));
                    Assert.assertEquals(2, options.length());
                    Assert.assertEquals(JsonFormConstants.CHECK_BOX, field.optString(JsonFormConstants.TYPE));
                }
            }
        }
    }

    private Set<String> getOptionKeys(JSONArray options) {
        Set<String> keySet = new HashSet<>();
        for (int i = 0; i < options.length(); i++) {
            JSONObject obj = options.optJSONObject(i);
            keySet.add(obj.optString(JsonFormConstants.KEY));
        }
        return keySet;
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
    }


    @Test
    public void testFormTagCreatesValidInstance() {

        String provider = "demo";
        String appVersionName = "1.0.3";

        Mockito.doReturn(provider).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(3).when(childLibrary).getApplicationVersion();
        Mockito.doReturn(appVersionName).when(childLibrary).getApplicationVersionName();
        Mockito.doReturn(5).when(childLibrary).getDatabaseVersion();

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        FormTag formTag = ChildJsonFormUtils.formTag(allSharedPreferences);

        Assert.assertNotNull(formTag);
        Assert.assertEquals(provider, formTag.providerId);
        Assert.assertEquals(appVersionName, formTag.appVersionName);
        Assert.assertEquals(Integer.valueOf(3), formTag.appVersion);
        Assert.assertEquals(Integer.valueOf(5), formTag.databaseVersion);
    }

    @Test
    public void testUpdateClientAttribute() throws Exception {


        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
        Mockito.doReturn(sqLiteDatabase).when(repository).getReadableDatabase();
        Mockito.doReturn(repository).when(childLibrary).getRepository();
        Mockito.doReturn(opensrpContext).when(coreLibrary).context();
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        Mockito.doReturn(clientProcessorForJava).when(childLibrary).getClientProcessorForJava();

        String attributeName = "Child_Status";
        String attributeValue = "Inactive";
        String oldAttributValue = "Lost to follow up";
        String baseEntityId = "b8798571-dee6-43b5-a289-fc75ab703792";
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null, null, null, true, new RegisterQueryProvider());
        Mockito.when(childLibrary.metadata()).thenReturn(metadata);

        JSONObject client = new JSONObject();
        client.put("baseEntityId", baseEntityId);
        JSONObject clientAttributes = new JSONObject();
        clientAttributes.put(attributeName, oldAttributValue);
        client.put(ChildJsonFormUtils.attributes, clientAttributes);

        Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        Mockito.doReturn(client).when(eventClientRepository).getClientByBaseEntityId(baseEntityId);
        Mockito.doReturn(eventClientRepository).when(opensrpContext).getEventClientRepository();
        Mockito.doReturn(eventClientRepository).when(childLibrary).eventClientRepository();
        Mockito.doNothing().when(eventClientRepository).addorUpdateClient(ArgumentMatchers.endsWith(baseEntityId), ArgumentMatchers.any(JSONObject.class));

        Mockito.doReturn("My Location").when(allSharedPreferences).fetchCurrentLocality();


        //Verify client details updated correctly on device
        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put("baseEntityId", baseEntityId);
        childDetails.put("first_name", "Alaine");
        childDetails.put("last_name", "Nasenyana");
        childDetails.put(attributeName, oldAttributValue);

        ArrayList<HashMap<String, String>> dbRecords = new ArrayList<>();
        dbRecords.add(childDetails);

        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(baseEntityId, childDetails, "child");
        commonPersonObjectClient.setColumnmaps(childDetails);

        Mockito.doReturn(dbRecords).when(eventClientRepository).rawQuery(ArgumentMatchers.any(SQLiteDatabase.class), ArgumentMatchers.anyString());

        ChildJsonFormUtils.updateClientAttribute(opensrpContext, commonPersonObjectClient, locationHelper, attributeName, attributeValue);

        ArgumentCaptor<String> tableArgumentCaptor = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<ContentValues> queryParamsArgumentCaptor = ArgumentCaptor.forClass(ContentValues.class);

        ArgumentCaptor<String> filterArgumentCaptor = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<String[]> filterArgumentsCaptor = ArgumentCaptor.forClass(String[].class);

        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(tableArgumentCaptor.capture(), queryParamsArgumentCaptor.capture(), filterArgumentCaptor.capture(), filterArgumentsCaptor.capture());

        String table = tableArgumentCaptor.getValue();

        Assert.assertNotNull(table);
        Assert.assertEquals("ec_child_details", table);

        String filterArgumentCaptorValue = filterArgumentCaptor.getValue();

        Assert.assertNotNull(filterArgumentCaptorValue);
        Assert.assertEquals("base_entity_id= ?", filterArgumentCaptorValue);

        String[] filterArgumentsCaptorValue = filterArgumentsCaptor.getValue();

        Assert.assertNotNull(filterArgumentsCaptorValue);
        Assert.assertEquals(1, filterArgumentsCaptorValue.length);
        Assert.assertEquals(baseEntityId, filterArgumentsCaptorValue[0]);

        ContentValues contentValues = queryParamsArgumentCaptor.getValue();

        Assert.assertNotNull(contentValues);
        Assert.assertEquals("child_status=Inactive", contentValues.toString());


        //Verify Client document is generated correctly
        ArgumentCaptor<JSONObject> updatedClientArgCaptor = ArgumentCaptor.forClass(JSONObject.class);
        ArgumentCaptor<String> baseEntityIdArgCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(eventClientRepository).addorUpdateClient(baseEntityIdArgCaptor.capture(), updatedClientArgCaptor.capture());

        JSONObject updatedClient = updatedClientArgCaptor.getValue();
        Assert.assertNotNull(updatedClient);
        Assert.assertEquals(baseEntityId, client.getString("baseEntityId"));
        Assert.assertTrue(updatedClient.has(ChildJsonFormUtils.attributes));
        Assert.assertEquals("{\"Child_Status\":\"Inactive\"}", updatedClient.getString(ChildJsonFormUtils.attributes));


        //Verify update Event document is generated correctly
        ArgumentCaptor<JSONObject> updatedEventArgCaptor = ArgumentCaptor.forClass(JSONObject.class);
        ArgumentCaptor<String> baseEntityIdArgCaptor2 = ArgumentCaptor.forClass(String.class);
        Mockito.verify(eventClientRepository).addEvent(baseEntityIdArgCaptor2.capture(), updatedEventArgCaptor.capture());

        JSONObject updatedEvent = updatedEventArgCaptor.getValue();
        Assert.assertNotNull(updatedEvent);
        Assert.assertEquals(baseEntityId, updatedEvent.getString("baseEntityId"));
        Assert.assertEquals("Event", updatedEvent.getString("type"));
        Assert.assertEquals("Update Birth Registration", updatedEvent.getString("eventType"));
        Assert.assertEquals("demo", updatedEvent.getString("providerId"));
    }
}