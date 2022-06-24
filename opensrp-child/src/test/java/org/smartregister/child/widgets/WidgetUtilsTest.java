package org.smartregister.child.widgets;

import android.view.View;

import androidx.test.core.app.ApplicationProvider;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.fragment.ChildFormFragment;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.MotherLookUpUtils;
import org.smartregister.util.AppProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WidgetUtilsTest extends BaseUnitTest {

    private ChildFormFragment formFragment;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private AppProperties appProperties;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        Mockito.when(childLibrary.getProperties()).thenReturn(appProperties);
        Mockito.when(appProperties.getProperty(Constants.PROPERTY.MOTHER_LOOKUP_SHOW_RESULTS_DURATION, Constants.MOTHER_LOOKUP_SHOW_RESULTS_DEFAULT_DURATION)).thenReturn("300");
        Mockito.when(appProperties.getProperty(Constants.PROPERTY.MOTHER_LOOKUP_UNDO_DURATION, Constants.MOTHER_LOOKUP_UNDO_DEFAULT_DURATION)).thenReturn("300");
        formFragment = Mockito.spy(ChildFormFragment.class);
    }

    @Test
    public void testHookupLookupDoesNotUpdateLookupMapWhenEntityIdIsNotSet() throws JSONException {
        MaterialEditText edtLastName = new MaterialEditText(ApplicationProvider.getApplicationContext());
        edtLastName.setTag(com.vijay.jsonwizard.R.id.key, MotherLookUpUtils.lastName);

        String formField = "{\"key\":\"Last_Name\",\"type\":\"edit_text\",\"look_up\":\"true\"}";
        JSONObject jsonObject = new JSONObject(formField);

        Map<String, List<View>> lookupMap = new HashMap<>();
        lookupMap.put(Constants.KEY.MOTHER, new ArrayList<>());
        Mockito.doReturn(lookupMap).when(formFragment).getLookUpMap();

        WidgetUtils.hookupLookup(edtLastName, jsonObject, formFragment);
        Assert.assertEquals(0, lookupMap.get(Constants.KEY.MOTHER).size());
    }

    @Test
    public void testHookupLookupInsertsViewIntoLookupMapWhenEntityIdIsSet() throws JSONException {
        MaterialEditText edtLastName = new MaterialEditText(ApplicationProvider.getApplicationContext());
        edtLastName.setTag(com.vijay.jsonwizard.R.id.key, MotherLookUpUtils.lastName);

        String formField = "{\"key\":\"Last_Name\",\"type\":\"edit_text\",\"entity_id\":\"mother\",\"look_up\":\"true\"}";
        JSONObject jsonObject = new JSONObject(formField);

        Map<String, List<View>> lookupMap = new HashMap<>();
        lookupMap.put(Constants.KEY.MOTHER, new ArrayList<View>());
        Mockito.doReturn(lookupMap).when(formFragment).getLookUpMap();

        WidgetUtils.hookupLookup(edtLastName, jsonObject, formFragment);
        Assert.assertEquals(1, lookupMap.get(Constants.KEY.MOTHER).size());
        Assert.assertTrue(lookupMap.get(Constants.KEY.MOTHER).contains(edtLastName));
    }
}
