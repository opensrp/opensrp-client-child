package org.smartregister.child.model;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.util.Utils;

import java.util.Map;

import static org.smartregister.child.util.Constants.CHILD_STATUS.ACTIVE;
import static org.smartregister.child.util.Constants.KEY.INACTIVE;
import static org.smartregister.child.util.Constants.KEY.LOST_TO_FOLLOW_UP;

public abstract class BaseChildAdvancedSearchModel extends BaseChildRegisterFragmentModel implements ChildAdvancedSearchContract.Model {


    public static final String GLOBAL_FIRST_NAME = "firstName";
    public static final String GLOBAL_LAST_NAME = "lastName";
    public static final String GLOBAL_BIRTH_DATE = "birthdate";
    public static final String GLOBAL_ATTRIBUTE = "attribute";
    public static final String GLOBAL_IDENTIFIER = "identifier";
    public static final String OPENSRP_ID = "ZEIR_ID";
    public static final String PHONE_NUMBER = "contact_phone_number";
    public static final String FIRST_NAME = "First name:";
    public static final String LAST_NAME = "Last name:";
    public static final String SEARCH_TERM_OPENSRP_ID = "OPENSRP ID:";
    public static final String DOB = "Dob:";
    public static final String MOBILE_PHONE_NUMBER = "Mobile phone number:";
    public static final String AND = "AND";
    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";


    @Override
    public String createSearchString(Map<String, String> searchMap) {
        String searchCriteria = "";

      /*  if (outsideInside.isChecked()) {
            outOfArea = true;
            searchCriteria += " \"Outside and Inside My Catchment Area\", ";
        } else if (myCatchment.isChecked()) {
            outOfArea = false;
            searchCriteria += " \"My Catchment Area\", ";
        }
*/

        if (searchMap == null || searchMap.isEmpty()) {
            return searchCriteria;
        }

        for (Map.Entry<String, String> entry : searchMap.entrySet()) {
            String key = entry.getKey();
            key = key.contains(".") ? key.substring(key.indexOf('.') + 1) : key;
            String value = entry.getValue();

            if (!StringUtils.isBlank(searchCriteria)) {

                searchCriteria += " " + AND;
            }

            searchCriteria += " " + Utils.getTranslatedIdentifier(key) + " " + value + "";

        }
        return removeLastSemiColon(searchCriteria);
    }

    public abstract Map<String, String> createEditMap(Map<String, String> editMap_, boolean isLocal);


    @Override
    public String getMainConditionString(Map<String, String> editMap) {
        {

            final String parentTableName = Utils.metadata().childRegister.motherTableName;
            String tableName = Utils.metadata().childRegister.tableName;

            final String startDateKey = START_DATE;
            final String endDateKey = END_DATE;

            final String motherFirstNameKey = parentTableName + "." + FIRST_NAME;
            final String motherLastNameKey = parentTableName + "." + LAST_NAME;

            String mainConditionString = "";
            for (Map.Entry<String, String> entry : editMap.entrySet()) {
                String key = Utils.metadata().childRegister.tableName + "." + entry.getKey();
                String value = entry.getValue();
                if (!key.equals(startDateKey) && !key.equals(endDateKey) && !key.contains(ACTIVE) && !key.contains(INACTIVE) && !key.contains(LOST_TO_FOLLOW_UP) && !key.contains(motherFirstNameKey) && !key.contains(motherLastNameKey)) {
                    boolean activeStatus = key.contains(ACTIVE) || key.contains(INACTIVE) || key.contains(LOST_TO_FOLLOW_UP);
                    if (StringUtils.isBlank(mainConditionString)) {

                        if (editMap.containsKey(startDateKey) && editMap.containsKey(endDateKey)) {
                            mainConditionString += " " + tableName + ".dob BETWEEN '" + editMap.get(startDateKey) + "' AND '" + editMap.get(endDateKey) + "'";
                        } else if (editMap.containsKey(startDateKey)) {
                            mainConditionString += " " + tableName + ".dob >= '" + editMap.get(startDateKey) + "'";

                        } else if (editMap.containsKey(endDateKey)) {
                            mainConditionString += " " + tableName + ".dob <= '" + editMap.get(endDateKey) + "'";
                        } else if (editMap.containsKey(motherFirstNameKey) && editMap.containsKey(motherLastNameKey)) {
                            mainConditionString += " " + motherFirstNameKey + " Like '%" + editMap.get(motherFirstNameKey) + "%' OR " + motherLastNameKey + " Like '%" + editMap.get(motherLastNameKey) + "%'";

                        } else if (activeStatus) {
                            String statusConditionString = "";
                            if (key.contains(ACTIVE) && !key.contains(INACTIVE)) {
                                statusConditionString += " ( ( " + tableName + "." + INACTIVE + " IS NULL OR " + tableName + "." + INACTIVE + " != '" + Boolean.TRUE.toString() + "' ) " +
                                        " AND ( " + tableName + "." + LOST_TO_FOLLOW_UP + " IS NULL OR " + tableName + "." + LOST_TO_FOLLOW_UP + " != '" + Boolean.TRUE.toString() + "' ) ) ";
                            } else {
                                statusConditionString += " " + key + " = '" + value + "'";
                            }

                            mainConditionString = getStatusConditionString(mainConditionString, statusConditionString);
                        } else {

                            mainConditionString += " " + key + " Like '%" + value + "%'";
                        }

                    } else {
                        if (editMap.containsKey(startDateKey) && editMap.containsKey(endDateKey)) {
                            mainConditionString += " AND " + tableName + ".dob BETWEEN '" + editMap.get(startDateKey) + "' AND '" + editMap.get(endDateKey) + "'";
                        } else if (editMap.containsKey(startDateKey)) {
                            mainConditionString += " AND " + tableName + ".dob >= '" + editMap.get(startDateKey) + "'";

                        } else if (editMap.containsKey(endDateKey)) {
                            mainConditionString += " AND " + tableName + ".dob <= '" + editMap.get(endDateKey) + "'";
                        } else if (editMap.containsKey(motherFirstNameKey) && editMap.containsKey(motherLastNameKey)) {
                            mainConditionString += " AND  (" + motherFirstNameKey + " Like '%" + editMap.get(motherFirstNameKey) + "%' OR " + motherLastNameKey + " Like '%" + editMap.get(motherLastNameKey) + "%' ) ";

                        } else if (activeStatus) {

                            String statusConditionString = "";
                            if (key.contains(ACTIVE) && !key.contains(INACTIVE)) {
                                statusConditionString += " OR ( ( " + tableName + "." + INACTIVE + " IS NULL OR " + tableName + "." + INACTIVE + " != '" + Boolean.TRUE.toString() + "' ) " +
                                        " AND ( " + tableName + "." + LOST_TO_FOLLOW_UP + " IS NULL OR " + tableName + "." + LOST_TO_FOLLOW_UP + " != '" + Boolean.TRUE.toString() + "' ) ) ";

                            } else {
                                statusConditionString += " OR " + key + " = '" + value + "'";
                            }

                            mainConditionString = getStatusConditionString(mainConditionString, statusConditionString);
                        } else {
                            mainConditionString += " AND " + key + " Like '%" + value + "%'";
                        }


                    }
                }


            }


            return mainConditionString;

        }

    }

    private String getStatusConditionString(String mainConditionString, String statusConditionString) {
        if (!statusConditionString.isEmpty()) {
            if (StringUtils.isBlank(mainConditionString)) {
                mainConditionString += statusConditionString;
            } else {
                mainConditionString += " AND (" + statusConditionString + ")";
            }
        }
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
