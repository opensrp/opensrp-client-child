package org.smartregister.child;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.Repository;

import id.zelory.compressor.Compressor;


@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@PrepareForTest({ChildLibrary.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*", "androidx.*", "javax.management.*", "org.xmlpull.v1.*",})
public class ChildLibraryTest {

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

    private final String versionName = "1.0.0";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetLocationRepositoryReturnsRepositoryInstance() {
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        childLibrary.setApplicationVersionName("1.2.0");
        Mockito.doReturn(locationRepository).when(childLibrary).getLocationRepository();
        LocationRepository repository = ChildLibrary.getInstance().getLocationRepository();
        Mockito.verify(ChildLibrary.getInstance()).getLocationRepository();
        Assert.assertEquals(locationRepository, repository);
    }

    @Test
    public void testChildLibraryInitsCorrectly() {
        ChildLibrary.init(context, repository, metadata, 4, versionName, 1);
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

        Mockito.doReturn(ApplicationProvider.getApplicationContext()).when(context).applicationContext();

        ChildLibrary.init(context, repository, metadata, 4, versionName, 1);
        ChildLibrary childLibrary = ChildLibrary.getInstance();

        Compressor compressor = childLibrary.getCompressor();
        Assert.assertNotNull(compressor);
    }

    @Test
    public void testGetEventClientRepositoryNotNull() {
        Mockito.doReturn(ApplicationProvider.getApplicationContext()).when(context).applicationContext();

        ChildLibrary.init(context, repository, metadata, 4,  versionName,1);
        ChildLibrary childLibrary = ChildLibrary.getInstance();

        Assert.assertNotNull(childLibrary);
        Assert.assertNotNull(childLibrary.eventClientRepository());
    }

    @After
    public void tearDown() {
        ChildLibrary.destroyInstance();
    }
}
