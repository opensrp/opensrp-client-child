package org.smartregister.child.widgets;

import android.content.Context;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.smartregister.child.BaseUnitTest;

public class ChildDatePickerFactoryTest extends BaseUnitTest {

    private ChildDatePickerFactory childDatePickerFactory;

    @Mock
    private JsonFormFragment formFragment;

    @Spy
    private Context context;

    private MaterialEditText materialEditText;

    @Mock
    private TextView durationTextView;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childDatePickerFactory = Mockito.spy(ChildDatePickerFactory.class);
        materialEditText = Mockito.mock(MaterialEditText.class);
    }

    @Test
    public void testAttachLayout() throws JSONException {
        JSONObject jsonObject = new JSONObject("{\"look_up\": \"true\", \"entity_id\": \"some_entity_id\"}");
        childDatePickerFactory.attachLayout("step1", context, formFragment, jsonObject, materialEditText, durationTextView);
        Mockito.verify(formFragment, Mockito.atLeastOnce()).getLookUpMap();
    }

    @After
    public void tearDown() {
        childDatePickerFactory = null;
        materialEditText = null;
    }
}