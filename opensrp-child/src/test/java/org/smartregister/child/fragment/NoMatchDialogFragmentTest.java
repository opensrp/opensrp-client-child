package org.smartregister.child.fragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.child.TestChildApp;
import org.smartregister.child.impl.activity.TestBaseChildRegisterActivity;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = TestChildApp.class)
public class NoMatchDialogFragmentTest {

    private TestBaseChildRegisterActivity childRegisterActivity;

    private NoMatchDialogFragment noMatchDialogFragment;

    @Before
    public void setUp() {
        childRegisterActivity = Robolectric.buildActivity(TestBaseChildRegisterActivity.class).create().get();
    }

    @Test
    public void launchDialog() {
        noMatchDialogFragment = NoMatchDialogFragment.launchDialog(childRegisterActivity, NoMatchDialogFragment.TAG, "some-unique-id");
        Assert.assertNotNull(noMatchDialogFragment);
    }
}