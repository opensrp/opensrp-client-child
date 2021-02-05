package org.smartregister.child.fragment;

import android.view.View;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.child.R;
import org.smartregister.child.TestChildApp;
import org.smartregister.child.impl.activity.TestBaseChildRegisterActivity;
import org.smartregister.child.impl.presenter.TestAdvanceSearchPresenter;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(application = TestChildApp.class, sdk = 27)
public class BaseAdvancedSearchFragmentTest {

    private TestBaseChildRegisterActivity baseChildRegisterActivity;

    private BaseAdvancedSearchFragment baseAdvancedSearchFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        org.smartregister.Context.bindtypes = new ArrayList<>();
        baseChildRegisterActivity = Robolectric.buildActivity(TestBaseChildRegisterActivity.class).create().get();
        baseAdvancedSearchFragment = Mockito.mock(BaseAdvancedSearchFragment.class, Mockito.CALLS_REAL_METHODS);
        BaseChildAdvancedSearchModel baseChildAdvancedSearchModel = Mockito.mock(BaseChildAdvancedSearchModel.class, Mockito.CALLS_REAL_METHODS);
        BaseChildAdvancedSearchPresenter baseChildAdvancedSearchPresenter = new TestAdvanceSearchPresenter(baseAdvancedSearchFragment, "", baseChildAdvancedSearchModel);
        Mockito.doReturn(baseChildRegisterActivity).when(baseAdvancedSearchFragment).getActivity();
        Mockito.doReturn(baseChildRegisterActivity).when(baseAdvancedSearchFragment).requireActivity();
        Mockito.doReturn(baseChildAdvancedSearchPresenter).when(baseAdvancedSearchFragment).getPresenter();
        baseAdvancedSearchFragment.initializePresenter();
    }

    @Test
    public void testThatPresenterIsNotNull() {
        Assert.assertNotNull(baseAdvancedSearchFragment.getPresenter());
    }

    @Test
    @Ignore("Fix this java.lang.IllegalStateException: Can't access ViewModels from detached fragment")
    public void testThatViewsAreCorrectlySetup() {
        View view = baseChildRegisterActivity.getLayoutInflater().inflate(R.layout.fragment_advanced_search, null, false);
        baseAdvancedSearchFragment.setupViews(view);
        View listView = view.findViewById(R.id.advanced_search_list);
        Assert.assertEquals(listView.getVisibility(), View.GONE);

    }

}