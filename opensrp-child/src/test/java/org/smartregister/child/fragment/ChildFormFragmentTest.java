package org.smartregister.child.fragment;

import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.customviews.MaterialSpinner;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.MotherLookUpUtils;
import org.smartregister.util.AppProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildFormFragmentTest extends BaseUnitTest {
    private ChildFormFragment formFragment;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private AppProperties appProperties;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        Mockito.when(childLibrary.getProperties()).thenReturn(appProperties);
        Mockito.when(appProperties.getProperty(Constants.PROPERTY.MOTHER_LOOKUP_SHOW_RESULTS_DURATION, Constants.MOTHER_LOOKUP_SHOW_RESULTS_DEFAULT_DURATION)).thenReturn("300");
        Mockito.when(appProperties.getProperty(Constants.PROPERTY.MOTHER_LOOKUP_UNDO_DURATION, Constants.MOTHER_LOOKUP_UNDO_DEFAULT_DURATION)).thenReturn("300");
        formFragment = Mockito.spy(ChildFormFragment.class);
    }

    private Map<String, List<View>> getLookUpMap() {
        Map<String, List<View>> lookupMap = new HashMap<>();

        MaterialEditText edtFirstName = new MaterialEditText(RuntimeEnvironment.application);
        edtFirstName.setTag(com.vijay.jsonwizard.R.id.key, MotherLookUpUtils.firstName);
        edtFirstName.setText("Jane");

        MaterialEditText edtLastName = new MaterialEditText(RuntimeEnvironment.application);
        edtLastName.setTag(com.vijay.jsonwizard.R.id.key, MotherLookUpUtils.lastName);
        edtLastName.setText("Doe");

        MaterialEditText edtNationalId = new MaterialEditText(RuntimeEnvironment.application);
        edtNationalId.setTag(com.vijay.jsonwizard.R.id.key, MotherLookUpUtils.MOTHER_GUARDIAN_NRC);
        edtNationalId.setText("23322-23");

        MaterialEditText edtDob = new MaterialEditText(RuntimeEnvironment.application);
        edtDob.setTag(com.vijay.jsonwizard.R.id.key, MotherLookUpUtils.birthDate);
        edtDob.setText("2018-01-15");

        List<View> viewList = new ArrayList<>(Arrays.asList((View) edtFirstName, edtLastName, edtNationalId));
        lookupMap.put(Constants.KEY.MOTHER, viewList);
        return lookupMap;
    }

    @Test
    public void testClearMotherLookUpShouldClearViews() throws Exception {
        Map<String, List<View>> lookupMap = getLookUpMap();

        ReflectionHelpers.setField(formFragment, "lookedUp", true);

        MaterialEditText motherDOBMaterialEditText = new MaterialEditText(RuntimeEnvironment.application);
        ReflectionHelpers.setField(formFragment, "motherDOBMaterialEditText", motherDOBMaterialEditText);

        MaterialSpinner materialSpinner = new MaterialSpinner(RuntimeEnvironment.application);
        ReflectionHelpers.setField(formFragment, "spinner", materialSpinner);

        AppCompatCheckBox appCompatCheckBox = new AppCompatCheckBox(RuntimeEnvironment.application);
        ReflectionHelpers.setField(formFragment, "compatCheckBox", appCompatCheckBox);

        Mockito.doReturn(lookupMap).when(formFragment).getLookUpMap();

        Mockito.doNothing().when(formFragment).writeMetaDataValue(Mockito.anyString(), Mockito.any(Map.class));
        Whitebox.invokeMethod(formFragment, "clearMotherLookUp");

        List<View> lookUpViews = lookupMap.get(Constants.KEY.MOTHER);

        assert lookUpViews != null;
        for (View view : lookUpViews) {
            if (view instanceof MaterialEditText) {
                MaterialEditText materialEditText = (MaterialEditText) view;
                Assert.assertTrue(materialEditText.getText().toString().isEmpty());
                Assert.assertTrue(materialEditText.isEnabled());
                Assert.assertFalse((Boolean) materialEditText.getTag(com.vijay.jsonwizard.R.id.after_look_up));
            }
        }
        Assert.assertFalse((Boolean) ReflectionHelpers.getField(formFragment, "lookedUp"));

        Assert.assertTrue(motherDOBMaterialEditText.isEnabled());
        Assert.assertTrue(motherDOBMaterialEditText.getText().toString().isEmpty());
        Assert.assertFalse((Boolean) motherDOBMaterialEditText.getTag(com.vijay.jsonwizard.R.id.after_look_up));


        Assert.assertTrue(materialSpinner.isEnabled());
        Assert.assertEquals(0, materialSpinner.getSelectedItemPosition());


        Assert.assertTrue(appCompatCheckBox.isEnabled());
        Assert.assertFalse(appCompatCheckBox.isChecked());

    }

    @Test
    public void testGetRelevantTextViewStringShouldReturnTextViewValue() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        TextView textView = new TextView(RuntimeEnvironment.application);
        textView.setTag(com.vijay.jsonwizard.R.id.key, "test_key");
        textView.setText("text");
        linearLayout.addView(textView);
        Mockito.doReturn(linearLayout).when(formFragment).getMainView();
        Assert.assertEquals("text", formFragment.getRelevantTextViewString("test_key"));
    }

    @Test
    public void testGetRelevantTextViewStringShouldReturnEmptyOnKeyMismatch() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        TextView textView = new TextView(RuntimeEnvironment.application);
        textView.setTag(com.vijay.jsonwizard.R.id.key, "test_key2");
        textView.setText("text");
        linearLayout.addView(textView);
        Mockito.doReturn(linearLayout).when(formFragment).getMainView();
        Assert.assertTrue(formFragment.getRelevantTextViewString("test_key").isEmpty());
    }

    @Test
    public void testUpdateRelevantTextViewStringShouldReturnTextPassed() throws Exception {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        TextView textView = new TextView(RuntimeEnvironment.application);
        textView.setTag(com.vijay.jsonwizard.R.id.key, "test_key");
        linearLayout.addView(textView);
        Mockito.doReturn(linearLayout).when(formFragment).getMainView();
        Whitebox.invokeMethod(formFragment, "updateRelevantTextView", linearLayout, "text", "test_key");
        Assert.assertEquals("text", textView.getText().toString());
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }
}