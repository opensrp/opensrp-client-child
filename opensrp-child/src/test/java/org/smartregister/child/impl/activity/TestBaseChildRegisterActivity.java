package org.smartregister.child.impl.activity;

import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class TestBaseChildRegisterActivity extends BaseChildRegisterActivity {

    @Override
    public String getRegistrationForm() {
        return null;
    }

    @Override
    public void startNFCCardScanner() {

    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return null;
    }
}
