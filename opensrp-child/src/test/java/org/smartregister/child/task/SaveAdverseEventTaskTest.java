package org.smartregister.child.task;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.view.activity.DrishtiApplication;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class})
public class SaveAdverseEventTaskTest {

    private SaveAdverseEventTask saveAdverseEventTask;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

    }
}
