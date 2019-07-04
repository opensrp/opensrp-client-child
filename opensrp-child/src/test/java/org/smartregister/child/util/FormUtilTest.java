package org.smartregister.child.util;

import android.content.Context;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BaseUnitTest;

/**
 * Created by ndegwamartin on 2019-06-03.
 */

public class FormUtilTest extends BaseUnitTest {


    @Mock
    private Context context;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFormUtilInstantiatesCorrectly() {

        FormUtils formUtils = FormUtils.getInstance(context);
        Assert.assertNotNull(formUtils);

    }

    @Test
    public void testGetFormJsonThrowsExceptionForFileNotExists() {
        FormUtils formUtils = FormUtils.getInstance(context);

        JSONObject jsonObject = formUtils.getFormJson("non_existent_form.json");
        Assert.assertNull(jsonObject);

    }
}
