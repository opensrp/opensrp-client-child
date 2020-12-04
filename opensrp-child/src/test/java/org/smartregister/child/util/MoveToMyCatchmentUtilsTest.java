package org.smartregister.child.util;


import androidx.core.util.Pair;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.SyncConfiguration;
import org.smartregister.SyncFilter;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.MoveToCatchmentEvent;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.immunization.service.intent.VaccineIntentService;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.service.UserService;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.CredentialsHelper;

import java.util.List;

import edu.emory.mathcs.backport.java.util.Arrays;

@PrepareForTest({ChildJsonFormUtils.class, ChildLibrary.class, CoreLibrary.class, CredentialsHelper.class})
public class MoveToMyCatchmentUtilsTest extends BasePowerMockUnitTest {

    @Mock
    private Context context;

    @Mock
    private ChildLibrary childLibrary;

    private ECSyncHelper ecSyncHelper;

    @Mock
    private DristhiConfiguration configuration;

    @Mock
    private HTTPAgent httpAgent;

    @Mock
    private UserService userService;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    private static final String TEST_BASE_URL = "http://test-smartregister.com/";

    @Before
    public void setUp() throws JSONException {

        MockitoAnnotations.initMocks(this);


        Mockito.doReturn(userService).when(context).userService();
        Mockito.doReturn(allSharedPreferences).when(userService).getAllSharedPreferences();

        Mockito.doReturn(TEST_BASE_URL).when(configuration).dristhiBaseURL();
        Mockito.doReturn(configuration).when(context).configuration();

        JSONObject jsonObject = Mockito.spy(JSONObject.class);
        jsonObject.put(Constants.NO_OF_EVENTS, 20);

        Response<String> response = new Response<>(ResponseStatus.success, jsonObject.toString()).withTotalRecords(20L);
        Mockito.doReturn(response).when(httpAgent).fetch(ArgumentMatchers.anyString());
        Mockito.doReturn(httpAgent).when(context).getHttpAgent();

        PowerMockito.mockStatic(CredentialsHelper.class);
        PowerMockito.when(CredentialsHelper.shouldMigrate()).thenReturn(false);

        SyncConfiguration syncConfiguration = Mockito.mock(SyncConfiguration.class);
        Mockito.doReturn(SyncFilter.LOCATION).when(syncConfiguration).getEncryptionParam();
        CoreLibrary.init(context, syncConfiguration);

        ecSyncHelper = Mockito.mock(ECSyncHelper.class, Mockito.CALLS_REAL_METHODS);

        ReflectionHelpers.setField(ecSyncHelper, "eventClientRepository", new EventClientRepository());
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
        ReflectionHelpers.setField(ecSyncHelper, "eventClientRepository", null);
    }

    @Test
    public void testCreateMoveToCatchmentEventMakesValidServerRequest() throws JSONException {

        @Nullable MoveToCatchmentEvent moveToCatchmentEvent = MoveToMyCatchmentUtils.createMoveToCatchmentEvent(Arrays.asList(new String[]{"843-34-343-3", "0333-34-00099-1"}), true, true);
        Assert.assertNotNull(moveToCatchmentEvent);
        Assert.assertEquals(moveToCatchmentEvent.getJsonObject().getInt(Constants.NO_OF_EVENTS), 20);
        Assert.assertTrue(moveToCatchmentEvent.isCreateEvent());
        Assert.assertTrue(moveToCatchmentEvent.isPermanent());

        String expectedRequestURL = "http://test-smartregister.com//rest/event/sync?baseEntityId=843-34-343-3%2C0333-34-00099-1&limit=1000";

        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(CoreLibrary.getInstance().context().getHttpAgent()).fetch(urlArgumentCaptor.capture());

        String capturedURLString = urlArgumentCaptor.getValue();
        Assert.assertNotNull(capturedURLString);
        Assert.assertEquals(expectedRequestURL, capturedURLString);

    }

    @Test
    public void testCreateMoveToCatchmentEventReturnsNullWhenNoIds() {

        @Nullable MoveToCatchmentEvent moveToCatchmentEvent = MoveToMyCatchmentUtils.createMoveToCatchmentEvent(Arrays.asList(new String[]{}), true, true);
        Assert.assertNull(moveToCatchmentEvent);

        moveToCatchmentEvent = MoveToMyCatchmentUtils.createMoveToCatchmentEvent(null, true, true);
        Assert.assertNull(moveToCatchmentEvent);
    }

    @Test
    public void testCreateEventList() throws JSONException {
        JSONArray jsonArray = new JSONArray();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "Event");
        jsonObject.put("eventType", Constants.EventType.BITRH_REGISTRATION);
        jsonObject.put("entityType", "child");
        jsonObject.put("baseEntityId", "2q32q3-3dq23-23-s3ew2");
        jsonObject.put("serverVersion", 2323232L);
        jsonArray.put(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("type", "Event");
        jsonObject.put("eventType", Constants.EventType.NEW_WOMAN_REGISTRATION);
        jsonObject.put("entityType", "mother");
        jsonObject.put("baseEntityId", "4k4kab6-3qass3k1-2acab");
        jsonObject.put("serverVersion", 8939333L);
        jsonArray.put(jsonObject);

        jsonObject = new JSONObject();
        jsonObject.put("type", "Event");
        jsonObject.put("eventType", VaccineIntentService.EVENT_TYPE);
        jsonObject.put("entityType", "child");
        jsonObject.put("baseEntityId", "d3f6-343ss-lfj90-aaah34");
        jsonObject.put("serverVersion", 6387389L);
        jsonArray.put(jsonObject);

        List<Pair<Event, JSONObject>> pairList = MoveToMyCatchmentUtils.createEventList(ecSyncHelper, jsonArray);
        Assert.assertNotNull(pairList);
        Assert.assertEquals(3, pairList.size());
        Assert.assertEquals(Constants.EventType.BITRH_REGISTRATION, pairList.get(0).first.getEventType());
        Assert.assertEquals(Constants.EventType.NEW_WOMAN_REGISTRATION, pairList.get(1).first.getEventType());
        Assert.assertEquals(VaccineIntentService.EVENT_TYPE, pairList.get(2).first.getEventType());
    }
}
