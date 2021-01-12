package org.smartregister.child.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.smartregister.child.widgets.ChildCheckboxTextFactory;
import org.smartregister.child.widgets.ChildDatePickerFactory;
import org.smartregister.child.widgets.ChildEditTextFactory;
import org.smartregister.child.widgets.ChildSpinnerFactory;

import java.util.Map;

public class ChildFormInteractorTest {

    @Spy
    private ChildFormInteractor childFormInteractor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetChildInteractorInstance() {
        JsonFormInteractor childInteractorInstance = ChildFormInteractor.getChildInteractorInstance();
        Assert.assertNotNull(childInteractorInstance);
        Assert.assertTrue(childInteractorInstance instanceof ChildFormInteractor);
    }

    @Test
    public void testRegisterWidgets() {
        childFormInteractor.registerWidgets();
        Map<String, FormWidgetFactory> registeredViews = childFormInteractor.map;
        Assert.assertTrue(registeredViews.get(JsonFormConstants.EDIT_TEXT) instanceof ChildEditTextFactory);
        Assert.assertTrue(registeredViews.get(JsonFormConstants.DATE_PICKER) instanceof ChildDatePickerFactory);
        Assert.assertTrue(registeredViews.get(JsonFormConstants.CHECK_BOX) instanceof ChildCheckboxTextFactory);
        Assert.assertTrue(registeredViews.get(JsonFormConstants.SPINNER) instanceof ChildSpinnerFactory);

    }
}