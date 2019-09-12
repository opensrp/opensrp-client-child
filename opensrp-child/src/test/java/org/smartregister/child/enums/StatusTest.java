package org.smartregister.child.enums;

import org.junit.Assert;
import org.junit.Test;

public class StatusTest {
    @Test
    public void testNameOfEnumShouldNotBeNull(){
        for(Status status: Status.values()){
            Assert.assertNotNull(status.name());
        }
    }
}
