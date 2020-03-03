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
import org.smartregister.child.shadows.ShadowAssetHandler;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.Expiry;
import org.smartregister.repository.Repository;
import org.smartregister.util.AssetHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2020-02-12
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, shadows = {ShadowAssetHandler.class})
public class UtilsRobolectricTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {

        Context context = Context.getInstance();
        context.updateApplicationContext(RuntimeEnvironment.application);

        ImmunizationLibrary.init(context, Mockito.mock(Repository.class), Mockito.mock(CommonFtsObject.class), 1, 1);
        ImmunizationLibrary immunizationLibrary = Mockito.spy(ImmunizationLibrary.getInstance());
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", immunizationLibrary);
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

        Assert.assertFalse(Utils.isVaccineDue(vaccineList, dob, testVaccine, false));
    }

}
