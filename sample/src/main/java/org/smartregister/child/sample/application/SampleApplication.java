package org.smartregister.child.sample.application;

import android.util.Log;

import com.evernote.android.job.JobManager;

import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.activity.ChildFormActivity;
import org.smartregister.child.sample.activity.ChildProfileActivity;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.sample.job.SampleJobCreator;
import org.smartregister.child.sample.repository.SampleRepository;
import org.smartregister.child.sample.util.SampleConstants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.child.sample.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SampleApplication extends DrishtiApplication {
    private static final String TAG = SampleApplication.class.getCanonicalName();

    private static CommonFtsObject commonFtsObject;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());

        //Initialize Modules
        CoreLibrary.init(context);
        ConfigurableViewsLibrary.init(context, getRepository());
        ChildLibrary.init(context, getRepository(), getMetadata(), BuildConfig.VERSION_CODE, BuildConfig.DATABASE_VERSION);

        SyncStatusBroadcastReceiver.init(this);
        LocationHelper.init(Utils.ALLOWED_LEVELS, Utils.DEFAULT_LOCATION_LEVEL);


        //Auto login by default
        String password = "pwd";
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(password);
        context.session().setPassword(password);


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
            return new String[]{DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.VILLAGE_TOWN, DBConstants.KEY.FIRST_NAME,
                    DBConstants.KEY.LAST_NAME, DBConstants.KEY.UNIQUE_ID};
        }
        return null;
    }

    private static String[] getFtsSortFields(String tableName) {
        if (tableName.equals(SampleConstants.TABLE_NAME.CHILD)) {
            return new String[]{DBConstants.KEY.LAST_INTERACTED_WITH, DBConstants.KEY.DATE_REMOVED};
        }
        return null;
    }

    private ChildMetadata getMetadata() {
        ChildMetadata metadata = new ChildMetadata(ChildFormActivity.class, ChildProfileActivity.class, true);
        metadata.updateChildRegister(SampleConstants.JSON_FORM.CHILD_ENROLLMENT, SampleConstants.TABLE_NAME.CHILD, SampleConstants.EventType.CHILD_REGISTRATION, SampleConstants.EventType.UPDATE_CHILD_REGISTRATION, SampleConstants.CONFIGURATION.FAMILY_REGISTER, SampleConstants.RELATIONSHIP.PRIMARY_CAREGIVER, SampleConstants.JSON_FORM.OUT_OF_CATCHMENT_SERVICE);
         return metadata;
    }

    private void sampleUniqueIds() {
        List<String> ids = generateIds(20);
        ChildLibrary.getInstance().getUniqueIdRepository().bulkInserOpenmrsIds(ids);
    }

    private List<String> generateIds(int size) {
        List<String> ids = new ArrayList<>();
        Random r = new Random();

        for (int i = 0; i < size; i++) {
            Integer randomInt = r.nextInt(1000) + 1;
            ids.add(randomInt.toString());
        }

        return ids;
    }

}