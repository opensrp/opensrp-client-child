package org.smartregister.child.sample.activity;

import android.support.v4.app.Fragment;
import android.view.MenuItem;

import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.child.presenter.BaseChildRegisterPresenter;
import org.smartregister.child.sample.application.SampleApplication;
import org.smartregister.child.sample.fragment.ChildRegisterFragment;
import org.smartregister.child.sample.util.SampleConstants;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class ChildRegisterActivity extends BaseChildRegisterActivity {

    @Override
    protected void initializePresenter() {
        presenter = new BaseChildRegisterPresenter(this, new BaseChildRegisterModel());
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new ChildRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[0];
    }

    @Override
    public String getRegistrationForm() {
        return SampleConstants.JSON_FORM.CHILD_ENROLLMENT;
    }

    @Override
    protected void registerBottomNavigation() {
        super.registerBottomNavigation();

        MenuItem clients = bottomNavigationView.getMenu().findItem(org.smartregister.R.id.action_clients);
        if (clients != null) {
            clients.setTitle(getString(R.string.header_children));
        }

        bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_library);
    }

    @Override
    public WeightRepository getWeightRepository() {
        return SampleApplication.getInstance().weightRepository();
    }

    @Override
    public VaccineRepository getVaccineRepository() {
        return SampleApplication.getInstance().vaccineRepository();
    }

    @Override
    public AlertService getAlertService() {
        return SampleApplication.getInstance().context().alertService();
    }

}
