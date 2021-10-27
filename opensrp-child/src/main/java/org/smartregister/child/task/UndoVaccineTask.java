package org.smartregister.child.task;

import android.os.AsyncTask;
import android.view.View;

import org.joda.time.DateTime;
import org.smartregister.child.R;
import org.smartregister.child.contract.ChildImmunizationContract;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.VaccineWrapper;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndegwamartin on 08/09/2020.
 */
public class UndoVaccineTask extends AsyncTask<Void, Void, Void> {

    private final VaccineWrapper tag;
    private final VaccineRepository vaccineRepository;
    private final AlertService alertService;
    private List<Vaccine> vaccineList;
    private List<Alert> alertList;
    private List<String> affectedVaccines;
    private CommonPersonObjectClient childDetails;
    private ChildImmunizationContract.Presenter presenter;

    public UndoVaccineTask(ChildImmunizationContract.Presenter presenter, VaccineWrapper tag, CommonPersonObjectClient childDetails, AlertService alertService) {
        this.tag = tag;
        this.vaccineRepository = ImmunizationLibrary.getInstance().vaccineRepository();
        this.childDetails = childDetails;
        this.alertService = alertService;
        this.presenter = presenter;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (tag != null && tag.getDbKey() != null) {
            Long dbKey = tag.getDbKey();
            vaccineRepository.deleteVaccine(dbKey);


            String dobString = Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.DOB, false);
            DateTime dateTime = Utils.dobStringToDateTime(dobString);
            if (dateTime != null) {
                affectedVaccines = VaccineSchedule.updateOfflineAlertsAndReturnAffectedVaccineNames(childDetails.entityId(), dateTime, Constants.KEY.CHILD);
                vaccineList = vaccineRepository.findByEntityId(childDetails.entityId());
                alertList = alertService.findByEntityId(childDetails.entityId());
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        presenter.getView().showProgressDialog(((ChildImmunizationContract.View) presenter.getView()).getString(R.string.updating_dialog_title), null);
    }

    @Override
    protected void onPostExecute(Void params) {
        presenter.getView().hideProgressDialog();
        super.onPostExecute(params);

        // Refresh the vaccine group with the updated vaccine
        tag.setUpdatedVaccineDate(null, false);
        tag.setDbKey(null);

        View view = ((ChildImmunizationContract.View) presenter.getView()).getLastOpenedView();

        List<VaccineWrapper> wrappers = new ArrayList<>();
        wrappers.add(tag);

        ((ChildImmunizationContract.View) presenter.getView()).updateVaccineGroupViews(view, wrappers, vaccineList, true);
        ((ChildImmunizationContract.View) presenter.getView()).updateVaccineGroupsUsingAlerts(affectedVaccines, vaccineList, alertList);
        ((ChildImmunizationContract.View) presenter.getView()).showVaccineNotifications(vaccineList, alertList);
    }
}
