package org.smartregister.child.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
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
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.UniqueId;
import org.smartregister.domain.db.EventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ChildLibrary.class)
public class UtilsTest {
    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private UniqueIdRepository uniqueIdRepository;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private Context opensrpContext;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getNextOpenMrsId() {
        UniqueId uniqueId = new UniqueId();
        uniqueId.setId("1");
        uniqueId.setCreatedAt(new Date());
        uniqueId.setOpenmrsId("34334-9");
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        PowerMockito.when(childLibrary.getUniqueIdRepository()).thenReturn(uniqueIdRepository);
        PowerMockito.when(uniqueIdRepository.getNextUniqueId()).thenReturn(uniqueId);
        Assert.assertEquals(uniqueId.getOpenmrsId(), Utils.getNextOpenMrsId());

    }

    @Test
    public void getChildBirthDate() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FormEntityConstants.Person.birthdate.toString(), "2019-09-04T03:00:00.000+03:00");
        String expected = "2019-09-04";
        Assert.assertEquals(expected, Utils.getChildBirthDate(jsonObject));
    }

    @Test
    public void testFormatIdentifiersFormatsValueCorrectly() {
        String testString = "3929389829839829839835";
        String formattedIdentifier = Utils.formatIdentifiers(testString);
        Assert.assertEquals("3929-3898-2983-9829-8398-35", formattedIdentifier);
    }

    @Test
    public void testPutAll() {
        Map<String, String> map = new HashMap<>();
        map.put("1", "one");
        map.put("2", "two");

        Map<String, String> extend = new HashMap<>();
        extend.put("3", "three");
        extend.put("4", null);

        Utils.putAll(map, extend);
        Assert.assertEquals(3, map.size());
    }

    @Test
    public void testFormatNumber() {
        String s1 = Utils.formatNumber("1234W");
        String s2 = Utils.formatNumber("01234");
        String s3 = Utils.formatNumber(" 1234");

        Assert.assertEquals("1234", s1);
        Assert.assertEquals("1234", s2);
        Assert.assertEquals(" 1234", s3);
    }

    @Test
    public void testBold() {
        String s1 = Utils.bold("test");

        Assert.assertEquals("<b>test</b>", s1);
    }

    @Test
    public void testReverseHyphenatedString() {
        String s1 = Utils.reverseHyphenatedString("04-05-2020");

        Assert.assertEquals("2020-05-04", s1);
    }

    @Test
    public void testGetDate() {
        Date dt1 = Utils.getDate("2020-05-05 11:46:00");
        Date dt2 = Utils.getDate("20200505997782329");

        Assert.assertNotNull(dt1);
        Assert.assertNull(dt2);
    }

    @Test
    public void testFormatAtBirthKey() {
        String s1 = Utils.formatAtBirthKey("birth");
        String s2 = Utils.formatAtBirthKey("Birth");
        String s3 = Utils.formatAtBirthKey("test");

        Assert.assertEquals("at_birth", s1);
        Assert.assertEquals("at_Birth", s2);
        Assert.assertEquals("test", s3);
    }

    @Test
    public void testCreateArchiveRecordEventShouldCreateValidEvent() throws Exception {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);
        Mockito.doReturn(ecSyncHelper).when(childLibrary).getEcSyncHelper();
        Mockito.doReturn(opensrpContext).when(coreLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();
        Map<String, String> details = new HashMap<>();
        details.put(Constants.KEY.BASE_ENTITY_ID, "232-erer7");
        String baseEntityId = details.get(Constants.KEY.BASE_ENTITY_ID);
        Assert.assertNotNull(baseEntityId);
        Event result = Utils.createArchiveRecordEvent(baseEntityId);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getFormSubmissionId());
        Mockito.verify(ecSyncHelper, Mockito.times(1)).addEvent(Mockito.eq(baseEntityId), Mockito.any(JSONObject.class));
    }

    @Test
    public void testCreateArchiveRecordEventShouldCreateValidEvents() throws Exception {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);
        Mockito.doReturn(ecSyncHelper).when(childLibrary).getEcSyncHelper();
        Mockito.doReturn(opensrpContext).when(coreLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        Mockito.doReturn("demo").when(allSharedPreferences).fetchRegisteredANM();
        List<String> baseEntityIds = new ArrayList<>();
        baseEntityIds.add("231-erer7");
        baseEntityIds.add("232-erer7");
        List<Event> result = Utils.createArchiveRecordEvents(baseEntityIds);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 2);
        Mockito.verify(ecSyncHelper, Mockito.times(1)).addEvent(Mockito.eq("231-erer7"), Mockito.any(JSONObject.class));
        Mockito.verify(ecSyncHelper, Mockito.times(1)).addEvent(Mockito.eq("232-erer7"), Mockito.any(JSONObject.class));
    }

    @Test
    public void testInitiateEventProcessingShouldInitEventProcessing() throws Exception {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);
        ClientProcessorForJava clientProcessorForJava = Mockito.mock(ClientProcessorForJava.class);
        Mockito.doReturn(clientProcessorForJava).when(childLibrary).getClientProcessorForJava();
        Mockito.doReturn(ecSyncHelper).when(childLibrary).getEcSyncHelper();
        Mockito.doReturn(opensrpContext).when(coreLibrary).context();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        long now = new Date().getTime();
        Mockito.doReturn(now).when(allSharedPreferences).fetchLastUpdatedAtDate(0);

        List<String> list = Arrays.asList("233-sdsd");
        List<Event> eventList = new ArrayList<>();

        Utils.initiateEventProcessing(list);
        Mockito.doReturn(eventList).when(ecSyncHelper).getEvents(list);
        Mockito.verify(clientProcessorForJava, Mockito.times(1)).processClient(Mockito.<EventClient>anyList());
        Mockito.verify(allSharedPreferences).saveLastUpdatedAtDate(Mockito.eq(now));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }
}