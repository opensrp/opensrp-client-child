package org.smartregister.child.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.util.EasyMap;

import java.util.Map;

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
}