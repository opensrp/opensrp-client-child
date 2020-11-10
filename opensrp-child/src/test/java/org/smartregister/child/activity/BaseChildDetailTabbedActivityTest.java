package org.smartregister.child.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.view.Menu;

import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.Context;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.R;
import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.child.util.ChildDbUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({GrowthMonitoringLibrary.class, ChildDbUtils.class})
public class BaseChildDetailTabbedActivityTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private GrowthMonitoringLibrary growthMonitoringLibrary;

    @Mock
    private WeightRepository weightRepository;

    @Mock
    private HeightRepository heightRepository;

    @Mock
    private Weight weight;

    @Mock
    private Height height;

    @Mock
    private Context opensrpContext;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateOptionsMenuShouldDisableMenuItemIfLessThan3Months() {
        List<Vaccine> vaccineList = new ArrayList<>();
        Vaccine vaccine = new Vaccine();
        DateTime dateTime = new DateTime();
        vaccine.setCreatedAt(dateTime.toDate());
        vaccineList.add(vaccine);
        List<ServiceRecord> serviceRecordList = new ArrayList<>();
        ServiceRecord serviceRecord = new ServiceRecord();
        serviceRecord.setCreatedAt(dateTime.toDate());
        serviceRecordList.add(serviceRecord);
        List<Weight> weightList = new ArrayList<>();
        Weight weight = new Weight();
        weight.setCreatedAt(dateTime.toDate());
        Weight weight2 = new Weight();
        weight2.setCreatedAt(dateTime.toDate());
        weightList.add(weight);
        weightList.add(weight2);

        Menu menu = new MenuBuilder(RuntimeEnvironment.application);
        menu.add(0, R.id.immunization_data, 1, "title1");
        menu.add(0, R.id.recurring_services_data, 1, "title2");
        menu.add(0, R.id.weight_data, 1, "title3");

        TestBaseChildDetailTabbedActivity.setOverflow(menu);
        TestBaseChildDetailTabbedActivity.updateOptionsMenu(vaccineList, serviceRecordList, weightList, null);
        Assert.assertTrue(menu.findItem(R.id.immunization_data).isEnabled());
        Assert.assertTrue(menu.findItem(R.id.recurring_services_data).isEnabled());
        Assert.assertTrue(menu.findItem(R.id.weight_data).isEnabled());
    }


    private static class TestBaseChildDetailTabbedActivity extends BaseChildDetailTabbedActivity {

        @Override
        protected BaseChildRegistrationDataFragment getChildRegistrationDataFragment() {
            return null;
        }

        @Override
        protected void navigateToRegisterActivity() {
            Timber.e("navigateToRegisterActivity");
        }

        @Override
        public void onUniqueIdFetched(Triple<String, Map<String, String>, String> triple, String entityId) {
            Timber.e("onUniqueIdFetched");
        }

        @Override
        public void onNoUniqueId() {
            Timber.e("onNoUniqueId");
        }
    }

    @Test
    public void testUpdateWeightWrapper() throws Exception {
        BaseChildDetailTabbedActivity baseChildDetailTabbedActivity = Mockito.mock(BaseChildDetailTabbedActivity.class, Mockito.CALLS_REAL_METHODS);
        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);

        Mockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        Mockito.when(growthMonitoringLibrary.weightRepository()).thenReturn(weightRepository);
        Mockito.when(weightRepository.find(any(Long.class))).thenReturn(weight);
        Mockito.doReturn(opensrpContext).when(baseChildDetailTabbedActivity).getOpenSRPContext();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        Mockito.doReturn("user-1").when(allSharedPreferences).fetchRegisteredANM();

        Method updateWeightWrapper = BaseChildDetailTabbedActivity.class.getDeclaredMethod("updateWeightWrapper", WeightWrapper.class);
        updateWeightWrapper.setAccessible(true);

        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.FIRST_NAME, "John");
        childDetails.put(Constants.KEY.LAST_NAME, "Doe");

        Whitebox.setInternalState(baseChildDetailTabbedActivity, "childDetails", getChildDetails());

        WeightWrapper weightWrapper = new WeightWrapper();
        weightWrapper.setDbKey(3l);
        weightWrapper.setUpdatedWeightDate(new DateTime().minusHours(1), true);
        weightWrapper.setWeight(45f);

        updateWeightWrapper.invoke(baseChildDetailTabbedActivity, weightWrapper);

        verify(weight).setKg(45f);
        verify(weight).setBaseEntityId("id-1");
        verify(weight).setOutOfCatchment(0);
        verify(weight).setAnmId("user-1");
    }

    @Test
    public void testUpdateHeightWrapper() throws Exception {
        BaseChildDetailTabbedActivity baseChildDetailTabbedActivity = Mockito.mock(BaseChildDetailTabbedActivity.class, Mockito.CALLS_REAL_METHODS);
        PowerMockito.mockStatic(GrowthMonitoringLibrary.class);

        Mockito.when(GrowthMonitoringLibrary.getInstance()).thenReturn(growthMonitoringLibrary);
        Mockito.when(growthMonitoringLibrary.heightRepository()).thenReturn(heightRepository);
        Mockito.when(heightRepository.find(any(Long.class))).thenReturn(height);
        Mockito.doReturn(opensrpContext).when(baseChildDetailTabbedActivity).getOpenSRPContext();
        Mockito.doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
        Mockito.doReturn("user-1").when(allSharedPreferences).fetchRegisteredANM();

        Method updateHeightWrapper = BaseChildDetailTabbedActivity.class.getDeclaredMethod("updateHeightWrapper", HeightWrapper.class);
        updateHeightWrapper.setAccessible(true);

        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put(Constants.KEY.FIRST_NAME, "John");
        childDetails.put(Constants.KEY.LAST_NAME, "Doe");

        Whitebox.setInternalState(baseChildDetailTabbedActivity, "childDetails", getChildDetails());

        HeightWrapper heightWrapper = new HeightWrapper();
        heightWrapper.setDbKey(3l);
        heightWrapper.setUpdatedHeightDate(new DateTime().minusHours(1), true);
        heightWrapper.setHeight(45f);

        updateHeightWrapper.invoke(baseChildDetailTabbedActivity, heightWrapper);

        verify(height).setCm(45f);
        verify(height).setBaseEntityId("id-1");
        verify(height).setOutOfCatchment(0);
        verify(height).setAnmId("user-1");
    }

    private CommonPersonObjectClient getChildDetails() {

        HashMap<String, String> childDetails = new HashMap<>();
        childDetails.put("baseEntityId", "id-1");
        childDetails.put(Constants.KEY.DOB, "1990-05-09");
        childDetails.put(Constants.KEY.BIRTH_HEIGHT, "48");
        childDetails.put(Constants.KEY.BIRTH_WEIGHT, "3.6");

        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient("id-1", childDetails, Constants.KEY.CHILD);
        commonPersonObjectClient.setColumnmaps(childDetails);

        return commonPersonObjectClient;
    }

    @Test
    public void testInitLoadChildDetails() throws Exception {
        BaseChildDetailTabbedActivity baseChildDetailTabbedActivity = Mockito.mock(BaseChildDetailTabbedActivity.class, Mockito.CALLS_REAL_METHODS);
        PowerMockito.mockStatic(ChildDbUtils.class);

        Method initLoadChildDetails = BaseChildDetailTabbedActivity.class.getDeclaredMethod("initLoadChildDetails");
        initLoadChildDetails.setAccessible(true);

        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_KEY.LOCATION_ID, "loc-1");
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, "id-1");

        doReturn(intent).when(baseChildDetailTabbedActivity).getIntent();
        when(ChildDbUtils.fetchCommonPersonObjectClientByBaseEntityId("id-1")).thenReturn(getChildDetails());

        Bundle res = (Bundle) initLoadChildDetails.invoke(baseChildDetailTabbedActivity);
        Assert.assertEquals("loc-1", res.getString(Constants.INTENT_KEY.LOCATION_ID));
        Assert.assertEquals("id-1", res.getString(Constants.INTENT_KEY.BASE_ENTITY_ID));
    }
}