package org.smartregister.child.util;


import android.database.Cursor;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.child.BuildConfig;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.EntityLookUp;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, ChildMetadata.class})
public class MotherLookUpUtilsTest {

    @Mock
    private Context context;

    @Mock
    private CommonRepository commonRepository;

    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMainConditionString() throws Exception {
        Map<String, String> entityMap = new HashMap<>();
        entityMap.put("first_name", "first_name");
        entityMap.put("last_name", "last_name");

        String expected = " last_name Like '%last_name%' AND first_name Like '%first_name%'";
        String result = Whitebox.invokeMethod(MotherLookUpUtils.class, "getMainConditionString", entityMap);
        Assert.assertEquals(expected, result);

        entityMap.put("date_birth", "2017-09-09");
        String expectedWithDate = " cast(dob as date)  =  cast('2017-09-09'as date)  AND" + expected;
        String resultWihDate = Whitebox.invokeMethod(MotherLookUpUtils.class, "getMainConditionString", entityMap);
        Assert.assertEquals(expectedWithDate, resultWihDate);
    }

    @Test
    public void lookUpReturnEmptyArrayListWithNullContext() throws Exception {
        Context context = null;
        EntityLookUp entityLookUp = null;
        HashMap<CommonPersonObject, List<CommonPersonObject>> objectListHashMap = Whitebox
                .invokeMethod(MotherLookUpUtils.class, "lookUp", context, entityLookUp);
        Assert.assertEquals(0, objectListHashMap.size());
    }

    @Test
    public void lookUpReturnEmptyArrayListWithEntityLookUpEmpty() throws Exception {
        Context context = null;
        EntityLookUp entityLookUp = null;
        HashMap<CommonPersonObject, List<CommonPersonObject>> objectListHashMap = Whitebox
                .invokeMethod(MotherLookUpUtils.class, "lookUp", context, entityLookUp);
        Assert.assertEquals(0, objectListHashMap.size());
    }

    @Test
    public void lookUp() throws Exception {
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, true);
        metadata.updateChildRegister("test", "test",
                "test", "test",
                "test", "test",
                "test",
                "test", "test");
        Cursor cursor = Mockito.mock(Cursor.class);
        PowerMockito.when(cursor.getCount()).thenReturn(1);
        PowerMockito.when(cursor.moveToFirst()).thenReturn(true);
        PowerMockito.when(cursor.moveToNext()).thenReturn(false);
        PowerMockito.when(cursor.isAfterLast()).then(new Answer<Boolean>() {
            int count = 0;

            @Override
            public Boolean answer(InvocationOnMock invocation) {
                if (count > 0) {
                    return true;
                }
                count++;
                return false;
            }
        });
        PowerMockito.when(commonRepository.readAllcommonforCursorAdapter(cursor))
                .thenReturn(Mockito.mock(CommonPersonObject.class));
        PowerMockito.when(commonRepository.rawCustomQueryForAdapter(Mockito.any(String.class))).thenReturn(cursor);
        Mockito.when(context.commonrepository("test")).thenReturn(commonRepository);

        ChildLibrary.init(context, Mockito.mock(Repository.class), metadata, BuildConfig.VERSION_CODE, 1);

        EntityLookUp entityLookUp = new EntityLookUp();
        entityLookUp.put("first_name", "john");
        HashMap<CommonPersonObject, List<CommonPersonObject>> objectListHashMap = Whitebox
                .invokeMethod(MotherLookUpUtils.class, "lookUp", context, entityLookUp);
        Assert.assertEquals(1, objectListHashMap.size());
    }

    @Test
    public void lookUpWithEmptyResult() throws Exception {
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, true);
        metadata.updateChildRegister("test", "test",
                "test", "test",
                "test", "test",
                "test",
                "test", "test");
        Cursor cursor = Mockito.mock(Cursor.class);
        PowerMockito.when(cursor.getCount()).thenReturn(0);
        PowerMockito.when(cursor.moveToFirst()).thenReturn(false);
        PowerMockito.when(cursor.moveToNext()).thenReturn(false);
        PowerMockito.when(cursor.isAfterLast()).thenReturn(false);
        PowerMockito.when(commonRepository.readAllcommonforCursorAdapter(cursor))
                .thenReturn(Mockito.mock(CommonPersonObject.class));
        PowerMockito.when(commonRepository.rawCustomQueryForAdapter(Mockito.any(String.class))).thenReturn(cursor);
        Mockito.when(context.commonrepository("test")).thenReturn(commonRepository);

        ChildLibrary.init(context, Mockito.mock(Repository.class), metadata, BuildConfig.VERSION_CODE, 1);

        EntityLookUp entityLookUp = new EntityLookUp();
        entityLookUp.put("Sdf", "sdf");
        HashMap<CommonPersonObject, List<CommonPersonObject>> objectListHashMap = Whitebox
                .invokeMethod(MotherLookUpUtils.class, "lookUp", context, entityLookUp);
        Assert.assertEquals(0, objectListHashMap.size());
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }

}