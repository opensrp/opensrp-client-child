package org.smartregister.child.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import org.smartregister.util.LangUtils;

import java.util.List;

/**
 * Created by ndegwamartin on 01/03/2019.
 */
public class BaseChildFormActivity extends JsonFormActivity {
    ChildFormFragment childFormFragment;
    private String TAG = BaseChildFormActivity.class.getCanonicalName();
    private boolean enableOnCloseDialog = true;
    private JSONObject form;

    @Override
    protected void attachBaseContext(android.content.Context base) {

        String language = LangUtils.getLanguage(base);
        super.attachBaseContext(LangUtils.setAppLocale(base, language));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            form = new JSONObject(currentJsonState());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        enableOnCloseDialog = getIntent().getBooleanExtra(Constants.FormActivity.EnableOnCloseDialog, true);

    }

    @Override
    public void initializeFormFragment() {
        initializeFormFragmentCore();
    }

    protected void initializeFormFragmentCore() {
        childFormFragment = ChildFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction()
                .add(com.vijay.jsonwizard.R.id.container, childFormFragment).commit();
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        if (toolbar != null) {
            toolbar.setContentInsetStartWithNavigation(0);
        }
        super.setSupportActionBar(toolbar);
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId, boolean popup) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
        if (ChildLibrary.getInstance().metadata().formWizardValidateRequiredFieldsBefore) {
            validateActivateNext();
        }
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value,
                           String openMrsEntityParent, String openMrsEntity, String openMrsEntityId, boolean popup)
    throws JSONException {
        super.writeValue(stepName, parentKey, childObjectKey, childKey, value, openMrsEntityParent, openMrsEntity,
                openMrsEntityId, popup);
        if (ChildLibrary.getInstance().metadata().formWizardValidateRequiredFieldsBefore) {
            validateActivateNext();
        }
    }

    @Override
    public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity,
                           String openMrsEntityId) throws JSONException {
        super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId);
        if (ChildLibrary.getInstance().metadata().formWizardValidateRequiredFieldsBefore) {
            validateActivateNext();
        }
    }

    @Override
    public void writeValue(String stepName, String parentKey, String childObjectKey, String childKey, String value,
                           String openMrsEntityParent, String openMrsEntity, String openMrsEntityId) throws JSONException {
        super.writeValue(stepName, parentKey, childObjectKey, childKey, value, openMrsEntityParent, openMrsEntity,
                openMrsEntityId);
        if (ChildLibrary.getInstance().metadata().formWizardValidateRequiredFieldsBefore) {
            validateActivateNext();
        }
    }

    /**
     * Conditionaly display the confirmation dialog
     */
    @Override
    public void onBackPressed() {
        if (enableOnCloseDialog) {
            AlertDialog dialog = new AlertDialog.Builder(this, R.style.AppThemeAlertDialog).setTitle(confirmCloseTitle)
                    .setMessage(confirmCloseMessage).setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BaseChildFormActivity.this.finish();
                        }
                    }).setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "No button on dialog in " + JsonFormActivity.class.getCanonicalName());
                        }
                    }).create();

            dialog.show();

        } else {
            BaseChildFormActivity.this.finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            String et = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
            setConfirmCloseTitle(this.getString(R.string.confirm_form_close));
            setConfirmCloseMessage(
                    et.trim().toLowerCase().contains("update") ? this.getString(R.string.any_changes_you_make) : this
                            .getString(R.string.confirm_form_close_explanation));

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
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

    public boolean checkIfBalanceNegative() {
        boolean balancecheck = true;
        String balancestring = childFormFragment.getRelevantTextViewString(Constants.BALANCE);

        if (balancestring.contains(Constants.NEW_BALANCE) && StringUtils.isNumeric(balancestring)) {
            int balance = Integer.parseInt(balancestring.replace(Constants.NEW_BALANCE_, "").trim());
            if (balance < 0) {
                balancecheck = false;
            }
        }

        return balancecheck;
    }

    public boolean checkIfAtLeastOneServiceGiven() {
        JSONObject object = getStep(Constants.STEP_1);
        try {
            if (object.getString(Constants.TITLE).contains("Record out of catchment area service")) {
                JSONArray fields = object.getJSONArray(Constants.FIELDS);
                for (int i = 0; i < fields.length(); i++) {
                    JSONObject vaccineGroup = fields.getJSONObject(i);
                    if (vaccineGroup.has(Constants.KEY.KEY) && vaccineGroup.has(Constants.IS_VACCINE_GROUP)) {
                        if (vaccineGroup.getBoolean(Constants.IS_VACCINE_GROUP) && vaccineGroup.has(Constants.OPTIONS)) {
                            JSONArray vaccineOptions = vaccineGroup.getJSONArray(Constants.OPTIONS);
                            for (int j = 0; j < vaccineOptions.length(); j++) {
                                JSONObject vaccineOption = vaccineOptions.getJSONObject(j);
                                if (vaccineOption.has(Constants.VALUE) && vaccineOption.getBoolean(Constants.VALUE)) {
                                    return true;
                                }
                            }
                        }
                    } else if (vaccineGroup.has(Constants.KEY.KEY) && vaccineGroup.getString(Constants.KEY.KEY).equals(
                            Constants.WEIGHT_KG) && vaccineGroup
                            .has(Constants.VALUE) && vaccineGroup.getString(Constants.VALUE).length() > 0) {
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

