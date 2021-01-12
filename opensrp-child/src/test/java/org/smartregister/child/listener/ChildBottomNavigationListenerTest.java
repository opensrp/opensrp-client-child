package org.smartregister.child.listener;

import android.view.MenuItem;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.view.activity.BaseRegisterActivity;

public class ChildBottomNavigationListenerTest {


    private ChildBottomNavigationListener bottomNavigationListener;

    @Mock
    private BaseRegisterActivity baseRegisterActivity;

    @Before
    public void setUp() {
        baseRegisterActivity = Mockito.mock(BaseChildRegisterActivity.class);
        bottomNavigationListener = new ChildBottomNavigationListener(baseRegisterActivity);
    }

    @Test
    public void testOnNavigationItemSelectedWithDifferentMenuItemsSelected() {
        MenuItem menuItem = Mockito.spy(MenuItem.class);

        Mockito.when(menuItem.getItemId()).thenReturn(R.id.action_home);
        bottomNavigationListener.onNavigationItemSelected(menuItem);
        Mockito.verify(baseRegisterActivity, Mockito.atMost(1)).switchToBaseFragment();

        Mockito.when(menuItem.getItemId()).thenReturn(R.id.action_scan_qr);
        bottomNavigationListener.onNavigationItemSelected(menuItem);
        Mockito.verify(baseRegisterActivity, Mockito.atMost(1)).startQrCodeScanner();

        Mockito.when(menuItem.getItemId()).thenReturn(R.id.action_scan_card);
        bottomNavigationListener.onNavigationItemSelected(menuItem);
        Mockito.verify((BaseChildRegisterActivity)baseRegisterActivity, Mockito.atMost(1)).startNFCCardScanner();

        Mockito.when(menuItem.getItemId()).thenReturn(R.id.action_search);
        bottomNavigationListener.onNavigationItemSelected(menuItem);
        Mockito.verify(baseRegisterActivity, Mockito.atMost(1)).switchToFragment(1);

        Mockito.when(menuItem.getItemId()).thenReturn(R.id.action_register);
        bottomNavigationListener.onNavigationItemSelected(menuItem);
        Mockito.verify(baseRegisterActivity, Mockito.atMost(1)).startRegistration();
    }
}