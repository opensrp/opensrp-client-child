package org.smartregister.child.provider;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.commonregistry.CommonFtsObject;

public class RegisterQueryProvider {

    public String getObjectIdsQuery(String mainCondition, String filters) {

        String strMainCondition = getMainCondition(mainCondition);

        String strFilters = getFilter(filters);

        if (StringUtils.isNotBlank(strFilters) && StringUtils.isBlank(strMainCondition)) {
            strFilters = String.format(" where " + getDemographicTable() + ".phrase MATCH '*%s*'", filters);
        }

        return "select " + getDemographicTable() + ".object_id from " + CommonFtsObject.searchTableName(getDemographicTable()) + " " + getDemographicTable() + "  " +
                "join " + getChildDetailsTable() + " on " + getDemographicTable() + ".object_id =  " + getChildDetailsTable() + ".id " +
                "left join " + CommonFtsObject.searchTableName(getChildDetailsTable()) + " on " + getDemographicTable() + ".object_id =  " + CommonFtsObject.searchTableName(getChildDetailsTable()) + ".object_id "
                + strMainCondition + strFilters;
    }


    private String getFilter(String filters) {
        if (StringUtils.isNotBlank(filters)) {
            return String.format(" AND " + getDemographicTable() + ".phrase MATCH '*%s*'", filters);
        }
        return "";
    }

    private String getMainCondition(String mainCondition) {
        if (!StringUtils.isBlank(mainCondition)) {
            return " where " + mainCondition;
        }
        return "";
    }

    public String getCountExecuteQuery(String mainCondition, String filters) {

        String strMainCondition = getMainCondition(mainCondition);

        String strFilters = getFilter(filters);

        if (StringUtils.isNotBlank(strFilters) && StringUtils.isBlank(strMainCondition)) {
            strFilters = String.format(" where " + getDemographicTable() + ".phrase MATCH '*%s*'", filters);
        }

        return "select count(" + getDemographicTable() + ".object_id) from " + CommonFtsObject.searchTableName(getDemographicTable()) + " " + getDemographicTable() + "  " +
                "join " + getChildDetailsTable() + " on " + getDemographicTable() + ".object_id =  " + getChildDetailsTable() + ".id " +
                "left join " + CommonFtsObject.searchTableName(getChildDetailsTable()) + " on " + getDemographicTable() + ".object_id =  " + CommonFtsObject.searchTableName(getChildDetailsTable()) + ".object_id " +
                strMainCondition + strFilters;
    }

    public String mainRegisterQuery() {
        return "select " + StringUtils.join(mainColumns(), ",") + " from " + getChildDetailsTable() + " " +
                "join " + getMotherDetailsTable() + " on " + getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + getDemographicTable() + " on " + getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID + " = " + getChildDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + getDemographicTable() + " mother on mother." + Constants.KEY.BASE_ENTITY_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID;
    }

    public String mainRegisterQuery(String select) {
        return "select " + select + " from " + getChildDetailsTable() + " " +
                "join " + getMotherDetailsTable() + " on " + getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + getDemographicTable() + " on " + getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID + " = " + getChildDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "join " + getDemographicTable() + " mother on mother." + Constants.KEY.BASE_ENTITY_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID;
    }

    public String[] mainColumns() {
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
                getChildDetailsTable() + "." + Constants.CHILD_STATUS.INACTIVE,
                getChildDetailsTable() + "." + Constants.KEY.LOST_TO_FOLLOW_UP,
                getChildDetailsTable() + "." + Constants.KEY.CONTACT_PHONE_NUMBER
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
