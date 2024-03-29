package org.smartregister.child.impl.activity;

import androidx.fragment.app.Fragment;

import org.mockito.Mockito;
import org.smartregister.Context;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.impl.fragment.TestChildRegisterFragment;
import org.smartregister.child.model.BaseChildRegisterModel;
import org.smartregister.child.presenter.BaseChildRegisterPresenter;
import org.smartregister.view.fragment.BaseRegisterFragment;

public class TestBaseChildRegisterActivity extends BaseChildRegisterActivity {

    @Override
    public String getRegistrationForm() {
        return null;
    }

    @Override
    protected void initializePresenter() {
        presenter = new BaseChildRegisterPresenter(this, new BaseChildRegisterModel());
    }


    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[1];
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new TestChildRegisterFragment();
    }

    @Override
    public Context context() {
        Context context = Mockito.mock(Context.class);

        return context;
    }
}
