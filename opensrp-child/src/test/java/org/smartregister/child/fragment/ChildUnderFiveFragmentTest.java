package org.smartregister.child.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.shadows.ChildDetailTabbedActivityShadow;
import org.smartregister.child.shadows.CustomFontTextViewShadow;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.util.AppProperties;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.O_MR1, shadows = {CustomFontTextViewShadow.class})
public class ChildUnderFiveFragmentTest {

    private ChildUnderFiveFragment childUnderFiveFragment;

    private ChildDetailTabbedActivityShadow childDetailTabbedActivityShadow;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private GrowthMonitoringLibrary growthMonitoringLibrary;

    @Mock
    private AppProperties appProperties;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childUnderFiveFragment = ChildUnderFiveFragment.newInstance(new Bundle());
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        ReflectionHelpers.setStaticField(GrowthMonitoringLibrary.class, "instance", growthMonitoringLibrary);
        Mockito.when(growthMonitoringLibrary.getAppProperties()).thenReturn(appProperties);
        childDetailTabbedActivityShadow = Robolectric.buildActivity(ChildDetailTabbedActivityShadow.class).create().resume().get();
    }

    @Test
    public void testFragmentIsCreated() {
        Assert.assertNotNull(childUnderFiveFragment);
        childDetailTabbedActivityShadow.getSupportFragmentManager()
                .beginTransaction()
                .add(childUnderFiveFragment, "ChildUnderFiveFragment")
                .commit();
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(GrowthMonitoringLibrary.class, "instance", null);
    }
}