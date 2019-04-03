package org.smartregister.child.sample.activity;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;

import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.child.presenter.BaseChildRegisterPresenter;
import org.smartregister.child.sample.application.SampleApplication;
import org.smartregister.child.sample.fragment.AdvancedSearchFragment;
import org.smartregister.child.sample.fragment.ChildRegisterFragment;
import org.smartregister.child.sample.util.SampleConstants;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;

public class ChildRegisterActivity extends BaseChildRegisterActivity {

    private boolean isAdvancedSearch = false;
    private String advancedSearchQrText = "";
    private HashMap<String, String> advancedSearchFormData = new HashMap<>();


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
            setSelectedBottomBarMenuItem(R.id.action_search);
            setAdvancedFragmentSearchTerm(advancedSearchQrText);
            setFormData(advancedSearchFormData);
            advancedSearchQrText = "";
            isAdvancedSearch = false;
            advancedSearchFormData = new HashMap<>();
        }
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseChildRegisterPresenter(this, new BaseChildRegisterModel());
    }

    @Override
    protected Fragment[] getOtherFragments() {
        ADVANCED_SEARCH_POSITION = 1;

        Fragment[] fragments = new Fragment[1];
        fragments[ADVANCED_SEARCH_POSITION - 1] = new AdvancedSearchFragment();

        return fragments;
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ChildRegisterFragment();
    }

    @Override
    public String getRegistrationForm() {
        return SampleConstants.JSON_FORM.CHILD_ENROLLMENT;
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        MenuItem clients = bottomNavigationView.getMenu().findItem(R.id.action_clients);
        if (clients != null) {
            clients.setTitle(getString(R.string.header_children));
        }

        //bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_library);
    }

    public void startAdvancedSearch() {
        try {
            mPager.setCurrentItem(ADVANCED_SEARCH_POSITION, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
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

    @Override
    public WeightRepository getWeightRepository() {
        return SampleApplication.getInstance().weightRepository();
    }

    @Override
    public VaccineRepository getVaccineRepository() {
        return SampleApplication.getInstance().vaccineRepository();
    }

}
