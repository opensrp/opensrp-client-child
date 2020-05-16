package org.smartregister.child.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.repository.AllSharedPreferences;

@PrepareForTest({MoveToMyCatchmentUtilsTest.class, JsonFormUtils.class, ChildLibrary.class, CoreLibrary.class})
public class MoveToMyCatchmentUtilsTest {

    @Mock
    private org.smartregister.Context context;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context);
    }

    @Test
    public void testProcessMoveToCatchmentWhenEventIsZeroWithCoreLibraryInstanceNull(){
        PowerMockito
                .when(MoveToMyCatchmentUtils.processMoveToCatchment(Mockito.mock(Context.class), Mockito.mock(AllSharedPreferences.class), Mockito.mock(JSONObject.class)))
        .thenReturn(false);

        Assert.assertFalse(MoveToMyCatchmentUtils.processMoveToCatchment(Mockito.mock(Context.class), Mockito.mock(AllSharedPreferences.class), Mockito.mock(JSONObject.class)));
    }

    @Test
    public void testProcessMoveToCatchmentWhenEventIsMoreThanZeroWithCoreLibraryInstanceNull() throws JSONException {
        JSONObject jsonObject = Mockito.spy(JSONObject.class);
        jsonObject.put(Constants.NO_OF_EVENTS, 20);

        PowerMockito
                .when(MoveToMyCatchmentUtils.processMoveToCatchment(Mockito.mock(Context.class), Mockito.mock(AllSharedPreferences.class), jsonObject))
                .thenReturn(true);

        Assert.assertFalse(MoveToMyCatchmentUtils.processMoveToCatchment(Mockito.mock(Context.class), Mockito.mock(AllSharedPreferences.class), jsonObject));
    }
}
