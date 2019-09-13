package org.smartregister.child.enums;


import org.junit.Assert;
import org.junit.Test;

public class LocationHierarchyTest {
    @Test
    public void testNameOfEnumShouldNotBeNull(){
        for(LocationHierarchy hierarchy: LocationHierarchy.values()){
            Assert.assertNotNull(hierarchy.name());
        }
    }

}
