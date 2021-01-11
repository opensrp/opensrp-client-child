package org.smartregister.child.widgets;

import android.content.Context;
import android.widget.ImageView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.widgets.EditTextFactory;

import org.json.JSONObject;
import org.smartregister.child.util.Constants;
import org.smartregister.child.watchers.LookUpTextWatcher;

/**
 * Created by keyman on 11/04/2017.
 */
public class ChildEditTextFactory extends EditTextFactory {

    @Override
    public void attachLayout(String stepName, Context context, JsonFormFragment formFragment, JSONObject jsonObject,
                             MaterialEditText editText, ImageView editable) throws Exception {
        super.attachLayout(stepName, context, formFragment, jsonObject, editText, editable);

        WidgetUtils.hookupLookup(editText, jsonObject, formFragment);

        if (jsonObject.has(Constants.KEY.LOOK_UP) && jsonObject.get(Constants.KEY.LOOK_UP).toString().equalsIgnoreCase(Boolean.TRUE.toString())) {

            String entityId = jsonObject.getString(Constants.KEY.ENTITY_ID);
            editText.addTextChangedListener(new LookUpTextWatcher(formFragment, editText, entityId));
            editText.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
        }
    }
}