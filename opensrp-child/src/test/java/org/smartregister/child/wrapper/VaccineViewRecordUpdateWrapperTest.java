package org.smartregister.child.wrapper;

import android.view.View;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.view.contract.SmartRegisterClient;

import java.util.ArrayList;
import java.util.HashMap;

public class VaccineViewRecordUpdateWrapperTest {

    @Test
    public void testVaccineViewRecordUpdateWrapper(){
        VaccineViewRecordUpdateWrapper vaccineViewRecordUpdateWrapper = new VaccineViewRecordUpdateWrapper();
        Assert.assertNull(vaccineViewRecordUpdateWrapper.getNv());
        Assert.assertNull(vaccineViewRecordUpdateWrapper.getVaccines());
        Assert.assertNull(vaccineViewRecordUpdateWrapper.getClient());
        Assert.assertNull(vaccineViewRecordUpdateWrapper.getConvertView());
        Assert.assertNull(vaccineViewRecordUpdateWrapper.getLostToFollowUp());
        Assert.assertNull(vaccineViewRecordUpdateWrapper.getInactive());

        vaccineViewRecordUpdateWrapper.setNv(new HashMap<String, Object>());
        vaccineViewRecordUpdateWrapper.setVaccines(new ArrayList<Vaccine>());
        vaccineViewRecordUpdateWrapper.setClient(Mockito.mock(SmartRegisterClient.class));
        vaccineViewRecordUpdateWrapper.setInactive("some String");
        vaccineViewRecordUpdateWrapper.setConvertView(Mockito.mock(View.class));
        vaccineViewRecordUpdateWrapper.setLostToFollowUp("test");

        Assert.assertNotNull(vaccineViewRecordUpdateWrapper.getNv());
        Assert.assertNotNull(vaccineViewRecordUpdateWrapper.getVaccines());
        Assert.assertNotNull(vaccineViewRecordUpdateWrapper.getClient());
        Assert.assertNotNull(vaccineViewRecordUpdateWrapper.getConvertView());
        Assert.assertNotNull(vaccineViewRecordUpdateWrapper.getLostToFollowUp());
        Assert.assertNotNull(vaccineViewRecordUpdateWrapper.getInactive());
    }
}
