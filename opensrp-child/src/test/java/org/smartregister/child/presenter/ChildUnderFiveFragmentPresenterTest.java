package org.smartregister.child.presenter;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.child.contract.ChildUnderFiveFragmentContract;
import org.smartregister.child.util.Constants;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 01/12/2020.
 */
public class ChildUnderFiveFragmentPresenterTest extends BasePowerMockUnitTest {

    private ChildUnderFiveFragmentContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new ChildUnderFiveFragmentPresenter();
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
}