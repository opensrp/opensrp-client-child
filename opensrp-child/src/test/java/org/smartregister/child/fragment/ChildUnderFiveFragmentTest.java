package org.smartregister.child.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.R;
import org.smartregister.child.TestChildApp;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.domain.ExtraVaccineUpdateEvent;
import org.smartregister.child.shadows.CustomFontTextViewShadow;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.util.EasyMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = TestChildApp.class, shadows = CustomFontTextViewShadow.class)
public class ChildUnderFiveFragmentTest {
    private static final String TEST_KEY = "test_key";
    private static final String TEST_VAL = "test_val";
    private final String entityId = "some-entity-id";
    private final String vaccine = "BCG Booster";
    private final String vaccineDate = "2020-01-29";

    @Mock
    private LinearLayout serviceGroupCanvasLL;

    private LinearLayout boosterImmunizationsLayout;

    private AppCompatActivity appCompatActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        appCompatActivity = Robolectric.buildActivity(AppCompatActivity.class).create().resume().get();
        boosterImmunizationsLayout = new LinearLayout(appCompatActivity);
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

    @Test
    public void testThatLayoutIsChangedAfterUpdatingVaccine() {
        ChildUnderFiveFragment fragment = initFragment();
        fragment.onVaccineUpdated(new ExtraVaccineUpdateEvent(entityId, vaccine, vaccineDate));

        assertEquals(boosterImmunizationsLayout.getChildCount(), 2);
        View immunizationRow = boosterImmunizationsLayout.getChildAt(1);

        TextView vaccineTextView = immunizationRow.findViewById(R.id.name_tv);
        assertEquals(vaccineTextView.getText().toString(), vaccine);

        Button statusButton = immunizationRow.findViewById(R.id.status_iv);
        assertEquals(statusButton.getVisibility(), View.VISIBLE);

        TextView dateTextView = immunizationRow.findViewById(R.id.status_text_tv);
        assertEquals(dateTextView.getText().toString(), "29-01-2020");

    }

    @Test
    public void testThatLayoutIsNotChangedAfterUndoingVaccine(){
        //When vaccine is undone view should be cleared
        ChildUnderFiveFragment fragment = initFragment();
        fragment.onVaccineUpdated(new ExtraVaccineUpdateEvent(entityId, vaccine, vaccineDate, true));
        assertEquals(((ViewGroup) boosterImmunizationsLayout).getChildCount(), 0);

    }

    private ChildUnderFiveFragment initFragment() {
        ChildUnderFiveFragment fragment = spy(ChildUnderFiveFragment.newInstance(null));
        assertNotNull(fragment);
        doReturn(appCompatActivity).when(fragment).getActivity();
        doReturn(appCompatActivity).when(fragment).getContext();

        List<Triple<String, String, String>> immunizations = new ArrayList<Triple<String, String, String>>() {{
            add(Triple.of(entityId, vaccine, vaccineDate));
        }};

        Whitebox.setInternalState(fragment, "boosterImmunizationsLayout", boosterImmunizationsLayout);
        fragment.setBoosterImmunizations(immunizations);
        return fragment;
    }
}