package org.smartregister.child.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.child.ChildLibrary;
import org.smartregister.domain.UniqueId;
import org.smartregister.repository.UniqueIdRepository;

import java.util.Date;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ChildLibrary.class)
public class UtilsTest {
    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private UniqueIdRepository uniqueIdRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getNextOpenMrsId() {
        UniqueId uniqueId = new UniqueId();
        uniqueId.setId("1");
        uniqueId.setCreatedAt(new Date());
        uniqueId.setOpenmrsId("34334-9");
        PowerMockito.mockStatic(ChildLibrary.class);
        PowerMockito.when(ChildLibrary.getInstance()).thenReturn(childLibrary);
        PowerMockito.when(childLibrary.getUniqueIdRepository()).thenReturn(uniqueIdRepository);
        PowerMockito.when(uniqueIdRepository.getNextUniqueId()).thenReturn(uniqueId);
        Assert.assertEquals(uniqueId.getOpenmrsId(), Utils.getNextOpenMrsId());

    }
}