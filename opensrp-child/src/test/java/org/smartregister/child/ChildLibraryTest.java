package org.smartregister.child;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.repository.LocationRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ChildLibrary.class})
public class ChildLibraryTest {

    @Mock
    private ChildLibrary childLibrary;

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
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        doReturn(context).when(childLibrary).context();
        doReturn(locationRepository).when(childLibrary).getLocationRepository();
        LocationRepository repository = ChildLibrary.getInstance().getLocationRepository();
        verify(ChildLibrary.getInstance()).getLocationRepository();
        assertEquals(locationRepository, repository);
    }
}
