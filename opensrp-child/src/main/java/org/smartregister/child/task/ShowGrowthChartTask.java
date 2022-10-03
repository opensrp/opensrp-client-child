package org.smartregister.child.task;

import org.smartregister.child.contract.ChildImmunizationContract;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.util.AppExecutorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 08/09/2020.
 */
public class ShowGrowthChartTask implements OnTaskExecutedActions<Map<String, List>> {

    private ChildImmunizationContract.Presenter presenter;
    private CommonPersonObjectClient childDetails;
    private AppExecutorService appExecutors;

    public ShowGrowthChartTask(ChildImmunizationContract.Presenter presenter, CommonPersonObjectClient childDetails) {
        this.presenter = presenter;
        this.childDetails = childDetails;
    }

    @Override
    public void onTaskStarted() {
        presenter.getView().showProgressDialog();
    }

    @Override
    public void execute() {
        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            Map<String, List> growthMonitoringData = getGrowthMonitoringData();

            appExecutors.mainThread().execute(() -> onTaskResult(growthMonitoringData));
        });
    }

    @Override
    public void onTaskResult(Map<String, List> growthMonitoringData) {
        presenter.getView().hideProgressDialog();

        ((ChildImmunizationContract.View) presenter.getView()).showGrowthDialogFragment(growthMonitoringData);
    }

    private Map<String, List> getGrowthMonitoringData() {
        Map<String, List> growthMonitoringData = new HashMap<>();

        try {
            List<Weight> allWeights = presenter.getAllWeights(childDetails);
            List<Height> allHeights = presenter.getAllHeights(childDetails);
            growthMonitoringData.put(Constants.HEIGHT, allHeights);
            growthMonitoringData.put(Constants.WEIGHT, allWeights);
        } catch (Exception e) {
            Timber.e(e);
        }

        return growthMonitoringData;
    }
}
