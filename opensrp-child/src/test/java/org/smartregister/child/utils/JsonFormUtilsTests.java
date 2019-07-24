package org.smartregister.child.utils;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class})
public class JsonFormUtilsTests extends BaseUnitTest {

    private JSONObject jsonObject;
    private static final String MY_KEY = "my_key";
    private static final String MY_LOCATION_ID = "mylo-cati-onid-endt-ifie-r00";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jsonObject = new JSONObject();
    }

    @Test
    public void isDateApproxWithNonNumberTest() throws Exception {
        boolean isApproximate = Whitebox.invokeMethod(JsonFormUtils.class, "isDateApprox", "1500208620000L");
        Assert.assertFalse(isApproximate);
    }

    @Test
    public void isDateApproxWithNumberTest() throws Exception {
        boolean isApproximate = Whitebox.invokeMethod(JsonFormUtils.class, "isDateApprox", "1");
        Assert.assertTrue(isApproximate);
    }

    @Test(expected = IllegalArgumentException.class)
    public void processAgeWithWrongDateFormatTest() throws Exception {
        JSONObject result = Whitebox.invokeMethod(JsonFormUtils.class, "processAge", "7/19/19", jsonObject);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.has(JsonFormUtils.VALUE));
        Assert.assertNotNull(result.get(JsonFormUtils.VALUE));
    }

    @Test
    public void processAgeTest() throws Exception {
        Whitebox.invokeMethod(JsonFormUtils.class, "processAge", "2017-01-01", jsonObject);
        Assert.assertTrue(jsonObject.has(JsonFormUtils.VALUE));
        Assert.assertNotNull(jsonObject.get(JsonFormUtils.VALUE));
        Assert.assertEquals(2, jsonObject.get(JsonFormUtils.VALUE));
    }

    @Test
    public void testAddLocationDefault() throws Exception {
        jsonObject.put(JsonFormConstants.KEY, MY_KEY);
        JSONArray array = new JSONArray();
        array.put(MY_LOCATION_ID);
        Whitebox.invokeMethod(JsonFormUtils.class, "addLocationDefault", MY_KEY, jsonObject, array.toString());
        Assert.assertTrue(jsonObject.has(JsonFormConstants.DEFAULT));
        Assert.assertNotNull(jsonObject.get(JsonFormConstants.DEFAULT));
        Assert.assertEquals(array, jsonObject.get(JsonFormConstants.DEFAULT));
    }
}
