package org.smartregister.child.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public class BaseChildRegisterFragmentModelTest {
    @Mock
    private BaseChildRegisterFragmentModel baseChildRegisterFragmentModel;

    @Mock
    private Response<String> response;

    @Before
    public void setUp(){
        PowerMockito.mockStatic(Log.class);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetJsonObject() throws JSONException {
        baseChildRegisterFragmentModel = Mockito.spy(BaseChildRegisterFragmentModel.class);
        JSONObject jsonObject = new JSONObject().put("test","test");

        //Json array Null
        Assert.assertNull(baseChildRegisterFragmentModel.getJsonObject(null,0));

        //Json array len 0
        Assert.assertNull(baseChildRegisterFragmentModel.getJsonObject(new JSONArray(),0));

        //Json array len 0 with a jsonObject
        Assert.assertNotNull(baseChildRegisterFragmentModel.getJsonObject(new JSONArray().put(0,jsonObject),0));

        //Json Object Null
        Assert.assertNull(baseChildRegisterFragmentModel.getJsonObject(null,""));

        //Json Object Not null
        Assert.assertNotNull(baseChildRegisterFragmentModel.getJsonObject(new JSONObject().put("name",jsonObject),"name"));
    }

    @Test
    public void testGetJsonString() throws JSONException {
        baseChildRegisterFragmentModel = Mockito.spy(BaseChildRegisterFragmentModel.class);

        JSONObject jsonObject = new JSONObject().put("test","test");
        JSONObject jsonObjectWithValueEmpty = new JSONObject().put("test","");


        //Json Object Null
        Assert.assertEquals(baseChildRegisterFragmentModel.getJsonString(null,null),"");

        //Json Object not null but has no field
        Assert.assertEquals(baseChildRegisterFragmentModel.getJsonString(jsonObject,"some"),"");

        //Json Object not null but has field but string blank
        Assert.assertEquals(baseChildRegisterFragmentModel.getJsonString(jsonObjectWithValueEmpty,"test"),"");

        //Json Object not null but has field
        Assert.assertEquals(baseChildRegisterFragmentModel.getJsonString(jsonObject,"test"), "test");
    }

    @Test
    public void testGetJsonArray() throws JSONException {
        //Response null
        Assert.assertNull(baseChildRegisterFragmentModel.getJsonArray(response));

        //Expected Value
        JSONArray jsonArray = new JSONArray().put(0, new JSONObject().put("test","test"));
        Response<String> response = new Response<>(ResponseStatus.success, jsonArray.toString());
        Mockito.when(baseChildRegisterFragmentModel.getJsonArray(response)).thenReturn(jsonArray);
        Assert.assertNotNull(baseChildRegisterFragmentModel.getJsonArray(response));
    }

    @Test
    public void testGetFilterTextWithListAndFilterTitleNull(){
        String result = "<font color=#727272></font> <font color=#f0ab41>()</font>";
        Mockito.when(baseChildRegisterFragmentModel.getFilterText(null, null))
                .thenReturn(result);
        Assert.assertEquals(baseChildRegisterFragmentModel.getFilterText(null, null), result);
    }

    @Test
    public void testGetFilterTextWithListAndFilterTitleNonNull(){
        String result = "<font color=#727272>test</font> <font color=#f0ab41>(0)</font>";
        Mockito.when(baseChildRegisterFragmentModel.getFilterText(null, "test"))
                .thenReturn(result);
        Assert.assertEquals(baseChildRegisterFragmentModel.getFilterText(null, "test"), result);
    }

    @Test
    public void testGetSortTextWithFieldNull() {
        Assert.assertEquals(baseChildRegisterFragmentModel.getSortText(null),"");
    }

    @Test
    public void testGetSortTextWithFieldNotNull(){
        Field field = new Field();
        Mockito.when(baseChildRegisterFragmentModel.getSortText(field)).thenReturn("");
        Assert.assertEquals(baseChildRegisterFragmentModel.getSortText(field),"");

        //With displayName
        field.setDisplayName("test");
        Mockito.when(baseChildRegisterFragmentModel.getSortText(field)).thenReturn("(Sort: " + field.getDisplayName() + ")");
        Assert.assertEquals(baseChildRegisterFragmentModel.getSortText(field),"(Sort: " + field.getDisplayName() + ")");

        //With alias
        field.setDisplayName("");
        field.setDbAlias("test");
        Mockito.when(baseChildRegisterFragmentModel.getSortText(field)).thenReturn("(Sort: " + field.getDbAlias() + ")");
        Assert.assertEquals(baseChildRegisterFragmentModel.getSortText(field),"(Sort: " + field.getDbAlias() + ")");
    }

}
