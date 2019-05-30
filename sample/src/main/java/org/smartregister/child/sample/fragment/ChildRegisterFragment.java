package org.smartregister.child.sample.fragment;

import android.view.View;

import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.fragment.BaseChildRegisterFragment;
import org.smartregister.child.sample.R;
import org.smartregister.child.sample.activity.ChildImmunizationActivity;
import org.smartregister.child.sample.activity.ChildRegisterActivity;
import org.smartregister.child.sample.model.ChildRegisterFragmentModel;
import org.smartregister.child.sample.presenter.ChildRegisterFragmentPresenter;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.ArrayList;
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


    @Override
    protected String filterSelectionCondition(boolean urgentOnly) {
        final String AND = " AND ";
        final String OR = " OR ";
        final String IS_NULL_OR = " IS NULL OR ";
        final String TRUE = "'true'";

        String mainCondition = " ( " + Constants.KEY.DOD + " is NULL OR " + Constants.KEY.DOD + " = '' ) " +
                AND + " (" + Constants.CHILD_STATUS.INACTIVE + IS_NULL_OR + Constants.CHILD_STATUS.INACTIVE + " != " + TRUE + " ) " +
                AND + " (" + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + IS_NULL_OR + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " != " + TRUE + " ) " +
                AND + " ( ";
        ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines("child");

        vaccines.remove(VaccineRepo.Vaccine.bcg2);
        vaccines.remove(VaccineRepo.Vaccine.ipv);
        vaccines.remove(VaccineRepo.Vaccine.opv0);
        vaccines.remove(VaccineRepo.Vaccine.opv4);
        vaccines.remove(VaccineRepo.Vaccine.measles1);
        vaccines.remove(VaccineRepo.Vaccine.mr1);
        vaccines.remove(VaccineRepo.Vaccine.measles2);
        vaccines.remove(VaccineRepo.Vaccine.mr2);

        final String URGENT = "'" + AlertStatus.urgent.value() + "'";
        final String NORMAL = "'" + AlertStatus.normal.value() + "'";
        final String COMPLETE = "'" + AlertStatus.complete.value() + "'";


        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + URGENT + " ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + URGENT + OR;
            }
        }

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " != " + COMPLETE + " ) ";

        if (urgentOnly) {
            return mainCondition + " ) ";
        }

        mainCondition += OR;
        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + NORMAL + " ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + NORMAL + OR;
            }
        }

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " != " + COMPLETE + " ) ";

        return mainCondition + " ) ";
    }

}
