package org.smartregister.child.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Photo;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.AppProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class BaseChildImmunizationActivityRobolectricTest extends BaseUnitTest {

    private TestBaseChildImmunizationActivity immunizationActivity;

    @Mock
    private SyncStatusBroadcastReceiver syncStatusBroadcastReceiver;

    @Mock
    private ImmunizationLibrary immunizationLibrary;

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private GrowthMonitoringLibrary growthMonitoringLibrary;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private AppProperties appProperties;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);


        ReflectionHelpers.setStaticField(SyncStatusBroadcastReceiver.class, "singleton", syncStatusBroadcastReceiver);

        doReturn(vaccineRepository).when(immunizationLibrary).vaccineRepository();
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", immunizationLibrary);

        doReturn(appProperties).when(childLibrary).getProperties();

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        ReflectionHelpers.setStaticField(GrowthMonitoringLibrary.class, "instance", growthMonitoringLibrary);

        RegisterClickables registerClickables = new RegisterClickables();

        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, "wewr34");
        intent.putExtra(Constants.INTENT_KEY.EXTRA_REGISTER_CLICKABLES, registerClickables);

        immunizationActivity = spy(Robolectric.buildActivity(TestBaseChildImmunizationActivity.class, intent).create().get());
    }

    @Test
    public void testUpdateViewsShouldInvokeUpdateViewTask() throws InterruptedException {
        doNothing().when(immunizationActivity).updateScheduleDate();
        immunizationActivity.updateViews();
        Thread.sleep(ASYNC_TIMEOUT);
        verify(immunizationActivity).startUpdateViewTask();
    }

    @Test
    public void testSetUpFloatingActionButtonShouldShowButtonIfNfcFeatureEnabled() throws Exception {
        LinearLayout floatingActionButton = spy(immunizationActivity.findViewById(R.id.fab));

        ReflectionHelpers.setField(immunizationActivity, "floatingActionButton", floatingActionButton);

        doReturn(true).when(appProperties).getPropertyBoolean(ChildAppProperties.KEY.FEATURE_NFC_CARD_ENABLED);

        WhiteboxImpl.invokeMethod(immunizationActivity, "setUpFloatingActionButton");

        verify(floatingActionButton).setOnClickListener(eq(immunizationActivity));

        verify(floatingActionButton).setVisibility(eq(View.VISIBLE));
    }

    @Test
    public void testUpdateGrowthViewsShouldUpdateHeightAndWeightViews() throws Exception {
        Weight lastUnsyncedWeight = new Weight();
        lastUnsyncedWeight.setId(2L);
        lastUnsyncedWeight.setKg(5f);
        lastUnsyncedWeight.setUpdatedAt(DateTime.now().minusDays(1).getMillis());

        Height lastUnsyncedHeight = new Height();
        lastUnsyncedHeight.setId(3L);
        lastUnsyncedHeight.setCm(20f);
        lastUnsyncedHeight.setUpdatedAt(DateTime.now().minusDays(1).getMillis());

        View spyRecordGrowth = immunizationActivity.findViewById(R.id.record_growth);
        TextView spyRecordWeightText = immunizationActivity.findViewById(R.id.record_growth_text);
        ImageButton spyGrowthChartButton = immunizationActivity.findViewById(R.id.growth_chart_button);
        ReflectionHelpers.setField(immunizationActivity, "recordGrowth", spyRecordGrowth);
        ReflectionHelpers.setField(immunizationActivity, "recordWeightText", spyRecordWeightText);
        ReflectionHelpers.setField(immunizationActivity, "growthChartButton", spyGrowthChartButton);

        ReflectionHelpers.setField(immunizationActivity, "monitorGrowth", true);

        WhiteboxImpl.invokeMethod(immunizationActivity, "updateGrowthViews", lastUnsyncedWeight, lastUnsyncedHeight, true);

        verify(immunizationActivity).updateRecordGrowthMonitoringViews(any(WeightWrapper.class), nullable(HeightWrapper.class), eq(true));

        assertTrue(spyGrowthChartButton.hasOnClickListeners());
        assertEquals("5.0 kg, 20.0 cm", spyRecordWeightText.getText().toString());
        assertNotNull(spyRecordGrowth.getTag(R.id.weight_wrapper));
        assertNotNull(spyRecordGrowth.getTag(R.id.height_wrapper));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(SyncStatusBroadcastReceiver.class, "singleton", null);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(GrowthMonitoringLibrary.class, "instance", null);

        immunizationActivity.finish();
    }


    public static class TestBaseChildImmunizationActivity extends BaseChildImmunizationActivity {


        @Override
        protected void goToRegisterPage() {
            //Do nothing
        }

        @Override
        protected int getDrawerLayoutId() {
            return 0;
        }

        @Override
        public void launchDetailActivity(Context fromContext, CommonPersonObjectClient childDetails, RegisterClickables registerClickables) {
            //Do nothing
        }

        @Override
        protected Activity getActivity() {
            return null;
        }

        @Override
        public boolean isLastModified() {
            return false;
        }

        @Override
        public void setLastModified(boolean lastModified) {
            //Do nothing
        }

        @Override
        public void onClick(View view) {
            //Do nothing
        }

        @Override
        public void onUniqueIdFetched(Triple<String, Map<String, String>, String> triple, String entityId) {
            //Do nothing
        }

        @Override
        public void onNoUniqueId() {
            //Do nothing
        }

        @Override
        public void onRegistrationSaved(boolean isEdit) {
            //Do nothing
        }

        @Override
        public org.smartregister.Context getOpenSRPContext() {
            AllSharedPreferences allSharedPreferences = mock(AllSharedPreferences.class);
            org.smartregister.Context opensrpContext = mock(org.smartregister.Context.class);
            doReturn(allSharedPreferences).when(opensrpContext).allSharedPreferences();
            doReturn(null).when(opensrpContext).alertService();
            return opensrpContext;
        }

        @Override
        protected CommonPersonObjectClient getChildDetails(String caseId) {
            Map<String, String> clientDetails = new LinkedHashMap<>();
            clientDetails.put(Constants.KEY.FIRST_NAME, "John");
            clientDetails.put(Constants.KEY.LAST_NAME, "Doe");
            clientDetails.put(Constants.KEY.ZEIR_ID, "2045");
            clientDetails.put(Constants.KEY.MOTHER_FIRST_NAME, "Jane");
            clientDetails.put(Constants.KEY.MOTHER_LAST_NAME, "Doe");
            clientDetails.put(Constants.KEY.GENDER, Constants.GENDER.MALE);
            clientDetails.put(Constants.KEY.DOB, "2021-01-09");
            CommonPersonObjectClient client = new CommonPersonObjectClient("23weq", clientDetails, "John Doe");
            client.setColumnmaps(clientDetails);
            return client;
        }

        @Override
        protected Photo getProfilePhotoByClient(CommonPersonObjectClient childDetails) {
            return mock(Photo.class);
        }
    }
}
