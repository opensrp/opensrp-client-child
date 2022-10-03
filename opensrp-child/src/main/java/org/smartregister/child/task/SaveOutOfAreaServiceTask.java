package org.smartregister.child.task;

import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.OutOfAreaServiceUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.util.AppExecutorService;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 05/03/2019.
 */
public class SaveOutOfAreaServiceTask implements OnTaskExecutedActions<TaskResult> {

    private final String formString;
    private WeightRepository weightRepository;
    private VaccineRepository vaccineRepository;
    private ChildRegisterContract.ProgressDialogCallback progressDialogCallback;
    private AppExecutorService appExecutors;

    public SaveOutOfAreaServiceTask(String formString, ChildRegisterContract.ProgressDialogCallback progressDialogCallback) {
        this.formString = formString;
        this.weightRepository = GrowthMonitoringLibrary.getInstance().weightRepository();
        this.vaccineRepository = ImmunizationLibrary.getInstance().vaccineRepository();
        this.progressDialogCallback = progressDialogCallback;
    }

    @Override
    public void onTaskStarted() {
        // notify on UI
    }

    @Override
    public void execute() {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            try {
                String locationId = ChildJsonFormUtils.getProviderLocationId(ChildLibrary.getInstance().context().applicationContext());
                JSONObject outOfAreaFormJsonObject = new JSONObject(formString);

                // Get metadata from the form
                Map<String, String> metadata = OutOfAreaServiceUtils.getOutOfAreaMetadata(outOfAreaFormJsonObject);

                // Create a weight object if weight was recorded
                Weight weight = OutOfAreaServiceUtils.getRecordedWeight(ChildLibrary.getInstance().context(), outOfAreaFormJsonObject, locationId, metadata);
                if (weight != null) {
                    weightRepository.add(weight);
                }

                // Create a vaccine object for all recorded vaccines
                List<Vaccine> vaccines = OutOfAreaServiceUtils.getRecordedVaccines(ChildLibrary.getInstance().context(), outOfAreaFormJsonObject, locationId, metadata);
                for (Vaccine curVaccine : vaccines) {
                    Utils.addVaccine(vaccineRepository, curVaccine);
                }

                OutOfAreaServiceUtils.createOutOfAreaRecurringServiceEvents(outOfAreaFormJsonObject, metadata);
            } catch (Exception e) {
                if (progressDialogCallback != null) {
                    progressDialogCallback.dissmissProgressDialog();
                }
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> onTaskResult(TaskResult.SUCCESS));
        });
    }

    @Override
    public void onTaskResult(TaskResult result) {
        if (progressDialogCallback != null) {
            progressDialogCallback.dissmissProgressDialog();
        }
    }
}
