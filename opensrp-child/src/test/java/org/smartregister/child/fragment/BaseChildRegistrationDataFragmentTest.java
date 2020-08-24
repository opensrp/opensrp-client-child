package org.smartregister.child.fragment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseChildRegistrationDataFragmentTest {

    @Spy
    private BaseChildRegistrationDataFragment baseChildRegistrationDataFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddUnformattedKeys() {
        assertEquals(baseChildRegistrationDataFragment.addUnFormattedNumberFields(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()).size(), 2);
    }

    @Test
    public void testCleanResultShouldReturnFormattedStringIfInputIsANumber() throws Exception {
        Method method = BaseChildRegistrationDataFragment.class.getDeclaredMethod("cleanResult", String.class);
        method.setAccessible(true);

        String inputString = "12345679";
        assertEquals("12345679", method.invoke(baseChildRegistrationDataFragment, inputString));
    }

    @Test
    public void testCleanResultShouldReturnSameStringIfInputIsNotANumber() throws Exception {
        Method method = BaseChildRegistrationDataFragment.class.getDeclaredMethod("cleanResult", String.class);
        method.setAccessible(true);

        String inputString = "samplestring";
        assertEquals("samplestring", method.invoke(baseChildRegistrationDataFragment, inputString));
    }

    @Test
    public void testIsSkippableValueReturnsTrueIfValueIsOther() throws Exception {
        Method method = BaseChildRegistrationDataFragment.class.getDeclaredMethod("isSkippableValue", String.class);
        method.setAccessible(true);

        String inputString = "[\"Other\"]";
        assertTrue((Boolean) method.invoke(baseChildRegistrationDataFragment, inputString));
    }
}