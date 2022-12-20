package org.smartregister.child.widgets;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.widget.ImageView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.utils.AppExecutors;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Executor;

public class ChildEditTextFactoryTest {

    private ChildEditTextFactory childEditTextFactory;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private JsonFormActivity formActivity;

    private MaterialEditText materialEditText;

    private ImageView imageView;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        childEditTextFactory = Mockito.spy(ChildEditTextFactory.class);
        materialEditText = Mockito.mock(MaterialEditText.class);
        imageView = Mockito.spy(new ImageView(formActivity));
    }

    @Test
    public void testAttachLayout() throws Exception {
        materialEditText.setText("Text");
        JSONObject jsonObject = new JSONObject("{\"value\": \"text\", \"look_up\":\"true\",\"entity_id\":\"some_entity_id\",\"key\":\"user_first_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"User First name\",\"edit_type\":\"name\"}");
        Editable editable = new SpannableStringBuilder("text");
        Mockito.doReturn(editable).when(materialEditText).getText();
        Mockito.doReturn(formActivity).when(formFragment).getJsonApi();
        AppExecutors mockAppExecutors = Mockito.mock(AppExecutors.class);
        Executor mockExecutor = Mockito.mock(Executor.class);
        Mockito.doNothing().when(mockExecutor).execute(Mockito.any(Runnable.class));
        Mockito.doReturn(mockExecutor).when(mockAppExecutors).mainThread();
        Mockito.doReturn(mockAppExecutors).when(formActivity).getAppExecutors();
        Mockito.doNothing().when(formActivity).addSkipLogicView(materialEditText);
        Mockito.doNothing().when(formActivity).addCalculationLogicView(materialEditText);
        Mockito.doNothing().when(formActivity).addConstrainedView(materialEditText);
        childEditTextFactory.attachLayout("step1", formActivity, formFragment, jsonObject, materialEditText, imageView);
        Mockito.verify(formFragment, Mockito.atLeastOnce()).getLookUpMap();
    }
}