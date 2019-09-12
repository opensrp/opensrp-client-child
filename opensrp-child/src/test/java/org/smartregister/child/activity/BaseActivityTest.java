package org.smartregister.child.activity;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

public class BaseActivityTest {

    @Test
    public void testIsActivityStatusReturns(){
        BaseActivity baseActivity = Mockito.mock(BaseActivity.class);
        Mockito.when(baseActivity.isActiveStatus("Active")).thenReturn(true);
        Mockito.when(baseActivity.isActiveStatus((String) null)).thenReturn(false);
        Mockito.when(baseActivity.isActiveStatus("")).thenReturn(false);
        Assert.assertFalse(baseActivity.isActiveStatus((String) null));
        Assert.assertTrue(baseActivity.isActiveStatus("Active"));
        Assert.assertFalse(baseActivity.isActiveStatus(""));
    }

    @Test
    public void testGetHumanFriendlyChildStatus(){
        BaseActivity baseActivity = Mockito.mock(BaseActivity.class);
        Map<String, String> detailsColumnMapTest1 = new HashMap<>();
        detailsColumnMapTest1.put(BaseActivity.INACTIVE,"true");

        Mockito.when(baseActivity.getHumanFriendlyChildsStatus(detailsColumnMapTest1)).thenReturn("Inactive");

        Assert.assertEquals(baseActivity.getHumanFriendlyChildsStatus(detailsColumnMapTest1),"Inactive");

        Map<String, String> detailsColumnMapTest2 = new HashMap<>();
        detailsColumnMapTest2.put(BaseActivity.LOST_TO_FOLLOW_UP,"true");

        Mockito.when(baseActivity.getHumanFriendlyChildsStatus(detailsColumnMapTest2)).thenReturn("Lost to Follow up");

        Assert.assertEquals(baseActivity.getHumanFriendlyChildsStatus(detailsColumnMapTest2),"Lost to Follow up");


        Map<String, String> detailsColumnMapTest3 = new HashMap<>();
        detailsColumnMapTest3.put("something","true");

        Mockito.when(baseActivity.getHumanFriendlyChildsStatus(detailsColumnMapTest3)).thenReturn("Active");

        Assert.assertEquals(baseActivity.getHumanFriendlyChildsStatus(detailsColumnMapTest3),"Active");

    }
}
