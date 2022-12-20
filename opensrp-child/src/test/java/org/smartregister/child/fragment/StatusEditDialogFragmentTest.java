package org.smartregister.child.fragment;

import androidx.appcompat.app.AppCompatActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@PrepareForTest({CoreLibrary.class, Utils.class})
public class StatusEditDialogFragmentTest extends BaseUnitTest {
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private StatusEditDialogFragment fragment;

    private AppCompatActivity appCompatActivity;

    private TestAppCompactActivity testAppCompactActivity;


    @Before
    public void setUp() {
        appCompatActivity = Robolectric.buildActivity(AppCompatActivity.class).create().start().get();
        testAppCompactActivity = Robolectric.buildActivity(TestAppCompactActivity.class).create().start().get();
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown(){
        try {
            appCompatActivity.finish();
            testAppCompactActivity.finish();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void testInstantiation() {
        Map<String, String> details = new HashMap<>();
        details.put(Constants.KEY.FIRST_NAME, "John");
        details.put(Constants.KEY.LAST_NAME, "Doe");
        details.put(Constants.CHILD_STATUS.INACTIVE, "true");

        fragment = StatusEditDialogFragment.newInstance(details);

        assertNotNull(fragment);
    }

    @Test
    public void testOnAttachWithCorrectActivity() {
        Map<String, String> details = new HashMap<>();
        details.put(Constants.KEY.FIRST_NAME, "John");
        details.put(Constants.KEY.LAST_NAME, "Doe");
        details.put(Constants.CHILD_STATUS.INACTIVE, "true");

        fragment = StatusEditDialogFragment.newInstance(details);

        assertNull(Whitebox.getInternalState(fragment, "listener"));

        fragment.show(testAppCompactActivity.getFragmentManager(), "DIALOG_TAG");

        assertNotNull(Whitebox.getInternalState(fragment, "listener"));
    }

    @Test(expected=ClassCastException.class)
    public void testOnAttachWithIncorrectActivityThrowsException() {
        Map<String, String> details = new HashMap<>();
        details.put(Constants.KEY.FIRST_NAME, "John");
        details.put(Constants.KEY.LAST_NAME, "Doe");
        details.put(Constants.CHILD_STATUS.INACTIVE, "true");

        fragment = StatusEditDialogFragment.newInstance(details);

        assertNull(Whitebox.getInternalState(fragment, "listener"));

        fragment.show(appCompatActivity.getFragmentManager(), "DIALOG_TAG");
    }

    @Test
    public void testOnCreateViewWhenLostToFollowUpIsSet() {
        Map<String, String> details = new HashMap<>();
        details.put(Constants.KEY.FIRST_NAME, "John");
        details.put(Constants.KEY.LAST_NAME, "Doe");
        details.put(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP, "true");

        Map<String, String> detailsMock = Mockito.spy(details);

        fragment = StatusEditDialogFragment.newInstance(detailsMock);

        fragment.show(testAppCompactActivity.getFragmentManager(), "DIALOG_TAG");

        Mockito.verify(detailsMock).containsKey(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP);
        Mockito.verify(detailsMock, Mockito.atLeastOnce()).get(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP);
        assertEquals("true", detailsMock.get(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP));
    }

    @Test
    public void testOnCreateViewWhenInactiveAndLostToFollowUpAreNotSet() {
        Map<String, String> details = new HashMap<>();
        details.put(Constants.KEY.FIRST_NAME, "John");
        details.put(Constants.KEY.LAST_NAME, "Doe");

        Map<String, String> detailsMock = Mockito.spy(details);

        fragment = StatusEditDialogFragment.newInstance(detailsMock);

        fragment.show(testAppCompactActivity.getFragmentManager(), "DIALOG_TAG");

        Mockito.verify(detailsMock, Mockito.never()).get(Constants.CHILD_STATUS.INACTIVE);
        Mockito.verify(detailsMock, Mockito.never()).get(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP);
    }

}
