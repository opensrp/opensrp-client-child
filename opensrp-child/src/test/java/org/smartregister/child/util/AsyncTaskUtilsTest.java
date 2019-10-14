package org.smartregister.child.util;

import org.junit.Assert;
import org.junit.Test;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.domain.NamedObject;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsyncTaskUtilsTest extends BaseUnitTest {

    @Test
    public void testExtractVaccinesWithEmptyMap(){
        Map<String, NamedObject<?>> map = new HashMap<>();
        Assert.assertEquals(AsyncTaskUtils.extractVaccines(map), new ArrayList<Vaccine>());
    }

    @Test
    public void testExtractVaccinesWithNonEmptyMap(){
        Map<String, NamedObject<?>> map = new HashMap<>();
        List<Vaccine> vaccines = new ArrayList<>();
        NamedObject<List<Vaccine>> namedObject = new NamedObject<>("test", vaccines);
        map.put(Vaccine.class.getName(), namedObject);
        Assert.assertEquals(AsyncTaskUtils.extractVaccines(map), vaccines);
    }
}
