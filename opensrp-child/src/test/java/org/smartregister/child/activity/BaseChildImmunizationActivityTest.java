package org.smartregister.child.activity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;

import java.util.ArrayList;
import java.util.List;

public class BaseChildImmunizationActivityTest extends BaseUnitTest {

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getVaccineByName(){
        Vaccine vaccine = new Vaccine();
        vaccine.name = "some";
        BaseChildImmunizationActivity baseChildImmunizationActivity = Mockito.spy(BaseChildImmunizationActivity.class);
        List<Vaccine> vaccines = new ArrayList<>();
        vaccines.add(vaccine);
        Assert.assertEquals(baseChildImmunizationActivity.getVaccineByName(vaccines, "some").name, "some");
    }

    @Test
    public void getVaccineGroupName(){
        VaccineGroup vaccineGroup = new VaccineGroup();
        vaccineGroup.id = "some";
        BaseChildImmunizationActivity baseChildImmunizationActivity = Mockito.spy(BaseChildImmunizationActivity.class);
        List<VaccineGroup> vaccineGroups = new ArrayList<>();
        vaccineGroups.add(vaccineGroup);
        Assert.assertEquals(baseChildImmunizationActivity.getVaccineGroupByName(vaccineGroups, "some").id,"some");

    }
}
