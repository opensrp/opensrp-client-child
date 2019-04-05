package org.smartregister.child.sample.model;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.model.BaseChildRegisterFragmentModel;
import org.smartregister.child.sample.contract.AdvancedSearchContract;
import org.smartregister.child.util.DBConstants;

import java.util.HashMap;
import java.util.Map;

public class AdvancedSearchModel extends BaseChildRegisterFragmentModel implements AdvancedSearchContract.Model {


    public static final String GLOBAL_FIRST_NAME = "firstName";
    public static final String GLOBAL_LAST_NAME = "lastName";
    public static final String GLOBAL_BIRTH_DATE = "birthdate";
    public static final String GLOBAL_ATTRIBUTE = "attribute";
    public static final String GLOBAL_IDENTIFIER = "identifier";
    public static final String OPENSRP_ID = "ZEIR_ID";
    public static final String EDD_ATTR = "edd";
    public static final String PHONE_NUMBER = "contact_phone_number";
    public static final String ALT_CONTACT_NAME = "alt_name";
    public static final String FIRST_NAME = "First name:";
    public static final String LAST_NAME = "Last name:";
    public static final String SEARCH_TERM_OPENSRP_ID = "OPENSRP ID:";
    public static final String EDD = "Edd:";
    public static final String DOB = "Dob:";
    public static final String MOBILE_PHONE_NUMBER = "Mobile phone number:";
    public static final String ALTERNATE_CONTACT_NAME = "Alternate contact name:";
    public static final String LIKE = "Like";
    public static final String AND = "AND";


    @Override
    public Map<String, String> createEditMap(Map<String, String> editMap_, boolean isLocal) {

        Map<String, String> editMap = new HashMap<>();
        editMap.putAll(editMap_);

        String firstName = editMap.get(DBConstants.KEY.FIRST_NAME);
        String lastName = editMap.get(DBConstants.KEY.LAST_NAME);
        String opensrpID = editMap.get(DBConstants.KEY.ZEIR_ID);
        String dob = editMap.get(DBConstants.KEY.DOB);
        String phoneNumber = editMap.get(DBConstants.KEY.CONTACT_PHONE_NUMBER);

        if (StringUtils.isNotBlank(firstName)) {
            editMap.put(isLocal ? DBConstants.KEY.FIRST_NAME : GLOBAL_FIRST_NAME, firstName);
        }
        if (StringUtils.isNotBlank(lastName)) {
            editMap.put(isLocal ? DBConstants.KEY.LAST_NAME : GLOBAL_LAST_NAME, lastName);
        }
        if (StringUtils.isNotBlank(opensrpID)) {
            editMap.put(isLocal ? DBConstants.KEY.ZEIR_ID : GLOBAL_IDENTIFIER, isLocal ? opensrpID : OPENSRP_ID + ":" + opensrpID);
        }

        if (StringUtils.isNotBlank(dob)) {
            editMap.put(isLocal ? DBConstants.KEY.DOB : GLOBAL_BIRTH_DATE, dob);
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            editMap.put(isLocal ? DBConstants.KEY.CONTACT_PHONE_NUMBER : PHONE_NUMBER, phoneNumber);
        }
        return editMap;
    }

    @Override
    public String createSearchString(Map<String, String> searchMap) {
        String searchCriteria = "";
        if (searchMap == null || searchMap.isEmpty()) {
            return searchCriteria;
        }

        for (Map.Entry<String, String> entry : searchMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (!StringUtils.isBlank(searchCriteria)) {

                searchCriteria += " " + AND;
            }

            searchCriteria += " " + getKey(key) + " " + value + ";";

        }
        return removeLastSemiColon(searchCriteria);
    }

    protected String getKey(String key) {
        String resKey = "";
        switch (key) {
            case DBConstants.KEY.FIRST_NAME:
                resKey = FIRST_NAME;

                break;


            case DBConstants.KEY.LAST_NAME:
                resKey = LAST_NAME;

                break;
            case DBConstants.KEY.ZEIR_ID:
                resKey = SEARCH_TERM_OPENSRP_ID;

                break;
            case DBConstants.KEY.DOB:
                resKey = DOB;

                break;
            case DBConstants.KEY.CONTACT_PHONE_NUMBER:
                resKey = MOBILE_PHONE_NUMBER;

                break;

            default:
                break;

        }


        return resKey;
    }

    @Override
    public String getMainConditionString(Map<String, String> editMap) {

        String mainConditionString = "";
        if (editMap == null || editMap.isEmpty()) {
            return mainConditionString;
        }

        for (Map.Entry<String, String> entry : editMap.entrySet()) {
            String key = "ec_child." + entry.getKey();
            String value = entry.getValue();

            if (!StringUtils.isBlank(mainConditionString)) {
                mainConditionString += " " + AND;
            }

            mainConditionString += " " + key + " " + LIKE + " '%" + value + "%'";

        }

        mainConditionString += " ";

        return mainConditionString;

    }

    private String removeLastSemiColon(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        String s = str.trim();
        if (s.charAt(s.length() - 1) == ';') {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

}
