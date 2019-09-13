package org.smartregister.child.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;


public class BaseChildAdvancedSearchModelTest {
    @Mock
    private BaseChildAdvancedSearchModel baseChildAdvancedSearchModel;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testRemoveLastSemicolonWithEmptyString() throws Exception {
        String result = Whitebox.invokeMethod(baseChildAdvancedSearchModel,"removeLastSemiColon","");
        Assert.assertEquals(result,"");
    }

    @Test
    public void testRemoveLastSemicolonWithExpectedValue() throws Exception {
        String result = Whitebox.invokeMethod(baseChildAdvancedSearchModel,"removeLastSemiColon",";");
        Assert.assertEquals(result,"");

    }

    @Test
    public void testCreateSearchString(){
        Map<String, String> map = new HashMap<>();
        BaseChildAdvancedSearchModel baseChildAdvancedSearchModel = Mockito.spy(BaseChildAdvancedSearchModel.class);
        Assert.assertEquals(baseChildAdvancedSearchModel.createSearchString(map),"");
    }
}
