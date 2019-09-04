package org.smartregister.child.utils;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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
import org.smartregister.repository.BaseRepository;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, VaccineRepo.class})
public class UtilsTest{
    @Mock
    VaccineRepository vaccineRepository;

    @InjectMocks
    BaseRepository baseRepository;

    @Mock
    SQLiteDatabase sqLiteDatabase;



    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        Assert.assertNotNull(sqLiteDatabase);
        Assert.assertNotNull(baseRepository);
        sqLiteDatabase = Mockito.mock(SQLiteDatabase.class);
    }

    @Test
    public void testAddVaccineShouldCall(){
        PowerMockito.mockStatic(Utils.class);
        Vaccine vaccine = new Vaccine();
        Utils.addVaccine(vaccineRepository, vaccine);
        PowerMockito.verifyStatic(Utils.class);
        Utils.addVaccine(vaccineRepository, vaccine);
        PowerMockito.verifyStatic(Utils.class, Mockito.times(1));
        Utils.addVaccine(vaccineRepository, vaccine);
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
}
