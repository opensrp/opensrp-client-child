package org.smartregister.child.sample.activity;

import android.os.Bundle;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.sample.application.SampleApplication;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;

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
}
