package org.smartregister.child.listener;

import android.app.Activity;
import android.util.Log;

import org.smartregister.AllConstants;
import org.smartregister.child.toolbar.BaseToolbar;
import org.smartregister.child.toolbar.LocationSwitcherToolbar;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.location.helper.LocationHelper;

import static org.smartregister.child.util.JsonFormUtils.REQUEST_CODE_GET_JSON;

/**
 * Created by samuelgithengi on 2/14/18.
 */

public class BaseListener {

    private final static String TAG = BaseListener.class.getName();

    protected Activity context;
    private BaseToolbar toolbar;

    public BaseListener(Activity context) {
        this.context = context;
    }

    public BaseListener(Activity context, BaseToolbar toolbar) {
        this.context = context;
        this.toolbar = toolbar;
    }

    protected void startJsonForm(String formName, String entityId) {
        try {
            String locationId = null;
            locationId = Utils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            JsonFormUtils.startForm(context, REQUEST_CODE_GET_JSON, formName, entityId, locationId);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

}
