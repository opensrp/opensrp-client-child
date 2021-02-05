package org.smartregister.child;

import net.sqlcipher.database.SQLiteDatabase;

import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.child.activity.BaseChildFormActivity;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.BaseProfileActivity;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestChildApp extends DrishtiApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        CoreLibrary.init(context);
        ConfigurableViewsLibrary.init(context);
        ChildLibrary.init(context, getRepository(), getMetadata(), 1, 1);
        GrowthMonitoringLibrary.init(context, repository, 1, 1);
        CommonFtsObject commonFtsObject = Mockito.mock(CommonFtsObject.class);
        ImmunizationLibrary.init(context, repository, commonFtsObject, 1, 1);
        setTheme(R.style.Theme_AppCompat);
    }

    private ChildMetadata getMetadata() {
        return new ChildMetadata(BaseChildFormActivity.class, BaseProfileActivity.class, BaseChildImmunizationActivity.class, null, true);
    }

    @Override
    public Repository getRepository() {
        repository = mock(Repository.class);
        SQLiteDatabase sqLiteDatabase = mock(SQLiteDatabase.class);
        when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);

        return repository;
    }

    @Override
    public void logoutCurrentUser() {
        Timber.v("Logout");
    }

    @Override
    public void onTerminate() {
        Robolectric.flushBackgroundThreadScheduler();
    }
}
