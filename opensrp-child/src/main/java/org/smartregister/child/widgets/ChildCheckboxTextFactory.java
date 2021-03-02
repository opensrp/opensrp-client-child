package org.smartregister.child.widgets;

import android.view.View;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.widgets.CheckBoxFactory;

import org.json.JSONObject;

/**
 * Created by ndegwamartin on 2020-03-25.
 */
public class ChildCheckboxTextFactory extends CheckBoxFactory {

    @Override
    public void genericWidgetLayoutHookback(View view, JSONObject jsonObject, JsonFormFragment formFragment) {

        WidgetUtils.hookupLookup(view, jsonObject, formFragment);
    }

}