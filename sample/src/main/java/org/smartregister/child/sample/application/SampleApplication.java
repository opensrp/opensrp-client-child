package org.smartregister.child.sample.application;

import android.util.Log;

import com.evernote.android.job.JobManager;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.activity.ChildFormActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.sample.BuildConfig;
import org.smartregister.child.sample.activity.ChildImmunizationActivity;
import org.smartregister.child.sample.activity.ChildProfileActivity;
import org.smartregister.child.sample.configuration.SampleSyncConfiguration;
import org.smartregister.child.sample.job.SampleJobCreator;
import org.smartregister.child.sample.repository.SampleRepository;
import org.smartregister.child.sample.util.SampleConstants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.repository.ZScoreRepository;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;
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

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context, new SampleSyncConfiguration());
        GrowthMonitoringLibrary.init(context, getRepository(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ImmunizationLibrary.init(context, getRepository(), createCommonFtsObject(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);
        ConfigurableViewsLibrary.init(context, getRepository());
        ChildLibrary.init(context, getRepository(), getMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        initRepositories();

        SyncStatusBroadcastReceiver.init(this);
        LocationHelper.init(Utils.ALLOWED_LEVELS, Utils.DEFAULT_LOCATION_LEVEL);


        //Auto login by default
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(SampleRepository.PASSWORD);
        context.session().setPassword(SampleRepository.PASSWORD);


        //init Job Manager
        JobManager.create(this).addJobCreator(new SampleJobCreator());

        sampleUniqueIds();

    }

    @Override
    public void logoutCurrentUser() {
    }

    public static synchronized SampleApplication getInstance() {
        return (SampleApplication) mInstance;
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

    private void initRepositories() {
        weightRepository();
        vaccineRepository();
        zScoreRepository();
    }

    public static CommonFtsObject createCommonFtsObject() {
        if (commonFtsObject == null) {
            commonFtsObject = new CommonFtsObject(getFtsTables());
            for (String ftsTable : commonFtsObject.getTables()) {
                commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
                commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
            }
        }
        return commonFtsObject;
    }

    private static String[] getFtsTables() {
        return new String[]{SampleConstants.TABLE_NAME.CHILD};
    }

    private static String[] getFtsSearchFields(String tableName) {
        if (tableName.equals(SampleConstants.TABLE_NAME.CHILD)) {
            return new String[]{DBConstants.KEY.ZEIR_ID, DBConstants.KEY.FIRST_NAME,
                    DBConstants.KEY.LAST_NAME, DBConstants.KEY.EPI_CARD_NUMBER};
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {
        if (tableName.equals(SampleConstants.TABLE_NAME.CHILD)) {
            ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines(SampleConstants.VACCINE.CHILD);
            List<String> names = new ArrayList<>();
            names.add(DBConstants.KEY.FIRST_NAME);
            names.add(DBConstants.KEY.DOB);
            names.add(DBConstants.KEY.ZEIR_ID);
            names.add(DBConstants.KEY.LAST_INTERACTED_WITH);
            names.add(DBConstants.KEY.INACTIVE);
            names.add(DBConstants.KEY.LOST_TO_FOLLOW_UP);
            names.add(DBConstants.KEY.DOD);
            names.add(tableName + "." + DBConstants.KEY.DATE_REMOVED);

            for (VaccineRepo.Vaccine vaccine : vaccines) {
                names.add("alerts." + VaccinateActionUtils.addHyphen(vaccine.display()));
            }

            return names.toArray(new String[names.size()]);
        }
        return null;
    }

    private ChildMetadata getMetadata() {
        ChildMetadata metadata = new ChildMetadata(ChildFormActivity.class, ChildProfileActivity.class, ChildImmunizationActivity.class, true);
        metadata.updateChildRegister(SampleConstants.JSON_FORM.CHILD_ENROLLMENT, SampleConstants.TABLE_NAME.CHILD, SampleConstants.TABLE_NAME.MOTHER_TABLE_NAME, SampleConstants.EventType.CHILD_REGISTRATION, SampleConstants.EventType.UPDATE_CHILD_REGISTRATION, SampleConstants.CONFIGURATION.CHILD_REGISTER, SampleConstants.RELATIONSHIP.MOTHER, SampleConstants.JSON_FORM.OUT_OF_CATCHMENT_SERVICE);
        return metadata;
    }

    private void sampleUniqueIds() {
        List<String> ids = generateIds(20);
        ChildLibrary.getInstance().getUniqueIdRepository().bulkInserOpenmrsIds(ids);
    }

    private List<String> generateIds(int size) {
        List<String> ids = new ArrayList<>();
        Random r = new Random();

        for (int i = 10; i < size; i++) {
            Integer randomInt = r.nextInt(10000) + 1;
            ids.add(randomInt.toString());
        }

        return ids;
    }

    public WeightRepository weightRepository() {
        return GrowthMonitoringLibrary.getInstance().weightRepository();
    }

    public Context context() {
        return context;
    }

    public VaccineRepository vaccineRepository() {
        return ImmunizationLibrary.getInstance().vaccineRepository();
    }

    public ZScoreRepository zScoreRepository() {
        return GrowthMonitoringLibrary.getInstance().zScoreRepository();
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
}