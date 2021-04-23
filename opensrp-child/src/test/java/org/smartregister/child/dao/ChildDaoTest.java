package org.smartregister.child.dao;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.child.util.Constants;
import org.smartregister.repository.Repository;

import java.util.List;

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

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{"base_entity_id", "selected_vaccines", "vaccine_date"});
        matrixCursor.addRow(new Object[]{"crazy-1290", "Vaccine 1 - FIRST VACCINE,Vaccine 2 - SECOND VACCINE ", "2019-03-09"});
        matrixCursor.addRow(new Object[]{"crazy-1291", "Vaccine 3 - THIRD VACCINE ", "2020-04-10"});
        Mockito.doReturn(matrixCursor).when(database).rawQuery(
                "SELECT base_entity_id, selected_vaccines, vaccine_date FROM ec_booster_vaccines WHERE entity_id = '12345' AND is_closed IS 0",
                new String[]{});

        List<Triple<String, String, String>> extraVaccines = ChildDao.getChildExtraVaccines(Constants.Tables.EC_BOOSTER_VACCINES, "12345");

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
                "SELECT DISTINCT type FROM recurring_service_types",
                new String[]{});

        List<String> recurringServiceTypes = ChildDao.getRecurringServiceTypes();

        Assert.assertEquals(recurringServiceTypes.size(), 3);
        Assert.assertEquals(recurringServiceTypes.get(0), "Deworming");
        Assert.assertEquals(recurringServiceTypes.get(1), "ITN");
        Assert.assertEquals(recurringServiceTypes.get(2), "Vit_A");
    }
}