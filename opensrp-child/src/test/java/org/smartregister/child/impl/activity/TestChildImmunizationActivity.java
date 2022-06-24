package org.smartregister.child.impl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.Map;

public class TestChildImmunizationActivity extends BaseChildImmunizationActivity {

    @Override
    protected void goToRegisterPage() {
        //Do nothing
    }

    @Override
    protected int getDrawerLayoutId() {
        return 0;
    }

    @Override
    public void launchDetailActivity(Context fromContext, CommonPersonObjectClient childDetails, RegisterClickables registerClickables) {
        //Do nothing
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
        //Do nothing
    }

    @Override
    public void onClick(View view) {
        //Do nothing
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
    public void onCreate(Bundle state) {

    }
    @Override
    public void startActivity(Intent intent) {

    }
    @Override
    public void startActivity(Intent intent, Bundle o) {

    }
}
