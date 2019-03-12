package org.smartregister.child.sample.fragment;

import android.database.Cursor;
import android.view.View;

import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.fragment.BaseAdvancedSearchFragment;
import org.smartregister.child.sample.R;
import org.smartregister.child.sample.activity.ChildImmunizationActivity;
import org.smartregister.child.sample.contract.AdvancedSearchContract;
import org.smartregister.child.sample.model.AdvancedSearchModel;
import org.smartregister.child.sample.presenter.AdvancedSearchPresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.List;

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

        CommonPersonObjectClient client = null;
        if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
            client = (CommonPersonObjectClient) view.getTag();
        }
        RegisterClickables registerClickables = new RegisterClickables();
        switch (view.getId()) {
            case R.id.back_btn_layout:
            case R.id.back_button:
                goBack();
                break;
            case R.id.filter_selection:
                ((BaseChildRegisterActivity) getActivity()).filterSelection();
                break;
            case R.id.search_layout:
            case R.id.search:
                search(view);
                break;
            case R.id.child_profile_info_layout:
                ChildImmunizationActivity.launchActivity(getActivity(), client, null);
                break;
            case R.id.record_weight:
                if (client == null && view.getTag() != null && view.getTag() instanceof String) {
                    String zeirId = view.getTag().toString();
                    ((BaseChildRegisterActivity) getActivity()).startFormActivity("out_of_catchment_service", zeirId, null);
                } else {
                    registerClickables.setRecordWeight(true);
                    ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                }
                break;

            case R.id.record_vaccination:
                if (client != null) {
                    registerClickables.setRecordAll(true);
                    ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                }
                break;
            case R.id.move_to_catchment:
                if (client == null && view.getTag() != null && view.getTag() instanceof List) {
                    @SuppressWarnings("unchecked") List<String> ids = (List<String>) view.getTag();
                    moveToMyCatchmentArea(ids);
                }
                break;
        }
    }


}
