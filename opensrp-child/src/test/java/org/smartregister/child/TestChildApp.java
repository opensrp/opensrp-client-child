package org.smartregister.child;

import org.robolectric.Robolectric;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

public class TestChildApp extends DrishtiApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        CoreLibrary.init(context);
        ConfigurableViewsLibrary.init(context);
        setTheme(R.style.Theme_AppCompat);
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
