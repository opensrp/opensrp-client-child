package org.smartregister.child.sample.activity;


import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.sample.fragment.SampleChildFormFragment;

public class ChildFormActivity extends BaseChildFormActivity {
    @Override
    protected void initializeFormFragmentCore() {
        SampleChildFormFragment childFormFragment = SampleChildFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        getSupportFragmentManager().beginTransaction().add(com.vijay.jsonwizard.R.id.container, childFormFragment).commit();
    }
}
