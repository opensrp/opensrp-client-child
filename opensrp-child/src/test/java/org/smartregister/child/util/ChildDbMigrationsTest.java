package org.smartregister.child.util;


import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.provider.RegisterQueryProvider;

import java.util.List;

/**
 * Created by ndegwamartin on 11/08/2020.
 */
@PrepareForTest({Utils.class, ChildLibrary.class})
public class ChildDbMigrationsTest extends BasePowerMockUnitTest {

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private ChildMetadata childMetadata;

    private RegisterQueryProvider registerQueryProvider;

    @Mock
    private ChildLibrary childLibrary;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        registerQueryProvider = Mockito.spy(RegisterQueryProvider.class);

        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);

    }

    @Test
    public void testSddShowBcg2ReminderAndBcgScarColumnsToEcChildDetailsGeneratesCorrectQueries() {

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.metadata()).thenReturn(childMetadata);
        Mockito.doReturn(registerQueryProvider).when(childMetadata).getRegisterQueryProvider();
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

        boolean success = ChildDbMigrations.addShowBcg2ReminderAndBcgScarColumnsToEcChildDetails(sqLiteDatabase);

        Mockito.verify(sqLiteDatabase, Mockito.times(2)).execSQL(stringArgumentCaptor.capture());
        List<String> queryList = stringArgumentCaptor.getAllValues();
        Assert.assertEquals(2, queryList.size());
        Assert.assertEquals("ALTER TABLE ec_child_details ADD COLUMN show_bcg2_reminder TEXT default null", queryList.get(0));
        Assert.assertEquals("ALTER TABLE ec_child_details ADD COLUMN show_bcg_scar TEXT default null", queryList.get(1));
        Assert.assertTrue(success);
    }

    @Test
    public void testSddShowBcg2ReminderAndBcgScarColumnsToEcChildDetailsReturnsFalseOnException() {

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.metadata()).thenReturn(childMetadata);

        Mockito.doReturn(registerQueryProvider).when(childMetadata).getRegisterQueryProvider();
        Mockito.doThrow(new SQLException()).when(sqLiteDatabase).execSQL(ArgumentMatchers.anyString());

        boolean success = ChildDbMigrations.addShowBcg2ReminderAndBcgScarColumnsToEcChildDetails(sqLiteDatabase);

        Mockito.verify(sqLiteDatabase, Mockito.times(1)).execSQL(ArgumentMatchers.anyString());
        Assert.assertFalse(success);
    }
}