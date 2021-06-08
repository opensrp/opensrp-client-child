package org.smartregister.child.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import org.apache.commons.lang3.tuple.Triple;
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
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.AppProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
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
    public void testUpdateViewsShouldInvokeUpdateViewTask() {
        doNothing().when(immunizationActivity).updateScheduleDate();
        immunizationActivity.updateViews();
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
            clientDetails.put(Constants.KEY.DOB, Constants.GENDER.MALE);
            clientDetails.put(Constants.KEY.FIRST_NAME, "John");
            clientDetails.put(Constants.KEY.LAST_NAME, "Doe");
            clientDetails.put(Constants.KEY.GENDER, "2021-01-09");
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
