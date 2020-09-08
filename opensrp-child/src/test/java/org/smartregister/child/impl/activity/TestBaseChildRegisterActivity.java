package org.smartregister.child.impl.activity;

import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class TestBaseChildRegisterActivity extends BaseChildRegisterActivity {

    @Override
    public String getRegistrationForm() {
        return null;
    }

    @Override
    public void startNFCCardScanner() {
        // Do nothing
    }

    @Override
    protected void initializePresenter() {
        // Do nothing
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return null;
    }
}
