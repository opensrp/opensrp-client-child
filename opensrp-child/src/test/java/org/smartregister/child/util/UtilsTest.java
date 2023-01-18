package org.smartregister.child.util;

import android.content.res.Resources;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.api.constants.Gender;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.UniqueId;
import org.smartregister.domain.db.EventClient;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.sync.helper.ECSyncHelper;
import org.smartregister.util.AppProperties;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ChildLibrary.class, Utils.class, LocationHelper.class})
public class UtilsTest {
    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private UniqueIdRepository uniqueIdRepository;

    @Mock
    private android.content.Context context;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private Context opensrpContext;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private AppProperties appProperties;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.when(context.getApplicationContext()).thenReturn(context);
        PowerMockito.doReturn(appProperties).when(childLibrary).getProperties();
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
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);
        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);
        Mockito.doReturn(ecSyncHelper).when(childLibrary).getEcSyncHelper();
        Mockito.doReturn(opensrpContext).when(childLibrary).context();
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
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", locationHelper);
        ECSyncHelper ecSyncHelper = Mockito.mock(ECSyncHelper.class);
        Mockito.doReturn(ecSyncHelper).when(childLibrary).getEcSyncHelper();
        Mockito.doReturn(opensrpContext).when(childLibrary).context();
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
        CoreLibrary.destroyInstance();
        ChildLibrary.destroyInstance();
        ReflectionHelpers.setStaticField(LocationHelper.class, "instance", null);
    }


    public void testGetProfileImageResourceIDentifier() {
        int i = Utils.getProfileImageResourceIDentifier();
        Assert.assertEquals(R.mipmap.ic_child, i);
    }

    @Test
    public void testAddAsInts() {
        int i = Utils.addAsInts(true, "10", "", "12");
        Assert.assertEquals(22, i);
    }

    @Test
    public void testGetTodaysDate() {
        String date1 = Utils.getTodaysDate();
        String date2 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        Assert.assertEquals(date2, date1);
    }

    @Test
    public void testContext() {
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        Context context = Utils.context();
        Assert.assertEquals(childLibrary.context(), context);
    }

    @Test
    public void testGetWeeksDue() {
        int i1 = Utils.getWeeksDue(new DateTime());
        int i2 = Utils.getWeeksDue(new DateTime().plusMinutes(1).plusWeeks(1));
        int i3 = Utils.getWeeksDue(new DateTime().plusMinutes(1).plusWeeks(2));

        Assert.assertEquals(0, i1);
        Assert.assertEquals(1, i2);
        Assert.assertEquals(2, i3);
    }

    @Test
    public void testGetDataRow() {
        TableRow tr = Utils.getDataRow(context);
        Assert.assertEquals(0, tr.getPaddingTop());
    }

    @Test
    public void testGetDataRow2() throws Exception {
        TableRow tableRow = PowerMockito.mock(TableRow.class);
        TextView textView = PowerMockito.mock(TextView.class);

        PowerMockito.whenNew(TableRow.class).withArguments(context).thenReturn(tableRow);
        PowerMockito.whenNew(TextView.class).withArguments(context).thenReturn(textView);

        PowerMockito.when(tableRow.getPaddingTop()).thenReturn(10);
        PowerMockito.when(tableRow.getPaddingBottom()).thenReturn(5);
        PowerMockito.when(textView.getTextSize()).thenReturn(14F);

        TableRow tr = Utils.getDataRow(context, "label", "value", null);

        Assert.assertEquals(10, tr.getPaddingTop());
        Assert.assertEquals(5, tr.getPaddingBottom());
        Assert.assertEquals(14F, textView.getTextSize(), 0);
    }

    @Test
    public void testGetDataRow3() throws Exception {
        TableRow tableRow = PowerMockito.mock(TableRow.class);
        EditText editText = PowerMockito.mock(EditText.class);

        PowerMockito.whenNew(TableRow.class).withArguments(context).thenReturn(tableRow);
        PowerMockito.whenNew(EditText.class).withArguments(context).thenReturn(editText);

        PowerMockito.when(tableRow.getPaddingTop()).thenReturn(20);
        PowerMockito.when(tableRow.getPaddingBottom()).thenReturn(15);
        PowerMockito.when(editText.getCurrentTextColor()).thenReturn(Color.BLACK);

        TableRow tr = Utils.getDataRow(context, "label", "value", "field", null);

        Assert.assertEquals(20, tr.getPaddingTop());
        Assert.assertEquals(15, tr.getPaddingBottom());
        Assert.assertEquals(Color.BLACK, editText.getCurrentTextColor());
    }

    @Test
    public void testGetCleanMap() {
        Map<String, String> map1 = new HashMap<>();
        map1.put("1", "one");
        map1.put("2", "two");
        map1.put("3", "null");
        map1.put("4", "four");

        Map<String, String> map2 = Utils.getCleanMap(map1);

        Assert.assertEquals(3, map2.size());
    }

    @Test
    public void testUpdateFTSForCombinedVaccineAlternativesShouldPassCorrectValues() {
        ImmunizationLibrary immunizationLibrary = Mockito.mock(ImmunizationLibrary.class);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", immunizationLibrary);
        ImmunizationLibrary.COMBINED_VACCINES_MAP.put("opv", "opv1/pcv1");
        VaccineRepository vaccineRepository = Mockito.mock(VaccineRepository.class);
        Vaccine vaccine = new Vaccine();
        vaccine.setName("opv");
        Vaccine vaccineOpv1 = new Vaccine();
        vaccineOpv1.setName("opv1");
        Vaccine vaccinePcv1 = new Vaccine();
        vaccinePcv1.setName("pcv1");
        Utils.updateFTSForCombinedVaccineAlternatives(vaccineRepository, vaccine);
        Mockito.verify(vaccineRepository, Mockito.times(1)).updateFtsSearch(ArgumentMatchers.refEq(vaccineOpv1));
        Mockito.verify(vaccineRepository, Mockito.times(1)).updateFtsSearch(ArgumentMatchers.refEq(vaccinePcv1));
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", null);
    }

    @Test
    public void testGetTranslatedIdentifier() {
        Context opensrpContext = Mockito.mock(Context.class);
        Resources resources = Mockito.mock(Resources.class);
        android.content.Context context = Mockito.mock(android.content.Context.class);
        Mockito.when(context.getResources()).thenReturn(resources);
        Mockito.when(context.getString(Mockito.anyInt())).thenReturn("testValue");
        Mockito.when(opensrpContext.applicationContext()).thenReturn(context);
        Mockito.when(childLibrary.context()).thenReturn(opensrpContext);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        String result = Utils.getTranslatedIdentifier("testKey");
        Assert.assertEquals(result, "testValue");
    }

    @Test
    public void testIsSameDayShouldReturnTrue() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long timeA = new DateTime(simpleDateFormat.parse("2020-09-09 04:18:02").getTime()).toDate().getTime();
        long timeB = new DateTime(simpleDateFormat.parse("2020-09-09 14:18:02").getTime()).toDate().getTime();
        Assert.assertTrue(Utils.isSameDay(timeA, timeB, null));
    }

    @Test
    public void testGetGenderEnumReturnsMaleEnumForGenderMaleValue() {
        HashMap<String, String> details = new HashMap<>();
        details.put("gender", "male");
        Gender gender = Utils.getGenderEnum(details);
        Assert.assertEquals(Gender.MALE, gender);
    }

    @Test
    public void testDobStringToDateReturnsNullWhenDobStringIsNull() {
        Assert.assertNull(Utils.dobStringToDate(null));
    }

    @Test
    public void testDobStringToDateReturnsNullWhenDobStringIsEmpty() {
        Assert.assertNull(Utils.dobStringToDate(""));
    }

    @Test
    public void testDobStringToDateReturnsNullWhenDobStringIsInvalidDateFormat() {
        Assert.assertNull(Utils.dobStringToDate("20211-31-02"));
    }

    @Test
    public void testDobStringToDateReturnsCorrectDateWhenDobStringIsAValidDateFormat() {
        String dobString = "2021-12-12";
        Date date = Utils.dobStringToDate(dobString);
        Assert.assertNotNull(date);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Assert.assertEquals(dobString, dateFormat.format(date));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateGrowthValueThrowsIllegalArgumentExceptionWhenStringValueIsNotANumber() {
        Utils.updateGrowthValue("ABC123.45");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateGrowthValueThrowsIllegalArgumentExceptionWhenStringValueIsANegativeNumber() {
        Utils.updateGrowthValue("-123.45");
    }

    @Test
    public void testUpdateGrowthValueReturnsSameValueWhenStringValueIsAPositiveNumber() {
        String number = "123.45";
        String value = Utils.updateGrowthValue(number);
        Assert.assertEquals(number, value);
    }

    @Test
    public void testUpdateGrowthValueAppendsDecimalPointWhenStringValueIsAPositiveInteger() {
        String number = "123";
        String value = Utils.updateGrowthValue(number);
        Assert.assertEquals(number + ".0", value);
    }

    @Test
    public void testGetGenderEnumReturnsMaleWhenChildGenderIsMale() {
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put("gender", "male");
        Gender gender = Utils.getGenderEnum(childDetails);
        Assert.assertEquals("MALE", gender.toString());
    }

    @Test
    public void testGetGenderEnumReturnsFemaleWhenChildGenderIsFemale() {
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put("gender", "female");
        Gender gender = Utils.getGenderEnum(childDetails);
        Assert.assertEquals("FEMALE", gender.toString());
    }

    @Test
    public void testGetGenderEnumReturnsUnknownWhenChildGenderIsNeitherMaleOrFemale() {
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put("gender", "other");
        Gender gender = Utils.getGenderEnum(childDetails);
        Assert.assertEquals("UNKNOWN", gender.toString());
    }

    @Test
    public void testProcessExtraVaccinesEventObsAddsSelectedVaccinesAndSelectedVaccinesCounterObservations() throws JSONException {
        String vaccineField = "163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

        Event event = new Event();
        event.setBaseEntityId("baseEntityId");
        event.setEventType("testEventType");
        event.setFormSubmissionId("342290-342290");
        event.setEntityType(Constants.CHILD_TYPE);

        Obs obs = new Obs();
        obs.setFieldCode(vaccineField);
        obs.setFieldType("concept");
        obs.setFieldDataType("start");

        List<Object> humanReadableValues = new ArrayList<>();
        humanReadableValues.add("dawa");
        obs.setHumanReadableValues(humanReadableValues);

        event.addObs(obs);

        int obsCount = event.getObs().size();

        Utils.processExtraVaccinesEventObs(event, vaccineField);
        Assert.assertEquals((obsCount + 1), event.getObs().size());
        Assert.assertEquals(Constants.KEY.SELECTED_VACCINES, event.getObs().get(obsCount - 1).getFieldCode());
    }

    @Test
    public void testHasCompassRelationshipIdShouldReturnTrueIfExists() {
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put("mother_compass_relationship_id", "123");

        boolean hasCompassRelationshipId = Utils.hasCompassRelationshipId(childDetails);
        Assert.assertTrue(hasCompassRelationshipId);

    }

    @Test
    public void testHasCompassRelationshipIdShouldReturnFalseIfNotExists() {
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put("mother_compass_relationship_id", "");

        boolean hasCompassRelationshipId = Utils.hasCompassRelationshipId(childDetails);
        Assert.assertFalse(hasCompassRelationshipId);

    }

    @Test
    public void testIsChildHasNFCCardShouldReturnTrueIfCardIsNotBlacklisted() {
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put("nfc_card_blacklisted", "false");
        childDetails.put("nfc_card_identifier", "0099887711112222");

        boolean isChildHasNFCCard = Utils.isChildHasNFCCard(childDetails);
        Assert.assertTrue(isChildHasNFCCard);
    }

    @Test
    public void testIsChildHasNFCCardShouldReturnTrueIfCardIsBlacklisted() {
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put("nfc_card_blacklisted", "true");
        childDetails.put("nfc_card_identifier", "0099887711112222");

        boolean isChildHasNFCCard = Utils.isChildHasNFCCard(childDetails);
        Assert.assertFalse(isChildHasNFCCard);
    }
}