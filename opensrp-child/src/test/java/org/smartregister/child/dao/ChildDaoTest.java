package org.smartregister.child.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.repository.Repository;

import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ChildDaoTest extends ChildDao {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setRepository(repository);
    }

    @Test
    public void testGetChildExtraVaccines() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"selected_vaccines", "vaccine_date"});
        matrixCursor.addRow(new Object[]{"Vaccine 1 - FIRST VACCINE,Vaccine 2 - SECOND VACCINE ", "2019-03-09"});
        matrixCursor.addRow(new Object[]{"Vaccine 3 - THIRD VACCINE ", "2020-04-10"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(
                "SELECT selected_vaccines,vaccine_date FROM ec_dynamic_vaccines WHERE entity_id = '12345'",
                new String[]{});

        List<Map.Entry<String, String>> extraVaccines = ChildDao.getChildExtraVaccines("12345");

        Assert.assertEquals(extraVaccines.size(), 3);
    }

    @Test
    public void testGetRecurringServiceTypes() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"type"});
        matrixCursor.addRow(new Object[]{"Deworming"});
        matrixCursor.addRow(new Object[]{"ITN"});
        matrixCursor.addRow(new Object[]{"Vit_A"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(
                "SELECT DISTINCT type FROM recurring_service_types ORDER BY type;",
                new String[]{});

        List<String> recurringServiceTypes = ChildDao.getRecurringServiceTypes();

        Assert.assertEquals(recurringServiceTypes.size(), 3);
        Assert.assertEquals(recurringServiceTypes.get(0), "Deworming");
        Assert.assertEquals(recurringServiceTypes.get(1), "ITN");
        Assert.assertEquals(recurringServiceTypes.get(2), "Vit_A");
    }
}