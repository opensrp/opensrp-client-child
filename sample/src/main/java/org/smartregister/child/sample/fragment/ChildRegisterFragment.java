package org.smartregister.child.sample.fragment;

import android.view.View;

import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.fragment.BaseChildRegisterFragment;
import org.smartregister.child.model.BaseChildRegisterFragmentModel;
import org.smartregister.child.sample.R;
import org.smartregister.child.sample.activity.ChildImmunizationActivity;
import org.smartregister.child.sample.activity.ChildRegisterActivity;
import org.smartregister.child.sample.presenter.ChildRegisterFragmentPresenter;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
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

    @Override
    protected void onViewClicked(View view) {

        super.onViewClicked(view);

        if (view.getTag() != null && view.getTag(org.smartregister.child.R.id.record_action) != null) {
            goToChildImmunizationActivity((CommonPersonObjectClient) view.getTag(), (Constants.RECORD_ACTION) view.getTag(org.smartregister.child.R.id.record_action));
        }

        //starto
        CommonPersonObjectClient client = null;
        if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
            client = (CommonPersonObjectClient) view.getTag();
        }
        RegisterClickables registerClickables = new RegisterClickables();

        switch (view.getId()) {
            case R.id.child_profile_info_layout:

                ChildImmunizationActivity.launchActivity(getActivity(), client, null);
                break;
            case R.id.record_weight:
                registerClickables.setRecordWeight(true);
                ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                break;

            case R.id.record_vaccination:
                registerClickables.setRecordAll(true);
                ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                break;
            case R.id.filter_selection:
                toggleFilterSelection();
                break;

            case R.id.global_search:
                ((ChildRegisterActivity) getActivity()).startAdvancedSearch();
                break;

            case R.id.scan_qr_code:
                ((ChildRegisterActivity) getActivity()).startQrCodeScanner();
                break;
            default:
                break;
        }

    }


    @Override
    public void onClick(View view) {
        onViewClicked(view);
    }
}
