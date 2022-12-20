package org.smartregister.child.task;

import android.os.AsyncTask;

import org.smartregister.child.contract.ChildImmunizationContract;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 08/09/2020.
 */
public class ShowGrowthChartTask extends AsyncTask<Void, Void, Map<String, List>> {

    private ChildImmunizationContract.Presenter presenter;
    private CommonPersonObjectClient childDetails;

    public ShowGrowthChartTask(ChildImmunizationContract.Presenter presenter, CommonPersonObjectClient childDetails) {
        this.presenter = presenter;
        this.childDetails = childDetails;
    }

    @Override
    protected Map<String, List> doInBackground(Void... params) {
        Map<String, List> growthMonitoring = new HashMap<>();
        List<Weight> allWeights = presenter.getAllWeights(childDetails);
        List<Height> allHeights = presenter.getAllHeights(childDetails);
        growthMonitoring.put(Constants.HEIGHT, allHeights);
        growthMonitoring.put(Constants.WEIGHT, allWeights);

        return growthMonitoring;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        presenter.getView().showProgressDialog();
    }

    @Override
    protected void onPostExecute(Map<String, List> growthMonitoring) {

        super.onPostExecute(growthMonitoring);

        presenter.getView().hideProgressDialog();

        ((ChildImmunizationContract.View) presenter.getView()).showGrowthDialogFragment(growthMonitoring);
    }
}
