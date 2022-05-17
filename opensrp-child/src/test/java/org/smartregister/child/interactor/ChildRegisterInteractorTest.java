package org.smartregister.child.interactor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.smartregister.child.util.ChildAppProperties.KEY.TETANUS_VACCINE_SYNC_STATUS_UN_SYNCED;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.JsonFormAssetsUtils;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.util.AppExecutors;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.UniqueId;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AppProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-21
 */

public class ChildRegisterInteractorTest extends BaseUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    private ChildRegisterInteractor interactor;
    private String jsonEnrollmentForm = JsonFormAssetsUtils.childEnrollmentJsonForm;
    private String childRegistrationClient = "{\"firstName\":\"Doe\",\"middleName\":\"Jane\",\"lastName\":\"Jane\",\"birthdate\":\"2019-07-02T02:00:00.000+02:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{\"mother\":[\"bdf50ebc-c352-421c-985d-9e9880d9ec58\",\"bdf50ebc-c352-421c-985d-9e9880d9ec58\"]},\"baseEntityId\":\"c4badbf0-89d4-40b9-8c37-68b0371797ed\",\"identifiers\":{\"zeir_id\":\"14750004\"},\"addresses\":[{\"addressType\":\"usual_residence\",\"addressFields\":{\"address5\":\"Not sure\"}}],\"attributes\":{\"age\":\"0.0\",\"Birth_Certificate\":\"ADG\\/23652432\\/1234\",\"second_phone_number\":\"0972343243\"},\"dateCreated\":\"2019-07-02T15:42:57.838+02:00\",\"serverVersion\":1562074977828,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":1,\"type\":\"Client\",\"id\":\"b8798571-dee6-43b5-a289-fc75ab703792\",\"revision\":\"v1\"}";

    @Mock
    private AppProperties appProperties;

    @Mock
    private AppExecutors appExecutors;

    @Captor
    private ArgumentCaptor syncHelperAddClientArgumentCaptor;

    @Captor
    private ArgumentCaptor syncHelperAddEventArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = new ChildRegisterInteractor(appExecutors);
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(GrowthMonitoringLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);
    }

    @Test
    public void processWeightShouldNotProcessWeightIfIdentifierContainsMzeirId() throws JSONException {

        HashMap<String, String> identifiers = new HashMap<>();
        identifiers.put("M_ZEIR_ID", "9029393");

        UpdateRegisterParams updateRegisterParam = new UpdateRegisterParams();
        updateRegisterParam.setEditMode(false);

        FormTag formTag = new FormTag();
        formTag.providerId = "provider-id";
        formTag.appVersion = 21;
        formTag.databaseVersion = 3;

        updateRegisterParam.setFormTag(formTag);


        String womanRegistrationClient = "{\"firstName\":\"Mary\",\"lastName\":\"Janostri\",\"birthdate\":\"2009-06-20T02:00:00.000+02:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"baseEntityId\":\"f2f5dfb6-5110-42f6-88bb-951a070f5df2\",\"identifiers\":{\"M_ZEIR_ID\":\"14656508_mother\"},\"addresses\":[],\"attributes\":{},\"dateCreated\":\"2019-06-24T12:45:44.100+02:00\",\"dateEdited\":\"2019-06-25T10:23:10.491+02:00\",\"serverVersion\":1561451012837,\"type\":\"Client\",\"id\":\"703652b4-3516-49a2-80f8-2ace440e4fad\",\"revision\":\"v3\"}";
        JSONObject clientJson = Mockito.spy(new JSONObject(womanRegistrationClient));
        interactor.processWeight(identifiers, jsonEnrollmentForm, updateRegisterParam, clientJson);
        Mockito.verify(clientJson, Mockito.times(0))
                .getString(eq(FormEntityConstants.Person.gender.name()));
    }

    @Test
    public void processWeightShouldProcessWeightIfIdentifierDoesNotContainsMzeirId() throws Exception {
        HashMap<String, String> identifiers = new HashMap<>();
        identifiers.put("ZEIR_ID", "9029393");

        UpdateRegisterParams updateRegisterParam = new UpdateRegisterParams();
        updateRegisterParam.setEditMode(false);


        FormTag formTag = new FormTag();
        formTag.providerId = "provider-id";
        formTag.appVersion = 21;
        formTag.databaseVersion = 3;

        updateRegisterParam.setFormTag(formTag);
        GrowthMonitoringLibrary growthMonitoringLibrary = Mockito.mock(GrowthMonitoringLibrary.class);


        ReflectionHelpers.setStaticField(GrowthMonitoringLibrary.class, "instance", growthMonitoringLibrary);
        Mockito.doReturn(Mockito.mock(WeightRepository.class)).when(growthMonitoringLibrary).weightRepository();

        // Mock this call ChildLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM()
        ChildLibrary childLibrary = Mockito.mock(ChildLibrary.class);
        Context context = Mockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        Mockito.doReturn(appProperties).when(childLibrary).getProperties();

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        CoreLibrary coreLibrary = Mockito.mock(CoreLibrary.class);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        Mockito.doReturn(context).when(coreLibrary).context();
        Mockito.doReturn(context).when(childLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();
        Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();

        JSONObject clientJson = Mockito.spy(new JSONObject(childRegistrationClient));
        interactor.processWeight(identifiers, jsonEnrollmentForm, updateRegisterParam, clientJson);
        Mockito.verify(clientJson, Mockito.times(1))
                .getString(eq(FormEntityConstants.Person.gender.name()));
    }

    @Test
    public void isClientMotherShouldReturnFalseIfIdentifiersDoNotContainMzeirId() {
        HashMap<String, String> identifiers = new HashMap<>();
        identifiers.put("ZEIR_ID", "9029393");

        Assert.assertFalse(interactor.isClientMother(identifiers));
    }

    @Test
    public void isClientMotherShouldReturnTrueIfIdentifiersContainsMzeirId() {
        HashMap<String, String> identifiers = new HashMap<>();
        identifiers.put("M_ZEIR_ID", "9029393");

        Assert.assertTrue(interactor.isClientMother(identifiers));
    }

    @Test
    public void testSaveRegistrationShouldPassCorrectArguments() {
        ChildRegisterInteractor childRegisterInteractor = Mockito.spy(interactor);
        String baseEntityId = "234-24";
        Client client = new Client(baseEntityId);
        client.addIdentifier(ChildJsonFormUtils.ZEIR_ID, "7899");
        Event event = new Event();
        event.setBaseEntityId(baseEntityId);
        event.setFormSubmissionId("3422-90");
        event.setEntityType(Constants.CHILD_TYPE);
        ChildEventClient childEventClient = new ChildEventClient(client, event);
        UpdateRegisterParams params = new UpdateRegisterParams();
        params.setEditMode(false);
        List<ChildEventClient> childEventClientList = new ArrayList<>();
        childEventClientList.add(childEventClient);
        ClientProcessorForJava clientProcessorForJava = Mockito.mock(ClientProcessorForJava.class);
        Mockito.doReturn(clientProcessorForJava).when(childRegisterInteractor).getClientProcessorForJava();
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        Mockito.when(allSharedPreferences.fetchLastUpdatedAtDate(0)).thenReturn(1589270584000l);
        Mockito.doReturn(allSharedPreferences).when(childRegisterInteractor).getAllSharedPreferences();
        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);
        Mockito.doReturn(ecSyncHelper).when(childRegisterInteractor).getSyncHelper();
        UniqueIdRepository uniqueIdRepository = Mockito.mock(UniqueIdRepository.class);
        Mockito.doReturn(uniqueIdRepository).when(childRegisterInteractor).getUniqueIdRepository();
        Mockito.doReturn(appProperties).when(childRegisterInteractor).getAppProperties();
        Mockito.doReturn(true).when(appProperties).getPropertyBoolean(ChildAppProperties.KEY.MONITOR_HEIGHT);

        childRegisterInteractor.saveRegistration(childEventClientList, childRegistrationClient, params);

        Mockito.verify(ecSyncHelper).addClient((String) syncHelperAddClientArgumentCaptor.capture(), (JSONObject) syncHelperAddClientArgumentCaptor.capture());
        Mockito.verify(ecSyncHelper).addEvent((String) syncHelperAddEventArgumentCaptor.capture(), (JSONObject) syncHelperAddEventArgumentCaptor.capture(), (String) syncHelperAddEventArgumentCaptor.capture());
        Assert.assertNotNull(syncHelperAddClientArgumentCaptor.getAllValues().get(0));
        String resultBaseEntityId = (String) syncHelperAddClientArgumentCaptor.getAllValues().get(0);
        Assert.assertEquals(client.getBaseEntityId(), resultBaseEntityId);
        Assert.assertNotNull(syncHelperAddClientArgumentCaptor.getAllValues().get(1));
        JSONObject resultChildJson = (JSONObject) syncHelperAddClientArgumentCaptor.getAllValues().get(1);
        String expected = ChildJsonFormUtils.gson.toJson(client);
        Assert.assertEquals(expected, resultChildJson.toString());
        Assert.assertNotNull(syncHelperAddEventArgumentCaptor.getAllValues().get(0));
        Assert.assertEquals(event.getBaseEntityId(), syncHelperAddEventArgumentCaptor.getAllValues().get(0));
        Assert.assertNotNull(syncHelperAddEventArgumentCaptor.getAllValues().get(1));
        JSONObject resultEventJson = (JSONObject) syncHelperAddEventArgumentCaptor.getAllValues().get(1);
        Assert.assertEquals(event.getFormSubmissionId(), resultEventJson.optString("formSubmissionId"));
    }

    @Test
    public void testGetNextUniqueIdShouldCallOnNoUniqueIdWhenNoUniqueId() {
        interactor = Mockito.spy(interactor);
        Triple<String, Map<String, String>, String> triple = Triple.of("", new HashMap<>(), "");
        ChildRegisterContract.InteractorCallBack callBack = Mockito.mock(ChildRegisterContract.InteractorCallBack.class);
        Executor executor = Mockito.mock(Executor.class);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(Mockito.any(Runnable.class));
        Mockito.when(appExecutors.diskIO()).thenReturn(executor);
        Mockito.when(appExecutors.mainThread()).thenReturn(executor);
        UniqueIdRepository uniqueIdRepository = Mockito.mock(UniqueIdRepository.class);
        Mockito.when(uniqueIdRepository.getNextUniqueId())
                .thenReturn(null);
        Mockito.doReturn(uniqueIdRepository)
                .when(interactor)
                .getUniqueIdRepository();

        interactor.getNextUniqueId(triple, callBack);
        Mockito.verify(callBack).onNoUniqueId();
    }

    @Test
    public void testGetNextUniqueIdShouldCallOnUniqueIdFetched() {
        interactor = Mockito.spy(interactor);
        Triple<String, Map<String, String>, String> triple = Triple.of("", new HashMap<>(), "");
        ChildRegisterContract.InteractorCallBack callBack = Mockito.mock(ChildRegisterContract.InteractorCallBack.class);
        Executor executor = Mockito.mock(Executor.class);
        Mockito.doAnswer((Answer<Void>) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(Mockito.any(Runnable.class));
        Mockito.when(appExecutors.diskIO()).thenReturn(executor);
        Mockito.when(appExecutors.mainThread()).thenReturn(executor);
        UniqueIdRepository uniqueIdRepository = Mockito.mock(UniqueIdRepository.class);
        Mockito.doReturn(uniqueIdRepository)
                .when(interactor)
                .getUniqueIdRepository();

        UniqueId uniqueId = Mockito.mock(UniqueId.class);
        String entityId = "fake_entity_id";
        Mockito.when(uniqueId.getOpenmrsId())
                .thenReturn(entityId);
        Mockito.when(uniqueIdRepository.getNextUniqueId())
                .thenReturn(uniqueId);
        interactor.getNextUniqueId(triple, callBack);
        Mockito.verify(callBack).onUniqueIdFetched(eq(triple), eq(entityId));
    }

    @Test
    public void testProcessTetanusVaccineSavesVaccineObjectInDb() throws Exception {
        String jsonForm = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"step1\":{\"title\":\"Child Registration\",\"fields\":[{\"key\":\"Birth_Tetanus_Protection\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"164826AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"spinner\",\"hint\":\"Neonatal Tetanus Protection\",\"values\":[\"Yes\",\"No\",\"Don't Know\"],\"keys\":[\"Yes\",\"No\",\"DoNotKnow\"],\"openmrs_choice_ids\":{\"Yes\":\"1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"No\":\"1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"DoNotKnow\":\"1067AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter whether the child was protected at birth against tetanus\"},\"value\":\"Yes\"}]}}";

        HashMap<String, String> identifiers = new HashMap<>();

        UpdateRegisterParams updateRegisterParam = new UpdateRegisterParams();
        updateRegisterParam.setEditMode(false);

        FormTag formTag = new FormTag();
        formTag.providerId = "provider-id";
        formTag.appVersion = 21;
        formTag.databaseVersion = 3;

        updateRegisterParam.setFormTag(formTag);

        LocationHelper locationHelper = Mockito.mock(LocationHelper.class);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        JSONObject clientJson = Mockito.spy(new JSONObject(childRegistrationClient));
        VaccineRepository vaccineRepositorySpy = Mockito.spy(ImmunizationLibrary.getInstance().vaccineRepository());
        Mockito.when(vaccineRepositorySpy.findByBaseEntityIdAndVaccineName(anyString(), anyString())).thenReturn(null);
        ReflectionHelpers.setField(ImmunizationLibrary.getInstance(), "vaccineRepository", vaccineRepositorySpy);

        interactor.processTetanus(identifiers, jsonForm, updateRegisterParam, clientJson);
        Mockito.verify(vaccineRepositorySpy, Mockito.times(1)).add(Mockito.any());

    }

    @Test
    public void getTetanusVaccineObjectHasSyncStatusUnSyncedWhenAppPropertyIsTrue() throws JSONException {
        ChildLibrary childLibrary = Mockito.mock(ChildLibrary.class);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        AppProperties appProperties = Mockito.mock(AppProperties.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        Context context = Mockito.mock(Context.class);
        Mockito.when(childLibrary.getProperties()).thenReturn(appProperties);
        Mockito.when(childLibrary.context()).thenReturn(context);
        Mockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("");
        Mockito.when(appProperties.isTrue(eq(TETANUS_VACCINE_SYNC_STATUS_UN_SYNCED))).thenReturn(true);

        LocationHelper locationHelper = Mockito.mock(LocationHelper.class);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        JSONObject clientJson = Mockito.spy(new JSONObject(childRegistrationClient));
        Vaccine vaccine = interactor.getTetanusVaccineObject(clientJson);
        Assert.assertEquals(VaccineRepository.TYPE_Unsynced, vaccine.getSyncStatus());
    }

    @Test
    public void getTetanusVaccineObjectHasSyncStatusSyncedWhenAppPropertyIsFalse() throws JSONException {
        ChildLibrary childLibrary = Mockito.mock(ChildLibrary.class);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        AppProperties appProperties = Mockito.mock(AppProperties.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        Context context = Mockito.mock(Context.class);
        Mockito.when(childLibrary.getProperties()).thenReturn(appProperties);
        Mockito.when(childLibrary.context()).thenReturn(context);
        Mockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("");
        Mockito.when(appProperties.isTrue(eq(TETANUS_VACCINE_SYNC_STATUS_UN_SYNCED))).thenReturn(false);

        LocationHelper locationHelper = Mockito.mock(LocationHelper.class);
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);

        JSONObject clientJson = Mockito.spy(new JSONObject(childRegistrationClient));
        Vaccine vaccine = interactor.getTetanusVaccineObject(clientJson);
        Assert.assertEquals(VaccineRepository.TYPE_Synced, vaccine.getSyncStatus());
    }

}