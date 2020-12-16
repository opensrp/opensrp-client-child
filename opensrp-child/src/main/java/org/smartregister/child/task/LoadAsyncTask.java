package org.smartregister.child.task;

import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;

import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildDetailTabbedActivity;
import org.smartregister.child.domain.NamedObject;
import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.child.fragment.ChildUnderFiveFragment;
import org.smartregister.child.util.AsyncTaskUtils;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.child.util.ChildDbUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.Alert;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.util.AppProperties;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.ServiceRecord;
import org.smartregister.immunization.domain.ServiceType;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.service.AlertService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.login.task.RemoteLoginTask.getOpenSRPContext;

public class LoadAsyncTask extends AsyncTask<Void, Void, Map<String, NamedObject<?>>> {
    private Menu overflow;
    private org.smartregister.child.enums.Status status;
    private boolean fromUpdateStatus = false;
    private boolean monitorGrowth = false;
    private Map<String, String> detailsMap;
    private CommonPersonObjectClient childDetails;
    private BaseChildDetailTabbedActivity activity;
    private BaseChildRegistrationDataFragment childDataFragment;
    private ChildUnderFiveFragment childUnderFiveFragment;

    public LoadAsyncTask(Map<String, String> detailsMap, CommonPersonObjectClient childDetails, BaseChildDetailTabbedActivity activity, BaseChildRegistrationDataFragment childDataFragment, ChildUnderFiveFragment childUnderFiveFragment, Menu overflow) {
        this.status = org.smartregister.child.enums.Status.NONE;
        checkProperties();
        this.detailsMap = detailsMap;
        this.childDetails = childDetails;
        this.activity = activity;
        this.childDataFragment = childDataFragment;
        this.childUnderFiveFragment = childUnderFiveFragment;
        this.overflow = overflow;
    }

    public LoadAsyncTask(org.smartregister.child.enums.Status status, Map<String, String> detailsMap, CommonPersonObjectClient childDetails, BaseChildDetailTabbedActivity activity, BaseChildRegistrationDataFragment childDataFragment, ChildUnderFiveFragment childUnderFiveFragment, Menu overflow) {
        checkProperties();
        this.status = status;
        this.activity = activity;
        this.detailsMap = detailsMap;
        this.childDetails = childDetails;
        this.activity = activity;
        this.childDataFragment = childDataFragment;
        this.childUnderFiveFragment = childUnderFiveFragment;
        this.overflow = overflow;
    }

    private void checkProperties() {
        monitorGrowth = CoreLibrary.getInstance().context().getAppProperties().isTrue(AppProperties.KEY.MONITOR_GROWTH);

    }

    public void setFromUpdateStatus(boolean fromUpdateStatus) {
        this.fromUpdateStatus = fromUpdateStatus;
    }

    @Override
    protected Map<String, NamedObject<?>> doInBackground(Void... params) {
        Map<String, NamedObject<?>> map = new HashMap<>();
        detailsMap = ChildDbUtils.fetchChildDetails(childDetails.entityId());

        NamedObject<Map<String, String>> detailsNamedObject = new NamedObject<>(Map.class.getName(), detailsMap);
        map.put(detailsNamedObject.name, detailsNamedObject);

        List<Weight> weightList =
                GrowthMonitoringLibrary.getInstance().weightRepository().findLast5(childDetails.entityId());

        NamedObject<List<Weight>> weightNamedObject = new NamedObject<>(Weight.class.getName(), weightList);
        map.put(weightNamedObject.name, weightNamedObject);

        if (monitorGrowth) {
            List<Height> heightList =
                    GrowthMonitoringLibrary.getInstance().heightRepository().findLast5(childDetails.entityId());

            NamedObject<List<Height>> heightNamedObject = new NamedObject<>(Height.class.getName(), heightList);
            map.put(heightNamedObject.name, heightNamedObject);
        }

        VaccineRepository vaccineRepository = ImmunizationLibrary.getInstance().vaccineRepository();
        List<Vaccine> vaccineList = vaccineRepository.findByEntityId(childDetails.entityId());

        NamedObject<List<Vaccine>> vaccineNamedObject = new NamedObject<>(Vaccine.class.getName(), vaccineList);
        map.put(vaccineNamedObject.name, vaccineNamedObject);

        RecurringServiceTypeRepository recurringServiceTypeRepository =
                ImmunizationLibrary.getInstance().recurringServiceTypeRepository();
        RecurringServiceRecordRepository recurringServiceRecordRepository =
                ImmunizationLibrary.getInstance().recurringServiceRecordRepository();

        if (Boolean.parseBoolean(ChildLibrary.getInstance().getProperties()
                .getProperty(ChildAppProperties.KEY.FEATURE_RECURRING_SERVICE_ENABLED, "true"))
                &&  recurringServiceRecordRepository != null) {
            List<ServiceRecord> serviceRecords = recurringServiceRecordRepository.findByEntityId(childDetails.entityId());
            NamedObject<List<ServiceRecord>> serviceNamedObject =
                    new NamedObject<>(ServiceRecord.class.getName(), serviceRecords);
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

            NamedObject<Map<String, List<ServiceType>>> serviceTypeNamedObject =
                    new NamedObject<>(ServiceType.class.getName(), serviceTypeMap);
            map.put(serviceTypeNamedObject.name, serviceTypeNamedObject);
        }

        List<Alert> alertList = new ArrayList<>();
        AlertService alertService = getOpenSRPContext().alertService();
        if (alertService != null) {
            alertList = alertService.findByEntityId(childDetails.entityId());
        }

        NamedObject<List<Alert>> alertNamedObject = new NamedObject<>(Alert.class.getName(), alertList);
        map.put(alertNamedObject.name, alertNamedObject);

        return map;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        activity.runOnUiThread(() -> activity.showProgressDialog(activity.getString(R.string.refreshing), null));
    }

    @Override
    protected void onPostExecute(Map<String, NamedObject<?>> map) {

        try {

            MenuItem writeToCard = overflow.findItem(R.id.write_to_card);

            if (writeToCard != null) {
                writeToCard.setEnabled(detailsMap.get(Constants.KEY.NFC_CARD_IDENTIFIER) != null);
            }

            List<Weight> weightList = AsyncTaskUtils.extractWeights(map);
            List<Height> heightList = null;
            if (monitorGrowth) {
                heightList = AsyncTaskUtils.extractHeights(map);
            }
            List<Vaccine> vaccineList = AsyncTaskUtils.extractVaccines(map);
            Map<String, List<ServiceType>> serviceTypeMap = AsyncTaskUtils.extractServiceTypes(map);
            List<ServiceRecord> serviceRecords = AsyncTaskUtils.extractServiceRecords(map);
            List<Alert> alertList = AsyncTaskUtils.extractAlerts(map);

            boolean editVaccineMode = org.smartregister.child.enums.Status.EDIT_VACCINE.equals(status);
            boolean editServiceMode = org.smartregister.child.enums.Status.EDIT_SERVICE.equals(status);
            boolean editWeightMode = org.smartregister.child.enums.Status.EDIT_GROWTH.equals(status);

            if (org.smartregister.child.enums.Status.NONE.equals(status)) {
                BaseChildDetailTabbedActivity.updateOptionsMenu(vaccineList, serviceRecords, weightList, alertList);
            }

            childDataFragment.loadData(detailsMap);

            childUnderFiveFragment.setDetailsMap(detailsMap);
            childUnderFiveFragment.loadGrowthMonitoringView(weightList, heightList, editWeightMode);
            childUnderFiveFragment.updateVaccinationViews(vaccineList, alertList, editVaccineMode);
            childUnderFiveFragment.updateServiceViews(serviceTypeMap, serviceRecords, alertList, editServiceMode);
            childUnderFiveFragment.hideOrShowRecurringServices();

            if (!fromUpdateStatus) {
                activity.updateStatus(true);
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.renderProfileWidget(detailsMap);
                    activity.hideProgressDialog();
                }
            });
        }
    }
}
