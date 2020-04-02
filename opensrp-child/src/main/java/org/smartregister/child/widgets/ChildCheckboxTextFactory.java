package org.smartregister.child.widgets;

import android.util.Log;
import android.view.View;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.widgets.CheckBoxFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by ndegwamartin on 2020-03-25.
 */
public class ChildCheckboxTextFactory extends CheckBoxFactory {

    @Override
    public void genericWidgetLayoutHookback(View view, JSONObject jsonObject, JsonFormFragment formFragment) {

        try {

            if (jsonObject.has(Constants.KEY.LOOK_UP) && jsonObject.get(Constants.KEY.LOOK_UP).toString().equalsIgnoreCase(Boolean.TRUE.toString())) {

                String entityId = jsonObject.getString(Constants.KEY.ENTITY_ID);

                Map<String, List<View>> lookupMap = formFragment.getLookUpMap();

                List<View> lookUpViews = new ArrayList<>();
                if (lookupMap.containsKey(entityId)) {
                    lookUpViews = lookupMap.get(entityId);
                }

                if (!lookUpViews.contains(view)) {
                    lookUpViews.add(view);
                }

                lookupMap.put(entityId, lookUpViews);
            }

        } catch (JSONException exception) {

            Log.e(ChildCheckboxTextFactory.class.getCanonicalName(), exception.getMessage(), exception);

        }
    }

}