package org.smartregister.child.activity;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.shadows.ChildFormActivityShadow;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.MotherLookUpUtils;
import org.smartregister.util.AppProperties;

import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class BaseChildFormActivityTest {

    private static final String outOfAreaForm = "{\"count\":\"1\",\"encounter_type\":\"Out of Catchment Service\",\"entity_id\":\"\",\"step1\":{\"title\":\"Record out of catchment area service\",\"fields\":[]}}";
    private static final String outOfAreaFormWithWeight = "{\"count\":\"1\",\"encounter_type\":\"Out of Catchment Service\",\"entity_id\":\"\",\"metadata\":{},\"step1\":{\"title\":\"Record out of catchment area service\",\"fields\":[{\"key\":\"Weight_Kg\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Child's weight (kg)\",\"value\":3.7,\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"}}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.14.5.0-SNAPSHOT\",\"formVersion\":\"\"}}";
    private static final String outOfAreaFormWithVaccines = "{\"count\":\"1\",\"encounter_type\":\"Out of Catchment Service\",\"entity_id\":\"\",\"metadata\":{},\"step1\":{\"title\":\"Record out of catchment area service\",\"fields\":[{\"key\":\"opensrp_id\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"OpenSRP ID \",\"value\":\"3274343E\",\"v_regex\":{\"value\":\"^$|([0-9]{16})\",\"err\":\"The number must have a total of 16 digits\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the card Id\"}},{\"key\":\"OA_Service_Date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"type\":\"date_picker\",\"hint\":\"Date of Service\",\"expanded\":false,\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date of service\"},\"value\":\"10-10-2019\"},{\"key\":\"Vaccines_Provided_Label\",\"type\":\"label\",\"text\":\"Which vaccinations were provided?\",\"openmrs_entity_parent\":\"-\",\"openmrs_entity\":\"-\",\"openmrs_entity_id\":\"-\"},{\"key\":\"Birth\",\"type\":\"check_box\",\"is_vaccine_group\":true,\"label\":\"Birth\",\"openmrs_entity_parent\":\"-\",\"openmrs_entity\":\"-\",\"openmrs_entity_id\":\"-\",\"options\":[{\"key\":\"OPV 0\",\"text\":\"OPV 0\",\"value\":true,\"constraints\":[{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Six_Wks, \\\"[\\\"OPV 1\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"},{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Ten_Wks, \\\"[\\\"OPV 2\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"},{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"OPV 3\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"}]},{\"key\":\"BCG\",\"text\":\"BCG\",\"value\":\"false\"},{\"key\":\"HepB\",\"text\":\"HepB\",\"value\":true}],\"value\":[\"OPV 0\",\"HepB\"]}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.14.5.0-SNAPSHOT\",\"formVersion\":\"\"}}";
    private ChildFormActivityShadow childFormActivity;
    @Mock
    private ChildLibrary childLibrary;
    @Spy
    private AppProperties appProperties;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        appProperties.setProperty(Constants.PROPERTY.MOTHER_LOOKUP_SHOW_RESULTS_DURATION, String.valueOf(3000));
        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        childFormActivity = Robolectric.buildActivity(ChildFormActivityShadow.class).get();
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null, null,
                null, true);
        metadata.updateChildRegister("test", "test",
                "test", "ChildRegister",
                "test", "test",
                "test",
                "test", "test");
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        Mockito.doReturn(metadata).when(childLibrary).metadata();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);

        try {
            childFormActivity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testThatActivityWasCreated() {
        Assert.assertNotNull(childFormActivity);
    }

    @Test
    @Ignore("Fix java.lang.IllegalStateException")
    public void testVisibleFragments() {
        childFormActivity.initializeFormFragment();
        Fragment visibleFragment = childFormActivity.getVisibleFragment();
        Assert.assertNotNull(visibleFragment);
    }

    @Test
    public void testCheckIfAtLeastOneServiceGivenReturnsFalseWhenNoServiceIsSet() throws JSONException {
        JSONObject jsonForm = new JSONObject(outOfAreaForm);
        ReflectionHelpers.setField(childFormActivity, "mJSONObject", jsonForm);

        boolean isServiceGiven = childFormActivity.checkIfAtLeastOneServiceGiven();
        Assert.assertFalse(isServiceGiven);
    }

    @Test
    public void testCheckIfAtLeastOneServiceGivenReturnsTrueWhenWeightIsSet() throws JSONException {
        JSONObject jsonForm = new JSONObject(outOfAreaFormWithWeight);
        ReflectionHelpers.setField(childFormActivity, "mJSONObject", jsonForm);

        boolean isServiceGiven = childFormActivity.checkIfAtLeastOneServiceGiven();
        Assert.assertTrue(isServiceGiven);
    }

    @Test
    public void testCheckIfAtLeastOneServiceGivenReturnsTrueWhenVaccinesAreSet() throws JSONException {
        JSONObject jsonForm = new JSONObject(outOfAreaFormWithVaccines);
        ReflectionHelpers.setField(childFormActivity, "mJSONObject", jsonForm);

        boolean isServiceGiven = childFormActivity.checkIfAtLeastOneServiceGiven();
        Assert.assertTrue(isServiceGiven);
    }

    @Test
    public void testLookupQuery() {
        Map<String, String> entityMap = new HashMap<String, String>() {
            {
                put(MotherLookUpUtils.firstName, "sonia");
                put(MotherLookUpUtils.lastName, "mendes");
                put(MotherLookUpUtils.MOTHER_GUARDIAN_PHONE_NUMBER, "12312312");
                put(MotherLookUpUtils.MOTHER_GUARDIAN_NRC, "");
                put(MotherLookUpUtils.birthDate, "");
            }
        };
        String lookUpQuery = childFormActivity.lookUpQuery(entityMap, "table_test");
        Assert.assertEquals("Select table_test.id as _id , ec_client.relationalid , ec_client.details , zeir_id , " +
                "first_name , last_name , ec_client.gender , dob , nrc_number , mother_guardian_phone_number , " +
                "ec_mother_details.is_consented , ec_mother_details.preferred_language , ec_client.residential_area , " +
                "ec_client.residential_area_other , ec_client.residential_address , ec_client.base_entity_id FROM table_test  " +
                "join ec_child_details on ec_child_details.relational_id=ec_mother_details.base_entity_id join ec_mother_details " +
                "on ec_mother_details.base_entity_id = ec_client.base_entity_id " +
                "WHERE  mother_guardian_phone_number Like '%12312312%' AND nrc_number Like '%%' AND last_name Like '%mendes%' AND first_name Like '%sonia%' ;", lookUpQuery);
    }

    @Test
    public void testIsDateReturnsTrueWhenDobStringIsAValidDate() {
        String dobString = "2021-05-25";
        Assert.assertTrue(BaseChildFormActivity.isDate(dobString));
    }

    @Test
    public void testIsDateReturnsFalseWhenDobStringIsInvalidDate() {
        String dobString = "20210525";
        Assert.assertFalse(BaseChildFormActivity.isDate(dobString));
    }

    @Test
    public void testIsDateReturnsFalseWhenDobStringIsNull() {
        Assert.assertFalse(BaseChildFormActivity.isDate(null));
    }
}