package org.smartregister.child.sample.fragment;

import org.smartregister.child.fragment.BaseChildRegisterFragment;
import org.smartregister.child.model.BaseChildRegisterFragmentModel;
import org.smartregister.child.sample.presenter.ChildRegisterFragmentPresenter;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;

public class ChildRegisterFragment extends BaseChildRegisterFragment {

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new ChildRegisterFragmentPresenter(this, new BaseChildRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    protected String getMainCondition() {
        return presenter().getMainCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter().getDefaultSortQuery();
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {
        //do nothing
    }

}
