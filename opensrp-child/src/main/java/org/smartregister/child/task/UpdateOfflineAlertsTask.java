package org.smartregister.child.task;

import org.joda.time.DateTime;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.util.AppExecutorService;

public class UpdateOfflineAlertsTask implements OnTaskExecutedActions<TaskResult> {

    private static final String CHILD = "child";
    private CommonPersonObjectClient childDetails;
    private AppExecutorService appExecutors;

    public UpdateOfflineAlertsTask(CommonPersonObjectClient childDetails) {
        this.childDetails = childDetails;
    }

    @Override
    public void onTaskStarted() {
        // notify on UI
    }

    @Override
    public void execute() {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            DateTime birthDateTime = Utils.dobToDateTime(childDetails);
            if (birthDateTime != null) {
                VaccineSchedule.updateOfflineAlertsOnly(childDetails.entityId(), birthDateTime, CHILD);
            }

            appExecutors.mainThread().execute(() -> onTaskResult(TaskResult.SUCCESS));
        });
    }

    @Override
    public void onTaskResult(TaskResult result) {
        // notify on UI
    }
}
