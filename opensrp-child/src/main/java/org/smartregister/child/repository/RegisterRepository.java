package org.smartregister.child.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.Utils;

public class RegisterRepository {

    public String mainRegisterQuery() {
        return "select " + StringUtils.join(getMainColumns(), ",") + " from " + getChildDetailsTable() + " child_details " +
                "join " + getMotherDetailsTable() + " mother_details on child_details.relational_id = mother_details.base_entity_id " +
                "join " + getDemographicTable() + " on " + getDemographicTable() + ".base_entity_id = child_details.base_entity_id " +
                "join " + getDemographicTable() + " mother on mother.base_entity_id = mother_details.base_entity_id";
    }

    public String[] getMainColumns() {
        return new String[]{
                Utils.metadata().getRegisterRepository().getDemographicTable() + "." + Constants.KEY.ID + " as _id",
                Utils.metadata().getRegisterRepository().getDemographicTable() + "." + Constants.KEY.RELATIONALID,
                Utils.metadata().getRegisterRepository().getDemographicTable() + "." + Constants.KEY.ZEIR_ID,
                "child_details" + "." + Constants.KEY.RELATIONAL_ID,
                Utils.metadata().getRegisterRepository().getDemographicTable() + "." + Constants.KEY.GENDER,
                Utils.metadata().getRegisterRepository().getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID,
                Utils.metadata().getRegisterRepository().getDemographicTable() + "." + Constants.KEY.FIRST_NAME,
                Utils.metadata().getRegisterRepository().getDemographicTable() + "." + Constants.KEY.LAST_NAME,
                "mother" + "." + Constants.KEY.FIRST_NAME + " as mother_first_name",
                "mother" + "." + Constants.KEY.LAST_NAME + " as mother_last_name",
                Utils.metadata().getRegisterRepository().getDemographicTable() + "." + Constants.KEY.DOB,
                "mother" + "." + Constants.KEY.DOB + " as mother_dob",
                "mother_details" + "." + Constants.KEY.NRC_NUMBER + " as mother_nrc_number",
                "mother_details" + "." + Constants.KEY.FATHER_NAME,
                "mother_details" + "." + Constants.KEY.EPI_CARD_NUMBER,
                Utils.metadata().getRegisterRepository().getDemographicTable() + "." + Constants.KEY.CLIENT_REG_DATE,
                "child_details" + "." + Constants.KEY.PMTCT_STATUS,
                Utils.metadata().getRegisterRepository().getDemographicTable() + "." + Constants.KEY.LAST_INTERACTED_WITH,
                "child_details" + "." + Constants.KEY.INACTIVE,
                "child_details" + "." + Constants.KEY.LOST_TO_FOLLOW_UP,
                "child_details" + "." + Constants.KEY.CONSTANT_PHONE_NUMBER
        };
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
