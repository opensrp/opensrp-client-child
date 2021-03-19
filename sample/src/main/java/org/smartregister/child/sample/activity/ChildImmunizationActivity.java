package org.smartregister.child.sample.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.sample.application.SampleApplication;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.Utils;

import java.util.Map;

public class ChildImmunizationActivity extends BaseChildImmunizationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void goToRegisterPage() {

        Intent intent = new Intent(this, ChildRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected int getToolbarId() {
        return LocationSwitcherToolbar.TOOLBAR_ID;
    }

    @Override
    protected int getDrawerLayoutId() {
        return 0;
    }

    public void launchDetailActivity(Context fromContext, CommonPersonObjectClient childDetails, RegisterClickables registerClickables) {

        Intent intent = new Intent(fromContext, ChildDetailTabbedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.INTENT_KEY.LOCATION_ID, ChildJsonFormUtils.getProviderLocationId(this));
        bundle.putSerializable(Constants.INTENT_KEY.BASE_ENTITY_ID, childDetails.getCaseId());
        bundle.putSerializable(Constants.INTENT_KEY.EXTRA_REGISTER_CLICKABLES, registerClickables);
        intent.putExtras(bundle);

        fromContext.startActivity(intent);
    }

    @Override
    protected Activity getActivity() {
        return this;

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
    public void onClick(View view) {
        Utils.showToast(this, "Floating Action Button clicked...");
    }

    @Override
    public void onUniqueIdFetched(Triple<String, Map<String, String>, String> triple, String entityId) {
        //Do nothing
    }

    @Override
    public void onNoUniqueId() {
        //Do nothing
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        //Do nothing
    }

    @Override
    protected void onResume() {
        super.onResume();
        getServiceGroupCanvasLL().setVisibility(View.VISIBLE);
    }
}
