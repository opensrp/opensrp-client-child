package org.smartregister.child.util;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
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
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AppProperties;
import org.smartregister.util.JsonFormUtils;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 10/10/2020.
 */

@PrepareForTest({VaccinatorUtils.class})
public class OutOfAreaServiceUtilsTest extends BasePowerMockUnitTest {

    private static final String outOfAreaForm = "{\"count\":\"1\",\"encounter_type\":\"Out of Catchment Service\",\"entity_id\":\"\",\"metadata\":{},\"step1\":{\"title\":\"Out of Area Service\",\"fields\":[{\"key\":\"nfc_card_identifier\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"Card Id \",\"value\":\"8626625555522222\",\"v_numeric\":{\"value\":\"true\",\"err\":\"The number must have a total of 16 digits\"},\"v_regex\":{\"value\":\"^$|([0-9]{16})\",\"err\":\"The number must have a total of 16 digits\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the card Id\"}},{\"key\":\"OA_Service_Date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"type\":\"date_picker\",\"hint\":\"Date of Service\",\"expanded\":false,\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date of service\"},\"value\":\"10-10-2017\"},{\"key\":\"Vaccines_Provided_Label\",\"type\":\"label\",\"text\":\"Which vaccinations were provided?\",\"openmrs_entity_parent\":\"-\",\"openmrs_entity\":\"-\",\"openmrs_entity_id\":\"-\"},{\"key\":\"Birth\",\"type\":\"check_box\",\"is_vaccine_group\":true,\"label\":\"Birth\",\"openmrs_entity_parent\":\"-\",\"openmrs_entity\":\"-\",\"openmrs_entity_id\":\"-\",\"options\":[{\"key\":\"OPV 0\",\"text\":\"OPV 0\",\"value\":true,\"constraints\":[{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Six_Wks, \\\"[\\\"OPV 1\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"},{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Ten_Wks, \\\"[\\\"OPV 2\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"},{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"OPV 3\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"}]},{\"key\":\"BCG\",\"text\":\"BCG\",\"value\":\"false\"},{\"key\":\"HepB\",\"text\":\"HepB\",\"value\":true}],\"value\":[\"OPV 0\",\"HepB\"]}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.14.5.0-SNAPSHOT\",\"formVersion\":\"\"}}";
    private static final String outOfAreaFormWithWeight = "{\"count\":\"1\",\"encounter_type\":\"Out of Catchment Service\",\"entity_id\":\"\",\"metadata\":{},\"step1\":{\"title\":\"Out of Area Service\",\"fields\":[{\"key\":\"opensrp_id\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"OpenSRP ID \",\"value\":\"3274343E\",\"v_regex\":{\"value\":\"^$|([0-9]{16})\",\"err\":\"The number must have a total of 16 digits\"},\"v_required\":{\"value\":\"true\",\"err\":\"Enter the card Id\"}},{\"key\":\"OA_Service_Date\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"type\":\"date_picker\",\"hint\":\"Date of Service\",\"expanded\":false,\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Enter the date of service\"},\"value\":\"10-10-2019\"},{\"key\":\"Weight_Kg\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"text\",\"type\":\"edit_text\",\"hint\":\"Child's weight (kg)\",\"value\":3.7,\"v_min\":{\"value\":\"0.1\",\"err\":\"Weight must be greater than 0\"},\"v_numeric\":{\"value\":\"true\",\"err\":\"Enter a valid weight\"}},{\"key\":\"Vaccines_Provided_Label\",\"type\":\"label\",\"text\":\"Which vaccinations were provided?\",\"openmrs_entity_parent\":\"-\",\"openmrs_entity\":\"-\",\"openmrs_entity_id\":\"-\"},{\"key\":\"Birth\",\"type\":\"check_box\",\"is_vaccine_group\":true,\"label\":\"Birth\",\"openmrs_entity_parent\":\"-\",\"openmrs_entity\":\"-\",\"openmrs_entity_id\":\"-\",\"options\":[{\"key\":\"OPV 0\",\"text\":\"OPV 0\",\"value\":true,\"constraints\":[{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Six_Wks, \\\"[\\\"OPV 1\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"},{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Ten_Wks, \\\"[\\\"OPV 2\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"},{\"type\":\"array\",\"ex\":\"notEqualTo(step1:Fourteen_Weeks, \\\"[\\\"OPV 3\\\"]\\\")\",\"err\":\"Cannot be given with the other OPV dose\"}]},{\"key\":\"BCG\",\"text\":\"BCG\",\"value\":\"false\"},{\"key\":\"HepB\",\"text\":\"HepB\",\"value\":true}],\"value\":[\"OPV 0\",\"HepB\"]}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.14.5.0-SNAPSHOT\",\"formVersion\":\"\"}}";
    private static final String servicesJson = "[{\"name\":\"Recurring Services\",\"id\":\"Recurring_Services\",\"services\":[{\"type\":\"Vit A\"},{\"type\":\"Deworming\"},{\"type\":\"ITN\"}]}]";

    @Mock
    private Context opensrpContext;

    @Mock
    private android.content.Context context;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private AppProperties appProperties;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private ECSyncHelper ecSyncHelper;

    private String locationId = "Test_Location_ID";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        Mockito.doReturn("demo1").when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(opensrpContext).when(coreLibrary).context();
        Mockito.doReturn(appProperties).when(opensrpContext).getAppProperties();
        Mockito.doReturn(true).when(appProperties).isTrue(ChildAppProperties.KEY.SHOW_OUT_OF_CATCHMENT_RECURRING_SERVICES);
        Mockito.doReturn(ecSyncHelper).when(childLibrary).getEcSyncHelper();
    }

    @Test
    public void testGetRecordedWeight() throws JSONException, ParseException {
        JSONObject outOfAreaFormJsonObject = new JSONObject(outOfAreaFormWithWeight);
        Map<String, String> metadata = OutOfAreaServiceUtils.getOutOfAreaMetadata(outOfAreaFormJsonObject);
        Weight weight = OutOfAreaServiceUtils.getRecordedWeight(opensrpContext, outOfAreaFormJsonObject, locationId, metadata);

        Assert.assertNotNull(weight);
        Assert.assertEquals(Float.valueOf("3.7"), weight.getKg());
    }

    @Test
    public void testGetRecordedVaccines() throws Exception {
        JSONObject outOfAreaFormJsonObject = new JSONObject(outOfAreaForm);

        PowerMockito.mockStatic(VaccinatorUtils.class);
        PowerMockito.when(VaccinatorUtils.getVaccineCalculation(ArgumentMatchers.eq(context), ArgumentMatchers.anyString())).thenReturn(4);

        Map<String, String> metadata = OutOfAreaServiceUtils.getOutOfAreaMetadata(outOfAreaFormJsonObject);
        List<Vaccine> vaccines = OutOfAreaServiceUtils.getRecordedVaccines(opensrpContext, outOfAreaFormJsonObject, locationId, metadata);
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

    @Test
    public void testCreateOutOfAreaRecurringServiceEventsDoesNothingWhenShowOutOfCatchmentServicesPropertyIsNotSet() {
        Mockito.doReturn(false).when(appProperties).isTrue(ChildAppProperties.KEY.SHOW_OUT_OF_CATCHMENT_RECURRING_SERVICES);

        OutOfAreaServiceUtils.createOutOfAreaRecurringServiceEvents(new JSONObject(), new HashMap<>());

        Event event = Mockito.mock(Event.class);
        Mockito.verify(ecSyncHelper, Mockito.never()).addEvent(ArgumentMatchers.anyString(), ArgumentMatchers.any(JSONObject.class), ArgumentMatchers.anyString());
    }

    @Test
    public void testCreateOutOfAreaRecurringServiceEventsDoesNothingWhenShowOutOfCatchmentServicesPropertyIsSetAndServiceDateIsEmpty() {
        Mockito.doReturn(true).when(appProperties).isTrue(ChildAppProperties.KEY.SHOW_OUT_OF_CATCHMENT_RECURRING_SERVICES);

        OutOfAreaServiceUtils.createOutOfAreaRecurringServiceEvents(new JSONObject(), new HashMap<>());

        Event event = Mockito.mock(Event.class);
        Mockito.verify(ecSyncHelper, Mockito.never()).addEvent(ArgumentMatchers.anyString(), ArgumentMatchers.any(JSONObject.class), ArgumentMatchers.anyString());
    }

    @Test
    public void testCreateOutOfAreaRecurringServiceEvents() throws JSONException {
        PowerMockito.mockStatic(VaccinatorUtils.class);

        JSONObject outOfAreaForm = new JSONObject(outOfAreaFormWithWeight);

        JSONObject recurringServiceQuestion = new JSONObject();
        recurringServiceQuestion.put(JsonFormUtils.KEY, Constants.KEY.RECURRING_SERVICE_TYPES);
        recurringServiceQuestion.put(JsonFormConstants.TYPE, JsonFormConstants.CHECK_BOX);
        recurringServiceQuestion.put(JsonFormConstants.LABEL, context.getString(R.string.recurring_services_provided));
        recurringServiceQuestion.put(JsonFormUtils.OPENMRS_ENTITY_PARENT, Constants.KEY.RECURRING_SERVICE_TYPES);
        recurringServiceQuestion.put(JsonFormUtils.OPENMRS_ENTITY, JsonFormUtils.CONCEPT);
        recurringServiceQuestion.put(JsonFormUtils.OPENMRS_ENTITY_ID, Constants.KEY.RECURRING_SERVICE_TYPES);

        JSONArray serviceArray = new JSONArray(servicesJson);
        JSONObject serviceJson = serviceArray.getJSONObject(0);
        JSONArray services = serviceJson.getJSONArray(Constants.JSON_FORM_KEY.SERVVICES);

        recurringServiceQuestion.put(JsonFormConstants.VALUE, services);

        JSONArray options = new JSONArray();
        if (serviceJson.has(Constants.JSON_FORM_KEY.SERVVICES)) {
            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);
                if (service.has(Constants.TYPE)) {
                    String serviceType = service.getString(Constants.TYPE);
                    String serviceKey = serviceType.replaceAll(" ", "_").toLowerCase();
                    JSONObject option = new JSONObject();
                    option.put(JsonFormConstants.KEY, serviceKey);
                    option.put(JsonFormConstants.TEXT, VaccinatorUtils.getTranslatedVaccineName(context, serviceType));
                    options.put(option);
                }
            }
        }

        if (options != null && options.length() > 0) {
            recurringServiceQuestion.put(JsonFormConstants.OPTIONS_FIELD_NAME, options);
            outOfAreaForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(Constants.FIELDS).put(recurringServiceQuestion);
        }

        Map<String, String> metadata = OutOfAreaServiceUtils.getOutOfAreaMetadata(new JSONObject(outOfAreaFormWithWeight));

        OutOfAreaServiceUtils.createOutOfAreaRecurringServiceEvents(outOfAreaForm, metadata);
        Mockito.verify(ecSyncHelper).addEvent(ArgumentMatchers.anyString(), ArgumentMatchers.any(JSONObject.class), ArgumentMatchers.anyString());
    }
}