package org.smartregister.child.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.child.BaseUnitTest;
import org.smartregister.child.contract.ChildRegisterContract;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndegwamartin on 2019-07-09.
 */
public class BaseChildRegisterPresenterTest extends BaseUnitTest {

    @Mock
    protected WeakReference<ChildRegisterContract.View> viewReference;

    @Mock
    protected ChildRegisterContract.Model model;

    private BaseChildRegisterPresenter baseChildRegisterPresenter;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        baseChildRegisterPresenter = new BaseChildRegisterPresenter(viewReference.get(), model);
    }

    @Test
    public void testBaseChildRegisterPresenterInstantiatesCorrectly() {

        Assert.assertNotNull(baseChildRegisterPresenter);

    }

    @Test
    public void testRegisterViewConfigurationsInvokesModelMethodWithCorrectParameters() {
        List<String> viewIds = new ArrayList<>();
        viewIds.add("123");
        viewIds.add("456");

        baseChildRegisterPresenter.registerViewConfigurations(viewIds);

        Mockito.verify(model).registerViewConfigurations(viewIds);

    }

    @Test
    public void testSetInteractorFunctionsCorrectly() {

        Assert.assertNotNull(baseChildRegisterPresenter);

    }
}
