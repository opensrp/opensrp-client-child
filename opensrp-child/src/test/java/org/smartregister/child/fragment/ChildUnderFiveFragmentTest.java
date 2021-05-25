package org.smartregister.child.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.util.EasyMap;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by ndegwamartin on 12/01/2021.
 */

public class ChildUnderFiveFragmentTest extends BaseUnitTest {
    private static final String TEST_KEY = "test_key";
    private static final String TEST_VAL = "test_val";

    @Mock
    private LinearLayout serviceGroupCanvasLL;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testNewInstance() {
        Bundle bundle = new Bundle();
        bundle.putString(TEST_KEY, TEST_VAL);
        ChildUnderFiveFragment fragment = ChildUnderFiveFragment.newInstance(bundle);

        assertNotNull(fragment);
        assertNotNull(fragment.getArguments());
        assertNotNull(fragment.getArguments().get(TEST_KEY));
        assertEquals(TEST_VAL, fragment.getArguments().get(TEST_KEY));
    }

    @Test
    public void testSetDetailsMap() {

        ChildUnderFiveFragment fragmentSpy = spy(ChildUnderFiveFragment.newInstance(null));
        assertNotNull(fragmentSpy);

        BaseChildDetailTabbedActivity activity = mock(BaseChildDetailTabbedActivity.class);
        doReturn(activity).when(fragmentSpy).getActivity();

        fragmentSpy.setDetailsMap(EasyMap.mapOf(TEST_KEY, TEST_VAL));

        ArgumentCaptor<Map<String, String>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(activity).setChildDetails(mapArgumentCaptor.capture());

        Map<String, String> captured = mapArgumentCaptor.getValue();
        assertNotNull(captured);
        assertEquals(1, captured.size());
        assertEquals(TEST_VAL, captured.get(TEST_KEY));


    }

    @Test
    public void testHideOrShowRecurringServices() {

        ChildUnderFiveFragment fragment = spy(ChildUnderFiveFragment.newInstance(null));
        assertNotNull(fragment);

        Whitebox.setInternalState(fragment, "serviceGroupCanvasLL", serviceGroupCanvasLL);

        fragment.hideOrShowRecurringServices();
        verify(serviceGroupCanvasLL).setVisibility(View.VISIBLE);

        fragment.showRecurringServices(false);
        fragment.hideOrShowRecurringServices();
        verify(serviceGroupCanvasLL).setVisibility(View.GONE);
    }

    @Test
    public void testLoadGrowthMonitoringViewShouldAddHeightAndWeightWidgetToView() {
        FragmentActivity sampleActivity = spy(Robolectric.buildActivity(FragmentActivity.class).get());
        sampleActivity.setContentView(R.layout.child_under_five_fragment);

        ChildUnderFiveFragment spyFragment = spy(ChildUnderFiveFragment.newInstance(new Bundle()));
        doReturn(sampleActivity).when(spyFragment).getActivity();

        LinearLayout fragmentContainer = spy(sampleActivity.findViewById(R.id.container));
        LinearLayout spyWeightValuesLayout = spy(fragmentContainer.findViewById(R.id.weightvalues));
        LinearLayout spyHeightValuesLayout = spy(fragmentContainer.findViewById(R.id.heightvalues));

        doReturn(spyWeightValuesLayout).when(fragmentContainer).findViewById(R.id.weightvalues);
        doReturn(spyHeightValuesLayout).when(fragmentContainer).findViewById(R.id.heightvalues);

        String baseEntityId = "3434";
        String provider = "demo";
        Map<String, String> detailsMap = new LinkedHashMap<>();
        detailsMap.put(Constants.KEY.DOB, "1615895228000");
        CommonPersonObjectClient childDetails = new CommonPersonObjectClient(baseEntityId, detailsMap, "John Doe");
        childDetails.setColumnmaps(detailsMap);

        ReflectionHelpers.setField(spyFragment, "fragmentContainer", fragmentContainer);
        ReflectionHelpers.setField(spyFragment, "childDetails", childDetails);
        ReflectionHelpers.setField(spyFragment, "detailsMap", detailsMap);
        ReflectionHelpers.setField(spyFragment, "monitorGrowth", true);

        Weight weight = new Weight();
        weight.setId(1L);
        weight.setAnmId(provider);
        weight.setBaseEntityId(baseEntityId);
        weight.setKg(23f);
        weight.setDate(DateTime.now().minusYears(1).toDate());

        Height height = new Height();
        height.setId(1L);
        height.setAnmId(provider);
        height.setBaseEntityId(baseEntityId);
        height.setCm(180f);
        height.setDate(DateTime.now().minusYears(1).toDate());

        spyFragment.loadGrowthMonitoringView(Collections.singletonList(weight),
                Collections.singletonList(height), false);

        //populate weight values on table layout
        verify(spyWeightValuesLayout).removeAllViews();
        verify(spyWeightValuesLayout, times(1)).addView(any(View.class));

        //populate height values on table layout
        verify(spyHeightValuesLayout).removeAllViews();
        verify(spyHeightValuesLayout, times(1)).addView(any(View.class));

        sampleActivity.finish();
    }
}