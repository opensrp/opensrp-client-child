package org.smartregister.child.utils;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.api.constants.Gender;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.BuildConfig;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@PrepareForTest({VaccineRepo.class, ImmunizationLibrary.class})
public class UtilsTest extends BaseUnitTest {

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private WeightRepository weightRepository;

    @Mock
    private HeightRepository heightRepository;

    @Mock
    private Activity activity;
    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private Context opensrpContext;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private CoreLibrary coreLibrary;

    @Captor
    private ArgumentCaptor<Vaccine> vaccineArgumentCaptor;

    @Captor
    private ArgumentCaptor weightArgumentCaptor;

    @Captor
    private ArgumentCaptor heightArgumentCaptor;

    private String dobString = "2017-09-09";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(childLibrary.context()).thenReturn(opensrpContext);
        Mockito.when(opensrpContext.allSharedPreferences()).thenReturn(allSharedPreferences);
        String providerId = "demo";
        Mockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(providerId);
    }

    @Test
    public void getCombinedVaccineWithNonNullArgument() {

        Map<String, String> vaccineMap = ImmutableMap.of(VaccineRepo.Vaccine.measles1.display(), VaccineRepo.Vaccine.measles1.display() + "/" + VaccineRepo.Vaccine.mr1.display(), VaccineRepo.Vaccine.mr2.display(), VaccineRepo.Vaccine.measles2.display() + "/" + VaccineRepo.Vaccine.mr2.display(), VaccineRepo.Vaccine.mr1.display(), VaccineRepo.Vaccine.measles1.display() + "/" + VaccineRepo.Vaccine.mr1.display(), "Tri Vaccine 2", "Tri Vaccine 1 / Tri Vaccine 2 / Tri Vaccine 3 ");


        Assert.assertEquals(Arrays.asList(VaccineRepo.Vaccine.measles1.display()), Utils.getAlternativeCombinedVaccines("MR 1", vaccineMap));
        Assert.assertEquals(Arrays.asList(VaccineRepo.Vaccine.mr1.display()), Utils.getAlternativeCombinedVaccines("MEASLES 1", vaccineMap));
        Assert.assertEquals(Arrays.asList(VaccineRepo.Vaccine.measles2.display()), Utils.getAlternativeCombinedVaccines("MR 2", vaccineMap));
        Assert.assertEquals(Arrays.asList("Tri Vaccine 1", "Tri Vaccine 3"), Utils.getAlternativeCombinedVaccines("Tri Vaccine 2", vaccineMap));
        Assert.assertNull(Utils.getAlternativeCombinedVaccines("other", vaccineMap));
    }

    @Test
    public void addVaccineWithVaccineRepositoryOrVaccineNotNull() {
        Vaccine vaccine = new Vaccine();
        vaccine.setName("testvaccine");
        Utils.addVaccine(vaccineRepository, vaccine);
        Mockito.verify(vaccineRepository, Mockito.times(1)).add(vaccineArgumentCaptor.capture());
        Assert.assertEquals(vaccine, vaccineArgumentCaptor.getValue());
    }

    @Test
    public void addVaccineWithVaccineRepositoryIsNull() {
        Vaccine vaccine = new Vaccine();
        Utils.addVaccine(null, vaccine);
        Mockito.verify(vaccineRepository, Mockito.times(0)).add(vaccineArgumentCaptor.capture());
    }

    @Test
    public void addVaccineWithVaccineIsNull() {
        Utils.addVaccine(vaccineRepository, null);
        Mockito.verify(vaccineRepository, Mockito.times(0)).add(null);
    }

    @Test
    public void testAddVaccineShouldCatchNullPointerException() {
        Vaccine vaccine = new Vaccine();
        Utils.addVaccine(vaccineRepository, vaccine);
        Mockito.verify(vaccineRepository, Mockito.times(0)).add(vaccine);
    }

    @Test
    public void testDobStringToDateTimeBlankString() {
        DateTime result = Utils.dobStringToDateTime("");
        Assert.assertNull(result);
    }

    @Test
    public void testDobStringToDateTimeShouldCatchException() {
        DateTime result = Utils.dobStringToDateTime("Test");
        Assert.assertNull(result);
    }

    @Test
    public void testDobStringToDateTime() {
        DateTime result = Utils.dobStringToDateTime(dobString);
        Assert.assertEquals(new DateTime(dobString), result);
    }

    @Test
    public void testDobStringToDateShouldReturnNull() {
        Date result = Utils.dobStringToDate("test");
        Assert.assertNull(result);
    }

    @Test
    public void testDobStringToDate() {
        Date result = Utils.dobStringToDate(dobString);
        Assert.assertEquals(new DateTime(dobString).toDate(), result);
    }

    @Test
    public void testRecordWeightWithFemaleValidGenderAndNullDobString() {
        Context context = Mockito.mock(Context.class);
        ChildLibrary.init(context, Mockito.mock(Repository.class), Mockito.mock(ChildMetadata.class), 1, 1);
        Weight weight = new Weight();
        WeightWrapper weightWrapper = new WeightWrapper();
        weightWrapper.setUpdatedWeightDate(new DateTime(), true);
        weightWrapper.setGender(Constants.GENDER.FEMALE);
        weightWrapper.setWeight(20.0f);
        Mockito.when(weightRepository.find(weightWrapper.getDbKey())).thenReturn(weight);
        Mockito.when(context.allSharedPreferences()).thenReturn(Mockito.mock(AllSharedPreferences.class));
        Utils.recordWeight(weightRepository, weightWrapper, null);
        Mockito.verify(weightRepository, Mockito.times(1)).add((Weight) weightArgumentCaptor.capture());
        Assert.assertNotNull(weightArgumentCaptor.getValue());
    }

    @Test
    public void testRecordWeightWithValidFemaleGenderAndValidDobString() {
        Context context = Mockito.mock(Context.class);
        ChildLibrary.init(context, Mockito.mock(Repository.class), Mockito.mock(ChildMetadata.class), 1, 1);
        Weight weight = new Weight();
        WeightWrapper weightWrapper = new WeightWrapper();
        weightWrapper.setUpdatedWeightDate(new DateTime(), true);
        weightWrapper.setGender(Constants.GENDER.FEMALE);
        weightWrapper.setWeight(20.0f);
        weightWrapper.setDob(dobString);
        Mockito.when(weightRepository.find(weightWrapper.getDbKey())).thenReturn(weight);
        Mockito.when(context.allSharedPreferences()).thenReturn(Mockito.mock(AllSharedPreferences.class));
        Utils.recordWeight(weightRepository, weightWrapper, null);
        Mockito.verify(weightRepository, Mockito.times(1)).add((Date) weightArgumentCaptor.capture(), (Gender) weightArgumentCaptor.capture(), (Weight) weightArgumentCaptor.capture());
        Assert.assertEquals(3, weightArgumentCaptor.getAllValues().size());
        Assert.assertEquals(Gender.FEMALE, weightArgumentCaptor.getAllValues().get(1));
        Assert.assertEquals(Utils.dobStringToDate(dobString), weightArgumentCaptor.getAllValues().get(0));
        Assert.assertNotNull(weightArgumentCaptor.getAllValues().get(2));
    }

    @Test
    public void testRecordWeightWithValidMaleGenderAndValidDobString() {
        Context context = Mockito.mock(Context.class);
        ChildLibrary.init(context, Mockito.mock(Repository.class), Mockito.mock(ChildMetadata.class), 1, 1);
        Weight weight = new Weight();
        WeightWrapper weightWrapper = new WeightWrapper();
        weightWrapper.setUpdatedWeightDate(new DateTime(), true);
        weightWrapper.setGender(Constants.GENDER.MALE);
        weightWrapper.setWeight(20.0f);
        weightWrapper.setDob(dobString);
        Mockito.when(weightRepository.find(weightWrapper.getDbKey())).thenReturn(weight);
        Mockito.when(context.allSharedPreferences()).thenReturn(Mockito.mock(AllSharedPreferences.class));
        Utils.recordWeight(weightRepository, weightWrapper, null);
        Mockito.verify(weightRepository, Mockito.times(1)).add((Date) weightArgumentCaptor.capture(), (Gender) weightArgumentCaptor.capture(), (Weight) weightArgumentCaptor.capture());
        Assert.assertEquals(3, weightArgumentCaptor.getAllValues().size());
        Assert.assertEquals(Gender.MALE, weightArgumentCaptor.getAllValues().get(1));
        Assert.assertEquals(Utils.dobStringToDate(dobString), weightArgumentCaptor.getAllValues().get(0));
        Assert.assertNotNull(weightArgumentCaptor.getAllValues().get(2));
    }

    @Test
    public void testRecordHeightWithFemaleValidGenderAndNullDobString() {
        Context context = Mockito.mock(Context.class);
        ChildLibrary.init(context, Mockito.mock(Repository.class), Mockito.mock(ChildMetadata.class), 1, 1);
        Height height = new Height();
        HeightWrapper heightWrapper = new HeightWrapper();
        heightWrapper.setUpdatedHeightDate(new DateTime(), true);
        heightWrapper.setGender(Constants.GENDER.FEMALE);
        heightWrapper.setHeight(20.0f);
        heightWrapper.setId("213");
        Mockito.when(heightRepository.find(heightWrapper.getDbKey())).thenReturn(height);
        Mockito.when(context.allSharedPreferences()).thenReturn(Mockito.mock(AllSharedPreferences.class));
        Utils.recordHeight(heightRepository, heightWrapper, null);
        Mockito.verify(heightRepository, Mockito.times(1)).add((Height) heightArgumentCaptor.capture());
        Assert.assertNotNull(heightArgumentCaptor.getValue());
    }

    @Test
    public void testRecordHeightWithValidFemaleGenderAndValidDobString() {
        Context context = Mockito.mock(Context.class);
        ChildLibrary.init(context, Mockito.mock(Repository.class), Mockito.mock(ChildMetadata.class), 1, 1);
        Height height = new Height();
        HeightWrapper heightWrapper = new HeightWrapper();
        heightWrapper.setUpdatedHeightDate(new DateTime(), true);
        heightWrapper.setGender(Constants.GENDER.FEMALE);
        heightWrapper.setHeight(20.0f);
        heightWrapper.setId("213");
        heightWrapper.setDob(dobString);
        Mockito.when(heightRepository.find(heightWrapper.getDbKey())).thenReturn(height);
        Mockito.when(context.allSharedPreferences()).thenReturn(Mockito.mock(AllSharedPreferences.class));
        Utils.recordHeight(heightRepository, heightWrapper, null);
        Mockito.verify(heightRepository, Mockito.times(1)).add((Date) heightArgumentCaptor.capture(), (Gender) heightArgumentCaptor.capture(), (Height) heightArgumentCaptor.capture());
        Assert.assertEquals(3, heightArgumentCaptor.getAllValues().size());
        Assert.assertEquals(Gender.FEMALE, heightArgumentCaptor.getAllValues().get(1));
        Assert.assertEquals(Utils.dobStringToDate(dobString), heightArgumentCaptor.getAllValues().get(0));
        Assert.assertNotNull(heightArgumentCaptor.getAllValues().get(2));
    }

    @Test
    public void testRecordHeightWithValidMaleGenderAndValidDobString() {
        Context context = Mockito.mock(Context.class);
        ChildLibrary.init(context, Mockito.mock(Repository.class), Mockito.mock(ChildMetadata.class), 1, 1);
        Height height = new Height();
        HeightWrapper heightWrapper = new HeightWrapper();
        heightWrapper.setHeight(20.0f);
        heightWrapper.setId("213");
        heightWrapper.setUpdatedHeightDate(new DateTime(), true);
        heightWrapper.setGender(Constants.GENDER.MALE);
        heightWrapper.setDob(dobString);
        Mockito.when(heightRepository.find(heightWrapper.getDbKey())).thenReturn(height);
        Mockito.when(context.allSharedPreferences()).thenReturn(Mockito.mock(AllSharedPreferences.class));
        Utils.recordHeight(heightRepository, heightWrapper, null);
        Mockito.verify(heightRepository, Mockito.times(1)).add((Date) heightArgumentCaptor.capture(), (Gender) heightArgumentCaptor.capture(), (Height) heightArgumentCaptor.capture());
        Assert.assertEquals(3, heightArgumentCaptor.getAllValues().size());
        Assert.assertEquals(Gender.MALE, heightArgumentCaptor.getAllValues().get(1));
        Assert.assertEquals(Utils.dobStringToDate(dobString), heightArgumentCaptor.getAllValues().get(0));
        Assert.assertNotNull(heightArgumentCaptor.getAllValues().get(2));
    }

    @Test
    public void testGetCleanMapShouldCatchException() {
        Assert.assertEquals(new HashMap<>(), Utils.getCleanMap(null));
    }

    @Test
    public void testGetCleanMapWithDirtyValues() {
        Map<String, String> rawDetails = new HashMap<>();
        rawDetails.put("key", "");
        rawDetails.put("key1", "null");
        Map<String, String> results = Utils.getCleanMap(rawDetails);
        Assert.assertEquals(new HashMap<String, String>(), results);
    }

    @Test
    public void testGetCleanMapWithCleanValues() {
        Map<String, String> rawDetails = new HashMap<>();
        rawDetails.put("key", "value");
        rawDetails.put("key1", "value");
        Map<String, String> results = Utils.getCleanMap(rawDetails);
        Assert.assertEquals(rawDetails, results);
    }

    @Test
    public void testUpdateGrowthValueWithFloat() {
        String result = Utils.updateGrowthValue("20.0");
        Assert.assertEquals("20.0", result);
    }

    @Test
    public void testUpdateGrowthValueWithNonFloat() {
        String result = Utils.updateGrowthValue("20");
        Assert.assertEquals("20.0", result);
    }

    @Test
    public void testUpdateGrowthValueWithNonNumeric() {
        expectedException.expect(IllegalArgumentException.class);
        Utils.updateGrowthValue("23w");
    }

    @Test
    public void testFormatNumberShouldCatchParseException() {
        String raw = "raw";
        Assert.assertEquals(raw, Utils.formatNumber(raw));
    }

    @Test
    public void testFormatNumberWithValidNumber() {
        String raw = "099787762567";
        Assert.assertEquals(raw.substring(1), Utils.formatNumber(raw));
    }

    @Test
    public void localizeStateKeyShouldReturn6weeksWhenGivenMixedCase6WeeksStateKey() {

        String localizedStateKey = Utils.localizeStateKey(RuntimeEnvironment.application, "6 WEEKS");

        Assert.assertEquals("6 Weeks", localizedStateKey);

    }

    @Test
    public void testCreateGroupNameTextViewCreatesTextViewCorrectly() {

        TextView textView = Utils.createGroupNameTextView(RuntimeEnvironment.application, "14 weeks");

        Assert.assertNotNull(textView);
        Assert.assertEquals(Typeface.DEFAULT_BOLD, textView.getTypeface());
        Assert.assertEquals("14 Weeks", textView.getText());

    }

    @Test
    public void testRefreshDataCaptureStrategyBannerSetsCorrectText() {

        String testLocation = "Mobile Clinic";

        View bannerViewWrapper = Mockito.mock(View.class);
        TextView bannerTextView = Mockito.mock(TextView.class);
        String bannerText = String.format("Service Point: %s", testLocation);

        Mockito.doReturn(bannerViewWrapper).when(activity).findViewById(R.id.advanced_data_capture_strategy_wrapper);
        Mockito.doReturn(bannerTextView).when(activity).findViewById(R.id.advanced_data_capture_strategy);
        Mockito.doReturn(bannerText).when(activity).getString(R.string.service_point, testLocation);

        Utils.refreshDataCaptureStrategyBanner(activity, testLocation);

        ArgumentCaptor<String> bannerTextCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(bannerTextView).setText(bannerTextCaptor.capture());

        String capturedText = bannerTextCaptor.getValue();
        Assert.assertNotNull(capturedText);
        Assert.assertEquals(bannerText, capturedText);

    }

    @Test
    public void testRefreshDataCaptureStrategyBannerShowBannerForAdvancedStrategy() {

        String testLocation = "School Clinic";

        TextView bannerTextView = Mockito.mock(TextView.class);
        View bannerViewWrapper = Mockito.mock(View.class);
        String bannerText = String.format("Service Point: %s", testLocation);

        Mockito.doReturn(bannerViewWrapper).when(activity).findViewById(R.id.advanced_data_capture_strategy_wrapper);
        Mockito.doReturn(bannerText).when(activity).getString(R.string.service_point, testLocation);
        Mockito.doReturn(AllConstants.DATA_CAPTURE_STRATEGY.ADVANCED).when(allSharedPreferences).fetchCurrentDataStrategy();
        Mockito.doReturn(bannerTextView).when(activity).findViewById(R.id.advanced_data_capture_strategy);

        Utils.refreshDataCaptureStrategyBanner(activity, testLocation);

        ArgumentCaptor<Integer> visibilityParamCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(bannerViewWrapper).setVisibility(visibilityParamCaptor.capture());

        int captorValue = visibilityParamCaptor.getValue();

        Assert.assertNotNull(captorValue);
        Assert.assertEquals(View.VISIBLE, captorValue);

    }

    @Test
    public void testRefreshDataCaptureStrategyBannerHidesBannerForNormalStrategy() {

        TextView bannerTextView = Mockito.mock(TextView.class);
        View bannerViewWrapper = Mockito.mock(View.class);

        String testLocation = "Door to Door";
        String bannerText = String.format("Service Point: %s", testLocation);

        Mockito.doReturn(bannerText).when(activity).getString(R.string.service_point, testLocation);
        Mockito.doReturn(bannerTextView).when(activity).findViewById(R.id.advanced_data_capture_strategy);
        Mockito.doReturn(bannerViewWrapper).when(activity).findViewById(R.id.advanced_data_capture_strategy_wrapper);

        Utils.refreshDataCaptureStrategyBanner(activity, testLocation);

        ArgumentCaptor<Integer> visibilityParamCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(bannerViewWrapper).setVisibility(visibilityParamCaptor.capture());

        int captorValue = visibilityParamCaptor.getValue();

        Assert.assertNotNull(captorValue);
        Assert.assertEquals(View.GONE, captorValue);

    }

    @Test
    public void testIsFirstYearVaccineDoneTrue() {
        DateTime dob  = new DateTime(2019, 2, 8, 0,0);

        HashMap<String, Object> sch1 = new HashMap<>();
        sch1.put("date", new DateTime(2019, 2, 8, 0,0));
        sch1.put("vaccine", VaccineRepo.Vaccine.opv0);
        sch1.put("alert", null);
        sch1.put("status", "done");

        ArrayList<Map<String, Object>> schedules = new ArrayList<>();
        schedules.add(sch1);

        Assert.assertTrue(Utils.isAllVaccinesDoneWithIn(schedules, dob, 0, 365));
    }

    @Test
    public void testIsFirstYearVaccineDoneFalse() {
        DateTime dob  = new DateTime(2019, 2, 8, 0,0);

        HashMap<String, Object> sch1 = new HashMap<>();
        sch1.put("date", new DateTime(2019, 2, 8, 0,0));
        sch1.put("vaccine", VaccineRepo.Vaccine.opv0);
        sch1.put("alert", null);
        sch1.put("status", "due");

        ArrayList<Map<String, Object>> schedules = new ArrayList<>();
        schedules.add(sch1);

        Assert.assertFalse(Utils.isAllVaccinesDoneWithIn(schedules, dob, 0, 365));
    }

}