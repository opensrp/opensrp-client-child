package org.smartregister.child.task;

import static org.smartregister.login.task.RemoteLoginTask.getOpenSRPContext;

import android.view.View;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.util.ChildJsonFormUtils;
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

import timber.log.Timber;

public class SaveServiceTask implements OnTaskExecutedActions<Triple<ArrayList<ServiceWrapper>, List<ServiceRecord>, List<Alert>>> {

    private View view;
    private BaseChildDetailTabbedActivity activity;
    private CommonPersonObjectClient childDetails;
    private AppExecutorService appExecutors;
    private ServiceWrapper[] serviceWrapper;

    public SaveServiceTask(BaseChildDetailTabbedActivity activity, CommonPersonObjectClient childDetails, ServiceWrapper... serviceWrapper) {
        this.activity = activity;
        this.childDetails = childDetails;
        this.serviceWrapper = serviceWrapper;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    public void onTaskStarted() {
        activity.showProgressDialog();
    }

    @Override
    public void execute() {
        ArrayList<ServiceWrapper> list = new ArrayList<>();
        final List<ServiceRecord>[] serviceRecords = new List[]{null};
        final List<Alert>[] alerts = new List[]{null};

        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            try {
                for (ServiceWrapper tag : this.serviceWrapper) {
                    String providerId = getOpenSRPContext().allSharedPreferences().fetchRegisteredANM();
                    String locationId = ChildJsonFormUtils.getProviderLocationId(getOpenSRPContext().applicationContext());
                    String childLocationId = ChildJsonFormUtils.getChildLocationId(getOpenSRPContext().allSharedPreferences().fetchDefaultLocalityId(providerId), getOpenSRPContext().allSharedPreferences());
                    String team = getOpenSRPContext().allSharedPreferences().fetchDefaultTeam(providerId);
                    String teamId = getOpenSRPContext().allSharedPreferences().fetchDefaultTeamId(providerId);
                    RecurringServiceUtils.saveService(tag, childDetails.entityId(), providerId, locationId, team, teamId, childLocationId);
                    list.add(tag);

                    ServiceSchedule.updateOfflineAlerts(tag.getType(), childDetails.entityId(), Utils.dobToDateTime(childDetails));
                }

                List<ServiceRecord> serviceRecordList = ImmunizationLibrary.getInstance().recurringServiceRecordRepository()
                        .findByEntityId(childDetails.entityId());

                AlertService alertService = getOpenSRPContext().alertService();
                List<Alert> alertList = alertService.findByEntityId(childDetails.entityId());

                serviceRecords[0] = serviceRecordList;
                alerts[0] = alertList;
            } catch (Exception e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> onTaskResult(Triple.of(list, serviceRecords[0], alerts[0])));
        });
    }

    @Override
    public void onTaskResult(Triple<ArrayList<ServiceWrapper>, List<ServiceRecord>, List<Alert>> triple) {
        activity.hideProgressDialog();
        RecurringServiceUtils.updateServiceGroupViews(view, triple.getLeft(), triple.getMiddle(), triple.getRight());
    }
}
