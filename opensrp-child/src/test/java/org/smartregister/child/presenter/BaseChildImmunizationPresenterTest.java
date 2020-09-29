package org.smartregister.child.presenter;

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
import org.smartregister.child.contract.ChildImmunizationContract;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.repository.HeightRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ndegwamartin on 08/09/2020.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GrowthMonitoringLibrary.class})
public class BaseChildImmunizationPresenterTest {

    private String TEST_BASE_ENTITY_ID = "some-test-base-entity-id";
    private CommonPersonObjectClient childDetails;

    @Mock
    private ChildImmunizationContract.View view;

    @Mock
    private GrowthMonitoringLibrary growthMonitoringLibrary;

    @Mock
    private HeightRepository heightRepository;


    private BaseChildImmunizationPresenter presenter;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        presenter = new BaseChildImmunizationPresenter(view);
        childDetails = getChildDetails();

    }

    @Test
    public void testGetView() {
        ChildImmunizationContract.View presenterView = presenter.getView();
        Assert.assertNotNull(presenterView);
        Assert.assertEquals(view, presenterView);
    }

    @Test
    public void getAllHeights() {

        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);
        PowerMockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        Mockito.doReturn(heightRepository).when(growthMonitoringLibrary).heightRepository();

        Height height = new Height();
        height.setCm(39f);
        Height height2 = new Height();
        height2.setCm(46f);
        Height height3 = new Height();
        height3.setCm(51f);
        List<Height> heights = new ArrayList<>(Arrays.asList(new Height[]{height, height2, height3}));

        Mockito.doReturn(heights).when(heightRepository).findByEntityId(TEST_BASE_ENTITY_ID);

        List<Height> heightList = presenter.getAllHeights(childDetails);
        Assert.assertNotNull(heightList);
        Assert.assertEquals(4, heightList.size());
        Assert.assertEquals("39.0", heightList.get(0).getCm().toString());
        Assert.assertEquals("46.0", heightList.get(1).getCm().toString());
        Assert.assertEquals("51.0", heightList.get(2).getCm().toString());
        Assert.assertEquals("48.0", heightList.get(3).getCm().toString());

    }

    private CommonPersonObjectClient getChildDetails() {

        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put("baseEntityId", TEST_BASE_ENTITY_ID);
        childDetails.put(Constants.KEY.DOB, "1990-05-09");
        childDetails.put(Constants.KEY.BIRTH_HEIGHT, "48");
        childDetails.put(Constants.KEY.BIRTH_WEIGHT, "3.6");

        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(TEST_BASE_ENTITY_ID, childDetails, Constants.KEY.CHILD);
        commonPersonObjectClient.setColumnmaps(childDetails);

        return commonPersonObjectClient;
    }
}