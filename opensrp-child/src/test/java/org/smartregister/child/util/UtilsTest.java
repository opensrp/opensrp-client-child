package org.smartregister.child.util;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.child.ChildLibrary;
import org.smartregister.repository.Repository;

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
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(cursor.move(2)).thenReturn(true);
        PowerMockito.when(cursor.getString(0)).thenReturn("some valid id");
        PowerMockito.when(sqLiteDatabase.query(Constants.CoreTable.TABLE_NAME,
                new String[]{Constants.CoreTable.Columns.OPENMRSID}, Constants.CoreTable.Columns.STATUS + " = ?",
                new String[]{Constants.CoreTable.Columns.NOTUSED},
                null,
                null,
                Constants.CoreTable.Columns.CREATED_AT + " ASC", "2")).thenReturn(cursor);
        PowerMockito.when(childLibrary.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        String result = Utils.getOpenMrsIdForMother();
        Assert.assertEquals("some valid id", result);
    }
}