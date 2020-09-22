package org.smartregister.child;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.Repository;

import id.zelory.compressor.Compressor;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ChildLibrary.class})
public class ChildLibraryTest extends BaseUnitTest {

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private Context context;

    @Mock
    private Repository repository;

    @Mock
    private ChildMetadata metadata;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetLocationRepositoryReturnsRepositoryInstance() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        childLibrary.setApplicationVersionName("1.2.0");
        Mockito.doReturn(context).when(childLibrary).context();
        Mockito.doReturn(locationRepository).when(childLibrary).getLocationRepository();
        LocationRepository repository = ChildLibrary.getInstance().getLocationRepository();
        Mockito.verify(ChildLibrary.getInstance()).getLocationRepository();
        Assert.assertEquals(locationRepository, repository);
    }

    @Test
    public void testChildLibraryInitsCorrectly() {

        ChildLibrary.init(context, repository, metadata, 4, 1);
        ChildLibrary childLibrary = ChildLibrary.getInstance();

        Assert.assertNotNull(childLibrary);
        Assert.assertEquals(1, childLibrary.getDatabaseVersion());
        Assert.assertEquals(4, childLibrary.getApplicationVersion());
        Assert.assertEquals(metadata, childLibrary.metadata());
        Assert.assertEquals(context, childLibrary.context());
        Assert.assertEquals(repository, childLibrary.getRepository());
    }

    @Test
    public void testGetCompressorCreatesAndReturnsNonNullIninstance() {

        Mockito.doReturn(RuntimeEnvironment.application).when(context).applicationContext();

        ChildLibrary.init(context, repository, metadata, 4, 1);
        ChildLibrary childLibrary = ChildLibrary.getInstance();

        Compressor compressor = childLibrary.getCompressor();
        Assert.assertNotNull(compressor);

    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
    }

}
