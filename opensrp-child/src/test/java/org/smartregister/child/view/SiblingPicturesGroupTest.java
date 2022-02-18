package org.smartregister.child.view;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.activity.BaseActivity;

import java.util.Collections;

public class SiblingPicturesGroupTest extends BaseUnitTest {

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