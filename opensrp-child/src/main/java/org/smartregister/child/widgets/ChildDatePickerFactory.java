package org.smartregister.child.widgets;

import android.content.Context;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.util.Constants;
import org.smartregister.child.watchers.LookUpTextWatcher;

import timber.log.Timber;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

/**
 * Created by keyman on 11/04/2017.
 */
public class ChildDatePickerFactory extends DatePickerFactory {

    @Override
    public void attachLayout(String stepName, final Context context, JsonFormFragment formFragment, JSONObject jsonObject,
                             final MaterialEditText editText, final TextView duration) {
        super.attachLayout(stepName, context, formFragment, jsonObject, editText, duration);

        WidgetUtils.hookupLookup(editText, jsonObject, formFragment);

        try {
            if (jsonObject.has(Constants.KEY.LOOK_UP) && jsonObject.get(Constants.KEY.LOOK_UP).toString().equalsIgnoreCase(Boolean.TRUE.toString())) {
                String entityId = jsonObject.getString(Constants.KEY.ENTITY_ID);
                editText.addTextChangedListener(new LookUpTextWatcher(formFragment, editText, entityId));
                editText.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
            }
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e); Timber.e(e, getClass().getName(), e.toString());
        }
    }
}
