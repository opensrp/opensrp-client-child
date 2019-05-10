package org.smartregister.child.sample.activity;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.child.presenter.BaseChildRegisterPresenter;
import org.smartregister.child.sample.R;
import org.smartregister.child.sample.fragment.AdvancedSearchFragment;
import org.smartregister.child.sample.fragment.ChildRegisterFragment;
import org.smartregister.child.sample.util.SampleConstants;
import org.smartregister.child.util.Utils;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class ChildRegisterActivity extends BaseChildRegisterActivity {

    @Override
    protected void attachBaseContext(android.content.Context base) {
        // get language from prefs
        String lang = org.smartregister.child.sample.util.Utils.getLanguage(base.getApplicationContext());
        super.attachBaseContext(org.smartregister.child.sample.util.Utils.setAppLocale(base, lang));
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

        MenuItem clients = bottomNavigationView.getMenu().findItem(org.smartregister.child.R.id.action_clients);
        if (clients != null) {
            clients.setTitle(getString(org.smartregister.child.R.string.header_children));
        }
        bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_library);
        bottomNavigationView.getMenu().removeItem(R.id.action_scan_qr);
    }

    @Override
    public void startNFCCardScanner() {

        Utils.showToast(this, this.getResources().getString(org.smartregister.child.R.string.scan_nfc_card));
    }
}
