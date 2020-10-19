package org.smartregister.child.wrapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;

@RunWith(MockitoJUnitRunner.class)
public class GrowthMonitoringViewRecordUpdateWrapperTest {

    private GrowthMonitoringViewRecordUpdateWrapper growthMonitoringViewRecordUpdateWrapper;

    private Weight weight;
    private Height height;

    @Before
    public void setUp() {
        weight = new Weight();
        weight.setKg(2.8f);

        height = new Height();
        height.setCm(39f);

        growthMonitoringViewRecordUpdateWrapper = new GrowthMonitoringViewRecordUpdateWrapper();
    }

    @Test
    public void testGetWeight() {
        Assert.assertNull(growthMonitoringViewRecordUpdateWrapper.getWeight());
        Whitebox.setInternalState(growthMonitoringViewRecordUpdateWrapper, "weight", weight);
        Assert.assertEquals("2.8", growthMonitoringViewRecordUpdateWrapper.getWeight().getKg().toString());
    }

    @Test
    public void testSetWeight() {
        Assert.assertNull(growthMonitoringViewRecordUpdateWrapper.getWeight());
        growthMonitoringViewRecordUpdateWrapper.setWeight(weight);
        Assert.assertEquals("2.8", growthMonitoringViewRecordUpdateWrapper.getWeight().getKg().toString());
    }

    @Test
    public void testGetHeight() {
        Assert.assertNull(growthMonitoringViewRecordUpdateWrapper.getHeight());
        Whitebox.setInternalState(growthMonitoringViewRecordUpdateWrapper, "height", height);
        Assert.assertEquals("39.0", growthMonitoringViewRecordUpdateWrapper.getHeight().getCm().toString());
    }

    @Test
    public void testSetHeight() {
        Assert.assertNull(growthMonitoringViewRecordUpdateWrapper.getHeight());
        growthMonitoringViewRecordUpdateWrapper.setHeight(height);
        Assert.assertEquals("39.0", growthMonitoringViewRecordUpdateWrapper.getHeight().getCm().toString());
    }
}
