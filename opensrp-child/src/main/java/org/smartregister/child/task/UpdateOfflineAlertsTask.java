package org.smartregister.child.task;

import android.os.AsyncTask;

import org.joda.time.DateTime;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.immunization.domain.VaccineSchedule;

public class UpdateOfflineAlertsTask extends AsyncTask<Void, Void, Void> {
    private static final String CHILD = "child";
    private CommonPersonObjectClient childDetails;

    public UpdateOfflineAlertsTask(CommonPersonObjectClient childDetails) {
        this.childDetails = childDetails;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DateTime birthDateTime = Utils.dobToDateTime(childDetails);
        if (birthDateTime != null) {
            VaccineSchedule.updateOfflineAlertsOnly(childDetails.entityId(), birthDateTime, CHILD);
        }
        return null;
    }
}
