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
import org.smartregister.child.util.Constants;
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
                null,null, true);
        metadata.updateChildRegister("test", "test",
                "test", "ChildRegister",
                "test", "test",
                "test",
                "test", "test");
        ChildLibrary.init(context, Mockito.mock(Repository.class), metadata, 1, 1);

        BaseChildAdvancedSearchModel baseChildAdvancedSearchModel = Mockito.mock(BaseChildAdvancedSearchModel.class, Mockito.CALLS_REAL_METHODS);

        Map<String, String> editMap  = new HashMap<>();
        editMap.put("testKey", "testVal");
        editMap.put("testKey2", "testVal2");
        String mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("testKey2 Like '%testVal2%' AND testKey Like '%testVal%' AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put("start_date", "26-05-2020");
        editMap.put("end_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("ec_client.dob BETWEEN '2020-05-26' AND '2020-05-26' AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put("start_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("ec_client.dob >= '26-05-2020' AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put("end_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("ec_client.dob <= '26-05-2020' AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());
    }

    @Test
    public void testGetMainConditionStringWithCondition() {
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null,null, true);
        metadata.updateChildRegister("test", "test",
                "test", "ChildRegister",
                "test", "test",
                "test",
                "test", "test");
        ChildLibrary.init(context, Mockito.mock(Repository.class), metadata, 1, 1);
        BaseChildAdvancedSearchModel baseChildAdvancedSearchModel = Mockito.mock(BaseChildAdvancedSearchModel.class, Mockito.CALLS_REAL_METHODS);
        Map<String, String> editMap;
        String mainConditionString;

        editMap  = new HashMap<>();
        editMap.put("tesKey", "testVal");
        editMap.put("start_date", "26-05-2020");
        editMap.put("end_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("tesKey Like '%testVal%' AND ec_client.dob BETWEEN '2020-05-26' AND '2020-05-26' AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put("tesKey", "testVal");
        editMap.put("start_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("tesKey Like '%testVal%' AND ec_client.dob >= '26-05-2020' AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put("tesKey", "testVal");
        editMap.put("end_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("tesKey Like '%testVal%' AND ec_client.dob <= '26-05-2020' AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());
    }

    @Test
    public void testGetMainConditionStringMotherName() {
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null,null,  true);
        metadata.updateChildRegister("test", "test",
                "test", "ChildRegister",
                "test", "test",
                "test",
                "test", "test");
        ChildLibrary.init(context, Mockito.mock(Repository.class), metadata, 1, 1);
        BaseChildAdvancedSearchModel baseChildAdvancedSearchModel = Mockito.mock(BaseChildAdvancedSearchModel.class, Mockito.CALLS_REAL_METHODS);
        Map<String, String> editMap;
        String mainConditionString;

        editMap  = new HashMap<>();
        editMap.put(Constants.KEY.MOTHER_FIRST_NAME, "Mary");
        editMap.put(Constants.KEY.MOTHER_LAST_NAME, "Doe");
        editMap.put("start_date", "26-05-2020");
        editMap.put("end_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("ec_client.dob BETWEEN '2020-05-26' AND '2020-05-26' AND  (mother_first_name Like '%Mary%' AND mother_last_name Like '%Doe%' )  AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put(Constants.KEY.MOTHER_FIRST_NAME, "Mary");
        editMap.put(Constants.KEY.MOTHER_LAST_NAME, "Doe");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("mother_first_name Like '%Mary%' AND mother_last_name Like '%Doe%' AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put(Constants.KEY.MOTHER_FIRST_NAME, "Mary");
        editMap.put("start_date", "26-05-2020");
        editMap.put("end_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("ec_client.dob BETWEEN '2020-05-26' AND '2020-05-26' AND  (mother_first_name Like '%Mary%')  AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put(Constants.KEY.MOTHER_FIRST_NAME, "Mary");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("mother_first_name Like '%Mary%' AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put(Constants.KEY.MOTHER_LAST_NAME, "Doe");
        editMap.put("start_date", "26-05-2020");
        editMap.put("end_date", "26-05-2020");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("ec_client.dob BETWEEN '2020-05-26' AND '2020-05-26' AND  (mother_last_name Like '%Doe%' )  AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put(Constants.KEY.MOTHER_LAST_NAME, "Doe");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("mother_last_name Like '%Doe%' AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());
    }

    @Test
    public void testGetMainConditionStringChildStatus() {
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null,null,  true);
        metadata.updateChildRegister("test", "test",
                "test", "ChildRegister",
                "test", "test",
                "test",
                "test", "test");
        ChildLibrary.init(context, Mockito.mock(Repository.class), metadata, 1, 1);
        BaseChildAdvancedSearchModel baseChildAdvancedSearchModel = Mockito.mock(BaseChildAdvancedSearchModel.class, Mockito.CALLS_REAL_METHODS);
        Map<String, String> editMap;
        String mainConditionString;

        editMap  = new HashMap<>();
        editMap.put(Constants.CHILD_STATUS.ACTIVE, "true");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("( ( ec_child_details.inactive IS NULL OR ec_child_details.inactive != 'true' )  AND ( ec_child_details.lost_to_follow_up IS NULL OR ec_child_details.lost_to_follow_up != 'true' ) )  AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP, "true");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("lost_to_follow_up = 'true' AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put(Constants.CHILD_STATUS.ACTIVE, "true");
        editMap.put(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP, "true");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("lost_to_follow_up = 'true' OR ( ( ec_child_details.inactive IS NULL OR ec_child_details.inactive != 'true' )  " +
                "AND ( ec_child_details.lost_to_follow_up IS NULL OR ec_child_details.lost_to_follow_up != 'true' ) )  AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());

        editMap  = new HashMap<>();
        editMap.put(Constants.CHILD_STATUS.INACTIVE, "true");
        editMap.put(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP, "true");
        editMap.put("first_name", "Roja");
        mainConditionString =  baseChildAdvancedSearchModel.getMainConditionString(editMap);
        Assert.assertEquals("ec_client.first_name Like '%Roja%' AND ( inactive = 'true' OR lost_to_follow_up = 'true') AND (ec_client.date_removed is null AND ec_client.is_closed == '0')", mainConditionString.trim());
    }

}
