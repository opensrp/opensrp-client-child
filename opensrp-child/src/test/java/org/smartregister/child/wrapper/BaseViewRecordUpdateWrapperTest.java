package org.smartregister.child.wrapper;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.view.contract.SmartRegisterClient;

public class BaseViewRecordUpdateWrapperTest extends BasePowerMockUnitTest {

    @Mock
    private View view;

    @Mock
    private SmartRegisterClient smartRegisterClient;

    private BaseViewRecordUpdateWrapper baseViewRecordUpdateWrapper;

    @Before
    public void setUp() {
        baseViewRecordUpdateWrapper = new BaseViewRecordUpdateWrapper();
    }

    @Test
    public void testGetConvertView() {
        Assert.assertNull(baseViewRecordUpdateWrapper.getConvertView());
        Whitebox.setInternalState(baseViewRecordUpdateWrapper, "convertView", view);
        Assert.assertNotNull(baseViewRecordUpdateWrapper.getConvertView());
    }

    @Test
    public void testSetConvertView() {
        Assert.assertNull(baseViewRecordUpdateWrapper.getConvertView());
        baseViewRecordUpdateWrapper.setConvertView(view);
        Assert.assertNotNull(baseViewRecordUpdateWrapper.getConvertView());
    }

    @Test
    public void testGetLostToFollowUp() {
        Assert.assertNull(baseViewRecordUpdateWrapper.getLostToFollowUp());
        Whitebox.setInternalState(baseViewRecordUpdateWrapper, "lostToFollowUp", "TRUE");
        Assert.assertEquals("TRUE", baseViewRecordUpdateWrapper.getLostToFollowUp());
    }

    @Test
    public void testSetLostToFollowUp() {
        Assert.assertNull(baseViewRecordUpdateWrapper.getLostToFollowUp());
        baseViewRecordUpdateWrapper.setLostToFollowUp("TRUE");
        Assert.assertEquals("TRUE", baseViewRecordUpdateWrapper.getLostToFollowUp());
    }

    @Test
    public void testGetInactive() {
        Assert.assertNull(baseViewRecordUpdateWrapper.getInactive());
        Whitebox.setInternalState(baseViewRecordUpdateWrapper, "inactive", "TRUE");
        Assert.assertEquals("TRUE", baseViewRecordUpdateWrapper.getInactive());
    }

    @Test
    public void testSetInactive() {
        Assert.assertNull(baseViewRecordUpdateWrapper.getInactive());
        baseViewRecordUpdateWrapper.setInactive("TRUE");
        Assert.assertEquals("TRUE", baseViewRecordUpdateWrapper.getInactive());
    }

    @Test
    public void testGetClient() {
        Assert.assertNull(baseViewRecordUpdateWrapper.getClient());
        Whitebox.setInternalState(baseViewRecordUpdateWrapper, "client", smartRegisterClient);
        Assert.assertNotNull(baseViewRecordUpdateWrapper.getClient());
    }

    @Test
    public void testSetClient() {
        Assert.assertNull(baseViewRecordUpdateWrapper.getClient());
        baseViewRecordUpdateWrapper.setClient(smartRegisterClient);
        Assert.assertNotNull(baseViewRecordUpdateWrapper.getClient());
    }
}
