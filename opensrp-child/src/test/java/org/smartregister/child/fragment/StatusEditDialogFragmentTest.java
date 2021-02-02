package org.smartregister.child.fragment;

import androidx.appcompat.app.AppCompatActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.TestAppCompactActivity;
import org.smartregister.child.util.Constants;
import org.smartregister.util.Utils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@PrepareForTest({CoreLibrary.class, Utils.class})
public class StatusEditDialogFragmentTest extends BaseUnitTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private StatusEditDialogFragment fragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Map<String, String> details = new HashMap<>();
        details.put(Constants.KEY.FIRST_NAME, "John");
        details.put(Constants.KEY.LAST_NAME, "Doe");
        details.put(Constants.CHILD_STATUS.INACTIVE, "true");
        details.put(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP, "true");

        fragment = StatusEditDialogFragment.newInstance(details);
    }

    @Test
    public void testInstantiation() {
        assertNotNull(fragment);
    }

    @Test
    public void testOnAttachWithCorrectActivity() {
        assertNull(Whitebox.getInternalState(fragment, "listener"));

        TestAppCompactActivity activity = Robolectric.buildActivity(TestAppCompactActivity.class).create().start().get();
        fragment.show(activity.getFragmentManager(), "DIALOG_TAG");

        assertNotNull(Whitebox.getInternalState(fragment, "listener"));
    }

    @Test(expected=ClassCastException.class)
    public void testOnAttachWithIncorrectActivityThrowsException() {
        assertNull(Whitebox.getInternalState(fragment, "listener"));

        AppCompatActivity activity = Robolectric.buildActivity(AppCompatActivity.class).create().start().get();
        fragment.show(activity.getFragmentManager(), "DIALOG_TAG");
    }
}
