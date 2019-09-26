package org.smartregister.child.util;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.smartregister.Context;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.jsonmapping.Due;
import org.smartregister.immunization.domain.jsonmapping.Expiry;
import org.smartregister.immunization.domain.jsonmapping.Schedule;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-02
 */

@PrepareForTest({ImmunizationLibrary.class})
public class VaccineCalculatorTest extends BaseUnitTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private ImmunizationLibrary immunizationLibrary;
    @Mock
    private VaccineRepository vaccineRepository;
    @Mock
    private Context context;
    @Mock
    private AlertService alertService;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        mockImmunizationLibrary(immunizationLibrary, context, vaccineRepository, alertService);
        Mockito.doReturn(VaccineRepo.Vaccine.values()).when(immunizationLibrary).getVaccines();
    }

    @Test
    public void getVaccineDueDateShouldReturnNullWhenNoScheduleIsDefined() {

        Date today = Calendar.getInstance().getTime();
        org.smartregister.immunization.domain.jsonmapping.Vaccine vaccineJsonMapping = new org.smartregister.immunization.domain.jsonmapping.Vaccine();
        vaccineJsonMapping.setName("BCG");

        assertNull(VaccineCalculator.getVaccineDueDate(vaccineJsonMapping, today, new ArrayList<Vaccine>()));
    }

    @Test
    public void getVaccineDueDateShouldReturnNullWhenOneConditionInScheduleIsNotMet() {

        Date today = Calendar.getInstance().getTime();
        org.smartregister.immunization.domain.jsonmapping.Vaccine vaccineJsonMapping = new org.smartregister.immunization.domain.jsonmapping.Vaccine();
        vaccineJsonMapping.setName("BCG 2");

        vaccineJsonMapping.schedule = new Schedule();
        Due due = new Due();
        due.prerequisite = "BCG";
        due.offset = "+84d";
        due.reference = "prerequisite";

        List<Due> dueList = new ArrayList<>();
        dueList.add(due);

        vaccineJsonMapping.schedule.due = dueList;
        assertNull(VaccineCalculator.getVaccineDueDate(vaccineJsonMapping, today, new ArrayList<Vaccine>()));
    }

    @Test
    public void getVaccineDueDateShouldReturnNullWhenOneConditionInTheSchedulesIsNotMet() {

        Date today = Calendar.getInstance().getTime();
        org.smartregister.immunization.domain.jsonmapping.Vaccine vaccineJsonMapping = new org.smartregister.immunization.domain.jsonmapping.Vaccine();
        vaccineJsonMapping.setName("BCG 2 / Alternative Drug");


        Due due = new Due();
        due.prerequisite = "BCG";
        due.offset = "+84d";
        due.reference = "prerequisite";

        List<Due> dueList = new ArrayList<>();
        dueList.add(due);

        Schedule schedule = new Schedule();
        schedule.due = dueList;

        HashMap<String, Schedule> schedules = new HashMap<>();
        schedules.put("BCG", schedule);


        vaccineJsonMapping.schedules = schedules;
        assertNull(VaccineCalculator.getVaccineDueDate(vaccineJsonMapping, today, new ArrayList<Vaccine>()));
    }

    @Test
    public void getVaccineExpiryDateShouldReturnNullWhenNoExpiryIsDefined() {

        Date dob = Calendar.getInstance().getTime();
        Expiry expiry = null;

        assertNull(VaccineCalculator.getVaccineExpiryDate(dob, expiry));
    }

    @Test
    public void getVaccineExpiryDateShouldReturnExpiryDateWhenGivenExpiryObject() {

        Calendar dobCalendar = Calendar.getInstance();
        dobCalendar.add(Calendar.DAY_OF_MONTH, -60);
        Date dob = dobCalendar.getTime();

        Expiry expiry = new Expiry();
        expiry.offset = "+50d";
        expiry.reference = "dob";

        Calendar expiryCalendar = (Calendar) dobCalendar.clone();
        expiryCalendar.add(Calendar.DAY_OF_MONTH, 50);

        VaccineSchedule.standardiseCalendarDate(expiryCalendar);

        assertEquals(expiryCalendar.getTime(), VaccineCalculator.getVaccineExpiryDate(dob, expiry));
    }

    @Test
    public void getVaccineExpiryDateShouldReturnExpiryWhenGivenVaccineJsonMapping() {
        String bcgVaccineJsonMapping = "{\n" +
                "    \"name\": \"BCG 2\",\n" +
                "    \"type\": \"BCG\",\n" +
                "    \"openmrs_date\": {\n" +
                "      \"parent_entity\": \"886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "      \"entity\": \"concept\",\n" +
                "      \"entity_id\": \"1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"\n" +
                "    },\n" +
                "    \"openmrs_calculate\": {\n" +
                "      \"parent_entity\": \"886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "      \"entity\": \"concept\",\n" +
                "      \"entity_id\": \"1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\n" +
                "      \"calculation\": 2\n" +
                "    },\n" +
                "    \"schedule\": {\n" +
                "      \"due\": [\n" +
                "        {\n" +
                "          \"reference\": \"prerequisite\",\n" +
                "          \"prerequisite\": \"BCG\",\n" +
                "          \"offset\": \"+84d\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"expiry\": [\n" +
                "        {\n" +
                "          \"reference\": \"dob\",\n" +
                "          \"offset\": \"+1y\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }";

        org.smartregister.immunization.domain.jsonmapping.Vaccine vaccine = new Gson().fromJson(
                bcgVaccineJsonMapping,
                org.smartregister.immunization.domain.jsonmapping.Vaccine.class);


        Calendar dobCalendar = Calendar.getInstance();
        Date dob = dobCalendar.getTime();

        Calendar expiryCalendar = (Calendar) dobCalendar.clone();
        expiryCalendar.add(Calendar.YEAR, 1);

        VaccineSchedule.standardiseCalendarDate(expiryCalendar);

        assertEquals(expiryCalendar.getTime(), VaccineCalculator.getVaccineExpiryDate(dob, vaccine));
    }
}