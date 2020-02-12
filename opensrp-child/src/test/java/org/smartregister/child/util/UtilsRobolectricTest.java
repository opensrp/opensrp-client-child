package org.smartregister.child.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.Expiry;
import org.smartregister.immunization.util.VaccineCache;
import org.smartregister.repository.Repository;
import org.smartregister.util.AssetHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2020-02-12
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27)
public class UtilsRobolectricTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        ImmunizationLibrary immunizationLibrary = Mockito.mock(ImmunizationLibrary.class);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", immunizationLibrary);
        HashMap<String, VaccineCache> vaccineCacheHashMap = new HashMap<>();
        vaccineCacheHashMap.put("child", new VaccineCache());
        ReflectionHelpers.setField(immunizationLibrary, "vaccineCacheMap", vaccineCacheHashMap);
        immunizationLibrary.setVaccines(VaccineRepo.Vaccine.values());

        //ImmunizationLibrary.init(Context.getInstance(), Mockito.mock(Repository.class), Mockito.mock(CommonFtsObject.class), 1, 1);
    }

    @Test
    public void isVaccineDueShouldReturnTrueWhenVaccineDueExpiryPastAndBackEntryAllowed() {
        ArrayList<Vaccine> vaccineList = new ArrayList<>();

        // The child will always be 2 years young irrespective of when this test is written

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -2);

        Date dob = calendar.getTime();

        org.smartregister.immunization.domain.jsonmapping.Vaccine testVaccine = AssetHandler.jsonStringToJava(VaccineData.bcg2JsonData, org.smartregister.immunization.domain.jsonmapping.Vaccine.class);
        Expiry expiry = testVaccine.schedule.expiry.get(0);
        expiry.offset = "+1y";

        Assert.assertTrue(Utils.isVaccineDue(vaccineList, dob, testVaccine, true));
    }

    @Test
    public void isVaccineDueShouldReturnFalseWhenVaccineDueExpiryPastAndBackEntryIsNotAllowed() {
        ArrayList<Vaccine> vaccineList = new ArrayList<>();

        // The child will always be 2 years young irrespective of when this test is run
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -2);

        Date dob = calendar.getTime();

        org.smartregister.immunization.domain.jsonmapping.Vaccine testVaccine = AssetHandler.jsonStringToJava(VaccineData.bcg2JsonData, org.smartregister.immunization.domain.jsonmapping.Vaccine.class);
        Expiry expiry = testVaccine.schedule.expiry.get(0);
        expiry.offset = "+1y";

        Assert.assertTrue(Utils.isVaccineDue(vaccineList, dob, testVaccine, false));
    }

}
