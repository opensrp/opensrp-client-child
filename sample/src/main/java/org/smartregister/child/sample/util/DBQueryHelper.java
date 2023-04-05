package org.smartregister.child.sample.util;

import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.repository.VaccineOverdueCountRepository;
import org.smartregister.immunization.util.VaccinateActionUtils;

import java.util.List;

/**
 * Created by ndegwamartin on 28/01/2018.
 */

public class DBQueryHelper {

    public static String getHomeRegisterCondition() {
        return Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.DATE_REMOVED + " IS NULL AND " +
                Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.DOD + " IS NULL";
    }

    public static String getFilterSelectionCondition(boolean urgentOnly) {

        final String AND = " AND ";
        final String OR = " OR ";
        final String IS_NULL_OR = " IS NULL OR ";
        final String TRUE = "'true'";

        String childDetailsTable = Utils.metadata().getRegisterQueryProvider().getChildDetailsTable();

        String vaccineOverDueCountSelect = "SELECT " + Constants.KEY.BASE_ENTITY_ID + " FROM " + VaccineOverdueCountRepository.TABLE_NAME;
        if (urgentOnly) {
            vaccineOverDueCountSelect += " WHERE " + VaccineOverdueCountRepository.STATUS + " = 'urgent'";
        }

        String mainCondition = " ( " + Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.DOD + " IS NULL OR " + Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.DOD + " = '' ) " +
                AND + " ( " + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + IS_NULL_OR + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + " != " + TRUE + " ) " +
                AND + " ( " + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + IS_NULL_OR + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " != " + TRUE + " ) " +
                AND + " " + childDetailsTable + "." + Constants.KEY.BASE_ENTITY_ID + "+ IN (" + vaccineOverDueCountSelect;
        return mainCondition + " ) ";
    }

    public static String getSortQuery() {
        return Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }
}
