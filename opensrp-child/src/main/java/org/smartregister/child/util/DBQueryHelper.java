package org.smartregister.child.util;

/**
 * Created by ndegwamartin on 28/01/2018.
 */

public class DBQueryHelper {

    public static final String getHomePatientRegisterCondition() {
        return DBConstants.KEY.DATE_REMOVED + " IS NULL ";
    }
}
