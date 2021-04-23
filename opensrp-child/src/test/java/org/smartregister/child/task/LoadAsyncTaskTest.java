package org.smartregister.child.task;

import android.view.Menu;
import android.view.MenuItem;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.BasePowerMockUnitTest;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.enums.Status;
import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.child.fragment.ChildUnderFiveFragment;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.AppProperties;

import java.util.Map;

/**
 * Created by ndegwamartin on 23/03/2021.
 */
public class LoadAsyncTaskTest extends BasePowerMockUnitTest {

    @Mock
    private Map<String, String> detailsMap;

    @Mock
    private CommonPersonObjectClient childDetails;

    @Mock
    private BaseChildDetailTabbedActivity activity;

    @Mock
    private BaseChildRegistrationDataFragment childDataFragment;

    @Mock
    private ChildUnderFiveFragment childUnderFiveFragment;

    @Mock
    private Menu menu;

    @Mock
    private MenuItem menuItem;

    @Mock
    private AppProperties appProperties;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private ChildLibrary childLibrary;

    @Mock
    private Context openSRPContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", childLibrary);
        Mockito.doReturn(openSRPContext).when(coreLibrary).context();
        Mockito.doReturn(appProperties).when(openSRPContext).getAppProperties();
        Mockito.doReturn(appProperties).when(childLibrary).getProperties();
        Mockito.doReturn(false).when(appProperties).getPropertyBoolean("monitor.height");
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(ChildLibrary.class, "instance", null);
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