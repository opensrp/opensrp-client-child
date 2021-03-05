package org.smartregister.child.presenter;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.child.contract.ChildUnderFiveFragmentContract;
import org.smartregister.child.domain.WrapperParam;
import org.smartregister.child.util.Constants;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;

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

    private ChildUnderFiveFragmentContract.Presenter presenter;

    @Mock
    private GrowthMonitoringLibrary growthMonitoringLibrary;

    @Mock
    private HeightRepository heightRepository;

    @Mock
    private WeightRepository weightRepository;

    private String baseEntityId = "somebaseentyid";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(GrowthMonitoringLibrary.class, "instance", growthMonitoringLibrary);
        Mockito.doReturn(heightRepository).when(growthMonitoringLibrary).heightRepository();
        Mockito.doReturn(weightRepository).when(growthMonitoringLibrary).weightRepository();
        presenter = new ChildUnderFiveFragmentPresenter();
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
}