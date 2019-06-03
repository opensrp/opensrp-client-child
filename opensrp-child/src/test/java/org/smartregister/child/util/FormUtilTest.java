package org.smartregister.child.util;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BaseUnitTest;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ndegwamartin on 2019-06-03.
 */


public class FormUtilTest extends BaseUnitTest {
    private FormUtils formUtils;

    @Mock
    Context context;

    @Mock
    AssetManager assetManager;

    @Mock
    InputStream inputStream;

    @Before
    public void setUp() throws IOException {

        MockitoAnnotations.initMocks(this);
        Mockito.when(context.getAssets()).thenReturn(assetManager);
        Mockito.when(assetManager.open("json.form/child_enrollment.json")).thenReturn(inputStream);

        formUtils = FormUtils.getInstance(context);
    }

    @Test
    public void testFormUtilInstantiatesCorrectly() {

        Assert.assertNotNull(formUtils);

    }

    @Test
    public void testGetFormJsonThrowsExceptionForFileNotExists() {

        JSONObject jsonObject = formUtils.getFormJson("non_existent_form.json");
        Assert.assertNull(jsonObject);

    }
}
