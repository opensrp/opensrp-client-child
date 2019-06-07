package org.smartregister.child.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.fragment.BaseChildRegisterFragment;
import org.smartregister.child.listener.ChildBottomNavigationListener;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public abstract class BaseChildRegisterActivity extends BaseRegisterActivity implements ChildRegisterContract.View {
    public static final String TAG = BaseChildRegisterActivity.class.getCanonicalName();


    private boolean isAdvancedSearch = false;
    private String advancedSearchQrText = "";
    private HashMap<String, String> advancedSearchFormData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChildLibrary.getInstance().getLocationPickerView(this);
    }

    @Override
    public void startRegistration() {
        startFormActivity(getRegistrationForm(), null, null);
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            if (mBaseFragment instanceof BaseChildRegisterFragment) {
                String locationId = Utils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
                presenter().startForm(formName, entityId, metaData, locationId);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            displayToast(getString(R.string.error_unable_to_start_form));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        switchToAdvancedSearchFromBarcode();
    }

    /**
     * Forces the Home register activity to open the the Advanced search fragment after the barcode activity is closed (as
     * long as it was opened from the advanced search page)
     */
    private void switchToAdvancedSearchFromBarcode() {
        if (isAdvancedSearch) {
            switchToFragment(ADVANCED_SEARCH_POSITION);
            setSelectedBottomBarMenuItem(org.smartregister.child.R.id.action_search);
            setAdvancedFragmentSearchTerm(advancedSearchQrText);
            setFormData(advancedSearchFormData);
            advancedSearchQrText = "";
            isAdvancedSearch = false;
            advancedSearchFormData = new HashMap<>();
        }
    }


    public void setAdvancedSearch(boolean advancedSearch) {
        isAdvancedSearch = advancedSearch;
    }

    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {
        this.advancedSearchFormData = advancedSearchFormData;
    }

    private void setAdvancedFragmentSearchTerm(String searchTerm) {
        mBaseFragment.setUniqueID(searchTerm);
    }

    private void setFormData(HashMap<String, String> formData) {
        mBaseFragment.setAdvancedSearchFormData(formData);
    }


    public void startAdvancedSearch() {
        try {
            mPager.setCurrentItem(ADVANCED_SEARCH_POSITION, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }


    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, Utils.metadata().childFormActivity);
        intent.putExtra(Constants.INTENT_KEY.JSON, jsonForm.toString());

        Form form = new Form();
        // form.setName(getString(R.string.add_child));
        //form.setActionBarBackground(R.color.child_actionbar);
        //form.setNavigationBackground(R.color.child_navigation);
        //form.setHomeAsUpIndicator(R.mipmap.ic_arrow_forward);

        form.setWizard(false);
        form.setHideSaveLabel(true);
        form.setNextLabel("");

        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);

        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.INTENT_KEY.JSON);
                Log.d("JSONResult", jsonString);

                JSONObject form = new JSONObject(jsonString);
                if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().childRegister.registerEventType)) {
                    UpdateRegisterParams updateRegisterParam = new UpdateRegisterParams();
                    updateRegisterParam.setEditMode(false);
                    updateRegisterParam.setFormTag(JsonFormUtils.formTag(Utils.context().allSharedPreferences()));

                    showProgressDialog(R.string.saving_dialog_title);
                    presenter().saveForm(jsonString, updateRegisterParam);
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

        }
    }

    @Override
    protected void registerBottomNavigation() {

        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().removeItem(R.id.action_clients);
            bottomNavigationView.getMenu().removeItem(R.id.action_register);
            bottomNavigationView.getMenu().removeItem(R.id.action_search);
            bottomNavigationView.getMenu().removeItem(R.id.action_library);

            if (!ChildLibrary.getInstance().getProperties().getPropertyBoolean(Constants.PROPERTY.FEATURE_NFC_CARD_ENABLED)) {
                bottomNavigationView.getMenu().removeItem(R.id.action_scan_card);
            }

            bottomNavigationView.inflateMenu(R.menu.bottom_nav_child_menu);

            bottomNavigationHelper.disableShiftMode(bottomNavigationView);

            ChildBottomNavigationListener childBottomNavigationListener = new ChildBottomNavigationListener(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(childBottomNavigationListener);

        }
    }

    public abstract void startNFCCardScanner();

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(Utils.metadata().childRegister.config);
    }

    @Override
    public ChildRegisterContract.Presenter presenter() {
        return (ChildRegisterContract.Presenter) presenter;
    }

    @Override
    protected Fragment[] getOtherFragments() {

        return null;
    }

    public abstract String getRegistrationForm();


    public void updateAdvancedSearchFilterCount(int count) {
       /* BaseAdvancedSearchFragment advancedSearchFragment = (BaseAdvancedSearchFragment) findFragmentByPosition(ADVANCED_SEARCH_POSITION);
        if (advancedSearchFragment != null) {
            advancedSearchFragment.updateFilterCount(count);
        }*/

        //To remove comment
    }


    @Override
    public void onBackPressed() {
        if (currentPage == 0) {
            super.onBackPressed();
        } else {
            switchToBaseFragment();
            setSelectedBottomBarMenuItem(R.id.action_clients);
        }
    }

    public void filterSelection() {
        if (currentPage != 0) {
            switchToBaseFragment();
            BaseRegisterFragment registerFragment = (BaseRegisterFragment) findFragmentByPosition(0);
            if (registerFragment != null && registerFragment instanceof BaseChildRegisterFragment) {
                ((BaseChildRegisterFragment) registerFragment).triggerFilterSelection();
            }
        }
    }

    //To be overriden
    public void saveForm(String jsonString, UpdateRegisterParams updateRegisterParam) {
        presenter().saveForm(jsonString, updateRegisterParam);
    }
}
