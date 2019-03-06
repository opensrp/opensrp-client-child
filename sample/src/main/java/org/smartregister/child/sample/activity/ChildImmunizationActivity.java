package org.smartregister.child.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.sample.application.SampleApplication;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.location.helper.LocationHelper;

public class ChildImmunizationActivity extends BaseChildImmunizationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getDrawerLayoutId() {
        return 0;
    }

    @Override
    protected int getToolbarId() {
        return LocationSwitcherToolbar.TOOLBAR_ID;
    }

    @Override
    public boolean isLastModified() {
        SampleApplication application = (SampleApplication) getApplication();
        return application.isLastModified();
    }

    @Override
    public void setLastModified(boolean lastModified) {
        SampleApplication application = (SampleApplication) getApplication();
        if (lastModified != application.isLastModified()) {
            application.setLastModified(lastModified);
        }
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
    public RecurringServiceTypeRepository getRecurringServiceTypeRepository() {
        return SampleApplication.getInstance().recurringServiceTypeRepository();
    }

    @Override
    public RecurringServiceRecordRepository getRecurringServiceRecordRepository() {
        return SampleApplication.getInstance().recurringServiceRecordRepository();
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {

    }

    @Override
    public void onNoUniqueId() {

    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {

    }

    public void launchDetailActivity(Context fromContext, CommonPersonObjectClient childDetails, RegisterClickables registerClickables) {

        Intent intent = new Intent(fromContext, ChildDetailTabbedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY.LOCATION_NAME, LocationHelper.getInstance().getOpenMrsLocationId(getCurrentLocation()));
        bundle.putSerializable(Constants.INTENT_KEY.EXTRA_CHILD_DETAILS, childDetails);
        bundle.putSerializable(Constants.INTENT_KEY.EXTRA_REGISTER_CLICKABLES, registerClickables);
        intent.putExtras(bundle);

        fromContext.startActivity(intent);
    }
}
