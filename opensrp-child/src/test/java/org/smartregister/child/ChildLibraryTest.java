package org.smartregister.child;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.Repository;

import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ChildLibrary.class})
public class ChildLibraryTest {

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private Repository repository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private Context context;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetLocationRepositoryReturnsRepositoryInstance() {
        PowerMockito.mockStatic(ChildLibrary.class);
        when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        when(childLibrary.context()).thenReturn(context);
        when(context.getLocationRepository()).thenReturn(locationRepository);
        Assert.assertNotNull(locationRepository);
    }
}
