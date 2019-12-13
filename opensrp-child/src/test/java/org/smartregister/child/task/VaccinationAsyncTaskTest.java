package org.smartregister.child.task;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.RegisterActionParams;
import org.smartregister.child.util.Constants;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.util.VaccineCache;
import org.smartregister.util.AppProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<String, Object> vaccineRepoMap = new HashMap<>();

        List<Vaccine> groupVaccines = new ArrayList<>();
        Vaccine vaccine;

        vaccine = new Vaccine();
        vaccine.setName("opv0");
        groupVaccines.add(vaccine);

        vaccine = new Vaccine();
        vaccine.setName("opv1");
        groupVaccines.add(vaccine);

        vaccine = new Vaccine();
        vaccine.setName("bcg");
        groupVaccines.add(vaccine);

        vaccine = new Vaccine();
        vaccine.setName("0pv2");
        groupVaccines.add(vaccine);

        VaccineGroup vaccineGroup = Mockito.spy(VaccineGroup.class);
        vaccineGroup.vaccines = groupVaccines;

        vaccineRepoMap.put("vaccines.json", Arrays.asList(new VaccineGroup[]{vaccineGroup}));

        Mockito.when(immunizationLibrary.getVaccinesConfigJsonMap()).thenReturn(vaccineRepoMap);

        HashMap<String, VaccineCache> vaccineCacheHashMap = new HashMap<>();

        vaccineCacheHashMap.put(Constants.CHILD_TYPE, new VaccineCache());

        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "vaccineCacheMap", vaccineCacheHashMap);

        HashMap<String, String> vaccineGrouping = new HashMap<>();
        vaccineGrouping.put("bcg", "At Birth");
        vaccineGrouping.put("opv0", "6 weeks");
        vaccineGrouping.put("opv1", "10 weeks");
        vaccineGrouping.put("opv2", "14 weeks");


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
    public void tearDown() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(ImmunizationLibrary.class, "instance", null);
    }

    @Test
    public void localizeStateKeyShouldReturn6weeksWhenGivenMixedCase6WeeksStateKey() {

        String localizedStateKey = ReflectionHelpers.callInstanceMethod(vaccinationAsyncTask
                , "localizeStateKey"
                , ReflectionHelpers.ClassParameter.from(String.class, "6 WEEKS"));

        assertEquals("6 Weeks", localizedStateKey);

    }


    @Test
    public void testGetUpcomingState() throws Exception {
        DateTime dateTime = new DateTime();
        //UPCOMING
        VaccinationAsyncTask.State state = Whitebox.invokeMethod(vaccinationAsyncTask, "getUpcomingState", dateTime);
        Assert.assertEquals("UPCOMING", state.name());

        //UPCOMING_NEXT_7_DAYS
        dateTime = dateTime.plusDays(1);
        state = Whitebox.invokeMethod(vaccinationAsyncTask, "getUpcomingState", dateTime);
        Assert.assertEquals("UPCOMING_NEXT_7_DAYS", state.name());
    }
}