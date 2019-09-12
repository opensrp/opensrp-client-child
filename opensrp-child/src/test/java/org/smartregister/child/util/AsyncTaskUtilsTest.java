package org.smartregister.child.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.child.domain.NamedObject;
import org.smartregister.immunization.domain.Vaccine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AsyncTaskUtils.class})
public class AsyncTaskUtilsTest {

    @Test
    public void testExtractVaccinesWithEmptyMap(){
        Map<String, NamedObject<?>> map = new HashMap<>();
        PowerMockito.verifyStatic(AsyncTaskUtils.class, Mockito.times(1));
        AsyncTaskUtils.extractVaccines(map);
        Assert.assertEquals(AsyncTaskUtils.extractVaccines(map), new ArrayList<Vaccine>());
    }

    @Test
    public void testExtractVaccinesWithNonEmptyMap(){
        Map<String, NamedObject<?>> map = new HashMap<>();
        List<Vaccine> vaccines = new ArrayList<>();
        NamedObject<List<Vaccine>> namedObject = new NamedObject<>("test", vaccines);
        map.put(Vaccine.class.getName(), namedObject);
        AsyncTaskUtils.extractVaccines(map);
        Assert.assertEquals(AsyncTaskUtils.extractVaccines(map), vaccines);
    }
}
