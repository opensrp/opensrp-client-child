package org.smartregister.child.sample.fragment;

import android.os.Bundle;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.jetbrains.annotations.NotNull;
import org.smartregister.child.fragment.ChildFormFragment;
import org.smartregister.child.util.Constants;

import java.util.HashMap;
import java.util.HashSet;

public class SampleChildFormFragment extends ChildFormFragment {

    @Override
    protected @NotNull HashMap<String, String> getKeyAliasMap() {
        return new HashMap<String, String>() {
            {
                put("Mother_Guardian_Last_Name", Constants.KEY.LAST_NAME);
                put("Mother_Guardian_First_Name", Constants.KEY.FIRST_NAME);
                put("Mother_Guardian_Date_Birth", Constants.KEY.DOB);
                put("Mother_Guardian_Sex", Constants.KEY.GENDER);
                put("Mother_Guardian_Phone_Number", Constants.KEY.MOTHER_GUARDIAN_PHONE_NUMBER);
                put("Preferred_Language", "preferred_language");
                put("Residential_Area", "residential_area");
                put("Mother_Guardian_NRC", "nrc_number");
            }
        };
    }

    public static SampleChildFormFragment getFormFragment(String stepName) {
        SampleChildFormFragment jsonFormFragment = new SampleChildFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected @NotNull HashSet<String> getNonHumanizedFields() {
        HashSet<String> nonHumanizedFields = super.getNonHumanizedFields();
        nonHumanizedFields.add("is_consented");
        return nonHumanizedFields;
    }

    @Override
    protected void setValueOnView(String fieldName, String value, View view) {
        super.setValueOnView(fieldName, value, view);
        if ("Mother_Guardian_Date_Birth_Unknown".equalsIgnoreCase(fieldName)) {
            view.setVisibility(View.GONE);
        }
    }

}
