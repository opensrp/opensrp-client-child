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
        Map<String, List> growthMonitoring = new HashMap<>();

        appExecutors = new AppExecutorService();
        appExecutors.executorService().execute(() -> {
            try {
                List<Weight> allWeights = presenter.getAllWeights(childDetails);
                List<Height> allHeights = presenter.getAllHeights(childDetails);
                growthMonitoring.put(Constants.HEIGHT, allHeights);
                growthMonitoring.put(Constants.WEIGHT, allWeights);
            } catch (Exception e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> onTaskResult(growthMonitoring));
        });
    }

    @Override
    public void onTaskResult(Map<String, List> growthMonitoring) {
        presenter.getView().hideProgressDialog();

        ((ChildImmunizationContract.View) presenter.getView()).showGrowthDialogFragment(growthMonitoring);
    }
}
