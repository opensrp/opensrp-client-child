package org.smartregister.child.util;


import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

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
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.EntityLookUp;
import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, ChildMetadata.class, ChildLibrary.class})
public class MotherLookUpUtilsTest {

    @Mock
    private Context opensrpContext;

    @Mock
    private Repository repository;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private EventClientRepository eventClientRepository;

    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void lookUpReturnEmptyArrayListWithNullContext() throws Exception {
        Context context = null;
        EntityLookUp entityLookUp = null;
        HashMap<CommonPersonObject, List<CommonPersonObject>> objectListHashMap = Whitebox
                .invokeMethod(MotherLookUpUtils.class, "lookUp", opensrpContext, context, entityLookUp);
        Assert.assertEquals(0, objectListHashMap.size());
    }

    @Test
    public void testLookUpShouldReturnOneResult() throws Exception {
        android.content.Context context = Mockito.mock(BaseChildFormActivity.class);
        PowerMockito.mockStatic(ChildLibrary.class);
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, null,
                null, true, new RegisterQueryProvider());
        metadata.updateChildRegister("test", "ec_client",
                "ec_client", "test",
                "test", "test",
                "test",
                "test", "test");
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        PowerMockito.when(childLibrary.metadata()).thenReturn(metadata);
        PowerMockito.when(childLibrary.eventClientRepository()).thenReturn(eventClientRepository);
        PowerMockito.when(childLibrary.getRepository()).thenReturn(repository);
        SQLiteDatabase sqLiteDatabase = Mockito.mock(SQLiteDatabase.class);
        PowerMockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);

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
        CommonPersonObject commonPersonObject = Mockito.mock(CommonPersonObject.class);
        PowerMockito.when(commonPersonObject.getCaseId()).thenReturn("121-23");
        PowerMockito.when(commonRepository.readAllcommonforCursorAdapter(cursor))
                .thenReturn(commonPersonObject);
        PowerMockito.when(commonRepository.rawCustomQueryForAdapter(Mockito.any(String.class))).thenReturn(cursor);
        Mockito.when(opensrpContext.commonrepository("ec_client")).thenReturn(commonRepository);

        String query = "select ec_client.id as _id,ec_client.relationalid,ec_client.zeir_id,ec_child_details.relational_id,ec_client.gender,ec_client.base_entity_id,ec_client.first_name,ec_client.last_name," +
                "mother.first_name as mother_first_name,mother.last_name as mother_last_name,ec_client.dob,mother.dob as mother_dob,ec_mother_details.nrc_number as mother_nrc_number," +
                "ec_mother_details.father_name,ec_mother_details.epi_card_number,ec_client.client_reg_date,ec_child_details.pmtct_status,ec_client.last_interacted_with," +
                "ec_child_details.inactive,ec_child_details.lost_to_follow_up,ec_child_details.contact_phone_number from ec_child_details " +
                "join ec_mother_details on ec_child_details.relational_id = ec_mother_details.base_entity_id " +
                "join ec_client on ec_client.base_entity_id = ec_child_details.base_entity_id " +
                "join ec_client mother on mother.base_entity_id = ec_mother_details.base_entity_id where ec_child_details.relational_id IN ('121-23')";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("first_name", "jon");
        hashMap.put("last_name", "kol");
        hashMap.put("base_entity_id", "234");
        hashMap.put("relationalid", "234");


        ArrayList<HashMap<String, String>> childList = new ArrayList<>();
        childList.add(hashMap);
        PowerMockito.when(eventClientRepository.rawQuery(sqLiteDatabase, query)).thenReturn(childList);

        EntityLookUp entityLookUp = new EntityLookUp();
        entityLookUp.put("first_name", "john");
        PowerMockito.when(((BaseChildFormActivity) context).lookUpQuery(entityLookUp.getMap(), metadata.getRegisterQueryProvider().getDemographicTable())).thenReturn("testQuery");

        HashMap<CommonPersonObject, List<CommonPersonObject>> objectListHashMap = Whitebox
                .invokeMethod(MotherLookUpUtils.class, "lookUp", opensrpContext, context, entityLookUp);
        Assert.assertEquals(1, objectListHashMap.size());
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }

}