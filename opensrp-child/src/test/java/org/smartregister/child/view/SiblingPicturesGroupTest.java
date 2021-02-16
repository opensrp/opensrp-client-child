package org.smartregister.child.view;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.child.activity.BaseActivity;
import org.junit.Assert;
import java.util.Collections;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SiblingPicturesGroupTest {

    private SiblingPicturesGroup siblingPicturesGroup;

    @Mock
    private BaseActivity baseActivity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        siblingPicturesGroup = Mockito.spy(new SiblingPicturesGroup(ApplicationProvider.getApplicationContext()));
    }

    @Test
    public void testThatSetSiblingBaseEntityIdsWorks() {
        siblingPicturesGroup.setSiblingBaseEntityIds(baseActivity, Collections.singletonList("sskaslakkkk"));
        Assert.assertNotNull(siblingPicturesGroup.getSiblingsGV().getAdapter());

    }
}