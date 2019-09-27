package org.smartregister.child.fragment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;

public class BaseChildRegistrationDataFragmentTest {
    @Spy
    private BaseChildRegistrationDataFragment baseChildRegistrationDataFragment;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddUnformattedKeys(){
        assertEquals(baseChildRegistrationDataFragment.addUnFormattedNumberFields(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()).size(), 2);
    }
}