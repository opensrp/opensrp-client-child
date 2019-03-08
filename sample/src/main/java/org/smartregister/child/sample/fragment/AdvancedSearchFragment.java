package org.smartregister.child.sample.fragment;

import android.database.Cursor;
import android.view.View;

import org.smartregister.child.fragment.BaseAdvancedSearchFragment;
import org.smartregister.child.sample.contract.AdvancedSearchContract;
import org.smartregister.child.sample.model.AdvancedSearchModel;
import org.smartregister.child.sample.presenter.AdvancedSearchPresenter;
import org.smartregister.view.activity.BaseRegisterActivity;

/**
 * Created by ndegwamartin on 08/03/2019.
 */
public class AdvancedSearchFragment extends BaseAdvancedSearchFragment implements AdvancedSearchContract.View {


    @Override
    protected void initializePresenter() {
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new AdvancedSearchPresenter(this, new AdvancedSearchModel(), viewConfigurationIdentifier);
    }

    @Override
    public void switchViews(boolean showList) {

    }

    @Override
    public void updateSearchCriteria(String searchCriteriaString) {

    }

    @Override
    public String filterAndSortQuery() {
        return null;
    }

    @Override
    public Cursor getRawCustomQueryForAdapter(String query) {
        return null;
    }

    @Override
    public void onClick(View view) {
        onViewClicked(view);
    }
}
