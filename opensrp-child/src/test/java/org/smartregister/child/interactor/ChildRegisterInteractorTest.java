package org.smartregister.child.interactor;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.JsonFormAssetsUtils;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.util.AppExecutors;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.growthmonitoring.BuildConfig;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.repository.AllSharedPreferences;

import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-21
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.O_MR1)
public class ChildRegisterInteractorTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    private ChildRegisterInteractor interactor;
    private String jsonEnrollmentForm = JsonFormAssetsUtils.childEnrollmentJsonForm;
    private String womanRegistrationClient = "{\"firstName\":\"Mary\",\"lastName\":\"Janostri\",\"birthdate\":\"2009-06-20T02:00:00.000+02:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"baseEntityId\":\"f2f5dfb6-5110-42f6-88bb-951a070f5df2\",\"identifiers\":{\"M_ZEIR_ID\":\"14656508_mother\"},\"addresses\":[],\"attributes\":{},\"dateCreated\":\"2019-06-24T12:45:44.100+02:00\",\"dateEdited\":\"2019-06-25T10:23:10.491+02:00\",\"serverVersion\":1561451012837,\"type\":\"Client\",\"id\":\"703652b4-3516-49a2-80f8-2ace440e4fad\",\"revision\":\"v3\"}";
    private String childRegistrationClient = "{\"firstName\":\"Doe\",\"middleName\":\"Jane\",\"lastName\":\"Jane\",\"birthdate\":\"2019-07-02T02:00:00.000+02:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Female\",\"relationships\":{\"mother\":[\"bdf50ebc-c352-421c-985d-9e9880d9ec58\",\"bdf50ebc-c352-421c-985d-9e9880d9ec58\"]},\"baseEntityId\":\"c4badbf0-89d4-40b9-8c37-68b0371797ed\",\"identifiers\":{\"zeir_id\":\"14750004\"},\"addresses\":[{\"addressType\":\"usual_residence\",\"addressFields\":{\"address5\":\"Not sure\"}}],\"attributes\":{\"age\":\"0.0\",\"Birth_Certificate\":\"ADG\\/23652432\\/1234\",\"second_phone_number\":\"0972343243\"},\"dateCreated\":\"2019-07-02T15:42:57.838+02:00\",\"serverVersion\":1562074977828,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":1,\"type\":\"Client\",\"id\":\"b8798571-dee6-43b5-a289-fc75ab703792\",\"revision\":\"v1\"}";

    @Before
    public void setUp() throws Exception {
        interactor = new ChildRegisterInteractor(Mockito.mock(AppExecutors.class));
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


        JSONObject clientJson = Mockito.spy(new JSONObject(womanRegistrationClient));
        interactor.processWeight(identifiers, jsonEnrollmentForm, updateRegisterParam, clientJson);
        Mockito.verify(clientJson, Mockito.times(0))
                .getString(Mockito.eq(FormEntityConstants.Person.gender.name()));
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
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        Mockito.doReturn(context).when(childLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(context).allSharedPreferences();
        Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();

        JSONObject clientJson = Mockito.spy(new JSONObject(childRegistrationClient));
        interactor.processWeight(identifiers, jsonEnrollmentForm, updateRegisterParam, clientJson);
        Mockito.verify(clientJson, Mockito.times(1))
                .getString(Mockito.eq(FormEntityConstants.Person.gender.name()));
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
}