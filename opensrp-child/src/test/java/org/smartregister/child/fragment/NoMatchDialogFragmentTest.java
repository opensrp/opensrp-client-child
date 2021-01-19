package org.smartregister.child.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.R;
import org.smartregister.view.activity.BaseRegisterActivity;


import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NoMatchDialogFragmentTest extends BaseUnitTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private BaseRegisterActivity baseRegisterActivity;

    @Mock
    private FragmentManager fragmentManager;

    @Mock
    private FragmentTransaction fragmentTransaction;

    @Mock
    private LayoutInflater layoutInflater;

    @Mock
    private ViewGroup viewGroup;

    @Mock
    private Button button;

    @Mock
    private Fragment prevFragment;

    private NoMatchDialogFragment fragment;

    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        fragment = new NoMatchDialogFragment(baseRegisterActivity, "123");
        context = RuntimeEnvironment.application;

        AppCompatActivity activity = Robolectric.buildActivity(AppCompatActivity.class).create().start().get();
        activity.getSupportFragmentManager().beginTransaction().add(fragment, "Tasks").commit();
    }

    @Test
    public void testInstantiation() {
        assertNotNull(Whitebox.getInternalState(fragment,"uniqueId"));
    }

    @Test
    public void testLaunchDialogWhenActivityIsNotNullAndPreviousFragmentIsNull() {
        when(baseRegisterActivity.getSupportFragmentManager()).thenReturn(fragmentManager);
        when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);

        NoMatchDialogFragment.launchDialog(baseRegisterActivity, "tag123", "Id123");

        Mockito.verify(fragmentManager).findFragmentByTag("tag123");
    }

    @Test
    public void testLaunchDialogWhenActivityIsNull() {
        when(baseRegisterActivity.getSupportFragmentManager()).thenReturn(fragmentManager);
        when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);

        NoMatchDialogFragment noMatchDialogFragment = NoMatchDialogFragment.launchDialog(null, "tag123", "Id123");

        Assert.assertNull(noMatchDialogFragment);
    }

    @Test
    public void testLaunchDialogWhenPreviousFragmentIsNotNull() {
        when(baseRegisterActivity.getSupportFragmentManager()).thenReturn(fragmentManager);
        when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);

        doReturn(prevFragment).when(fragmentManager).findFragmentByTag("tag123");

        NoMatchDialogFragment noMatchDialogFragment = NoMatchDialogFragment.launchDialog(baseRegisterActivity, "tag123", "Id123");

        verify(fragmentTransaction).remove(prevFragment);
    }

    @Test
    public void testOnCreateView() {
        Mockito.doReturn(viewGroup).when(layoutInflater).inflate(R.layout.dialog_no_match, null, false);
        Mockito.doReturn(button).when(viewGroup).findViewById(R.id.cancel_no_match_dialog);
        Mockito.doReturn(button).when(viewGroup).findViewById(R.id.go_to_advanced_search);

        fragment.onCreateView(layoutInflater, null, null);

        verify(viewGroup).findViewById(R.id.cancel_no_match_dialog);
        verify(viewGroup).findViewById(R.id.go_to_advanced_search);

        verify(button, times(2)).setOnClickListener(any());
    }

    @Test
    public void testOnCancel() {
        fragment.onCancel(null);

        verify(baseRegisterActivity).setSearchTerm("");
    }

    @Test
    public void testNoMatchDialogActionHandlerWhenCancelIsClicked() {
        View v = fragment.onCreateView(LayoutInflater.from(context), null, null);

        v.findViewById(R.id.cancel_no_match_dialog).performClick();

        verify(baseRegisterActivity).setSearchTerm("");
    }

    @Test
    public void testNoMatchDialogActionHandlerWhenGoIsClicked() {
        View v = fragment.onCreateView(LayoutInflater.from(context), null, null);

        v.findViewById(R.id.go_to_advanced_search).performClick();

        verify(baseRegisterActivity).setSearchTerm("");
        verify(baseRegisterActivity).setSelectedBottomBarMenuItem(R.id.action_search);
    }

}
