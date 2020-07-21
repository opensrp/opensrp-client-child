package org.smartregister.child.task;

import android.content.Context;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.util.AppProperties;

import java.lang.reflect.Method;

import static org.mockito.Mockito.when;

@PrepareForTest({LocationHelper.class})
@RunWith(PowerMockRunner.class)
public class SaveOutOfAreaServiceTaskTest {

    @Mock
    private Context context;

    @Mock
    private ChildRegisterContract.ProgressDialogCallback progressDialogCallback;

    @Mock
    private org.smartregister.Context opensrpContext;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private LocationHelper locationHelper;

    private SaveOutOfAreaServiceTask saveOutOfAreaServiceTask;

    private String formString;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        GrowthMonitoringLibrary.init(opensrpContext, Mockito.mock(Repository.class), 1, 1);

        Mockito.doReturn(new AppProperties()).when(opensrpContext).getAppProperties();
        ImmunizationLibrary.init(opensrpContext, Mockito.mock(Repository.class), Mockito.mock(CommonFtsObject.class), 1, 1);

        saveOutOfAreaServiceTask = new SaveOutOfAreaServiceTask(context, "", progressDialogCallback);

        formString = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-07-21 10:59:17\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-07-21 11:00:00\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"21-07-2020\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Birth Registration\",\"fields\":[{\"key\":\"Child_Photo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\"},{\"key\":\"Home_Facility\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address1\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"hierarchy\":\"facility_only\",\"hint\":\"Child's Home Health Facility\",\"tree\":[{\"key\":\"Westeros\",\"level\":\"\",\"name\":\"Westeros\",\"nodes\":[{\"key\":\"The North\",\"level\":\"\",\"name\":\"The North\",\"nodes\":[]}]}],\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home health facility\"},\"value\":\"[\\\"Westeros\\\",\\\"The North\\\"]\"},{\"key\":\"ZEIR_ID\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"ZEIR_ID\",\"type\":\"barcode\",\"render_type\":\"ID\",\"barcode_type\":\"qrcode\",\"hint\":\"Child's ZEIR ID \",\"scanButtonText\":\"Scan QR Code\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Child's ZEIR ID\"},\"value\":\"8352\"},{\"key\":\"first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First name\",\"edit_type\":\"name\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"John\"},{\"key\":\"last_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"hint\":\"Last name \",\"edit_type\":\"name\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"Snow\"},{\"key\":\"Sex\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"spinner\",\"hint\":\"Sex \",\"values\":[\"Male\",\"Female\"],\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the sex\"},\"value\":\"Male\"},{\"key\":\"Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"date_picker\",\"hint\":\"Child's DOB \",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"},\"is-rule-check\":false,\"value\":\"21-07-2020\"},{\"key\":\"First_Health_Facility_Contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"date\",\"type\":\"date_picker\",\"hint\":\"Date first seen \",\"expanded\":false,\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date that the child was first seen at a health facility for immunization services\"},\"constraints\":[{\"type\":\"date\",\"ex\":\"greaterThanEqualTo(., step1:Date_Birth)\",\"err\":\"Date first seen can't occur before date of birth\"}],\"is-rule-check\":false,\"value\":\"21-07-2020\"},{\"key\":\"Weight_Kg\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"label_info_text\":\"The weight as measured when the child is born\",\"hint\":\"Birth weight\\/First weight (kg)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth weight\"},\"value\":\"4\"},{\"key\":\"Birth_Height\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5090AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth Height (cm)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Height must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid height\"},\"v_required\":{\"value\":false,\"err\":\"Enter the child's birth height\"},\"value\":\"\"},{\"key\":\"Mother_Guardian_First_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother\\/guardian first name \",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother\\/guardian's first name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"Mary\"},{\"key\":\"Mother_Guardian_Last_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother\\/guardian last name \",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother\\/guardian's last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s.-]*\",\"err\":\"Please enter a valid name\"},\"value\":\"Anne\"},{\"key\":\"Mother_Guardian_Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"entity_id\":\"mother\",\"type\":\"date_picker\",\"hint\":\"Mother\\/Guardian DOB\",\"look_up\":\"true\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"01-01-1900\",\"max_date\":\"today-10y\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother\\/guardian's DOB\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child-enrollment-relevance.yml\"}}},\"is_visible\":true,\"value\":\"21-07-2010\"},{\"key\":\"Mother_Guardian_Date_Birth_Unknown\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdateApprox\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"check_box\",\"label\":\"\",\"options\":[{\"key\":\"Mother_Guardian_Date_Birth_Unknown\",\"text\":\"DOB unknown?\",\"text_size\":\"18px\",\"value\":\"false\"}],\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"Mother_Guardian_Age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"mother_age\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother\\/Guardian Age\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a number\"},\"v_min\":{\"value\":\"0\",\"err\":\"Age must be equal or greater than 0\"},\"v_max\":{\"value\":\"99\",\"err\":\"Age must be equal or less than 99\"},\"v_regex\":{\"value\":\"^$|([0-9]+)\",\"err\":\"The number must be valid\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child-enrollment-relevance.yml\"}}},\"v_required\":{\"value\":true,\"err\":\"Please enter the age\"},\"is_visible\":false},{\"key\":\"Mother_Guardian_NRC\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"NRC_Number\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"Mother\\/guardian NRC number\",\"v_regex\":{\"value\":\"([0-9]{6}\\/[0-9]{2}\\/[0-9])|s*\",\"err\":\"Number must take the format of ######\\/##\\/#\"},\"value\":\"\"},{\"key\":\"Mother_Guardian_Phone_Number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"Mother\\/guardian phone number\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length\"},\"v_regex\":{\"value\":\"(09[5-7][0-9]{7})|s*\",\"err\":\"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length\"},\"value\":\"\"},{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Place of birth \",\"values\":[\"Health facility\",\"Home\"],\"openmrs_choice_ids\":{\"Health facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":true,\"err\":\"Please enter the place of birth\"},\"is-rule-check\":false,\"value\":\"Home\"},{\"key\":\"Birth_Facility_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Which health facility was the child born in? \",\"tree\":[{\"key\":\"Westeros\",\"level\":\"\",\"name\":\"Westeros\",\"nodes\":[{\"key\":\"The North\",\"level\":\"\",\"name\":\"The North\",\"nodes\":[{\"key\":\"The crypts\",\"level\":\"\",\"name\":\"The crypts\"}]}]},{\"key\":\"Other\",\"level\":\"\",\"name\":\"Other\"}],\"v_required\":{\"value\":true,\"err\":\"Please enter the birth facility name\"},\"relevance\":{\"step1:Place_Birth\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"Health facility\\\")\"}},\"is_visible\":false,\"is-rule-check\":false},{\"key\":\"Birth_Facility_Name_Other\",\"openmrs_entity_parent\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Other health facility \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please specify the health facility the child was born in\"},\"relevance\":{\"step1:Birth_Facility_Name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}},\"is_visible\":false},{\"key\":\"Residential_Area_Other\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address5\",\"type\":\"edit_text\",\"hint\":\"Other residential area \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please specify the residential area\"},\"relevance\":{\"step1:Residential_Area\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}},\"is_visible\":false},{\"key\":\"Residential_Address\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address2\",\"type\":\"edit_text\",\"hint\":\"Home address \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please enter the home address\"},\"value\":\"Home H\"}]},\"invisible_required_fields\":\"[Residential_Area_Other, Mother_Guardian_Age, Birth_Facility_Name, Birth_Facility_Name_Other]\",\"details\":{\"appVersionName\":\"1.10.0-SNAPSHOT\",\"formVersion\":\"\"}}\n";
    }

    @Test
    public void testGetWeightObjectWhenWeight_KgIsSet() throws Exception {
        Method getWeightObject = SaveOutOfAreaServiceTask.class.getDeclaredMethod("getWeightObject", org.smartregister.Context.class, JSONObject.class);
        getWeightObject.setAccessible(true);

        when(opensrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.mockStatic(LocationHelper.class);
        when(LocationHelper.getInstance()).thenReturn(locationHelper);

        Weight weight = (Weight) getWeightObject.invoke(saveOutOfAreaServiceTask, opensrpContext, new JSONObject(formString));

        Assert.assertEquals(4.0, weight.getKg(), 0.0);
    }
}
