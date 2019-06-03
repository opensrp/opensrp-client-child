package org.smartregister.child.model;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.contract.ChildAdvancedSearchContract;

import java.util.Map;

public abstract class BaseChildAdvancedSearchModel extends BaseChildRegisterFragmentModel implements ChildAdvancedSearchContract.Model {


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

    public abstract Map<String, String> createEditMap(Map<String, String> editMap_, boolean isLocal);

    protected abstract String getKey(String key);

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
