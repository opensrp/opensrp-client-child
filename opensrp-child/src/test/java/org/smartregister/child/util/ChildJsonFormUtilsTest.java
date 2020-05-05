package org.smartregister.child.util;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
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
import org.powermock.reflect.internal.WhiteboxImpl;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.enums.LocationHierarchy;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, LocationHelper.class})
public class ChildJsonFormUtilsTest {

    String registrationForm = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"title\":\"Birth Registration\",\"fields\":[{\"key\":\"Child_Photo\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\"},{\"key\":\"Home_Facility\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"hint\":\"Child's home health facility \",\"tree\":[],\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home facility\"}},{\"key\":\"ZEIR_ID\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_identifier\",\"openmrs_entity_id\":\"ZEIR_ID\",\"type\":\"barcode\",\"render_type\":\"ID\",\"barcode_type\":\"qrcode\",\"hint\":\"Child's ZEIR ID \",\"scanButtonText\":\"Scan QR Code\",\"value\":\"0\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a valid ID\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the Child's ZEIR ID\"}},{\"key\":\"First_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"type\":\"edit_text\",\"hint\":\"First name\",\"edit_type\":\"name\"},{\"key\":\"Last_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"type\":\"edit_text\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Last name \",\"edit_type\":\"name\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the last name\"}},{\"key\":\"Sex\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"gender\",\"type\":\"spinner\",\"hint\":\"Sex \",\"values\":[\"Male\",\"Female\"],\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the sex\"}},{\"key\":\"Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"type\":\"date_picker\",\"hint\":\"Child's DOB \",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the date of birth\"}},{\"key\":\"First_Health_Facility_Contact\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"date\",\"type\":\"date_picker\",\"hint\":\"Date first seen \",\"expanded\":false,\"min_date\":\"today-5y\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date that the child was first seen at a health facility for immunization services\"},\"constraints\":[{\"type\":\"date\",\"ex\":\"greaterThanEqualTo(., step1:Date_Birth)\",\"err\":\"Date first seen can't occur before date of birth\"}]},{\"key\":\"Birth_Weight\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth weight (kg) \",\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth weight\"}},{\"key\":\"Birth_Height\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Birth Height (cm)\",\"v_min\":{\"value\":\"0.1\",\"err\":\"Height must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid height\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the child's birth height\"}},{\"key\":\"Mother_Guardian_First_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian first name \",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's first name\"}},{\"key\":\"Mother_Guardian_Last_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian last name \",\"edit_type\":\"name\",\"look_up\":\"true\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's last name\"}},{\"key\":\"Mother_Guardian_Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdate\",\"entity_id\":\"mother\",\"type\":\"date_picker\",\"hint\":\"Mother/Guardian DOB\",\"look_up\":\"true\",\"expanded\":false,\"duration\":{\"label\":\"Age\"},\"min_date\":\"01-01-1900\",\"max_date\":\"today-10y\",\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the mother/guardian's DOB\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child-enrollment-relevance.yml\"}}}},{\"key\":\"Mother_Guardian_Date_Birth_Unknown\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"birthdateApprox\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"check_box\",\"label\":\"\",\"options\":[{\"key\":\"Mother_Guardian_Date_Birth_Unknown\",\"text\":\"DOB unknown?\",\"text_size\":\"18px\",\"value\":\"false\"}]},{\"key\":\"Mother_Guardian_Age\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"mother_age\",\"entity_id\":\"mother\",\"type\":\"edit_text\",\"hint\":\"Mother/Guardian Age\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Please enter a number\"},\"v_min\":{\"value\":\"0\",\"err\":\"Age must be equal or greater than 0\"},\"v_max\":{\"value\":\"99\",\"err\":\"Age must be equal or less than 99\"},\"v_regex\":{\"value\":\"^$|([0-9]+)\",\"err\":\"The number must be valid\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"child-enrollment-relevance.yml\"}}},\"v_required\":{\"value\":true,\"err\":\"Please enter the age\"}},{\"key\":\"Mother_Guardian_NRC\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person_attribute\",\"openmrs_entity_id\":\"NRC_Number\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian NRC number\"},{\"key\":\"Mother_Guardian_Phone_Number\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"Mother/guardian phone number\",\"v_numeric\":{\"value\":\"true\",\"err\":\"Number must begin with 095, 096, or 097 and must be a total of 10 digits in length\"}},{\"key\":\"Place_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Place of birth \",\"values\":[\"Health facility\",\"Home\"],\"openmrs_choice_ids\":{\"Health facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":true,\"err\":\"Please enter the place of birth\"}},{\"key\":\"Birth_Facility_Name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"entity_id\":\"mother\",\"look_up\":\"true\",\"hint\":\"Which health facility was the child born in? \",\"tree\":[],\"v_required\":{\"value\":true,\"err\":\"Please enter the birth facility name\"},\"relevance\":{\"step1:Place_Birth\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"Health facility\\\")\"}}},{\"key\":\"Birth_Facility_Name_Other\",\"openmrs_entity_parent\":\"163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160632AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Other health facility \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please specify the health facility the child was born in\"},\"relevance\":{\"step1:Birth_Facility_Name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}}},{\"key\":\"Residential_Area_Other\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address5\",\"type\":\"edit_text\",\"hint\":\"Other residential area \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please specify the residential area\"},\"relevance\":{\"step1:Residential_Area\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"[\\\"Other\\\"]\\\")\"}}},{\"key\":\"Residential_Address\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"address2\",\"type\":\"edit_text\",\"hint\":\"Home address \",\"edit_type\":\"name\",\"v_required\":{\"value\":true,\"err\":\"Please enter the home address\"}}]}}";

    @Mock
    private LocationHelper locationHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, true);
        childMetadata.setFieldsWithLocationHierarchy(Arrays.asList("Home_Facility"));
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
        Mockito.doReturn(entireTree).when(locationHelper).generateLocationHierarchyTree(true, healthFacilities);
        JSONObject jsonObject = new JSONObject();
        JSONObject form = new JSONObject(registrationForm);
        WhiteboxImpl.invokeMethod(ChildJsonFormUtils.class, "addChildRegLocHierarchyQuestions", form, LocationHierarchy.FACILITY_WITH_OTHER_STRING);
        String expectedTree = "[{\"nodes\":[{\"level\":\"Province\",\"name\":\"Central\",\"key\":\"1\"}],\"level\":\"Country\",\"name\":\"Kenya\",\"key\":\"0\"}]";
        JSONArray fields = com.vijay.jsonwizard.utils.FormUtils.getMultiStepFormFields(form);
        JSONObject homeFacility = ChildJsonFormUtils.getFieldJSONObject(fields, Constants.HOME_FACILITY);

        Assert.assertEquals(expectedTree, homeFacility.optString(JsonFormConstants.TREE));
    }

    @Test
    public void testUpdateLocationStringShouldPopulateTreeAndDefaultAttributeUsingLocationHierarchyTree() throws Exception {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.KEY, "village");
        jsonArray.put(jsonObject);
        String hierarchyString = "[\"Kenya\",\"Central\"]";
        String entireTree = "[{\"nodes\":[{\"level\":\"Province\",\"name\":\"Central\",\"key\":\"1\"}],\"level\":\"Country\",\"name\":\"Kenya\",\"key\":\"0\"}]";
        PowerMockito.mockStatic(Utils.class);
        ChildMetadata childMetadata = new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, true);
        Mockito.when(Utils.metadata()).thenReturn(childMetadata);
        childMetadata.setFieldsWithLocationHierarchy(Arrays.asList("village"));
        WhiteboxImpl.invokeMethod(ChildJsonFormUtils.class, "updateLocationTree", LocationHierarchy.ENTIRE_TREE, jsonArray, hierarchyString, hierarchyString, hierarchyString, hierarchyString, entireTree);
        Assert.assertTrue(jsonObject.has(JsonFormConstants.TREE));
        Assert.assertTrue(jsonObject.has(JsonFormConstants.DEFAULT));
        Assert.assertEquals(hierarchyString, jsonObject.optString(JsonFormConstants.DEFAULT));
        Assert.assertEquals(entireTree, jsonObject.optString(JsonFormConstants.TREE));
    }
}