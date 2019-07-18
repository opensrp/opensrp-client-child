package org.smartregister.child.task;

import android.os.AsyncTask;
import android.view.View;

import org.apache.commons.lang3.tuple.Triple;
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

public class SaveServiceTask extends AsyncTask<ServiceWrapper, Void, Triple<ArrayList<ServiceWrapper>, List<ServiceRecord>, List<Alert>>> {
    private View view;
    private BaseChildDetailTabbedActivity activity;
    private CommonPersonObjectClient childDetails;

    public SaveServiceTask(BaseChildDetailTabbedActivity activity, CommonPersonObjectClient childDetails) {
        this.activity = activity;
        this.childDetails = childDetails;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    protected Triple<ArrayList<ServiceWrapper>, List<ServiceRecord>, List<Alert>> doInBackground(
            ServiceWrapper... params) {

        ArrayList<ServiceWrapper> list = new ArrayList<>();

        for (ServiceWrapper tag : params) {
            RecurringServiceUtils.saveService(tag, childDetails.entityId(), null, null);
            list.add(tag);

            ServiceSchedule
                    .updateOfflineAlerts(tag.getType(), childDetails.entityId(), Utils.dobToDateTime(childDetails));
        }

        List<ServiceRecord> serviceRecordList = ImmunizationLibrary.getInstance().recurringServiceRecordRepository()
                .findByEntityId(childDetails.entityId());

        AlertService alertService = getOpenSRPContext().alertService();
        List<Alert> alertList = alertService.findByEntityId(childDetails.entityId());

        return Triple.of(list, serviceRecordList, alertList);

    }

    @Override
    protected void onPreExecute() {
        activity.showProgressDialog();
    }

    @Override
    protected void onPostExecute(Triple<ArrayList<ServiceWrapper>, List<ServiceRecord>, List<Alert>> triple) {
        activity.hideProgressDialog();
        RecurringServiceUtils.updateServiceGroupViews(view, triple.getLeft(), triple.getMiddle(), triple.getRight());
    }
}
