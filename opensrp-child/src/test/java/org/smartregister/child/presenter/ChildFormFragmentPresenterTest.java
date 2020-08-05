package org.smartregister.child.presenter;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.util.Constants;

/**
 * Created by ndegwamartin on 14/07/2020.
 */
public class ChildFormFragmentPresenterTest extends BaseUnitTest {

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private JsonFormInteractor jsonFormInteractor;

    @Mock
    private JSONObject mStepDetails;

    @Mock
    private JsonFormFragmentView<JsonFormFragmentViewState> formFragmentView;

    @Mock
    private JsonApi jsonApi;

    private ChildFormFragmentPresenter presenter;


    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        Mockito.doReturn(jsonApi).when(formFragment).getJsonApi();
        presenter = Mockito.spy(new ChildFormFragmentPresenter(formFragment, jsonFormInteractor));
        presenter.attachView(formFragmentView);

        Whitebox.setInternalState(presenter, "mStepName", "step2");

        Mockito.doReturn(true).when(mStepDetails).has(Constants.JSON_FORM_EXTRA.NEXT);
        Whitebox.setInternalState(presenter, "mStepDetails", mStepDetails);

    }

    @Test
    public void testSetUpToolBarInvokesSuperImplementation() {

        presenter.setUpToolBar();
        Mockito.verify(formFragmentView, Mockito.times(1)).setUpBackButton();

    }

    @Test
    public void testIntermediatePageChecksWhetherFormHasNextPage() {

        boolean result = presenter.intermediatePage();
        Mockito.verify(mStepDetails).has(Constants.JSON_FORM_EXTRA.NEXT);
        Assert.assertTrue(result);
    }
}