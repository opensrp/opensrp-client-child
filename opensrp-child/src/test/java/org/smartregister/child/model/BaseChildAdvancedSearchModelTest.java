package org.smartregister.child.model;

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
import org.smartregister.Context;
import org.smartregister.child.BuildConfig;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.util.Utils;
import org.smartregister.repository.Repository;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class})
public class BaseChildAdvancedSearchModelTest {
    @Mock
    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateSearchString() {
        Map<String, String> searchMap = new HashMap<>();
        searchMap.put("test", "test1");
        searchMap.put("test2", "");

        PowerMockito.mockStatic(Utils.class);

        PowerMockito.when(Utils.getTranslatedIdentifier(anyString())).thenReturn("test");
        PowerMockito.when(Utils.bold(anyString())).thenReturn("bold");

        BaseChildAdvancedSearchModel baseChildAdvancedSearchModel = Mockito.mock(BaseChildAdvancedSearchModel.class, Mockito.CALLS_REAL_METHODS);

        String searchString = baseChildAdvancedSearchModel.createSearchString(searchMap);
        Assert.assertEquals("test bold ; test bold", searchString);

        searchMap = new HashMap<>();
        searchString = baseChildAdvancedSearchModel.createSearchString(searchMap);
        Assert.assertEquals("", searchString);

        searchMap = new HashMap<>();
        searchMap.put("test", "test");
        PowerMockito.when(Utils.bold(anyString())).thenReturn("");

        searchString = baseChildAdvancedSearchModel.createSearchString(searchMap);
        Assert.assertEquals("test", searchString);

        searchMap = new HashMap<>();
        searchMap.put("test", "test");
        PowerMockito.when(Utils.bold(anyString())).thenReturn("bold;");

        searchString = baseChildAdvancedSearchModel.createSearchString(searchMap);
        Assert.assertEquals("test bold", searchString);
    }


    @Test
    public void testGetMainConditionString() {
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, true);
        metadata.updateChildRegister("test", "test",
                "test", "ChildRegister",
                "test", "test",
                "test",
                "test", "test");
        ChildLibrary.init(context, Mockito.mock(Repository.class), metadata, BuildConfig.VERSION_CODE, 1);

        BaseChildAdvancedSearchModel baseChildAdvancedSearchModel = Mockito.mock(BaseChildAdvancedSearchModel.class, Mockito.CALLS_REAL_METHODS);

        Map<String, String> editMap  = new HashMap<>();
        editMap.put("testKey", "testVal");
        String mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("testKey Like '%testVal%'", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put("start_date", "26-05-2020");
        editMap.put("end_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("ec_client.dob BETWEEN '26-05-2020' AND '26-05-2020'", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put("start_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("ec_client.dob >= '26-05-2020'", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put("end_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("ec_client.dob <= '26-05-2020'", mainConditionString.trim());
    }

}
