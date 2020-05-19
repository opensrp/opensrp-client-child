package org.smartregister.child.util;

import android.content.ContentValues;
import android.os.Build;
import android.telephony.TelephonyManager;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.growthmonitoring.BuildConfig;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.O_MR1)
public class JsonFormUtilsTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private Context opensrpContext;

    @Mock
    private android.content.Context context;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private EventClientRepository eventClientRepository;

    private String reportDeceasedForm = "{\"count\":\"1\",\"encounter_type\":\"Death\",\"entity_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-05-19 10:26:41\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-05-19 10:27:18\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"19-05-2020\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"\"},\"step1\":{\"title\":\"Report Deceased\",\"fields\":[{\"key\":\"Date_of_Death\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1543AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"date\",\"type\":\"date_picker\",\"hint\":\"Date of death \",\"expanded\":false,\"min_date\":\"19-05-2020\",\"max_date\":\"today\",\"v_required\":{\"value\":\"true\",\"err\":\"Date cannot be past today's date\"},\"constraints\":[{\"type\":\"date\",\"ex\":\"greaterThanEqualTo(., step1:Date_Birth)\",\"err\":\"Date of death can't occur before date of birth\"}],\"is-rule-check\":false,\"value\":\"19-05-2020\"},{\"key\":\"Cause_Death\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"160218AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Suspected cause of death\",\"edit_type\":\"name\",\"value\":\"something terrible\"},{\"key\":\"Place_Death\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1541AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_data_type\":\"select one\",\"type\":\"spinner\",\"hint\":\"Where did the death occur? \",\"values\":[\"Health facility\",\"Home\"],\"openmrs_choice_ids\":{\"Health facility\":\"1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"Home\":\"1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please select one option\"},\"value\":\"Home\"},{\"key\":\"Date_Birth\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"date_picker\",\"hint\":\"Child's DOB\",\"read_only\":true,\"hidden\":true,\"is_visible\":false,\"is-rule-check\":false,\"value\":\"19-05-2020\"}]},\"invisible_required_fields\":\"[]\",\"details\":{\"appVersionName\":\"1.8.1-SNAPSHOT\",\"formVersion\":\"\"}}";

    private String childRegistrationClient = "{\"firstName\":\"Doe\",\"middleName\":\"Jane\",\"lastName\":\"Jane\",\"birthdate\":\"2019-07-02T02:00:00.000+02:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{\"mother\":[\"bdf50ebc-c352-421c-985d-9e9880d9ec58\",\"bdf50ebc-c352-421c-985d-9e9880d9ec58\"]},\"baseEntityId\":\"c4badbf0-89d4-40b9-8c37-68b0371797ed\",\"identifiers\":{\"zeir_id\":\"14750004\"},\"addresses\":[{\"addressType\":\"usual_residence\",\"addressFields\":{\"address5\":\"Not sure\"}}],\"attributes\":{\"age\":\"0.0\",\"Birth_Certificate\":\"ADG\\/23652432\\/1234\",\"second_phone_number\":\"0972343243\"},\"dateCreated\":\"2019-07-02T15:42:57.838+02:00\",\"serverVersion\":1562074977828,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":1,\"type\":\"Client\",\"id\":\"b8798571-dee6-43b5-a289-fc75ab703792\",\"revision\":\"v1\"}";

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


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveReportDeceasedShouldPassCorrectArguments() throws JSONException {
        String entityId = "b8798571-dee6-43b5-a289-fc75ab703792";
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, true, new RegisterQueryProvider());
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
        Mockito.when(context.getSystemService(android.content.Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        JsonFormUtils.saveReportDeceased(context, reportDeceasedForm, "434-2342", entityId);

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
}