package org.smartregister.child.task;

import android.os.AsyncTask;
import android.view.View;

import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceSchedule;
import org.smartregister.immunization.domain.ServiceWrapper;
import org.smartregister.immunization.util.RecurringServiceUtils;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.List;

import static org.smartregister.login.task.RemoteLoginTask.getOpenSRPContext;

public class UndoServiceTask extends AsyncTask<Void, Void, Void> {
    private final View view;
    private final ServiceWrapper serviceWrapper;
    private List<ServiceRecord> serviceRecordList;
    private ArrayList<ServiceWrapper> wrappers;
    private List<Alert> alertList;
    private BaseChildDetailTabbedActivity activity;
    private CommonPersonObjectClient childDetails;

    public UndoServiceTask(ServiceWrapper serviceWrapper, View view, BaseChildDetailTabbedActivity activity, CommonPersonObjectClient childDetails) {
        this.serviceWrapper = serviceWrapper;
        this.view = view;
        this.activity = activity;
        this.childDetails = childDetails;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (serviceWrapper != null && serviceWrapper.getDbKey() != null) {
            Long dbKey = serviceWrapper.getDbKey();
            ImmunizationLibrary.getInstance().recurringServiceRecordRepository().deleteServiceRecord(dbKey);

            serviceRecordList = ImmunizationLibrary.getInstance().recurringServiceRecordRepository()
                    .findByEntityId(childDetails.entityId());

            wrappers = new ArrayList<>();
            wrappers.add(serviceWrapper);

            ServiceSchedule.updateOfflineAlerts(serviceWrapper.getType(), childDetails.entityId(), Utils.dobToDateTime(childDetails));

            AlertService alertService = getOpenSRPContext().alertService();
            alertList = alertService.findByEntityId(childDetails.entityId());
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        activity.showProgressDialog(activity.getString(R.string.updating_dialog_title), null);
    }

    @Override
    protected void onPostExecute(Void params) {
        super.onPostExecute(params);
        activity.hideProgressDialog();
        serviceWrapper.setUpdatedVaccineDate(null, false);
        serviceWrapper.setDbKey(null);

        RecurringServiceUtils.updateServiceGroupViews(view, wrappers, serviceRecordList, alertList, true);
    }
}
