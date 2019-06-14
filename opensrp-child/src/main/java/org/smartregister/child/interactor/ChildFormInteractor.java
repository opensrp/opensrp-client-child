package org.smartregister.child.interactor;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;

import org.smartregister.child.widgets.WellnessDatePickerFactory;
import org.smartregister.child.widgets.WellnessEditTextFactory;

/**
 * Created by ndegwamartin on 19/03/2019.
 */
public class ChildFormInteractor extends JsonFormInteractor {

    private static final ChildFormInteractor WELLNESS_INTERACTOR_INSTANCE = new ChildFormInteractor();

    private ChildFormInteractor() {
        super();
    }

    public static JsonFormInteractor getWellnessInteractorInstance() {
        return WELLNESS_INTERACTOR_INSTANCE;
    }

    @Override
    protected void registerWidgets() {
        super.registerWidgets();
        map.put(JsonFormConstants.EDIT_TEXT, new WellnessEditTextFactory());
        map.put(JsonFormConstants.DATE_PICKER, new WellnessDatePickerFactory());
    }
}
