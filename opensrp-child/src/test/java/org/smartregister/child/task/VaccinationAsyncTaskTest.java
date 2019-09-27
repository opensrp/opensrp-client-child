package org.smartregister.child.task;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.RegisterActionParams;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.util.AppProperties;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-06
 */

public class VaccinationAsyncTaskTest extends BaseUnitTest {

    private VaccinationAsyncTask vaccinationAsyncTask;
    private ImmunizationLibrary immunizationLibrary;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        immunizationLibrary = Mockito.mock(ImmunizationLibrary.class);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", immunizationLibrary);

        ChildLibrary childLibrary = Mockito.mock(ChildLibrary.class);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);

        AppProperties appProperties = Mockito.mock(AppProperties.class);

        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        Mockito.doReturn(false).when(appProperties).hasProperty(Mockito.anyString());

        vaccinationAsyncTask = new VaccinationAsyncTask(Mockito.mock(RegisterActionParams.class)
                , null
                , null
                , null
                , RuntimeEnvironment.application);
    }

    @After
    public void tearDown() throws Exception {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", (ChildLibrary) null);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", (ImmunizationLibrary) null);
    }

    @Test
    public void getGroupNameShouldCallImmunizationLibrary() {
        HashMap<String, String> vaccineGrouping = new HashMap<>();
        vaccineGrouping.put("bcg", "At Birth");
        vaccineGrouping.put("opv0", "6 weeks");
        vaccineGrouping.put("opv1", "10 weeks");
        vaccineGrouping.put("opv2", "14 weeks");

        Mockito.doReturn(vaccineGrouping).when(immunizationLibrary).getVaccineGroupings(Mockito.any(Context.class));

        assertEquals("14 weeks", ReflectionHelpers.callInstanceMethod(vaccinationAsyncTask
                , "getGroupName"
                , ReflectionHelpers.ClassParameter.from(VaccineRepo.Vaccine.class, VaccineRepo.Vaccine.opv2)));
        Mockito.verify(immunizationLibrary, Mockito.times(1))
                .getVaccineGroupings(Mockito.any(Context.class));
    }

    @Test
    public void localizeStateKeyShouldReturn6weeksWhenGivenMixedCase6WeeksStateKey() {
        String localizedStateKey = ReflectionHelpers.callInstanceMethod(vaccinationAsyncTask
                , "localizeStateKey"
                , ReflectionHelpers.ClassParameter.from(String.class, "6 WEEKS"));

        assertEquals("6 weeks", localizedStateKey);

    }
}