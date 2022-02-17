package org.smartregister.child.wrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.util.Constants;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VaccineViewRecordUpdateWrapperTest extends BaseUnitTest {

    private VaccineViewRecordUpdateWrapper vaccineViewRecordUpdateWrapper;

    private List<Vaccine> vaccineList;
    private Map<String, Object> nv;

    @Before
    public void setUp() {

        vaccineList = new ArrayList<>();
        Vaccine vaccine;
        vaccine = new Vaccine();
        vaccine.setName("opv0");
        vaccineList.add(vaccine);

        nv = new HashMap<>();
        nv.put(Constants.KEY.VACCINE, VaccineRepo.Vaccine.opv0);

        vaccineViewRecordUpdateWrapper = new VaccineViewRecordUpdateWrapper();
    }

    @Test
    public void testGetVaccines() {
        Assert.assertNull(vaccineViewRecordUpdateWrapper.getVaccines());
        Whitebox.setInternalState(vaccineViewRecordUpdateWrapper, "vaccines", vaccineList);
        Assert.assertEquals(1, vaccineViewRecordUpdateWrapper.getVaccines().size());
        Assert.assertEquals("opv0", vaccineViewRecordUpdateWrapper.getVaccines().get(0).getName());
    }

    @Test
    public void testSetVaccines() {
        Assert.assertNull(vaccineViewRecordUpdateWrapper.getVaccines());
        vaccineViewRecordUpdateWrapper.setVaccines(vaccineList);
        Assert.assertEquals(1, vaccineViewRecordUpdateWrapper.getVaccines().size());
        Assert.assertEquals("opv0", vaccineViewRecordUpdateWrapper.getVaccines().get(0).getName());
    }

    @Test
    public void testGetNv() {
        Assert.assertNull(vaccineViewRecordUpdateWrapper.getNv());
        Whitebox.setInternalState(vaccineViewRecordUpdateWrapper, "nv", nv);
        Assert.assertEquals(1, vaccineViewRecordUpdateWrapper.getNv().size());
        Assert.assertEquals(VaccineRepo.Vaccine.opv0, vaccineViewRecordUpdateWrapper.getNv().get(Constants.KEY.VACCINE));
    }

    @Test
    public void testSetNv() {
        Assert.assertNull(vaccineViewRecordUpdateWrapper.getNv());
        vaccineViewRecordUpdateWrapper.setNv(nv);
        Assert.assertEquals(1, vaccineViewRecordUpdateWrapper.getNv().size());
        Assert.assertEquals(VaccineRepo.Vaccine.opv0, vaccineViewRecordUpdateWrapper.getNv().get(Constants.KEY.VACCINE));
    }

}
