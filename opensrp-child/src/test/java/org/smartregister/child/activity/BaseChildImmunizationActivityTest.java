package org.smartregister.child.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.impl.activity.TestChildImmunizationActivity;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
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
import org.smartregister.util.AppProperties;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@PrepareForTest({Utils.class, LocationHelper.class, CoreLibrary.class, TextUtils.class, ChildLibrary.class})
@RunWith(PowerMockRunner.class)
public class BaseChildImmunizationActivityTest {

    private TestChildImmunizationActivity baseChildImmunizationActivity;

    @Mock
    private TextView textView;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private Context context;

    @Mock
    private LocationSwitcherToolbar toolbar;

    @Captor
    private ArgumentCaptor argumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        AppProperties appProperties = Mockito.mock(AppProperties.class);
        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        baseChildImmunizationActivity = new TestChildImmunizationActivity();
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
    public void createDummyVaccine() throws Exception {
        testInitHelper();
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("caseId", new HashMap<String, String>(), "name");
        Whitebox.setInternalState(baseChildImmunizationActivity, commonPersonObjectClient);
        org.smartregister.immunization.domain.Vaccine vaccine = Whitebox.invokeMethod(baseChildImmunizationActivity, "createDummyVaccine", "name",
                new Date(), SyncStatus.SYNCED.value());

        org.smartregister.immunization.domain.Vaccine vaccine2 = Whitebox.invokeMethod(baseChildImmunizationActivity, "createDummyVaccine", "name3",
                new Date(), SyncStatus.SYNCED.value());
        Assert.assertTrue((-1 == vaccine.getCalculation()));
        Assert.assertTrue((3 == vaccine2.getCalculation()));
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
        PowerMockito.when(baseChildImmunizationActivity.getResources().getColor(Mockito.any(Integer.class))).thenReturn(0);
        Whitebox.invokeMethod(baseChildImmunizationActivity, "updateWeightWrapper",
                weightWrapper, view, textView, imageView);
        Assert.assertNull(weightWrapper.getWeight());
        Assert.assertNull(weightWrapper.getDbKey());

        //
        weightWrapper.setDbKey(32l);
        weightWrapper.setWeight(49f);
        weightWrapper.setUpdatedWeightDate(new DateTime(), true);
        Whitebox.invokeMethod(baseChildImmunizationActivity, "updateWeightWrapper",
                weightWrapper, view, textView, imageView);
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
        PowerMockito.when(baseChildImmunizationActivity.getResources().getColor(Mockito.any(Integer.class))).thenReturn(0);
        Whitebox.invokeMethod(baseChildImmunizationActivity, "updateHeightWrapper",
                heightWrapper, view, imageView);
        Assert.assertNull(heightWrapper.getHeight());
        Assert.assertNull(heightWrapper.getDbKey());

        heightWrapper.setDbKey(3l);
        heightWrapper.setHeight(45f);
        heightWrapper.setUpdatedHeightDate(new DateTime(), true);
        Whitebox.invokeMethod(baseChildImmunizationActivity, "updateHeightWrapper",
                heightWrapper, view, imageView);
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
        PowerMockito.mockStatic(TextUtils.class);
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
        baseChildImmunizationActivity = new TestChildImmunizationActivity();
    }

    private void testInitHelper() {
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
        PowerMockito.when(toolbar.getCurrentLocation()).thenReturn("LocationId");
        Whitebox.setInternalState(baseChildImmunizationActivity, "toolbar", toolbar);
        PowerMockito.when(locationHelper.getOpenMrsLocationId(Mockito.any(String.class))).thenReturn("locationId");
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("providerId");
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
    }

    @Test
    public void testLaunchActivity() {
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null, TestChildImmunizationActivity.class,
                null, true);
        ChildMetadata metadataObj = Mockito.spy(metadata);
        when(Utils.metadata()).thenReturn(metadataObj);
        when(context.applicationContext()).thenReturn(Mockito.mock(android.content.Context.class));

        BaseChildImmunizationActivity.launchActivity(context.applicationContext(), getChildDetails(), null);

        Mockito.verify(context.applicationContext(), times(1)).startActivity(Mockito.any());
    }

    private CommonPersonObjectClient getChildDetails() {

        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put("baseEntityId", "id-1");
        childDetails.put(Constants.KEY.FIRST_NAME, "John");
        childDetails.put(Constants.KEY.DOB, "1990-05-09");
        childDetails.put(Constants.KEY.BIRTH_HEIGHT, "48");
        childDetails.put(Constants.KEY.BIRTH_WEIGHT, "3.6");

        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("id-1", childDetails, Constants.KEY.CHILD);
        commonPersonObjectClient.setColumnmaps(childDetails);

        return commonPersonObjectClient;
    }
}
