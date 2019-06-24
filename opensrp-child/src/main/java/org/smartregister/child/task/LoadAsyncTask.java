package org.smartregister.child.task;

/**
 * Created by ndegwamartin on 07/03/2019.
 */

import android.os.AsyncTask;

import org.smartregister.CoreLibrary;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.domain.NamedObject;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.domain.Alert;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ndegwamartin on 07/03/2019.
 */
public class LoadAsyncTask extends AsyncTask<Void, Void, Map<String, NamedObject<?>>> {

    private BaseChildDetailTabbedActivity.STATUS status;
    private boolean fromUpdateStatus = false;
    private RepositoryHolder repositoryHolder;
    private String baseEntityId;

    private LoadAsyncTask(String baseEntityId, RepositoryHolder repositoryHolder) {
        this.status = BaseChildDetailTabbedActivity.STATUS.NONE;
        this.repositoryHolder = repositoryHolder;
        this.baseEntityId = baseEntityId;
    }

    private LoadAsyncTask(String baseEntityId, BaseChildDetailTabbedActivity.STATUS status) {
        this.status = status;
    }

    public void setFromUpdateStatus(boolean fromUpdateStatus, RepositoryHolder repositoryHolder) {
        this.fromUpdateStatus = fromUpdateStatus;
        this.repositoryHolder = repositoryHolder;
    }

    @Override
    protected Map<String, NamedObject<?>> doInBackground(Void... params) {
        Map<String, NamedObject<?>> map = new HashMap<>();

        DetailsRepository detailsRepository = CoreLibrary.getInstance().context().detailsRepository();
        Map<String, String> detailsMap = detailsRepository.getAllDetailsForClient(baseEntityId);

        NamedObject<Map<String, String>> detailsNamedObject = new NamedObject<>(Map.class.getName(), detailsMap);
        map.put(detailsNamedObject.name, detailsNamedObject);

        WeightRepository weightRepository = repositoryHolder.getWeightRepository();
        List<Weight> weightList = weightRepository.findLast5(baseEntityId);

        NamedObject<List<Weight>> weightNamedObject = new NamedObject<>(Weight.class.getName(), weightList);
        map.put(weightNamedObject.name, weightNamedObject);

        HeightRepository heightRepository = repositoryHolder.getHeightRepository();
        List<Height> heightList = heightRepository.findLast5(baseEntityId);

        NamedObject<List<Height>> heightNamedObject = new NamedObject<>(Height.class.getName(), heightList);
        map.put(heightNamedObject.name, heightNamedObject);

        VaccineRepository vaccineRepository = repositoryHolder.getVaccineRepository();
        List<Vaccine> vaccineList = vaccineRepository.findByEntityId(baseEntityId);

        NamedObject<List<Vaccine>> vaccineNamedObject = new NamedObject<>(Vaccine.class.getName(), vaccineList);
        map.put(vaccineNamedObject.name, vaccineNamedObject);

        List<ServiceRecord> serviceRecords = new ArrayList<>();

        RecurringServiceTypeRepository recurringServiceTypeRepository = repositoryHolder.getRecurringServiceTypeRepository();
        RecurringServiceRecordRepository recurringServiceRecordRepository = repositoryHolder
                .getRecurringServiceRecordRepository();

        if (recurringServiceRecordRepository != null) {
            serviceRecords = recurringServiceRecordRepository.findByEntityId(baseEntityId);
        }

        NamedObject<List<ServiceRecord>> serviceNamedObject = new NamedObject<>(ServiceRecord.class.getName(),
                serviceRecords);
        map.put(serviceNamedObject.name, serviceNamedObject);

        Map<String, List<ServiceType>> serviceTypeMap = new LinkedHashMap<>();
        if (recurringServiceTypeRepository != null) {
            List<ServiceType> serviceTypes = recurringServiceTypeRepository.fetchAll();
            for (ServiceType serviceType : serviceTypes) {
                String type = serviceType.getType();
                List<ServiceType> serviceTypeList = serviceTypeMap.get(type);
                if (serviceTypeList == null) {
                    serviceTypeList = new ArrayList<>();
                }
                serviceTypeList.add(serviceType);
                serviceTypeMap.put(type, serviceTypeList);
            }
        }

        NamedObject<Map<String, List<ServiceType>>> serviceTypeNamedObject = new NamedObject<>(ServiceType.class.getName(),
                serviceTypeMap);
        map.put(serviceTypeNamedObject.name, serviceTypeNamedObject);

        List<Alert> alertList = new ArrayList<>();
        AlertService alertService = CoreLibrary.getInstance().context().alertService();
        if (alertService != null) {
            alertList = alertService.findByEntityId(baseEntityId);
        }

        NamedObject<List<Alert>> alertNamedObject = new NamedObject<>(Alert.class.getName(), alertList);
        map.put(alertNamedObject.name, alertNamedObject);

        return map;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //    showProgressDialog(getString(R.string.updating_dialog_title), null);
    }

    @Override
    protected void onPostExecute(Map<String, NamedObject<?>> map) {
/*
        detailsMap = AsyncTaskUtils.extractDetailsMap(map);
        Utils.putAll(detailsMap, childDetails.getColumnmaps());

        List<Weight> weightList = AsyncTaskUtils.extractWeights(map);
        List<Vaccine> vaccineList = AsyncTaskUtils.extractVaccines(map);
        Map<String, List<ServiceType>> serviceTypeMap = AsyncTaskUtils.extractServiceTypes(map);
        List<ServiceRecord> serviceRecords = AsyncTaskUtils.extractServiceRecords(map);
        List<Alert> alertList = AsyncTaskUtils.extractAlerts(map);

        boolean editVaccineMode = BaseChildDetailTabbedActivity.STATUS.EDIT_VACCINE.equals(status);
        boolean editServiceMode = BaseChildDetailTabbedActivity.STATUS.EDIT_SERVICE.equals(status);
        boolean editWeightMode = BaseChildDetailTabbedActivity.STATUS.EDIT_GROWTH.equals(status);

        if (BaseChildDetailTabbedActivity.STATUS.NONE.equals(status)) {
            updateOptionsMenu(vaccineList, serviceRecords, weightList, alertList);
            childDataFragment.loadData(detailsMap);
        }

        childUnderFiveFragment.setDetailsMap(detailsMap);
        childUnderFiveFragment.loadGrowthMonitoringView(weightList, editWeightMode);
        childUnderFiveFragment.updateVaccinationViews(vaccineList, alertList, editVaccineMode);
        childUnderFiveFragment.updateServiceViews(serviceTypeMap, serviceRecords, alertList, editServiceMode);

        if (!fromUpdateStatus) {
            updateStatus(true);
        }

        hideProgressDialog();*/
    }
}
