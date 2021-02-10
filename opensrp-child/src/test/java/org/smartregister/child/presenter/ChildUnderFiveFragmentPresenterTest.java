package org.smartregister.child.presenter;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.domain.WrapperParam;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.repository.ImageRepository;
import org.smartregister.util.EasyMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 01/12/2020.
 */
public class ChildUnderFiveFragmentPresenterTest extends BasePowerMockUnitTest {

    private static final String TEST_BASE_ENTITY_ID = "2323-a34-qf35q3w-4q4w3q-aaqr3w";
    private String TEST_PHOTO_LINE_PATH = "file:///some-random-file-path-for-testing";

    private ChildUnderFiveFragmentPresenter presenter;

    @Mock
    private HeightRepository heightRepository;

    @Mock
    private WeightRepository weightRepository;

    private String baseEntityId = "somebaseentyid";

    @Mock
    private ImageRepository imageRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = Mockito.spy(new ChildUnderFiveFragmentPresenter());
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(GrowthMonitoringLibrary.class, "instance", null);
    }

    @Test
    public void testConstructChildName() {
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.FIRST_NAME, "Demo");
        childDetails.put(Constants.KEY.LAST_NAME, "user");

        String result = presenter.constructChildName(childDetails);
        Assert.assertNotNull(result);
        Assert.assertEquals("Demo User", result);
    }

    @Test
    public void testGetBirthDate() {
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.DOB, "1990-01-03");

        Date result = presenter.getBirthDate(childDetails);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(result);
        Assert.assertNotNull(result);
        Assert.assertEquals(3, calendar.get(Calendar.DATE));
        Assert.assertEquals(0, calendar.get(Calendar.MONTH));//zero based index
        Assert.assertEquals(1990, calendar.get(Calendar.YEAR));
    }

    @Test
    public void testSortTheWeightsInDescendingOrder() {
        List<Weight> weightList = new ArrayList<>();
        Weight weight = new Weight();
        weight.setKg(3.5f);
        weight.setDate(LocalDate.parse("2020-04-28").toDate());
        weightList.add(weight);

        weight = new Weight();
        weight.setKg(1.9f);
        weight.setDate(LocalDate.parse("2020-02-11").toDate());
        weightList.add(weight);
        weight = new Weight();

        weight.setKg(5.0f);
        weight.setDate(LocalDate.parse("2020-12-01").toDate());
        weightList.add(weight);

        Assert.assertEquals(3.5f, weightList.get(0).getKg().floatValue(), 0);
        Assert.assertEquals(1.9f, weightList.get(1).getKg().floatValue(), 0);
        Assert.assertEquals(5.0f, weightList.get(2).getKg().floatValue(), 0);

        presenter.sortTheWeightsInDescendingOrder(weightList);

        Assert.assertEquals(5.0, weightList.get(0).getKg().floatValue(), 0);
        Assert.assertEquals(3.5f, weightList.get(1).getKg().floatValue(), 0);
        Assert.assertEquals(1.9f, weightList.get(2).getKg().floatValue(), 0);
    }

    @Test
    public void testSortTheHeightsInDescendingOrder() {
        List<Height> heightList = new ArrayList<>();
        Height height = new Height();
        height.setCm(35f);
        height.setDate(LocalDate.parse("2020-03-20").toDate());
        heightList.add(height);

        height = new Height();
        height.setCm(19f);
        height.setDate(LocalDate.parse("2020-01-10").toDate());
        heightList.add(height);
        height = new Height();

        height.setCm(50f);
        height.setDate(LocalDate.parse("2020-10-03").toDate());
        heightList.add(height);

        Assert.assertEquals(35f, heightList.get(0).getCm().floatValue(), 0);
        Assert.assertEquals(19f, heightList.get(1).getCm().floatValue(), 0);
        Assert.assertEquals(50f, heightList.get(2).getCm().floatValue(), 0);

        presenter.sortTheHeightsInDescendingOrder(heightList);

        Assert.assertEquals(50, heightList.get(0).getCm().floatValue(), 0);
        Assert.assertEquals(35f, heightList.get(1).getCm().floatValue(), 0);
        Assert.assertEquals(19f, heightList.get(2).getCm().floatValue(), 0);
    }

    @Test
    public void testGetHeightWrapper() {
        Height height = getHeight();

        Mockito.doReturn(Collections.singletonList(height)).when(heightRepository).findByEntityId(baseEntityId);
        WrapperParam wrapperParam = getWrapperParam(baseEntityId);
        HeightWrapper heightWrapper = presenter.getHeightWrapper(wrapperParam);
        Assert.assertNotNull(heightWrapper);
        Assert.assertEquals(heightWrapper.getGender(), wrapperParam.getGender());
        Assert.assertEquals(heightWrapper.getPatientName(), wrapperParam.getChildName());
        Assert.assertEquals(heightWrapper.getPatientNumber(), wrapperParam.getOpenSrpId());
    }

    @NotNull
    private Height getHeight() {
        Height height = new Height();
        height.setId(1L);
        height.setCm(90F);
        try {
            height.setDate(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01"));
        } catch (ParseException e) {
            Timber.e(e);
        }
        height.setBaseEntityId(baseEntityId);
        return height;
    }

    @Test
    public void testGetWeightWrapper() {

        Weight weight = getWeight();

        Mockito.doReturn(Collections.singletonList(weight)).when(heightRepository).findByEntityId(baseEntityId);
        WrapperParam wrapperParam = getWrapperParam(baseEntityId);
        WeightWrapper weightWrapper = presenter.getWeightWrapper(wrapperParam);
        Assert.assertNotNull(weightWrapper);
        Assert.assertEquals(weightWrapper.getGender(), wrapperParam.getGender());
        Assert.assertEquals(weightWrapper.getPatientName(), wrapperParam.getChildName());
        Assert.assertEquals(weightWrapper.getPatientNumber(), wrapperParam.getOpenSrpId());
    }

    @NotNull
    private Weight getWeight() {
        Weight weight = new Weight();
        weight.setId(1L);
        weight.setKg(6F);
        weight.setDate(new Date());
        weight.setBaseEntityId(baseEntityId);
        return weight;
    }

    @NotNull
    private WrapperParam getWrapperParam(String baseEntityId) {
        WrapperParam wrapperParam = new WrapperParam();
        wrapperParam.setBaseEntityId(baseEntityId);
        wrapperParam.setChildName("First Last");
        wrapperParam.setGender("Male");
        wrapperParam.setOpenSrpId("1920190");
        wrapperParam.setPosition(1);
        wrapperParam.setDuration("6");
        return wrapperParam;
    }

    @Test
    public void testGetBirthDateReturnsCorrectDate() {
        Map<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.DOB, "1990-01-03");

        Date result = presenter.getBirthDate(childDetails);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(result);
        Assert.assertNotNull(result);
        Assert.assertEquals(3, calendar.get(Calendar.DATE));
        Assert.assertEquals(0, calendar.get(Calendar.MONTH));//zero based index
        Assert.assertEquals(1990, calendar.get(Calendar.YEAR));
    }

    @Test
    public void testSortTheWeightsInDescendingOrderSortsCorrectly() {
        List<Weight> weightList = new ArrayList<>();
        Weight weight = new Weight();
        weight.setKg(3.5f);
        weight.setDate(LocalDate.parse("2020-04-28").toDate());
        weightList.add(weight);

        weight = new Weight();
        weight.setKg(1.9f);
        weight.setDate(LocalDate.parse("2020-02-11").toDate());
        weightList.add(weight);
        weight = new Weight();

        weight.setKg(5.0f);
        weight.setDate(LocalDate.parse("2020-12-01").toDate());
        weightList.add(weight);

        Assert.assertEquals(3.5f, weightList.get(0).getKg().floatValue(), 0);
        Assert.assertEquals(1.9f, weightList.get(1).getKg().floatValue(), 0);
        Assert.assertEquals(5.0f, weightList.get(2).getKg().floatValue(), 0);

        presenter.sortTheWeightsInDescendingOrder(weightList);

        Assert.assertEquals(5.0, weightList.get(0).getKg().floatValue(), 0);
        Assert.assertEquals(3.5f, weightList.get(1).getKg().floatValue(), 0);
        Assert.assertEquals(1.9f, weightList.get(2).getKg().floatValue(), 0);
    }

    @Test
    public void testSortTheHeightsInDescendingOrderCorrectly() {
        List<Height> heightList = new ArrayList<>();
        Height height = new Height();
        height.setCm(35f);
        height.setDate(LocalDate.parse("2020-03-20").toDate());
        heightList.add(height);

        height = new Height();
        height.setCm(19f);
        height.setDate(LocalDate.parse("2020-01-10").toDate());
        heightList.add(height);
        height = new Height();

        height.setCm(50f);
        height.setDate(LocalDate.parse("2020-10-03").toDate());
        heightList.add(height);

        Assert.assertEquals(35f, heightList.get(0).getCm().floatValue(), 0);
        Assert.assertEquals(19f, heightList.get(1).getCm().floatValue(), 0);
        Assert.assertEquals(50f, heightList.get(2).getCm().floatValue(), 0);

        presenter.sortTheHeightsInDescendingOrder(heightList);

        Assert.assertEquals(50, heightList.get(0).getCm().floatValue(), 0);
        Assert.assertEquals(35f, heightList.get(1).getCm().floatValue(), 0);
        Assert.assertEquals(19f, heightList.get(2).getCm().floatValue(), 0);
    }


    @Test
    public void testGetWeightsReturnsWeightList() {

        List<Weight> weightList = new ArrayList<>();
        Weight weight = new Weight();
        weight.setKg(2.8f);
        weight.setDate(LocalDate.parse("2021-01-28").toDate());
        weightList.add(weight);

        Mockito.doReturn(weightList).when(presenter).getChildSpecificWeights(TEST_BASE_ENTITY_ID, weightList);

        List<Weight> weights = presenter.getWeights(TEST_BASE_ENTITY_ID, weightList);
        Assert.assertNotNull(weights);
        Assert.assertEquals(1, weights.size());

        //Over 5 weights

        weightList = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            weight = new Weight();
            weight.setKg(2.8f + i);
            weight.setDate(LocalDate.parse("2021-01-21").toDate());
            weightList.add(weight);
        }

        Mockito.doReturn(weightList).when(presenter).getChildSpecificWeights(TEST_BASE_ENTITY_ID, weightList);

        weights = presenter.getWeights(TEST_BASE_ENTITY_ID, weightList);
        Assert.assertNotNull(weights);
        Assert.assertEquals(5, weights.size());

    }

    @Test
    public void testGetHeightsReturnsHeighList() {


        List<Height> HeightList = new ArrayList<>();
        Height Height = new Height();
        Height.setCm(2.8f);
        Height.setDate(LocalDate.parse("2021-01-28").toDate());
        HeightList.add(Height);

        Mockito.doReturn(HeightList).when(presenter).getChildSpecificHeights(TEST_BASE_ENTITY_ID, HeightList);

        List<Height> Heights = presenter.getHeights(TEST_BASE_ENTITY_ID, HeightList);
        Assert.assertNotNull(Heights);
        Assert.assertEquals(1, Heights.size());

        //Over 5 Heights

        HeightList = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            Height = new Height();
            Height.setCm(20.5f + i);
            Height.setDate(LocalDate.parse("2021-01-21").toDate());
            HeightList.add(Height);
        }

        Mockito.doReturn(HeightList).when(presenter).getChildSpecificHeights(TEST_BASE_ENTITY_ID, HeightList);

        Heights = presenter.getHeights(TEST_BASE_ENTITY_ID, HeightList);
        Assert.assertNotNull(Heights);
        Assert.assertEquals(5, Heights.size());


    }

    @Test
    public void testGetChildSpecificWeightsReturnsCorrectWeightList() {

        List<Weight> weightList = new ArrayList<>();
        Weight weight = new Weight();
        weight.setKg(1.9f);
        weight.setDate(LocalDate.parse("2021-01-15").toDate());
        weight.setBaseEntityId(TEST_BASE_ENTITY_ID);
        weightList.add(weight);

        weight = new Weight();
        weight.setKg(2.5f);
        weight.setDate(LocalDate.parse("2021-01-16").toDate());
        weight.setBaseEntityId(TEST_BASE_ENTITY_ID);
        weightList.add(weight);

        weight = new Weight();
        weight.setKg(3.2f);
        weight.setDate(LocalDate.parse("2021-01-17").toDate());
        weight.setBaseEntityId("OTHER_BASE_ENTITY_ID");
        weightList.add(weight);


        List<Weight> weights = presenter.getChildSpecificWeights(TEST_BASE_ENTITY_ID, weightList);

        Assert.assertNotNull(weights);
        Assert.assertEquals(2, weights.size());
    }

    @Test
    public void testGetChildSpecificHeightsReturnsCorrectHeightList() {

        List<Height> heightList = new ArrayList<>();
        Height height = new Height();
        height.setCm(19f);
        height.setDate(LocalDate.parse("2021-01-15").toDate());
        height.setBaseEntityId(TEST_BASE_ENTITY_ID);
        heightList.add(height);

        height = new Height();
        height.setCm(25f);
        height.setDate(LocalDate.parse("2021-01-16").toDate());
        height.setBaseEntityId("OTHER_BASE_ENTITY_ID");
        heightList.add(height);

        height = new Height();
        height.setCm(32f);
        height.setDate(LocalDate.parse("2021-01-17").toDate());
        height.setBaseEntityId(TEST_BASE_ENTITY_ID);
        heightList.add(height);


        List<Height> heights = presenter.getChildSpecificHeights(TEST_BASE_ENTITY_ID, heightList);

        Assert.assertNotNull(heights);
        Assert.assertEquals(2, heights.size());
    }

    @Test
    public void testGetWeightReturnsCorrectWeightList() {

        List<Weight> weightList = new ArrayList<>();
        Weight weight = new Weight();
        weight.setKg(2.8f);
        weight.setDate(LocalDate.parse("2021-01-28").toDate());
        weightList.add(weight);

        weight = new Weight();
        weight.setKg(3.4f);
        weight.setDate(LocalDate.parse("2021-01-22").toDate());
        weightList.add(weight);

        Mockito.doReturn(weightList).when(presenter).getChildSpecificWeights(TEST_BASE_ENTITY_ID, weightList);

        Weight weightRes = presenter.getWeight(weightList, LocalDate.parse("2021-01-22").toDate().getTime());
        Assert.assertNotNull(weightRes);
        Assert.assertEquals(3.4f, weightRes.getKg(), 0);

    }

    @Test
    public void testGetHeightReturnsCorrectHeightList() {

        List<Height> heightList = new ArrayList<>();
        Height height = new Height();
        height.setCm(14f);
        height.setDate(LocalDate.parse("2021-01-22").toDate());
        heightList.add(height);

        height = new Height();
        height.setCm(25.7f);
        height.setDate(LocalDate.parse("2021-01-21").toDate());
        heightList.add(height);

        Mockito.doReturn(heightList).when(presenter).getChildSpecificHeights(TEST_BASE_ENTITY_ID, heightList);

        Height heightRes = presenter.getHeight(heightList, LocalDate.parse("2021-01-21").toDate().getTime());
        Assert.assertNotNull(heightRes);
        Assert.assertEquals(25.7f, heightRes.getCm(), 0);

    }

    @Test
    public void testGetWeightWrapperConstructsValidWeightWrapper() {

        List<Weight> weightList = new ArrayList<>();
        Weight weight = new Weight();
        weight.setId(20001l);
        weight.setKg(3.8f);
        weight.setDate(LocalDate.parse("2020-01-20").toDate());
        weightList.add(weight);

        weight = new Weight();
        weight.setKg(5.4f);
        weight.setDate(LocalDate.parse("2021-01-23").toDate());
        weight.setId(20002l);
        weightList.add(weight);


        Mockito.doReturn(weightRepository).when(presenter).getWeightRepository();
        Mockito.doReturn(weightList).when(weightRepository).findByEntityId(TEST_BASE_ENTITY_ID);

        WrapperParam wrapperParam = new WrapperParam();
        wrapperParam.setPosition(LocalDate.parse("2021-01-23").toDate().getTime());
        wrapperParam.setBaseEntityId(TEST_BASE_ENTITY_ID);
        wrapperParam.setGender("Female");
        wrapperParam.setChildName("Mary Jay");
        wrapperParam.setOpenSrpId("400372");
        wrapperParam.setPmtctStatus("CNE");
        wrapperParam.setDuration("3");

        Photo photo = new Photo();
        photo.setFilePath(TEST_PHOTO_LINE_PATH);
        wrapperParam.setPhoto(photo);

        WeightWrapper weightWrapper = presenter.getWeightWrapper(wrapperParam);
        Assert.assertNotNull(weightWrapper);
        Assert.assertEquals("Female", weightWrapper.getGender());
        Assert.assertEquals("Mary Jay", weightWrapper.getPatientName());
        Assert.assertEquals("400372", weightWrapper.getPatientNumber());
        Assert.assertEquals("3", weightWrapper.getPatientAge());
        Assert.assertEquals("CNE", weightWrapper.getPmtctStatus());

        Assert.assertNotNull(weightWrapper.getPhoto());
        Assert.assertEquals(TEST_PHOTO_LINE_PATH, weightWrapper.getPhoto().getFilePath());

        Assert.assertEquals(20002l, weightWrapper.getDbKey(), 0);
        Assert.assertEquals(5.4f, weightWrapper.getWeight(), 0);

    }

    @Test
    public void testGetHeightWrapperConstructsValidHeightWrapper() {

        String photoFilePath = "file:///some-random-file-path-for-testing";

        List<Height> heightList = new ArrayList<>();
        Height height = new Height();
        height.setId(10001l);
        height.setCm(3.8f);
        height.setDate(LocalDate.parse("2020-01-20").toDate());
        heightList.add(height);

        height = new Height();
        height.setCm(5.4f);
        height.setDate(LocalDate.parse("2021-01-23").toDate());
        height.setId(10002l);
        heightList.add(height);


        Mockito.doReturn(heightRepository).when(presenter).getHeightRepository();
        Mockito.doReturn(heightList).when(heightRepository).findByEntityId(TEST_BASE_ENTITY_ID);

        WrapperParam wrapperParam = new WrapperParam();
        wrapperParam.setPosition(LocalDate.parse("2021-01-23").toDate().getTime());
        wrapperParam.setBaseEntityId(TEST_BASE_ENTITY_ID);
        wrapperParam.setGender("Male");
        wrapperParam.setChildName("Michael");
        wrapperParam.setOpenSrpId("2002");
        wrapperParam.setPmtctStatus("CNE");
        wrapperParam.setDuration("5");

        Photo photo = new Photo();
        photo.setFilePath(photoFilePath);
        wrapperParam.setPhoto(photo);


        HeightWrapper heightWrapper = presenter.getHeightWrapper(wrapperParam);
        Assert.assertNotNull(heightWrapper);
        Assert.assertEquals("Male", heightWrapper.getGender());
        Assert.assertEquals("Michael", heightWrapper.getPatientName());
        Assert.assertEquals("2002", heightWrapper.getPatientNumber());
        Assert.assertEquals("5", heightWrapper.getPatientAge());
        Assert.assertEquals("CNE", heightWrapper.getPmtctStatus());

        Assert.assertNotNull(heightWrapper.getPhoto());
        Assert.assertEquals(photoFilePath, heightWrapper.getPhoto().getFilePath());

        Assert.assertEquals(10002l, heightWrapper.getDbKey(), 0);
        Assert.assertEquals(5.4f, heightWrapper.getHeight(), 0);

    }

    @Test
    public void testGetWrapperParamReturnsValidWrapperFromDetailsParams() {
        String fname = "Test";
        String lname = "User";
        String gender = "Male";
        String zeirId = "100263K";
        String dob = "2015-09-12";
        String pmtct = "MSU";

        Map<String, String> childDetailsMap = new HashMap<>();
        childDetailsMap.put(Constants.KEY.FIRST_NAME, fname);
        childDetailsMap.put(Constants.KEY.LAST_NAME, lname);
        childDetailsMap.put(Constants.KEY.GENDER, gender);
        childDetailsMap.put(Constants.KEY.ZEIR_ID, zeirId);
        childDetailsMap.put(Constants.KEY.DOB, dob);
        childDetailsMap.put(BaseChildDetailTabbedActivity.PMTCT_STATUS_LOWER_CASE, pmtct);
        childDetailsMap.put(Constants.KEY.BASE_ENTITY_ID, TEST_BASE_ENTITY_ID);

        Mockito.doReturn("4").when(presenter).getChildAge(ArgumentMatchers.any(DateTime.class));
        Mockito.doReturn(imageRepository).when(presenter).getImageRepository();

        ProfileImage image = new ProfileImage();
        image.setFilepath(TEST_PHOTO_LINE_PATH);

        Mockito.doReturn(image).when(imageRepository).findByEntityId(TEST_BASE_ENTITY_ID);

        WrapperParam wrapperParam = presenter.getWrapperParam(childDetailsMap, 2000092l);

        Assert.assertNotNull(wrapperParam);
        Assert.assertEquals(String.format("%s %s", fname, lname), wrapperParam.getChildName());
        Assert.assertEquals(2000092l, wrapperParam.getPosition());
        Assert.assertEquals(gender, wrapperParam.getGender());
        Assert.assertEquals(zeirId, wrapperParam.getOpenSrpId());
        Assert.assertEquals(pmtct, wrapperParam.getPmtctStatus());
        Assert.assertEquals(TEST_BASE_ENTITY_ID, wrapperParam.getBaseEntityId());
        Assert.assertEquals("4", wrapperParam.getDuration());

        Assert.assertNotNull(wrapperParam.getDob());

        LocalDateTime localDateTime = LocalDateTime.fromDateFields(wrapperParam.getDob());
        Assert.assertEquals(12, localDateTime.getDayOfMonth());
        Assert.assertEquals(9, localDateTime.getMonthOfYear());
        Assert.assertEquals(2015, localDateTime.getYear());

    }

    @Test
    public void testGetProfilePhotoByClientReturnsCorrectPhotoInstance() {
        Mockito.doReturn(imageRepository).when(presenter).getImageRepository();

        ProfileImage image = new ProfileImage();
        image.setFilepath(TEST_PHOTO_LINE_PATH);

        Mockito.doReturn(image).when(imageRepository).findByEntityId(TEST_BASE_ENTITY_ID);

        Photo photo = presenter.getProfilePhotoByClient(EasyMap.mapOf(Constants.KEY.BASE_ENTITY_ID, TEST_BASE_ENTITY_ID));

        Assert.assertNotNull(photo);
        Assert.assertEquals(TEST_PHOTO_LINE_PATH, photo.getFilePath());
    }

    @Test
    public void testGetProfilePhotoByClientObjectReturnsCorrectPhotoInstance() {
        Mockito.doReturn(imageRepository).when(presenter).getImageRepository();

        ProfileImage image = new ProfileImage();
        image.setFilepath(TEST_PHOTO_LINE_PATH);

        Mockito.doReturn(image).when(imageRepository).findByEntityId(TEST_BASE_ENTITY_ID);

        CommonPersonObjectClient commonPersonObject = new CommonPersonObjectClient(TEST_BASE_ENTITY_ID, EasyMap.mapOf(Constants.KEY.BASE_ENTITY_ID, TEST_BASE_ENTITY_ID), "Test User");
        Photo photo = presenter.getProfilePhotoByClient(commonPersonObject);

        Assert.assertNotNull(photo);
        Assert.assertEquals(TEST_PHOTO_LINE_PATH, photo.getFilePath());
    }

}