package org.smartregister.child.sample.fragment;

import android.view.View;

import org.smartregister.child.fragment.BaseAdvancedSearchFragment;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.child.sample.presenter.AdvancedSearchPresenter;
import org.smartregister.view.activity.BaseRegisterActivity;

public class AdvancedSearchFragment extends BaseAdvancedSearchFragment {

    AdvancedSearchPresenter presenter;

    @Override
    protected BaseChildAdvancedSearchPresenter getPresenter() {

        if (presenter == null) {
            String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
            presenter = new AdvancedSearchPresenter(this, viewConfigurationIdentifier);
        }

        return presenter;
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter.getDefaultSortQuery();
    }

    @Override
    public void onClick(View view) {
        view.toString();
    }
}