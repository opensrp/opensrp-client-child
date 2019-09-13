package org.smartregister.child.wrapper;

import android.view.View;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.view.contract.SmartRegisterClient;

public class GrowthMonitoringViewRecordUpdateWrapperTest {

    @Test
    public void testGrowthMonitoringViewRecordUpdateWrapper(){
        GrowthMonitoringViewRecordUpdateWrapper growthMonitoringViewRecordUpdateWrapper =  new GrowthMonitoringViewRecordUpdateWrapper();
        Assert.assertNull(growthMonitoringViewRecordUpdateWrapper.getHeight());
        Assert.assertNull(growthMonitoringViewRecordUpdateWrapper.getWeight());
        Assert.assertNull(growthMonitoringViewRecordUpdateWrapper.getClient());
        Assert.assertNull(growthMonitoringViewRecordUpdateWrapper.getConvertView());
        Assert.assertNull(growthMonitoringViewRecordUpdateWrapper.getInactive());
        Assert.assertNull(growthMonitoringViewRecordUpdateWrapper.getLostToFollowUp());

        growthMonitoringViewRecordUpdateWrapper.setHeight(Mockito.mock(Height.class));
        growthMonitoringViewRecordUpdateWrapper.setWeight(Mockito.mock(Weight.class));
        growthMonitoringViewRecordUpdateWrapper.setClient(Mockito.mock(SmartRegisterClient.class));
        growthMonitoringViewRecordUpdateWrapper.setInactive("something");
        growthMonitoringViewRecordUpdateWrapper.setConvertView(Mockito.mock(View.class));
        growthMonitoringViewRecordUpdateWrapper.setLostToFollowUp("something");

        Assert.assertNotNull(growthMonitoringViewRecordUpdateWrapper.getHeight());
        Assert.assertNotNull(growthMonitoringViewRecordUpdateWrapper.getWeight());
        Assert.assertNotNull(growthMonitoringViewRecordUpdateWrapper.getClient());
        Assert.assertNotNull(growthMonitoringViewRecordUpdateWrapper.getConvertView());
        Assert.assertNotNull(growthMonitoringViewRecordUpdateWrapper.getInactive());
        Assert.assertNotNull(growthMonitoringViewRecordUpdateWrapper.getLostToFollowUp());
    }
}



