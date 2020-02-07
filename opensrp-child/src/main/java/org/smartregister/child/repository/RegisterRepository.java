package org.smartregister.child.repository;

import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;

public class RegisterRepository {

    public String mainRegisterQuery() {
        return "select " + getMainColumns() + " from " + getChildDetailsTable() + " child_details " +
                "join " + getMotherDetailsTable() + " mother_details on child_details.relational_id = mother_details.base_entity_id " +
                "join " + getDemographicTable() + " on " + getDemographicTable() + ".base_entity_id = child_details.base_entity_id " +
                "join " + getDemographicTable() + " mother on mother.base_entity_id = mother_details.base_entity_id";
    }

    public String getMainColumns() {
        return getDemographicTable() + "." + Constants.KEY.ID + " as _id, " +
                getDemographicTable() + "." + Constants.KEY.RELATIONALID + "," +
                getDemographicTable() + "." + Constants.KEY.ZEIR_ID + ", " +
                "child_details" + "." + Constants.KEY.RELATIONAL_ID + "," +
                getDemographicTable() + "." + Constants.KEY.GENDER + ", " +
                getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID + "," +
                getDemographicTable() + "." + Constants.KEY.FIRST_NAME + "," +
                getDemographicTable() + "." + Constants.KEY.LAST_NAME + "," +
                "mother" + "." + Constants.KEY.FIRST_NAME + " as mother_first_name," +
                "mother" + "." + Constants.KEY.LAST_NAME + " as mother_last_name," +
                getDemographicTable() + "." + Constants.KEY.DOB + "," +
                "mother" + "." + Constants.KEY.DOB + " as mother_dob," +
                "mother_details" + "." + Constants.KEY.NRC_NUMBER + " as mother_nrc_number," +
                "mother_details" + "." + Constants.KEY.FATHER_NAME + "," +
                "mother_details" + "." + Constants.KEY.EPI_CARD_NUMBER + "," +
                getDemographicTable() + "." + Constants.KEY.CLIENT_REG_DATE + "," +
                "child_details" + "." + Constants.KEY.PMTCT_STATUS + "," +
                getDemographicTable() + "." + Constants.KEY.LAST_INTERACTED_WITH + "," +
                "child_details" + "." + Constants.KEY.INACTIVE + "," +
                "child_details" + "." + Constants.KEY.LOST_TO_FOLLOW_UP + "," +
                "child_details" + "." + Constants.KEY.CONSTANT_PHONE_NUMBER;
    }

    public String getChildDetailsTable() {
        return DBConstants.RegisterTable.CHILD_DETAILS;
    }

    public String getMotherDetailsTable() {
        return DBConstants.RegisterTable.MOTHER_DETAILS;
    }

    public String getDemographicTable() {
        return DBConstants.RegisterTable.CLIENT;
    }


}
