package org.smartregister.child.sample.application;

import android.content.Intent;
import android.util.Log;

import com.evernote.android.job.JobManager;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.sample.BuildConfig;
import org.smartregister.child.sample.activity.ChildImmunizationActivity;
import org.smartregister.child.sample.activity.ChildProfileActivity;
import org.smartregister.child.sample.configuration.SampleSyncConfiguration;
import org.smartregister.child.sample.job.SampleJobCreator;
import org.smartregister.child.sample.repository.SampleRepository;
import org.smartregister.child.sample.util.DBConstants;
import org.smartregister.child.sample.util.SampleConstants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.HeightZScoreRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.repository.WeightZScoreRepository;
import org.smartregister.growthmonitoring.service.intent.ZScoreRefreshIntentService;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.VaccineSchedule;
import org.smartregister.immunization.domain.jsonmapping.Vaccine;
import org.smartregister.immunization.domain.jsonmapping.VaccineGroup;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.immunization.util.VaccinatorUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SampleApplication extends DrishtiApplication {
    private static final String TAG = SampleApplication.class.getCanonicalName();
    private static CommonFtsObject commonFtsObject;
    private boolean lastModified;

    public static CommonFtsObject createCommonFtsObject(android.content.Context context) {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable, context));
            }
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{SampleConstants.TABLE_NAME.CHILD};
    }

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(SampleConstants.TABLE_NAME.CHILD)) {
            return new String[]{DBConstants.KEY.ZEIR_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME, DBConstants.KEY.MOTHER_FIRST_NAME, DBConstants.KEY.MOTHER_LAST_NAME, DBConstants.KEY.EPI_CARD_NUMBER, DBConstants.KEY.LOST_TO_FOLLOW_UP, DBConstants.KEY.INACTIVE};
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName, android.content.Context context) {
        if (tableName.equals(SampleConstants.TABLE_NAME.CHILD)) {

            List<VaccineGroup> vaccineList = VaccinatorUtils.getVaccineGroupsFromVaccineConfigFile(context, VaccinatorUtils.vaccines_file);

            List<String> names = new ArrayList<>();
            names.add(DBConstants.KEY.FIRST_NAME);
            names.add(DBConstants.KEY.DOB);
            names.add(DBConstants.KEY.ZEIR_ID);
            names.add(DBConstants.KEY.LAST_INTERACTED_WITH);
            names.add(DBConstants.KEY.INACTIVE);
            names.add(DBConstants.KEY.LOST_TO_FOLLOW_UP);
            names.add(DBConstants.KEY.DOD);
            names.add(DBConstants.KEY.DATE_REMOVED);

            for (VaccineGroup vaccineGroup : vaccineList) {
                populateAlertColumnNames(vaccineGroup.vaccines, names);
            }

            return names.toArray(new String[names.size()]);
        }
        return null;
    }

    private static void populateAlertColumnNames(List<Vaccine> vaccines, List<String> names) {

        for (Vaccine vaccine : vaccines)
            if (vaccine.getVaccineSeparator() != null && vaccine.getName().contains(vaccine.getVaccineSeparator().trim())) {
                String[] individualVaccines = vaccine.getName().split(vaccine.getVaccineSeparator().trim());

                List<Vaccine> vaccineList = new ArrayList<>();
                for (String individualVaccine : individualVaccines) {
                    Vaccine vaccineClone = new Vaccine();
                    vaccineClone.setName(individualVaccine.trim());
                    vaccineList.add(vaccineClone);

                }
                populateAlertColumnNames(vaccineList, names);


            } else {

                names.add("alerts." + VaccinateActionUtils.addHyphen(vaccine.getName()));
            }
    }

    public static synchronized SampleApplication getInstance() {
        return (SampleApplication) mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject(getApplicationContext()));

        //Initialize Modules
        CoreLibrary.init(context, new SampleSyncConfiguration());
        GrowthMonitoringLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION, null);
        ImmunizationLibrary.init(context, getRepository(), createCommonFtsObject(context.applicationContext()), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ConfigurableViewsLibrary.init(context);
        ChildLibrary.init(context, getRepository(), getMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        initRepositories();

        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(SampleRepository.PASSWORD);
        context.session().setPassword(SampleRepository.PASSWORD);

        SyncStatusBroadcastReceiver.init(this);
        LocationHelper.init(Utils.ALLOWED_LEVELS, Utils.DEFAULT_LOCATION_LEVEL);

        //init Job Manager
        JobManager.create(this).addJobCreator(new SampleJobCreator());
        sampleUniqueIds();
        initOfflineSchedules();
        startZscoreRefreshService();
        initializeTestLocationData();
    }

    @Override
    public void logoutCurrentUser() {
    }

    @Override
    public Repository getRepository() {
        try {
            if (repository == null) {
                repository = new SampleRepository(getInstance().getApplicationContext(), context);
            }
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return repository;
    }

    private ChildMetadata getMetadata() {
        ChildMetadata metadata = new ChildMetadata(BaseChildFormActivity.class, ChildProfileActivity.class,
                ChildImmunizationActivity.class, true);
        metadata.updateChildRegister(SampleConstants.JSON_FORM.CHILD_ENROLLMENT, SampleConstants.TABLE_NAME.CHILD,
                SampleConstants.TABLE_NAME.MOTHER_TABLE_NAME, SampleConstants.EventType.CHILD_REGISTRATION,
                SampleConstants.EventType.UPDATE_CHILD_REGISTRATION, SampleConstants.EventType.OUT_OF_CATCHMENT_SERVICE, SampleConstants.CONFIGURATION.CHILD_REGISTER,
                SampleConstants.RELATIONSHIP.MOTHER, SampleConstants.JSON_FORM.OUT_OF_CATCHMENT_SERVICE);
        return metadata;
    }

    private void initRepositories() {
        weightRepository();
        heightRepository();
        vaccineRepository();
        weightZScoreRepository();
        heightZScoreRepository();
    }

    private void sampleUniqueIds() {
        List<String> ids = generateIds(20);
        ChildLibrary.getInstance().getUniqueIdRepository().bulkInsertOpenmrsIds(ids);
    }

    private void initOfflineSchedules() {
        try {
            List<VaccineGroup> childVaccines = VaccinatorUtils.getSupportedVaccines(this);
            List<Vaccine> specialVaccines = VaccinatorUtils.getSpecialVaccines(this);
            VaccineSchedule.init(childVaccines, specialVaccines, "child");
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public void startZscoreRefreshService() {
        Intent intent = new Intent(this.getApplicationContext(), ZScoreRefreshIntentService.class);
        this.getApplicationContext().startService(intent);
    }

    public WeightRepository weightRepository() {
        return GrowthMonitoringLibrary.getInstance().weightRepository();
    }

    public HeightRepository heightRepository() {
        return GrowthMonitoringLibrary.getInstance().heightRepository();
    }

    public VaccineRepository vaccineRepository() {
        return ImmunizationLibrary.getInstance().vaccineRepository();
    }

    public HeightZScoreRepository weightZScoreRepository() {
        return GrowthMonitoringLibrary.getInstance().heightZScoreRepository();
    }

    public WeightZScoreRepository heightZScoreRepository() {
        return GrowthMonitoringLibrary.getInstance().weightZScoreRepository();
    }

    private List<String> generateIds(int size) {
        List<String> ids = new ArrayList<>();
        Random r = new Random();

        for (int i = 10; i < size; i++) {
            Integer randomInt = r.nextInt(10000) + 1;
            ids.add(formatSampleId(randomInt.toString()));
        }

        return ids;
    }

    private String formatSampleId(String openmrsId) {
        int lastIndex = openmrsId.length() - 1;
        String tail = openmrsId.substring(lastIndex);
        return openmrsId.substring(0, lastIndex) + "-" + tail;
    }

    public Context context() {
        return context;
    }

    public RecurringServiceTypeRepository recurringServiceTypeRepository() {
        return ImmunizationLibrary.getInstance().recurringServiceTypeRepository();
    }

    public RecurringServiceRecordRepository recurringServiceRecordRepository() {
        return ImmunizationLibrary.getInstance().recurringServiceRecordRepository();
    }

    public boolean isLastModified() {
        return lastModified;
    }

    public void setLastModified(boolean lastModified) {
        this.lastModified = lastModified;
    }

    public void initializeTestLocationData() {
        // this function is for test purposes only
        String loc = "{\"locationsHierarchy\":{\"map\":{\"5b854508-42c5-4cc5-9bea-77335687a428\":{\"children\":{\"425b0ac3-05e7-4123-ad27-76f510d96a6a\":{\"children\":{\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\":{\"children\":{\"6ca7788c-d995-4431-a8a3-2f030db1aee0\":{\"id\":\"6ca7788c-d995-4431-a8a3-2f030db1aee0\",\"label\":\"The crypts\",\"node\":{\"locationId\":\"6ca7788c-d995-4431-a8a3-2f030db1aee0\",\"name\":\"The crypts\",\"parentLocation\":{\"locationId\":\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\",\"name\":\"Winterfell\",\"parentLocation\":{\"locationId\":\"425b0ac3-05e7-4123-ad27-76f510d96a6a\",\"name\":\"The North\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Facility\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\"}},\"id\":\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\",\"label\":\"Winterfell\",\"node\":{\"locationId\":\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\",\"name\":\"Winterfell\",\"parentLocation\":{\"locationId\":\"425b0ac3-05e7-4123-ad27-76f510d96a6a\",\"name\":\"The North\",\"parentLocation\":{\"locationId\":\"5b854508-42c5-4cc5-9bea-77335687a428\",\"name\":\"Westeros\",\"serverVersion\":0,\"voided\":false},\"serverVersion\":0,\"voided\":false},\"tags\":[\"Department\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"425b0ac3-05e7-4123-ad27-76f510d96a6a\"}},\"id\":\"425b0ac3-05e7-4123-ad27-76f510d96a6a\",\"label\":\"The North\",\"node\":{\"locationId\":\"425b0ac3-05e7-4123-ad27-76f510d96a6a\",\"name\":\"The North\",\"parentLocation\":{\"locationId\":\"5b854508-42c5-4cc5-9bea-77335687a428\",\"name\":\"Westeros\",\"serverVersion\":0,\"voided\":false},\"tags\":[\"Province\"],\"serverVersion\":0,\"voided\":false},\"parent\":\"5b854508-42c5-4cc5-9bea-77335687a428\"}},\"id\":\"5b854508-42c5-4cc5-9bea-77335687a428\",\"label\":\"Westeros\",\"node\":{\"locationId\":\"5b854508-42c5-4cc5-9bea-77335687a428\",\"name\":\"Westeros\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false}}},\"parentChildren\":{\"425b0ac3-05e7-4123-ad27-76f510d96a6a\":[\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\"],\"288403dc-e48f-4fa5-9cd2-f2293c07fe8c\":[\"6ca7788c-d995-4431-a8a3-2f030db1aee0\"],\"5b854508-42c5-4cc5-9bea-77335687a428\":[\"425b0ac3-05e7-4123-ad27-76f510d96a6a\"]}}}";
        context.allSettings().saveANMLocation(loc);
        context.allSettings().put("dfltLoc-", "6ca7788c-d995-4431-a8a3-2f030db1aee0");
    }
}
