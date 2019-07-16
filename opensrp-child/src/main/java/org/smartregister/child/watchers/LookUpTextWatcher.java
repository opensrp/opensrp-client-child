package org.smartregister.child.watchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.vijay.jsonwizard.fragments.JsonFormFragment;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.EntityLookUp;
import org.smartregister.child.fragment.ChildFormFragment;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.MotherLookUpUtils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookUpTextWatcher implements TextWatcher {
    private static Map<String, EntityLookUp> lookUpMap;

    private final View mView;
    private final JsonFormFragment formFragment;
    private final String mEntityId;


    public LookUpTextWatcher(JsonFormFragment formFragment, View view, String entityId) {
        this.formFragment = formFragment;
        mView = view;
        mEntityId = entityId;
        lookUpMap = new HashMap<>();

    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    public void afterTextChanged(Editable editable) {
        String text = (String) mView.getTag(com.vijay.jsonwizard.R.id.raw_value);

        if (text == null) {
            text = editable.toString();
        }

        String key = (String) mView.getTag(com.vijay.jsonwizard.R.id.key);

        boolean afterLookUp = (Boolean) mView.getTag(com.vijay.jsonwizard.R.id.after_look_up);
        if (afterLookUp) {
            mView.setTag(com.vijay.jsonwizard.R.id.after_look_up, false);
            return;
        }

        EntityLookUp entityLookUp = new EntityLookUp();
        if (lookUpMap.containsKey(mEntityId)) {
            entityLookUp = lookUpMap.get(mEntityId);
        }

        if (StringUtils.isBlank(text)) {
            if (entityLookUp.containsKey(key)) {
                entityLookUp.remove(key);
            }
        } else {
            entityLookUp.put(key, text);
        }

        lookUpMap.put(mEntityId, entityLookUp);


        Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>> listener = null;
        if (formFragment instanceof ChildFormFragment) {
            ChildFormFragment childFormFragment = (ChildFormFragment) formFragment;
            listener = childFormFragment.motherLookUpListener();
        }

        if (mEntityId.equalsIgnoreCase(Constants.KEY.MOTHER)) {
            MotherLookUpUtils.motherLookUp(ChildLibrary.getInstance().context(), lookUpMap.get(mEntityId), listener, null);
        }

    }

}
