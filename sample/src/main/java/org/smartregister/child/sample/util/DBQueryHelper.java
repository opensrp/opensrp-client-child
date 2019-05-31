package org.smartregister.child.sample.util;

import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.AlertStatus;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.util.VaccinateActionUtils;

import java.util.ArrayList;

/**
 * Created by ndegwamartin on 28/01/2018.
 */

public class DBQueryHelper {

    public static final String getHomeRegisterCondition() {
        //return String.format(" %s is null ", Constants.KEY.DATE_REMOVED)
        return Utils.metadata().childRegister.tableName + "." + Constants.KEY.DATE_REMOVED + " IS NULL AND " + Utils.metadata().childRegister.tableName + "." + Constants.KEY.DOD + " IS NULL";
    }

    public static String getFilterSelectionCondition(boolean urgentOnly) {

        final String AND = " AND ";
        final String OR = " OR ";
        final String IS_NULL_OR = " IS NULL OR ";
        final String TRUE = "'true'";

        String mainCondition = " ( " + Constants.KEY.DOD + " is NULL OR " + Constants.KEY.DOD + " = '' ) " +
                AND + " (" + Constants.CHILD_STATUS.INACTIVE + IS_NULL_OR + Constants.CHILD_STATUS.INACTIVE + " != " + TRUE + " ) " +
                AND + " (" + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + IS_NULL_OR + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " != " + TRUE + " ) " +
                AND + " ( ";
        ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines("child");

        vaccines.remove(VaccineRepo.Vaccine.bcg2);
        vaccines.remove(VaccineRepo.Vaccine.ipv);
        vaccines.remove(VaccineRepo.Vaccine.opv0);
        vaccines.remove(VaccineRepo.Vaccine.opv4);
        vaccines.remove(VaccineRepo.Vaccine.measles1);
        vaccines.remove(VaccineRepo.Vaccine.mr1);
        vaccines.remove(VaccineRepo.Vaccine.measles2);
        vaccines.remove(VaccineRepo.Vaccine.mr2);

        final String URGENT = "'" + AlertStatus.urgent.value() + "'";
        final String NORMAL = "'" + AlertStatus.normal.value() + "'";
        final String COMPLETE = "'" + AlertStatus.complete.value() + "'";


        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + URGENT + " ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + URGENT + OR;
            }
        }

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " != " + COMPLETE + " ) ";

        if (urgentOnly) {
            return mainCondition + " ) ";
        }

        mainCondition += OR;
        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + NORMAL + " ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + NORMAL + OR;
            }
        }

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " != " + COMPLETE + " ) ";

        return mainCondition + " ) ";
    }

    public static String getSortQuery() {
        return DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }
}
