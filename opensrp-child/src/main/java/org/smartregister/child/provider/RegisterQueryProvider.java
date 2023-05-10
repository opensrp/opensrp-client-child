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
            strFilters = String.format(" WHERE (" + getDemographicTable() + ".first_name LIKE '%%%s%%' OR "+getDemographicTable()+ ".last_name LIKE '%%%s%%')", filters, filters);
        }

        return "SELECT " + getDemographicTable() + ".id " +
                "FROM " + getDemographicTable() + " " + getDemographicTable() + " " +
                "LEFT JOIN " + getChildDetailsTable() + " ON " + getDemographicTable() + ".id = " + getChildDetailsTable() + ".base_entity_id " +
                strMainCondition + strFilters;
    }

    private String getFilter(String filters) {
        if (StringUtils.isNotBlank(filters)) {
            return String.format(" AND (" + getDemographicTable() + ".first_name LIKE '%%%s%%' OR "+getDemographicTable()+ ".last_name LIKE '%%%s%%')", filters, filters);
        }
        return "";
    }

    private String getMainCondition(String mainCondition) {
        if (!StringUtils.isBlank(mainCondition)) {
            return " WHERE " + mainCondition;
        }
        return "";
    }

    public String getCountExecuteQuery(String mainCondition, String filters) {
        String strMainCondition = getMainCondition(mainCondition);

        String strFilters = getFilter(filters);

        if (StringUtils.isNotBlank(strFilters) && StringUtils.isBlank(strMainCondition)) {
            strFilters = String.format(" WHERE (" + getDemographicTable() + ".first_name LIKE '%%%s%%' OR "+getDemographicTable()+ ".last_name LIKE '%%%s%%')", filters, filters);
        }

        return "SELECT count(" + getDemographicTable() + ".id) " +
                "FROM " + getDemographicTable() + " " + getDemographicTable() + " " +
                "LEFT JOIN " + getChildDetailsTable() + " ON " + getDemographicTable() + ".id = " + getChildDetailsTable() + ".base_entity_id " +
                strMainCondition + strFilters;
    }

    public String mainRegisterQuery() {
        return "SELECT " + StringUtils.join(mainColumns(), ",") + " " +
                "FROM " + getChildDetailsTable() + " " +
                "JOIN " + getMotherDetailsTable() + " ON " + getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "JOIN " + getDemographicTable() + " ON " + getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID + " = " + getChildDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "JOIN " + getDemographicTable() + " mother ON mother." + Constants.KEY.BASE_ENTITY_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID;
    }

    public String mainRegisterQuery(String select) {
        if (StringUtils.isBlank(select)) {
            select = StringUtils.join(mainColumns(), ",");
        }
        return "SELECT " + select + " " +
                "FROM " + getChildDetailsTable() + " " +
                "JOIN " + getMotherDetailsTable() + " ON " + getChildDetailsTable() + "." + Constants.KEY.RELATIONAL_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "JOIN " + getDemographicTable() + " ON " + getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID + " = " + getChildDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID + " " +
                "JOIN " + getDemographicTable() + " mother ON mother." + Constants.KEY.BASE_ENTITY_ID + " = " + getMotherDetailsTable() + "." + Constants.KEY.BASE_ENTITY_ID;
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
                getChildDetailsTable() + "." + Constants.KEY.MOTHER_GUARDIAN_PHONE_NUMBER,
                getDemographicTable() + "." + "address1",
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

    public String getActiveChildrenQuery() {
        return "SELECT count(id) FROM " + getChildDetailsTable() +
                " WHERE (" + Constants.KEY.DATE_REMOVED + " IS NULL " +
                " AND (" + getChildDetailsTable() + ".inactive is NOT true OR " + getChildDetailsTable() + ".inactive is NULL) " +
                " AND " + Constants.KEY.IS_CLOSED + " IS NOT '1')";
    }

    public String getActiveChildrenIds() {
        return "SELECT " + getChildDetailsTable() + "." + Constants.KEY.ID +
                " FROM " + getChildDetailsTable() + " INNER JOIN " + getDemographicTable() + " ON " + getChildDetailsTable() + "." + Constants.KEY.ID + " = " + getDemographicTable() + "." + Constants.KEY.ID +
                " WHERE (" + getChildDetailsTable() + "." + Constants.KEY.DATE_REMOVED + " IS NULL" +
                " AND (" + getChildDetailsTable() + ".inactive is NOT true OR " + getChildDetailsTable() + ".inactive is NULL)" +
                " AND " + getChildDetailsTable() + "." + Constants.KEY.IS_CLOSED + " IS NOT '1') " +
                "ORDER BY " + getDemographicTable() + "." + Constants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }
}
