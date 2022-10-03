package org.smartregister.child.task;

import static org.smartregister.login.task.RemoteLoginTask.getOpenSRPContext;

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
import org.smartregister.util.AppExecutorService;

import java.util.ArrayList;
import java.util.List;

public class UndoServiceTask implements OnTaskExecutedActions<TaskResult> {

    private final View view;
    private final ServiceWrapper serviceWrapper;
    private List<ServiceRecord> serviceRecordList;
    private ArrayList<ServiceWrapper> wrappers;
    private List<Alert> alertList;
    private BaseChildDetailTabbedActivity activity;
    private CommonPersonObjectClient childDetails;
    private AppExecutorService appExecutors;

    public UndoServiceTask(ServiceWrapper serviceWrapper, View view, BaseChildDetailTabbedActivity activity, CommonPersonObjectClient childDetails) {
        this.serviceWrapper = serviceWrapper;
        this.view = view;
        this.activity = activity;
        this.childDetails = childDetails;
    }

    @Override
    public void onTaskStarted() {
        activity.showProgressDialog(activity.getString(R.string.updating_dialog_title), null);
    }

    @Override
    public void execute() {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
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

            appExecutors.mainThread().execute(() -> onTaskResult(TaskResult.SUCCESS));
        });
    }

    @Override
    public void onTaskResult(TaskResult result) {
        activity.hideProgressDialog();
        serviceWrapper.setUpdatedVaccineDate(null, false);
        serviceWrapper.setDbKey(null);

        RecurringServiceUtils.updateServiceGroupViews(view, wrappers, serviceRecordList, alertList, true);
    }
}
