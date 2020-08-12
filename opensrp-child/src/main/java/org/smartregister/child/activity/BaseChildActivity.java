package org.smartregister.child.activity;

import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;

/**
 * Created by ndegwamartin on 2019-08-15.
 */
public abstract class BaseChildActivity extends BaseActivity {
    public CommonPersonObjectClient childDetails;

    protected String getActivityTitle() {
        String name = "";
        if (isDataOk()) {
            name = constructChildName();
        }
        //        return String.format("%s > %s", getString(R.string.app_name), name.trim());
        return name != null ? name.trim() : "";

    }

    protected boolean isDataOk() {
        return childDetails != null && childDetails.getDetails() != null;
    }

    protected String constructChildName() {
        String firstName = Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.FIRST_NAME, true);
        String lastName = Utils.getValue(childDetails.getColumnmaps(), Constants.KEY.LAST_NAME, true);
        return Utils.getName(firstName, lastName).trim();
    }
}
