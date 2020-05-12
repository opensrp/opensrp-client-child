package org.smartregister.child.widgets;

import android.view.View;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.widgets.SpinnerFactory;

import org.json.JSONObject;

/**
 * Created by ndegwamartin on 2020-04-28.
 */
public class ChildSpinnerFactory extends SpinnerFactory {

    @Override
    public void genericWidgetLayoutHookback(View view, JSONObject jsonObject, JsonFormFragment formFragment) {

        WidgetUtils.hookupLookup(view, jsonObject, formFragment);
    }
}
