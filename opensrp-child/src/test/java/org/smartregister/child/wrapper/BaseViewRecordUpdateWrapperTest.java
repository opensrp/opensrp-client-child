package org.smartregister.child.wrapper;

import android.view.View;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.view.contract.SmartRegisterClient;

public class BaseViewRecordUpdateWrapperTest {

    @Test
    public void testBaseViewRecordUpdateWrapper(){
        BaseViewRecordUpdateWrapper baseViewRecordUpdateWrapper = new BaseViewRecordUpdateWrapper();
        Assert.assertNull(baseViewRecordUpdateWrapper.getClient());
        Assert.assertNull(baseViewRecordUpdateWrapper.getConvertView());
        Assert.assertNull(baseViewRecordUpdateWrapper.getInactive());
        Assert.assertNull(baseViewRecordUpdateWrapper.getLostToFollowUp());

        baseViewRecordUpdateWrapper.setClient(Mockito.mock(SmartRegisterClient.class));
        baseViewRecordUpdateWrapper.setConvertView(Mockito.mock(View.class));
        baseViewRecordUpdateWrapper.setInactive("something");
        baseViewRecordUpdateWrapper.setLostToFollowUp("something");
        
        Assert.assertNotNull(baseViewRecordUpdateWrapper.getClient());
        Assert.assertNotNull(baseViewRecordUpdateWrapper.getConvertView());
        Assert.assertNotNull(baseViewRecordUpdateWrapper.getInactive());
        Assert.assertNotNull(baseViewRecordUpdateWrapper.getLostToFollowUp());
    }
}
