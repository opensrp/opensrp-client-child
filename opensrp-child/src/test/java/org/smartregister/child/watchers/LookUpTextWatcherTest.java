package org.smartregister.child.watchers;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.EditText;

import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.EntityLookUp;
import org.smartregister.child.fragment.ChildFormFragment;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.shadows.ChildFormActivityShadow;
import org.smartregister.child.util.Constants;
import org.smartregister.event.Listener;
import org.smartregister.util.AppProperties;

import java.util.LinkedHashMap;
import java.util.Map;

public class LookUpTextWatcherTest extends BaseUnitTest {

    private JsonFormFragment formFragment;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private AppProperties appProperties;

    @Before
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);

        Mockito.doReturn("20").when(appProperties)
                .getProperty(Constants.PROPERTY.MOTHER_LOOKUP_SHOW_RESULTS_DURATION,
                        Constants.MOTHER_LOOKUP_SHOW_RESULTS_DEFAULT_DURATION);

        Mockito.doReturn("20").when(appProperties)
                .getProperty(Constants.PROPERTY.MOTHER_LOOKUP_UNDO_DURATION,
                        Constants.MOTHER_LOOKUP_UNDO_DEFAULT_DURATION);

        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null, null, null, true, new RegisterQueryProvider());
        Mockito.when(childLibrary.metadata()).thenReturn(metadata);

        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        formFragment = Mockito.spy(ChildFormFragment.class);
        ChildFormActivityShadow childFormActivityShadow =  Robolectric.buildActivity(ChildFormActivityShadow.class).get();
        String formJson = "{\"count\":\"1\",\"encounter_type\":\"Birth Registration\",\"step1\":{\"title\":\"{{child_enrollment.step1.title}}\"," +
                "\"fields\":[{\"key\":\"first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"first_name\"," +
                "\"entity_id\":\"mother\",\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"First name\",\"edit_type\":\"name\"}," +
                "{\"key\":\"last_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"person\",\"openmrs_entity_id\":\"last_name\",\"entity_id\":\"mother\"," +
                "\"look_up\":\"true\",\"type\":\"edit_text\",\"hint\":\"Last name\",\"edit_type\":\"name\"}]}}";
        childFormActivityShadow.setmJSONObject(new JSONObject(formJson));
        ReflectionHelpers.setField(childFormActivityShadow, "calculationLogicViews", new LinkedHashMap<>());
        ReflectionHelpers.setField(childFormActivityShadow, "skipLogicViews", new LinkedHashMap<>());
        ReflectionHelpers.setField(childFormActivityShadow, "constrainedViews", new LinkedHashMap<>());
        Mockito.doReturn(childFormActivityShadow).when(formFragment).getContext();

    }

    @Test
    public void testAfterTextChangedShouldFillLookUpMapCorrectlyBeforeLookup() {
        EditText edtFirstName = new EditText(RuntimeEnvironment.application);
        edtFirstName.setTag(com.vijay.jsonwizard.R.id.key, "first_name");
        edtFirstName.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);

        LookUpTextWatcher lookUpTextWatcher = new LookUpTextWatcher(formFragment, edtFirstName, "mother");
        LookUpTextWatcher lookUpTextWatcherSpy = Mockito.spy(lookUpTextWatcher);

        edtFirstName.addTextChangedListener(lookUpTextWatcherSpy);
        edtFirstName.setText("john");

        EditText edtLastName = new EditText(RuntimeEnvironment.application);
        edtLastName.setTag(com.vijay.jsonwizard.R.id.key, "last_name");
        edtLastName.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
        ReflectionHelpers.setField(lookUpTextWatcherSpy, "mView", edtLastName);

        edtLastName.addTextChangedListener(lookUpTextWatcherSpy);

        edtLastName.setText("doe");

        Mockito.doNothing().when(lookUpTextWatcherSpy)
                .initiateLookUp(Mockito.any(Listener.class));

        Map<String, EntityLookUp> lookUpMap = ReflectionHelpers.getField(lookUpTextWatcherSpy, "lookUpMap");
        Assert.assertFalse(lookUpMap.isEmpty());
        Assert.assertEquals(2, lookUpMap.get("mother").getMap().size());

        Assert.assertEquals("john", lookUpMap.get("mother").getMap().get("first_name"));
        Assert.assertEquals("doe", lookUpMap.get("mother").getMap().get("last_name"));

        Mockito.verify(lookUpTextWatcherSpy, Mockito.times(2))
                .initiateLookUp(Mockito.any(Listener.class));
    }


    @Test
    public void testAfterTextChangedShouldNotLookUpAfterIfAfterLookUpIsTrue() {
        View view = new View(RuntimeEnvironment.application);
        view.setTag(com.vijay.jsonwizard.R.id.key, "first_name");
        view.setTag(com.vijay.jsonwizard.R.id.after_look_up, true);
        LookUpTextWatcher lookUpTextWatcher = new LookUpTextWatcher(formFragment, view, "mother");
        LookUpTextWatcher lookUpTextWatcherSpy = Mockito.spy(lookUpTextWatcher);

        Mockito.doNothing().when(lookUpTextWatcherSpy).initiateLookUp(Mockito.any(Listener.class));
        Editable editable = new SpannableStringBuilder().append("test");
        lookUpTextWatcherSpy.afterTextChanged(editable);

        Map<String, EntityLookUp> lookUpMap = ReflectionHelpers.getField(lookUpTextWatcherSpy, "lookUpMap");
        Assert.assertTrue(lookUpMap.isEmpty());

        Mockito.verify(lookUpTextWatcherSpy, Mockito.never())
                .initiateLookUp(Mockito.any(Listener.class));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }

}