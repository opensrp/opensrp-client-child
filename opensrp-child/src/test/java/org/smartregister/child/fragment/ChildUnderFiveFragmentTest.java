package org.smartregister.child.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.child.R;
import org.smartregister.child.TestChildApp;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.domain.ExtraVaccineUpdateEvent;
import org.smartregister.child.shadows.CustomFontTextViewShadow;
import org.smartregister.util.EasyMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        Assert.assertNotNull(fragment);
        Assert.assertNotNull(fragment.getArguments());
        Assert.assertNotNull(fragment.getArguments().get(TEST_KEY));
        Assert.assertEquals(TEST_VAL, fragment.getArguments().get(TEST_KEY));
    }

    @Test
    public void testSetDetailsMap() {

        ChildUnderFiveFragment fragmentSpy = Mockito.spy(ChildUnderFiveFragment.newInstance(null));
        Assert.assertNotNull(fragmentSpy);

        BaseChildDetailTabbedActivity activity = Mockito.mock(BaseChildDetailTabbedActivity.class);
        Mockito.doReturn(activity).when(fragmentSpy).getActivity();

        fragmentSpy.setDetailsMap(EasyMap.mapOf(TEST_KEY, TEST_VAL));

        ArgumentCaptor<Map<String, String>> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(activity).setChildDetails(mapArgumentCaptor.capture());

        Map<String, String> captured = mapArgumentCaptor.getValue();
        Assert.assertNotNull(captured);
        Assert.assertEquals(1, captured.size());
        Assert.assertEquals(TEST_VAL, captured.get(TEST_KEY));


    }

    @Test
    public void testHideOrShowRecurringServices() {

        ChildUnderFiveFragment fragment = Mockito.spy(ChildUnderFiveFragment.newInstance(null));
        Assert.assertNotNull(fragment);

        Whitebox.setInternalState(fragment, "serviceGroupCanvasLL", serviceGroupCanvasLL);

        fragment.hideOrShowRecurringServices();
        Mockito.verify(serviceGroupCanvasLL).setVisibility(View.VISIBLE);

        fragment.showRecurringServices(false);
        fragment.hideOrShowRecurringServices();
        Mockito.verify(serviceGroupCanvasLL).setVisibility(View.GONE);
    }

    @Test
    public void testThatLayoutIsChangedAfterUpdatingVaccine() {
        ChildUnderFiveFragment fragment = initFragment();
        fragment.onVaccineUpdated(new ExtraVaccineUpdateEvent(entityId, vaccine, vaccineDate));

        Assert.assertEquals(boosterImmunizationsLayout.getChildCount(), 2);
        View immunizationRow = boosterImmunizationsLayout.getChildAt(1);

        TextView vaccineTextView = immunizationRow.findViewById(R.id.name_tv);
        Assert.assertEquals(vaccineTextView.getText().toString(), vaccine);

        Button statusButton = immunizationRow.findViewById(R.id.status_iv);
        Assert.assertEquals(statusButton.getVisibility(), View.VISIBLE);

        TextView dateTextView = immunizationRow.findViewById(R.id.status_text_tv);
        Assert.assertEquals(dateTextView.getText().toString(), "29-01-2020");

    }

    @Test
    public void testThatLayoutIsNotChangedAfterUndoingVaccine(){
        //When vaccine is undone view should be cleared
        ChildUnderFiveFragment fragment = initFragment();
        fragment.onVaccineUpdated(new ExtraVaccineUpdateEvent(entityId, vaccine, vaccineDate, true));
        Assert.assertEquals(((ViewGroup) boosterImmunizationsLayout).getChildCount(), 0);

    }

    private ChildUnderFiveFragment initFragment() {
        ChildUnderFiveFragment fragment = Mockito.spy(ChildUnderFiveFragment.newInstance(null));
        Assert.assertNotNull(fragment);
        Mockito.doReturn(appCompatActivity).when(fragment).getActivity();
        Mockito.doReturn(appCompatActivity).when(fragment).getContext();

        List<Triple<String, String, String>> immunizations = new ArrayList<Triple<String, String, String>>() {{
            add(Triple.of(entityId, vaccine, vaccineDate));
        }};

        Whitebox.setInternalState(fragment, "boosterImmunizationsLayout", boosterImmunizationsLayout);
        fragment.setBoosterImmunizations(immunizations);
        return fragment;
    }
}