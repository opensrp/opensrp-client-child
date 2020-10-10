package org.smartregister.child.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.smartregister.Context;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.repository.AllSharedPreferences;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 10/10/2020.
 */

@PrepareForTest({VaccinatorUtils.class})
public class OutOfAreaServiceUtilsTest extends BasePowerMockUnitTest {

    private static final String outOfAreaForm = "{\"count\":\"1\",\"encounter_type\":\"Out of Catchment Service\",\"entity_id\":\"\",\"metadata\":{},\"step1\":{\"title\":\"Out of Area Service\",\"fields\":[{\"key\":\"nfc_card_identifier\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"Card Id \",\"value\":\"8626625555522222\",\"v_numeric\":{\"value\":\"true\",\"err\":\"The number must have a total of 16 digits\"},\"v_regex\":{\"value\":\"^$|([0-9]{16})\",\"err\":\"The number must have a total of 16 digits\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the card Id\"}},{\"key\":\"OA_Service_Date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"type\":\"date_picker\",\"hint\":\"Date of Service\",\"expanded\":false,\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date of service\"},\"value\":\"10-10-2017\"},{\"key\":\"Vaccines_Provided_Label\",\"type\":\"label\",\"text\":\"Which vaccinations were provided?\",\"openmrs_entity_parent\":\"-\",\"openmrs_entity\":\"-\",\"openmrs_entity_id\":\"-\"},{\"key\":\"Birth\",\"type\":\"check_box\",\"is_vaccine_group\":true,\"label\":\"Birth\",\"openmrs_entity_parent\":\"-\",\"openmrs_entity\":\"-\",\"openmrs_entity_id\":\"-\",\"options\":[{\"key\":\"OPV 0\",\"text\":\"OPV 0\",\"value\":true,\"constraints\":[{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Six_Wks, \\\"[\\\"OPV 1\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"},{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Ten_Wks, \\\"[\\\"OPV 2\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"},{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"OPV 3\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"}]},{\"key\":\"BCG\",\"text\":\"BCG\",\"value\":\"false\"},{\"key\":\"HepB\",\"text\":\"HepB\",\"value\":true}],\"value\":[\"OPV 0\",\"HepB\"]}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.14.5.0-SNAPSHOT\",\"formVersion\":\"\"}}";
    private static final String outOfAreaFormWithWeight = "{\"count\":\"1\",\"encounter_type\":\"Out of Catchment Service\",\"entity_id\":\"\",\"metadata\":{},\"step1\":{\"title\":\"Out of Area Service\",\"fields\":[{\"key\":\"opensrp_id\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"OpenSRP ID \",\"value\":\"3274343E\",\"v_regex\":{\"value\":\"^$|([0-9]{16})\",\"err\":\"The number must have a total of 16 digits\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the card Id\"}},{\"key\":\"OA_Service_Date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"type\":\"date_picker\",\"hint\":\"Date of Service\",\"expanded\":false,\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date of service\"},\"value\":\"10-10-2019\"},{\"key\":\"Weight_Kg\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Child's weight (kg)\",\"value\":3.7,\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"}},{\"key\":\"Vaccines_Provided_Label\",\"type\":\"label\",\"text\":\"Which vaccinations were provided?\",\"openmrs_entity_parent\":\"-\",\"openmrs_entity\":\"-\",\"openmrs_entity_id\":\"-\"},{\"key\":\"Birth\",\"type\":\"check_box\",\"is_vaccine_group\":true,\"label\":\"Birth\",\"openmrs_entity_parent\":\"-\",\"openmrs_entity\":\"-\",\"openmrs_entity_id\":\"-\",\"options\":[{\"key\":\"OPV 0\",\"text\":\"OPV 0\",\"value\":true,\"constraints\":[{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Six_Wks, \\\"[\\\"OPV 1\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"},{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Ten_Wks, \\\"[\\\"OPV 2\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"},{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"OPV 3\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"}]},{\"key\":\"BCG\",\"text\":\"BCG\",\"value\":\"false\"},{\"key\":\"HepB\",\"text\":\"HepB\",\"value\":true}],\"value\":[\"OPV 0\",\"HepB\"]}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.14.5.0-SNAPSHOT\",\"formVersion\":\"\"}}";

    @Mock
    private Context opensrpContext;

    @Mock
    private android.content.Context context;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    private String locationId = "Test_Location_ID";

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        Mockito.doReturn("demo1").when(allSharedPreferences).fetchRegisteredANM();

    }


    @Test
    public void testGetRecordedWeight() throws JSONException, ParseException {

        Map<String, String> metadata = OutOfAreaServiceUtils.getOutOfAreaMetadata(new JSONObject(outOfAreaFormWithWeight));
        Weight weight = OutOfAreaServiceUtils.getRecordedWeight(opensrpContext, new JSONObject(outOfAreaFormWithWeight), locationId, metadata);

        Assert.assertNotNull(weight);
        Assert.assertEquals(Float.valueOf("3.7"), weight.getKg());
    }

    @Test
    public void testGetRecordedVaccines() throws Exception {

        PowerMockito.mockStatic(VaccinatorUtils.class);
        PowerMockito.when(VaccinatorUtils.getVaccineCalculation(ArgumentMatchers.eq(context), ArgumentMatchers.anyString())).thenReturn(4);

        Map<String, String> metadata = OutOfAreaServiceUtils.getOutOfAreaMetadata(new JSONObject(outOfAreaForm));
        List<Vaccine> vaccines = OutOfAreaServiceUtils.getRecordedVaccines(opensrpContext, new JSONObject(outOfAreaForm), locationId, metadata);
        Assert.assertNotNull(vaccines);
    }

    @Test
    public void testGetOutOfAreaMetadata() throws JSONException {

        Map<String, String> metadata = OutOfAreaServiceUtils.getOutOfAreaMetadata(new JSONObject(outOfAreaFormWithWeight));
        Assert.assertNotNull(metadata);
        Assert.assertEquals("10-10-2019", metadata.get(Constants.KEY.OA_SERVICE_DATE));
        Assert.assertEquals("3274343E", metadata.get(Constants.KEY.OPENSRP_ID));


        metadata = OutOfAreaServiceUtils.getOutOfAreaMetadata(new JSONObject(outOfAreaForm));
        Assert.assertNotNull(metadata);
        Assert.assertEquals("10-10-2017", metadata.get(Constants.KEY.OA_SERVICE_DATE));
        Assert.assertEquals("c_08626625555522222", metadata.get(Constants.KEY.NFC_CARD_IDENTIFIER));
    }
}