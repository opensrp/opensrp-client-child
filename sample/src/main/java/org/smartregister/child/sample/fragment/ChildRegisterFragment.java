package org.smartregister.child.sample.fragment;

import android.view.View;

import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.fragment.BaseChildRegisterFragment;
import org.smartregister.child.sample.R;
import org.smartregister.child.sample.activity.ChildImmunizationActivity;
import org.smartregister.child.sample.activity.ChildRegisterActivity;
import org.smartregister.child.sample.model.ChildRegisterFragmentModel;
import org.smartregister.child.sample.presenter.ChildRegisterFragmentPresenter;
import org.smartregister.child.sample.util.DBQueryHelper;
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
        presenter = new ChildRegisterFragmentPresenter(this, new ChildRegisterFragmentModel(), viewConfigurationIdentifier);
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
    protected String filterSelectionCondition(boolean urgentOnly) {
        return DBQueryHelper.getFilterSelectionCondition(urgentOnly);
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {
        //do nothing
    }

    @Override
    protected void onViewClicked(View view) {

        super.onViewClicked(view);

        RegisterClickables registerClickables = new RegisterClickables();

        if (view.getTag(org.smartregister.child.R.id.record_action) != null) {

            registerClickables.setRecordWeight(Constants.RECORD_ACTION.WEIGHT.equals(view.getTag(org.smartregister.child.R.id.record_action)));
            registerClickables.setRecordAll(Constants.RECORD_ACTION.VACCINATION.equals(view.getTag(org.smartregister.child.R.id.record_action)));
            registerClickables.setNextAppointmentDate(view.getTag(R.id.next_appointment_date) != null ? String.valueOf(view.getTag(R.id.next_appointment_date)) : "");

        }

        CommonPersonObjectClient client = null;
        if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
            client = (CommonPersonObjectClient) view.getTag();
        }

        switch (view.getId()) {
            case R.id.child_profile_info_layout:

                ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
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

            case R.id.back_button:
                getActivity();
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
