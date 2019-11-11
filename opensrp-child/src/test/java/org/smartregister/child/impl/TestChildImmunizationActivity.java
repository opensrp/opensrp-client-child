package org.smartregister.child.impl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;

import org.apache.commons.lang3.tuple.Triple;
import org.mockito.Mockito;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.commonregistry.CommonPersonObjectClient;

public class TestChildImmunizationActivity extends BaseChildImmunizationActivity {
    @Override
    public Resources getResources() {
        return Mockito.mock(Resources.class);
    }

    @Override
    protected void goToRegisterPage() {

    }

    @Override
    protected int getDrawerLayoutId() {
        return 0;
    }

    @Override
    public void launchDetailActivity(Context fromContext, CommonPersonObjectClient childDetails, RegisterClickables registerClickables) {

    }

    @Override
    protected Activity getActivity() {
        return null;
    }

    @Override
    public boolean isLastModified() {
        return false;
    }

    @Override
    public void setLastModified(boolean lastModified) {

    }

    @Override
    public void onClick(View view) {

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
