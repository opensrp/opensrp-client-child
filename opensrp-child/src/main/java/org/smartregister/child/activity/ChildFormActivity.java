package org.smartregister.child.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.fragment.ChildFormFragment;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;

import java.util.List;

/**
 * Created by ndegwamartin on 01/03/2019.
 */
public class ChildFormActivity extends JsonFormActivity {
    private String TAG = ChildFormActivity.class.getCanonicalName();

    private boolean enableOnCloseDialog = true;
    ChildFormFragment childFormFragment;

    @Override
    protected void attachBaseContext(android.content.Context base) {

        String language = Utils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(Utils.setAppLocale(base, language));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableOnCloseDialog = getIntent().getBooleanExtra(Constants.FormActivity.EnableOnCloseDialog, true);

        try {
            JSONObject form = new JSONObject(currentJsonState());
            String et = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
            if (et.trim().toLowerCase().contains("update")) {
                setConfirmCloseMessage(getString(R.string.any_changes_you_make));
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.setContentInsetStartWithNavigation(0);
        }
        super.setSupportActionBar(toolbar);
    }

    protected void initializeFormFragmentCore() {
        childFormFragment = ChildFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, childFormFragment).commit();
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId, boolean popup) throws JSONException {
        super.writeValue(stepName, parentKey, childObjectKey, childKey, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
        if (ChildLibrary.getInstance().metadata().formWizardValidateRequiredFieldsBefore) {
            validateActivateNext();
        }
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId, boolean popup) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
        if (ChildLibrary.getInstance().metadata().formWizardValidateRequiredFieldsBefore) {
            validateActivateNext();
        }
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId);
        if (ChildLibrary.getInstance().metadata().formWizardValidateRequiredFieldsBefore) {
            validateActivateNext();
        }
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value, String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        super.writeValue(stepName, parentKey, childObjectKey, childKey, value, openMrsEntityParent, openMrsEntity, openMrsEntityId);
        if (ChildLibrary.getInstance().metadata().formWizardValidateRequiredFieldsBefore) {
            validateActivateNext();
        }
    }

    public void validateActivateNext() {
        Fragment fragment = getVisibleFragment();
        if (fragment != null && fragment instanceof ChildFormFragment) {
            ((ChildFormFragment) fragment).validateActivateNext();
        }
    }

    public Fragment getVisibleFragment() {
        List<Fragment> fragments = this.getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    /**
     * Conditionaly display the confirmation dialog
     */
    @Override
    public void onBackPressed() {
        if (enableOnCloseDialog) {
            super.onBackPressed();
        } else {
            ChildFormActivity.this.finish();
        }
    }

    public boolean checkIfBalanceNegative() {
        boolean balancecheck = true;
        String balancestring = childFormFragment.getRelevantTextViewString("Balance");

        if (balancestring.contains("New balance") && StringUtils.isNumeric(balancestring)) {
            int balance = Integer.parseInt(balancestring.replace("New balance:", "").trim());
            if (balance < 0) {
                balancecheck = false;
            }
        }

        return balancecheck;
    }

    public boolean checkIfAtLeastOneServiceGiven() {
        JSONObject object = getStep("step1");
        try {
            if (object.getString("title").contains("Record out of catchment area service")) {
                JSONArray fields = object.getJSONArray("fields");
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject vaccineGroup = fields.getJSONObject(i);
                    if (vaccineGroup.has("key") && vaccineGroup.has("is_vaccine_group")) {
                        if (vaccineGroup.getBoolean("is_vaccine_group") && vaccineGroup.has("options")) {
                            JSONArray vaccineOptions = vaccineGroup.getJSONArray("options");
                            for (int j = 0; j < vaccineOptions.length(); j++) {
                                JSONObject vaccineOption = vaccineOptions.getJSONObject(j);
                                if (vaccineOption.has("value") && vaccineOption.getBoolean("value")) {
                                    return true;
                                }
                            }
                        }
                    } else if (vaccineGroup.has("key") && vaccineGroup.getString("key").equals("Weight_Kg") && vaccineGroup.has("value") && vaccineGroup.getString("value").length() > 0) {
                        return true;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
}

