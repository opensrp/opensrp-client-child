package org.smartregister.child.util;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.child.ChildLibrary;
import org.smartregister.repository.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ChildLibrary.class)
public class UtilsTest {
    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private Cursor cursor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getOpenMrsIdForMother() {
        final String uuid1 = UUID.randomUUID().toString();
        final String uuid2 = UUID.randomUUID().toString();
        Assert.assertEquals(uuid2, getUnusedOpenMrsIds(uuid1, uuid2).get(1));
    }

    public List<String> getUnusedOpenMrsIds(final String uuid1, final String uuid2) {
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(cursor.moveToNext()).then(new Answer<Object>() {
            int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) {
                if (count > 1) {
                    return false;
                } else {
                    count++;
                    return true;
                }

            }
        });


        PowerMockito.when(cursor.getString(0)).then(new Answer<String>() {
            List<String> stringsIds = Arrays.asList(uuid1, uuid2);
            int index = 0;

            @Override
            public String answer(InvocationOnMock invocation) {
                String newId = stringsIds.get(index);
                index++;
                return newId;

            }
        });
        PowerMockito.when(sqLiteDatabase.query(Constants.UniqueIdsTable.TABLE_NAME,
                new String[]{Constants.UniqueIdsTable.Columns.OPENMRSID}, Constants.UniqueIdsTable.Columns.STATUS + " = ?",
                new String[]{Constants.UniqueIdsTable.Columns.NOTUSED},
                null,
                null,
                Constants.UniqueIdsTable.Columns.CREATED_AT + " ASC", "2")).thenReturn(cursor);
        PowerMockito.when(childLibrary.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        List<String> result = Utils.getUnusedOpenMrsIds(2);
        return result;
    }

}