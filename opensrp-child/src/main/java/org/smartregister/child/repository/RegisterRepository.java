package org.smartregister.child.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;

public class RegisterRepository {

    public String getObjectIdsQuery(String mainCondition, String filters) {
        if (!filters.isEmpty()) {
            filters = String.format(" AND ec_client.phrase MATCH '*%s*'", filters);
        }
        if (StringUtils.isNotBlank(filters) && StringUtils.isBlank(mainCondition)) {
            filters = String.format(" where ec_client.phrase MATCH '*%s*'", filters);
        }

        if (!StringUtils.isBlank(mainCondition)) {
            mainCondition = " where " + mainCondition;
        }

        return "select ec_client.object_id from ec_client_search ec_client join ec_child_details on ec_client.object_id =  ec_child_details.id " + mainCondition + filters;
    }


    public String getCountExecuteQuery(String mainCondition, String filters) {
        if (!filters.isEmpty()) {
            filters = String.format(" AND ec_client_search.phrase MATCH '*%s*'", filters);
        }
        if (StringUtils.isNotBlank(filters) && StringUtils.isBlank(mainCondition)) {
            filters = String.format(" where ec_client_search.phrase MATCH '*%s*'", filters);
        }

        if (!StringUtils.isBlank(mainCondition)) {
            mainCondition = " where " + mainCondition;
        }

        return "select count(ec_client.object_id) from ec_client_search ec_client join ec_child_details on ec_client.object_id =  ec_child_details.id " + mainCondition + filters;
    }

    public String mainRegisterQuery() {
        return "select " + StringUtils.join(getMainColumns(), ",") + " from " + getChildDetailsTable() + " " +
                "join " + getMotherDetailsTable() + " on ec_child_details.relational_id = ec_mother_details.base_entity_id " +
                "join " + getDemographicTable() + " on " + getDemographicTable() + ".base_entity_id = ec_child_details.base_entity_id " +
                "join " + getDemographicTable() + " mother on mother.base_entity_id = ec_mother_details.base_entity_id";
    }

    public String[] getMainColumns() {
        return new String[]{
                getDemographicTable() + "." + Constants.KEY.ID + " as _id",
                getDemographicTable() + "." + Constants.KEY.RELATIONALID,
                getDemographicTable() + "." + Constants.KEY.ZEIR_ID,
                getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID,
                getDemographicTable() + "." + Constants.KEY.GENDER,
                getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID,
                getDemographicTable() + "." + Constants.KEY.FIRST_NAME,
                getDemographicTable() + "." + Constants.KEY.LAST_NAME,
                "mother" + "." + Constants.KEY.FIRST_NAME + " as mother_first_name",
                "mother" + "." + Constants.KEY.LAST_NAME + " as mother_last_name",
                getDemographicTable() + "." + Constants.KEY.DOB,
                "mother" + "." + Constants.KEY.DOB + " as mother_dob",
                getMotherDetailsTable() + "." + Constants.KEY.NRC_NUMBER + " as mother_nrc_number",
                getMotherDetailsTable() + "." + Constants.KEY.FATHER_NAME,
                getMotherDetailsTable() + "." + Constants.KEY.EPI_CARD_NUMBER,
                getDemographicTable() + "." + Constants.KEY.CLIENT_REG_DATE,
                getChildDetailsTable() + "." + Constants.KEY.PMTCT_STATUS,
                getDemographicTable() + "." + Constants.KEY.LAST_INTERACTED_WITH,
                getChildDetailsTable() + "." + Constants.KEY.INACTIVE,
                getChildDetailsTable() + "." + Constants.KEY.LOST_TO_FOLLOW_UP,
                getChildDetailsTable() + "." + Constants.KEY.CONSTANT_PHONE_NUMBER
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
