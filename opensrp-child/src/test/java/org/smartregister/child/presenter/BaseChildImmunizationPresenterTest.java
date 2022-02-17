package org.smartregister.child.presenter;

import static org.mockito.Mockito.doReturn;

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
import org.smartregister.child.contract.ChildImmunizationContract;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.AppProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ndegwamartin on 08/09/2020.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GrowthMonitoringLibrary.class, LocationHelper.class, ChildJsonFormUtils.class})
public class BaseChildImmunizationPresenterTest {

    private String TEST_BASE_ENTITY_ID = "some-test-base-entity-id";
    private CommonPersonObjectClient childDetails;

    @Mock
    private ChildImmunizationContract.View view;

    @Mock
    private GrowthMonitoringLibrary growthMonitoringLibrary;

    @Mock
    private HeightRepository heightRepository;

    @Mock
    private WeightRepository weightRepository;

    @Mock
    private Context context;

    @Mock
    private LocationHelper locationHelper;

    @Mock
    private CommonPersonObject commonPersonObject;

    @Mock
    private CommonPersonObjectClient commonPersonObjectClient;

    private BaseChildImmunizationPresenter presenter;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private AppProperties appProperties;

    @Mock
    private ChildMetadata metadata;

    @Mock
    private RegisterQueryProvider registerQueryProvider;

    @Mock
    private CommonRepository commonRepository;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        presenter = new BaseChildImmunizationPresenter(view);
        childDetails = getChildDetails();

        doReturn(appProperties).when(context).getAppProperties();
        doReturn(context).when(coreLibrary).context();
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);

        Mockito.when(registerQueryProvider.getChildDetailsTable()).thenCallRealMethod();
        doReturn(registerQueryProvider).when(metadata).getRegisterQueryProvider();
        doReturn(metadata).when(childLibrary).metadata();
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

    }

    @Test
    public void tearDown() {

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
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

    @Test
    public void getAllWeights() {

        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);
        PowerMockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        Mockito.doReturn(weightRepository).when(growthMonitoringLibrary).weightRepository();

        Weight weight = new Weight();
        weight.setKg(2.8f);
        Weight weight2 = new Weight();
        weight2.setKg(4.4f);
        List<Weight> weights = new ArrayList<>(Arrays.asList(new Weight[]{weight, weight2,}));

        Mockito.doReturn(weights).when(weightRepository).findByEntityId(TEST_BASE_ENTITY_ID);

        List<Weight> weightList = presenter.getAllWeights(childDetails);
        Assert.assertNotNull(weightList);
        Assert.assertEquals(3, weightList.size());
        Assert.assertEquals("2.8", weightList.get(0).getKg().toString());
        Assert.assertEquals("3.6", weightList.get(2).getKg().toString());
        Assert.assertEquals("4.4", weightList.get(1).getKg().toString());

    }

    @Test
    public void testActivateChildStatus() throws Exception {
        PowerMockito.mockStatic(LocationHelper.class);
        PowerMockito.mockStatic(ChildJsonFormUtils.class);

        doReturn(commonPersonObject).when(commonRepository).findByBaseEntityId(TEST_BASE_ENTITY_ID);
        doReturn(commonRepository).when(context).commonrepository(DBConstants.RegisterTable.CHILD_DETAILS);
        PowerMockito.when(commonPersonObjectClient.entityId()).thenReturn(TEST_BASE_ENTITY_ID);
        PowerMockito.when(commonPersonObject.getColumnmaps()).thenReturn(getChildDetailsMap());
        PowerMockito.when(LocationHelper.getInstance()).thenReturn(locationHelper);
        PowerMockito.when(ChildJsonFormUtils.updateClientAttribute(context, getChildDetails(), locationHelper, Constants.CHILD_STATUS.INACTIVE, false)).thenReturn(getChildDetailsMap());

        presenter.activateChildStatus(context, commonPersonObjectClient);

        Mockito.verify(commonPersonObjectClient).entityId();
        Mockito.verify(commonPersonObject).getColumnmaps();
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

    private HashMap<String, String> getChildDetailsMap() {
        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.FIRST_NAME, "John");
        childDetails.put(Constants.KEY.LAST_NAME, "Doe");
        childDetails.put(Constants.CHILD_STATUS.INACTIVE, "false");
        childDetails.put(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP, "true");

        return childDetails;
    }
}
