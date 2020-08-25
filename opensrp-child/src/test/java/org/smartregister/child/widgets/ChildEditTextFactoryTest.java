package org.smartregister.child.widgets;

import android.content.Context;
import android.widget.ImageView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

@Ignore("Fix null pointer exception")
public class ChildEditTextFactoryTest {

    private ChildEditTextFactory childEditTextFactory;

    @Mock
    private JsonFormFragment formFragment;

    @Spy
    private Context context;

    private MaterialEditText materialEditText;

    private ImageView imageView;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childEditTextFactory = Mockito.spy(ChildEditTextFactory.class);
        materialEditText = Mockito.mock(MaterialEditText.class);
        imageView = Mockito.spy(new ImageView(context));
    }

    @Test
    public void testAttachLayout() throws Exception {
        JSONObject jsonObject = new JSONObject("{\"value\": \"text\", \"look_up\":\"true\",\"entity_id\":\"some_entity_id\",\"key\":\"user_first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"User First name\",\"edit_type\":\"name\"}");
        childEditTextFactory.attachLayout("step1", context, formFragment, jsonObject, materialEditText, imageView);
        Mockito.verify(formFragment, Mockito.atLeastOnce()).getLookUpMap();
    }
}