package org.smartregister.child.fragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.child.TestChildApp;
import org.smartregister.child.impl.activity.TestBaseChildRegisterActivity;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestChildApp.class, sdk = 27)
public class BaseAdvancedSearchFragmentTest {

    private TestBaseChildRegisterActivity baseChildRegisterActivity;

    private BaseAdvancedSearchFragment advancedSearchFragment;

    @Before
    public void setUp() {
        org.smartregister.Context.bindtypes = new ArrayList<>();
        baseChildRegisterActivity = Robolectric.buildActivity(TestBaseChildRegisterActivity.class).create().get();
        advancedSearchFragment = Mockito.mock(BaseAdvancedSearchFragment.class, Mockito.CALLS_REAL_METHODS);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSomething() {
       baseChildRegisterActivity.getSupportFragmentManager()
               .beginTransaction().add(advancedSearchFragment, "Search Fragment")
               .commitNow();
    }
}