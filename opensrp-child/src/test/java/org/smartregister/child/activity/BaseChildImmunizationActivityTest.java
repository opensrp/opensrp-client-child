package org.smartregister.child.activity;

import static org.mockito.Mockito.times;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.impl.activity.TestChildImmunizationActivity;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.SyncStatus;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;
import org.smartregister.service.UserService;
import org.smartregister.util.AppProperties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@PrepareForTest({Utils.class, LocationHelper.class, CoreLibrary.class, TextUtils.class})
public class BaseChildImmunizationActivityTest extends BaseUnitTest {

    private TestChildImmunizationActivity baseChildImmunizationActivity;

    @Mock
    private TextView textView;

    @Captor
    private ArgumentCaptor argumentCaptor;

    @Mock
    private org.smartregister.Context opensrpContext;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Spy
    private AppProperties appProperties;

    @Mock
    private UserService userService;

    @Mock
    private Repository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Mockito.doReturn(ApplicationProvider.getApplicationContext()).when(opensrpContext).applicationContext();
        Mockito.doReturn(appProperties).when(opensrpContext).getAppProperties();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        Mockito.doReturn(userService).when(opensrpContext).userService();
        Mockito.doReturn(allSharedPreferences).when(userService).getAllSharedPreferences();

        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null, null, null, true);
        metadata.updateChildRegister("test", "test",
                "test", "ChildRegister",
                "test", "test",
                "test",
                "test", "test");

        CoreLibrary.init(opensrpContext);
        ChildLibrary.init(opensrpContext, repository, metadata, 1, 1);

        baseChildImmunizationActivity = Robolectric.buildActivity(TestChildImmunizationActivity.class).create().get();
    }

    @Test
    public void getVaccineByName() {
        Vaccine vaccine = new Vaccine();
        vaccine.name = "some";
        List<Vaccine> vaccines = new ArrayList<>();
        vaccines.add(vaccine);
        Assert.assertEquals(baseChildImmunizationActivity.getVaccineByName(vaccines, "some").name, "some");
    }

    @Test
    public void getVaccineGroupName() {
        VaccineGroup vaccineGroup = new VaccineGroup();
        vaccineGroup.id = "some";
        List<VaccineGroup> vaccineGroups = new ArrayList<>();
        vaccineGroups.add(vaccineGroup);
        Assert.assertEquals(baseChildImmunizationActivity.getVaccineGroupByName(vaccineGroups, "some").id, "some");
    }

    @Test
    public void createBcg2Vaccine() {

        BaseChildImmunizationActivity baseChildImmunizationActivitySpy = Mockito.spy(baseChildImmunizationActivity);
        Mockito.doReturn("TEST_ID").when(baseChildImmunizationActivitySpy).getProviderLocationId();
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("caseId", new HashMap<>(), "name");
        Whitebox.setInternalState(baseChildImmunizationActivitySpy, commonPersonObjectClient);
        org.smartregister.immunization.domain.Vaccine vaccine = baseChildImmunizationActivitySpy.createBcg2Vaccine(new Date(), SyncStatus.SYNCED.value());
        Assert.assertTrue((-1 == vaccine.getCalculation()));
    }

    @Test
    public void updateWeightWrapper() throws Exception {
        Field fieldProperty = PowerMockito.field(baseChildImmunizationActivity.getClass(), "RECORD_WEIGHT_BUTTON_ACTIVE_MIN");
        int v = (int) fieldProperty.get(baseChildImmunizationActivity);
        WeightWrapper weightWrapper = new WeightWrapper();
        weightWrapper.setDbKey(3l);
        weightWrapper.setUpdatedWeightDate(new DateTime().minusHours(v + 1), true);
        weightWrapper.setWeight(45f);
        ImageView imageView = Mockito.mock(ImageView.class);
        View view = Mockito.mock(View.class);
        Whitebox.invokeMethod(baseChildImmunizationActivity, "updateWeightWrapper", weightWrapper, view, textView, imageView);
        Assert.assertNull(weightWrapper.getWeight());
        Assert.assertNull(weightWrapper.getDbKey());

        //
        weightWrapper.setDbKey(32l);
        weightWrapper.setWeight(49f);
        weightWrapper.setUpdatedWeightDate(new DateTime(), true);
        Whitebox.invokeMethod(baseChildImmunizationActivity, "updateWeightWrapper", weightWrapper, view, textView, imageView);
        Assert.assertNotNull(weightWrapper.getWeight());
        Assert.assertNotNull(weightWrapper.getDbKey());
    }

    @Test
    public void updateHeightWrapper() throws Exception {
        Field fieldProperty = PowerMockito.field(baseChildImmunizationActivity.getClass(), "RECORD_WEIGHT_BUTTON_ACTIVE_MIN");
        int v = (int) fieldProperty.get(baseChildImmunizationActivity);
        HeightWrapper heightWrapper = new HeightWrapper();
        heightWrapper.setDbKey(3l);
        heightWrapper.setUpdatedHeightDate(new DateTime().minusHours(v + 1), true);
        heightWrapper.setHeight(45f);
        ImageView imageView = Mockito.mock(ImageView.class);
        View view = Mockito.mock(View.class);
        Whitebox.invokeMethod(baseChildImmunizationActivity, "updateHeightWrapper", heightWrapper, view, imageView);
        Assert.assertNull(heightWrapper.getHeight());
        Assert.assertNull(heightWrapper.getDbKey());

        heightWrapper.setDbKey(3l);
        heightWrapper.setHeight(45f);
        heightWrapper.setUpdatedHeightDate(new DateTime(), true);
        Whitebox.invokeMethod(baseChildImmunizationActivity, "updateHeightWrapper", heightWrapper, view, imageView);
        Assert.assertNotNull(heightWrapper.getHeight());
        Assert.assertNotNull(heightWrapper.getDbKey());
    }

    @Test
    public void updateRecordWeightText() throws Exception {
        WeightWrapper weightWrapper = new WeightWrapper();
        weightWrapper.setDbKey(3l);
        weightWrapper.setWeight(45f);
        HeightWrapper heightWrapper = new HeightWrapper();
        heightWrapper.setHeight(34f);
        String weightText = weightWrapper.getWeight() + " kg, " + heightWrapper.getHeight() + " cm";

        Whitebox.setInternalState(baseChildImmunizationActivity, "recordWeightText", textView);
        Field fieldMonitorGrowth = PowerMockito.field(baseChildImmunizationActivity.getClass(), "monitorGrowth");
        fieldMonitorGrowth.set(baseChildImmunizationActivity, true);

        Whitebox.invokeMethod(baseChildImmunizationActivity, "updateRecordWeightText", weightWrapper, heightWrapper);
        Mockito.verify(textView, Mockito.atLeastOnce()).setText((CharSequence) argumentCaptor.capture());
        String expected = (String) argumentCaptor.getValue();
        Assert.assertEquals(expected, weightText);

        String weight = weightWrapper.getWeight() + " kg";
        Field fieldMonitorGrowth1 = PowerMockito.field(baseChildImmunizationActivity.getClass(), "monitorGrowth");
        fieldMonitorGrowth1.set(baseChildImmunizationActivity, false);
        Whitebox.invokeMethod(baseChildImmunizationActivity, "updateRecordWeightText", weightWrapper, heightWrapper);
        Mockito.verify(textView, Mockito.atLeastOnce()).setText((CharSequence) argumentCaptor.capture());
        String expected1 = (String) argumentCaptor.getValue();
        Assert.assertEquals(expected1, weight);
    }

    @After
    public void tearDown() {
        if (baseChildImmunizationActivity == null)
            baseChildImmunizationActivity.finish();
    }

    @Test
    public void testLaunchActivityInvokesStartActivity() {

        appProperties.put(ChildAppProperties.KEY.FEATURE_NFC_CARD_ENABLED, true);

        BaseChildImmunizationActivity baseChildImmunizationActivitySpy = Mockito.spy(baseChildImmunizationActivity);

        Mockito.when(opensrpContext.applicationContext()).thenReturn(Mockito.mock(android.content.Context.class));

        String nextAppointmentDate = "2022-01-01";
        RegisterClickables registerClickables = new RegisterClickables();
        registerClickables.setNextAppointmentDate(nextAppointmentDate);

        BaseChildImmunizationActivity.launchActivity(baseChildImmunizationActivitySpy, getChildDetails(), registerClickables, baseChildImmunizationActivitySpy.getClass());

        ArgumentCaptor<Intent> argumentCaptorForIntent = ArgumentCaptor.forClass(Intent.class);
        Mockito.verify(baseChildImmunizationActivitySpy, times(1)).startActivity(argumentCaptorForIntent.capture());

        Intent value = argumentCaptorForIntent.getValue();
        Assert.assertNotNull(value);
        Assert.assertNotNull(value.getExtras());
        Assert.assertEquals(3, value.getExtras().size());
        Assert.assertEquals("id-1", value.getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
        Assert.assertNotNull(value.getExtras().get(Constants.INTENT_KEY.EXTRA_REGISTER_CLICKABLES));
        Assert.assertTrue(value.getExtras().get(Constants.INTENT_KEY.EXTRA_REGISTER_CLICKABLES) instanceof RegisterClickables);
        Assert.assertEquals(nextAppointmentDate, value.getStringExtra(Constants.INTENT_KEY.NEXT_APPOINTMENT_DATE));
    }

    private CommonPersonObjectClient getChildDetails() {

        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put("baseEntityId", "id-1");
        childDetails.put(Constants.KEY.FIRST_NAME, "John");
        childDetails.put(Constants.KEY.DOB, "1990-05-09");
        childDetails.put(Constants.KEY.BIRTH_HEIGHT, "48");
        childDetails.put(Constants.KEY.BIRTH_WEIGHT, "3.6");
        childDetails.put(Constants.Client.SYSTEM_OF_REGISTRATION, "MVACC");
        childDetails.put(Constants.KEY.IS_CHILD_DATA_ON_DEVICE, Constants.FALSE);

        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("id-1", childDetails, Constants.KEY.CHILD);
        commonPersonObjectClient.setColumnmaps(childDetails);

        return commonPersonObjectClient;
    }

    @Test
    public void testGetGenderButtonColorReturnsBlueColorForMaleValue() {
        int maleColor = baseChildImmunizationActivity.getGenderButtonColor("male");
        Assert.assertEquals(R.drawable.pill_background_male_blue, maleColor);
    }

    @Test
    public void testGetGenderButtonColorReturnsPinkColorWhenFemaleValue() {
        int femaleColor = baseChildImmunizationActivity.getGenderButtonColor("female");
        Assert.assertEquals(R.drawable.pill_background_female_pink, femaleColor);
    }

    @Test
    public void testGetGenderButtonColorReturnsGreenColorForDefaultValue() {
        int defaulteColor = baseChildImmunizationActivity.getGenderButtonColor("default");
        Assert.assertEquals(R.drawable.pill_background_gender_neutral_green, defaulteColor);
    }

    @Test
    public void testGetContentViewReturnsReturnsChildImmunizationActivityId() {
        int contentView = baseChildImmunizationActivity.getContentView();
        Assert.assertEquals(R.layout.activity_child_immunization, contentView);
    }

    @Test
    public void testGetToolbarIdReturnsReturnsLocationSwitcherToolbarId() {
        int toolbarId = baseChildImmunizationActivity.getToolbarId();
        Assert.assertEquals(LocationSwitcherToolbar.TOOLBAR_ID, toolbarId);
    }

    @Test
    public void testConfigureFloatingActionBackgroundSetsItsVisibilityToVisible() {
        LinearLayout fab = Mockito.mock(LinearLayout.class);
        Mockito.doReturn(0).when(fab).getPaddingBottom();
        Mockito.doReturn(0).when(fab).getPaddingLeft();
        Mockito.doReturn(0).when(fab).getPaddingRight();
        Mockito.doReturn(0).when(fab).getPaddingTop();
        baseChildImmunizationActivity.floatingActionButton = fab;
        baseChildImmunizationActivity.configureFloatingActionBackground(0, null);

        Mockito.verify(fab).setVisibility(View.VISIBLE);
    }

    @Test
    public void testGetChildsThirdPersonPronounReturnsHimForMaleGender() {
        HashMap<String, String> details = new HashMap<>();
        details.put("gender", "male");
        CommonPersonObjectClient client = new CommonPersonObjectClient("caseId", details, "name");
        BaseChildImmunizationActivity activity = Mockito.spy(baseChildImmunizationActivity);
        Mockito.doReturn("him").when(activity).getString(R.string.him);
        String pronoun = ReflectionHelpers.callInstanceMethod(activity, "getChildsThirdPersonPronoun",
                ReflectionHelpers.ClassParameter.from(CommonPersonObjectClient.class, client));
        Assert.assertEquals("him", pronoun);
    }

    @Test
    public void testGetChildsThirdPersonPronounReturnsHerForFemaleGender() {
        HashMap<String, String> details = new HashMap<>();
        BaseChildImmunizationActivity activity = Mockito.spy(baseChildImmunizationActivity);
        Mockito.doReturn("her")
                .when(activity).getString(R.string.her);
        details.put("gender", "female");
        CommonPersonObjectClient client = new CommonPersonObjectClient("caseId", details, "name");
        String pronoun = ReflectionHelpers.callInstanceMethod(activity, "getChildsThirdPersonPronoun",
                ReflectionHelpers.ClassParameter.from(CommonPersonObjectClient.class, client));
        Assert.assertEquals("her", pronoun);
    }
}
