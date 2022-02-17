package org.smartregister.child.widgets;

import android.view.View;

import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BasePowerMockUnitTest;

public class ChildCheckboxTextFactoryTest extends BasePowerMockUnitTest {

    private ChildCheckboxTextFactory checkboxTextFactory;

    @Mock
    private JsonFormFragment formFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        checkboxTextFactory = Mockito.spy(ChildCheckboxTextFactory.class);
    }

    @Test
    public void testGenericWidgetLayoutHookback() throws JSONException {
        JSONObject jsonObject = new JSONObject("{\"look_up\": \"true\", \"entity_id\": \"some_entity_id\"}");
        checkboxTextFactory.genericWidgetLayoutHookback(Mockito.mock(View.class), jsonObject, formFragment);
        Mockito.verify(formFragment, Mockito.atLeastOnce()).getLookUpMap();
    }

    @After
    public void tearDown() {
        formFragment = null;
        checkboxTextFactory = null;
    }

}