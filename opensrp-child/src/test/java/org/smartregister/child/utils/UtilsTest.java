package org.smartregister.child.utils;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
public class UtilsTest {
    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private VaccineRepository vaccineRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Assert.assertNotNull(sqLiteDatabase);
        sqLiteDatabase = Mockito.mock(SQLiteDatabase.class);
    }

    @Test
    public void getCombinedVaccineWithNonNullArgument() throws Exception {
        PowerMockito.spy(Utils.class);
        PowerMockito.doReturn("any").when(Utils.class, "getCombinedVaccine", "something");
        PowerMockito.doCallRealMethod().when(Utils.class, "getCombinedVaccine", anyString());
        Assert.assertEquals(Utils.getCombinedVaccine("mr 1"), VaccineRepo.Vaccine.measles1.display());
        Assert.assertEquals(Utils.getCombinedVaccine("measles 1"), VaccineRepo.Vaccine.mr1.display());
        Assert.assertEquals(Utils.getCombinedVaccine("mr 2"), VaccineRepo.Vaccine.measles2.display());
        Assert.assertNull(Utils.getCombinedVaccine("other"));
    }

    @Test
    public void addVaccineWithVaccineRepositoryOrVaccineNotNull() {
        PowerMockito.spy(Utils.class);
        Vaccine vaccine = new Vaccine();
        vaccine.setName("testvaccine");
        Utils.addVaccine(vaccineRepository, vaccine);
        Mockito.verify(vaccineRepository, Mockito.times(1)).add(vaccine);

        ArgumentCaptor<VaccineRepository> vaccineRepositoryArgumentCaptor = ArgumentCaptor.forClass(VaccineRepository.class);
        ArgumentCaptor<Vaccine> vaccineArgumentCaptor = ArgumentCaptor.forClass(Vaccine.class);

        PowerMockito.verifyStatic(Utils.class);

        Utils.addVaccine(vaccineRepositoryArgumentCaptor.capture(), vaccineArgumentCaptor.capture());

        Assert.assertEquals(vaccineRepository, vaccineRepositoryArgumentCaptor.getValue());
        Assert.assertEquals(vaccine, vaccineArgumentCaptor.getValue());
    }

    @Test
    public void addVaccineWithVaccineRepositoryIsNull() {
        PowerMockito.spy(Utils.class);
        Vaccine vaccine = new Vaccine();
        Utils.addVaccine(null, vaccine);
        Mockito.verify(vaccineRepository, Mockito.times(0)).add(vaccine);

        ArgumentCaptor<VaccineRepository> vaccineRepositoryArgumentCaptor = ArgumentCaptor.forClass(VaccineRepository.class);
        ArgumentCaptor<Vaccine> vaccineArgumentCaptor = ArgumentCaptor.forClass(Vaccine.class);

        PowerMockito.verifyStatic(Utils.class);

        Utils.addVaccine(vaccineRepositoryArgumentCaptor.capture(), vaccineArgumentCaptor.capture());

        Assert.assertNull(vaccineRepositoryArgumentCaptor.getValue());
        Assert.assertEquals(vaccine, vaccineArgumentCaptor.getValue());
    }

    @Test
    public void addVaccineWithVaccineIsNull() {
        PowerMockito.spy(Utils.class);
        Utils.addVaccine(vaccineRepository, null);
        Mockito.verify(vaccineRepository, Mockito.times(0)).add(null);

        ArgumentCaptor<VaccineRepository> vaccineRepositoryArgumentCaptor = ArgumentCaptor.forClass(VaccineRepository.class);
        ArgumentCaptor<Vaccine> vaccineArgumentCaptor = ArgumentCaptor.forClass(Vaccine.class);

        PowerMockito.verifyStatic(Utils.class);

        Utils.addVaccine(vaccineRepositoryArgumentCaptor.capture(), vaccineArgumentCaptor.capture());

        Assert.assertEquals(vaccineRepository, vaccineRepositoryArgumentCaptor.getValue());
        Assert.assertNull(vaccineArgumentCaptor.getValue());
    }

    @Test
    public void testAddVaccineShouldReturnExceptionWhenVaccineNameIsNull() {
        PowerMockito.spy(Utils.class);
        Vaccine vaccine = new Vaccine();
        Utils.addVaccine(vaccineRepository, vaccine);

        Mockito.verify(vaccineRepository, Mockito.times(0)).add(vaccine);

        ArgumentCaptor<VaccineRepository> vaccineRepositoryArgumentCaptor = ArgumentCaptor.forClass(VaccineRepository.class);
        ArgumentCaptor<Vaccine> vaccineArgumentCaptor = ArgumentCaptor.forClass(Vaccine.class);

        PowerMockito.verifyStatic(Utils.class);

        Utils.addVaccine(vaccineRepositoryArgumentCaptor.capture(), vaccineArgumentCaptor.capture());

        Assert.assertEquals(vaccineRepository, vaccineRepositoryArgumentCaptor.getValue());
        Assert.assertNull(vaccineArgumentCaptor.getValue().getName());
    }


    @Test
    public void testReverseHyphenatedStringReturnsCorrectValueForHyphenatedParameter() {

        String testString = "20-05-2012";

        String answer = Utils.reverseHyphenatedString(testString);

        Assert.assertEquals("2012-05-20", answer);

    }
}
