package org.smartregister.child.task;

import android.view.Menu;
import android.view.MenuItem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.enums.Status;
import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.child.fragment.ChildUnderFiveFragment;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Map;

/**
 * Created by ndegwamartin on 23/03/2021.
 */
public class LoadAsyncTaskTest extends BaseUnitTest {

    @Mock
    Map<String, String> detailsMap;

    @Mock
    CommonPersonObjectClient childDetails;
    @Mock
    BaseChildDetailTabbedActivity activity;
    @Mock
    BaseChildRegistrationDataFragment childDataFragment;

    @Mock
    ChildUnderFiveFragment childUnderFiveFragment;

    @Mock
    private Menu menu;

    @Mock
    private MenuItem menuItem;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testActivateMenuItemByValueActivatesWhenValuePresent() {
        int menuItemIdA = 1;
        String cardId = "card-identifier-1000";

        Mockito.doReturn(menuItem).when(menu).findItem(menuItemIdA);

        LoadAsyncTask loadAsyncTask = new LoadAsyncTask(Status.EDIT_VACCINE, detailsMap, childDetails, activity, childDataFragment, childUnderFiveFragment, menu);
        loadAsyncTask.activateMenuItemByValue(menu, menuItemIdA, cardId);

        ArgumentCaptor<Boolean> booleanArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);

        Mockito.verify(menuItem).setEnabled(booleanArgumentCaptor.capture());

        Boolean capturedVal = booleanArgumentCaptor.getValue();
        Assert.assertNotNull(capturedVal);
        Assert.assertTrue(capturedVal);
    }

    @Test
    public void testActivateMenuItemByValueDoesNotActivateForNullValue() {
        int menuItemIdA = 1;

        Mockito.doReturn(menuItem).when(menu).findItem(menuItemIdA);

        LoadAsyncTask loadAsyncTask = new LoadAsyncTask(detailsMap, childDetails, activity, childDataFragment, childUnderFiveFragment, menu);
        loadAsyncTask.activateMenuItemByValue(menu, menuItemIdA, null);

        ArgumentCaptor<Boolean> booleanArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);

        Mockito.verify(menuItem).setEnabled(booleanArgumentCaptor.capture());

        Boolean capturedVal = booleanArgumentCaptor.getValue();
        Assert.assertNotNull(capturedVal);
        Assert.assertFalse(capturedVal);
    }
}