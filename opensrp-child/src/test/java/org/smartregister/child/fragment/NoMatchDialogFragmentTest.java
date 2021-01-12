package org.smartregister.child.fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.view.activity.BaseRegisterActivity;


import static org.junit.Assert.assertNotNull;

public class NoMatchDialogFragmentTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private BaseRegisterActivity baseRegisterActivity;

    @Mock
    private FragmentManager fragmentManager;

    @Mock
    private FragmentTransaction fragmentTransaction;

    private NoMatchDialogFragment fragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        fragment = new NoMatchDialogFragment(baseRegisterActivity, "123");
    }

    @Test
    public void testInstantiation() {
        assertNotNull(Whitebox.getInternalState(fragment,"uniqueId"));
    }

    @Test
    public void testLaunchDialog() {
        Mockito.when(baseRegisterActivity.getSupportFragmentManager()).thenReturn(fragmentManager);
        Mockito.when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);

        NoMatchDialogFragment.launchDialog(baseRegisterActivity, "tag123", "Id123");

        Mockito.verify(fragmentManager).findFragmentByTag("tag123");
    }

}
