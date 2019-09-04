package org.smartregister.child.utils;

import net.sqlcipher.database.SQLiteDatabase;

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
import org.smartregister.child.util.Utils;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;

import static org.mockito.ArgumentMatchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, VaccineRepo.class})
public class UtilsTest{
    @Mock
    SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        Assert.assertNotNull(sqLiteDatabase);
        sqLiteDatabase = Mockito.mock(SQLiteDatabase.class);
    }

    @Test
    public void testGetCombinedVaccineShouldReturnCorrectVaccine() throws Exception{
        PowerMockito.spy(Utils.class);
        PowerMockito.doReturn("any").when(Utils.class, "getCombinedVaccine", "something");
        PowerMockito.doCallRealMethod().when(Utils.class,"getCombinedVaccine", anyString());
        Assert.assertEquals(Utils.getCombinedVaccine("ipv"), VaccineRepo.Vaccine.opv3.display());
        Assert.assertEquals(Utils.getCombinedVaccine("opv 3"), VaccineRepo.Vaccine.ipv.display());
        Assert.assertEquals(Utils.getCombinedVaccine("mr 1"), VaccineRepo.Vaccine.measles1.display());
        Assert.assertEquals(Utils.getCombinedVaccine("measles 1"), VaccineRepo.Vaccine.mr1.display());
        Assert.assertEquals(Utils.getCombinedVaccine("mr 2"), VaccineRepo.Vaccine.measles2.display());
        Assert.assertNull(Utils.getCombinedVaccine("other"));
    }

    @Test
    public void testAddVaccineShouldCallAddMethod() throws Exception {
        PowerMockito.spy(Utils.class);
        PowerMockito.doReturn(true).when(Utils.class,"getCombinedVaccine", anyString());

    }
}
