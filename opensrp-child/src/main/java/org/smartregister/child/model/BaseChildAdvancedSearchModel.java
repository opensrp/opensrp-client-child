package org.smartregister.child.model;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public abstract class BaseChildAdvancedSearchModel extends BaseChildRegisterFragmentModel
        implements ChildAdvancedSearchContract.Model {


    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";


    @Override
    public String createSearchString(Map<String, String> searchMap) {
        String searchCriteria = "";

        if (searchMap == null || searchMap.isEmpty()) {
            return searchCriteria;
        }

        for (Map.Entry<String, String> entry : searchMap.entrySet()) {
            String key = entry.getKey();
            key = key.contains(".") ? key.substring(key.indexOf('.') + 1) : key;
            String value = entry.getValue();

            if (!StringUtils.isBlank(searchCriteria)) {

                searchCriteria += " ;";
            }

            searchCriteria += " " + Utils.getTranslatedIdentifier(key) + " " + Utils.bold(value) + "";

        }
        return removeLastSemiColon(searchCriteria);
    }

    @Override
    public String getMainConditionString(Map<String, String> editMap) {
        final String table = Utils.metadata().getRegisterQueryProvider().getDemographicTable();
        final String childDetailsTable = Utils.metadata().getRegisterQueryProvider().getChildDetailsTable();

        final String motherFirstNameKey = Constants.KEY.MOTHER_FIRST_NAME;
        final String motherLastNameKey = Constants.KEY.MOTHER_LAST_NAME;

        final String startDateKey = START_DATE;
        final String endDateKey = END_DATE;
        ArrayList<String> demographicTableColumns = new ArrayList<>(Arrays.asList(new String[]{"first_name", "last_name", "zeir_id"}));

        String mainConditionString = "";
        for (Map.Entry<String, String> entry : editMap.entrySet()) {
            String key = entry.getKey();
            if (demographicTableColumns.contains(key))
                key = table + "." + key;
            String value = entry.getValue();
            if (!key.contains(startDateKey) && !key.contains(endDateKey) && !key.contains(Constants.CHILD_STATUS.ACTIVE) && !key
                    .contains(Constants.CHILD_STATUS.INACTIVE) && !key.contains(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP) && !key.contains(motherFirstNameKey) && !key
                    .contains(motherLastNameKey)) {
                if (StringUtils.isBlank(mainConditionString)) {
                    mainConditionString += " " + key + " Like '%" + value + "%'";
                } else {
                    mainConditionString += " AND " + key + " Like '%" + value + "%'";

                }
            }
        }

        if (StringUtils.isBlank(mainConditionString)) {
            if (editMap.containsKey(startDateKey) && editMap.containsKey(endDateKey)) {
                mainConditionString += " " + table + ".dob BETWEEN '" + editMap
                        .get(startDateKey) + "' AND '" + editMap.get(endDateKey) + "'";
            } else if (editMap.containsKey(startDateKey)) {
                mainConditionString += " " + table + ".dob >= '" + editMap.get(startDateKey) + "'";

            } else if (editMap.containsKey(endDateKey)) {
                mainConditionString += " " + table + ".dob <= '" + editMap.get(endDateKey) + "'";
            }
        } else {
            if (editMap.containsKey(startDateKey) && editMap.containsKey(endDateKey)) {
                mainConditionString += " AND " + table + ".dob BETWEEN '" + editMap
                        .get(startDateKey) + "' AND '" + editMap.get(endDateKey) + "'";
            } else if (editMap.containsKey(startDateKey)) {
                mainConditionString += " AND " + table + ".dob >= '" + editMap.get(startDateKey) + "'";

            } else if (editMap.containsKey(endDateKey)) {
                mainConditionString += " AND " + table + ".dob <= '" + editMap.get(endDateKey) + "'";
            }
        }

        if (editMap.containsKey(motherFirstNameKey) && editMap.containsKey(motherLastNameKey)) {
            if (StringUtils.isBlank(mainConditionString)) {
                mainConditionString += motherFirstNameKey + " Like '%" + editMap
                        .get(motherFirstNameKey) + "%' AND " + motherLastNameKey + " Like '%" + editMap
                        .get(motherLastNameKey) + "%'";
            } else {
                mainConditionString += " AND  (" + motherFirstNameKey + " Like '%" + editMap
                        .get(motherFirstNameKey) + "%' AND " + motherLastNameKey + " Like '%" + editMap
                        .get(motherLastNameKey) + "%' ) ";
            }
        } else if (editMap.containsKey(motherFirstNameKey) && !editMap.containsKey(motherLastNameKey)) {
            if (StringUtils.isBlank(mainConditionString)) {
                mainConditionString += " " + motherFirstNameKey + " Like '%" + editMap
                        .get(motherFirstNameKey) + "%'";
            } else {
                mainConditionString += " AND  (" + motherFirstNameKey + " Like '%" + editMap
                        .get(motherFirstNameKey) + "%') ";
            }
        } else if (!editMap.containsKey(motherFirstNameKey) && editMap.containsKey(motherLastNameKey)) {
            if (StringUtils.isBlank(mainConditionString)) {
                mainConditionString += " " + motherLastNameKey + " Like '%" + editMap
                        .get(motherLastNameKey) + "%'";
            } else {
                mainConditionString += " AND  (" + motherLastNameKey + " Like '%" + editMap
                        .get(motherLastNameKey) + "%' ) ";
            }
        }

        String statusConditionString = "";
        for (Map.Entry<String, String> entry : editMap.entrySet()) {
            String key = entry.getKey();
            if (demographicTableColumns.contains(key))
                key = table + "." + key;
            String value = entry.getValue();
            if (key.contains(Constants.CHILD_STATUS.ACTIVE) || key.contains(Constants.CHILD_STATUS.INACTIVE) || key.contains(Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP)) {

                if (StringUtils.isBlank(statusConditionString)) {
                    if (key.contains(Constants.CHILD_STATUS.ACTIVE) && !key.contains(Constants.CHILD_STATUS.INACTIVE)) {
                        statusConditionString += " ( ( " + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + " IS NULL OR " + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + " != '" + Boolean.TRUE
                                .toString() + "' ) " +
                                " AND ( " + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " IS NULL OR " + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " != '" + Boolean.TRUE
                                .toString() + "' ) ) ";
                    } else {
                        statusConditionString += " " + key + " = '" + value + "'";
                    }
                } else {
                    if (key.contains(Constants.CHILD_STATUS.ACTIVE) && !key.contains(Constants.CHILD_STATUS.INACTIVE)) {
                        statusConditionString += " OR ( ( " + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + " IS NULL OR " + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + " != '" + Boolean.TRUE
                                .toString() + "' ) " +
                                " AND ( " + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " IS NULL OR " + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " != '" + Boolean.TRUE
                                .toString() + "' ) ) ";

                    } else {
                        statusConditionString += " OR " + key + " = '" + value + "'";
                    }

                }
            }
        }

        if (!statusConditionString.isEmpty()) {
            if (StringUtils.isBlank(mainConditionString)) {
                mainConditionString += statusConditionString;
            } else {
                mainConditionString += " AND (" + statusConditionString + ")";
            }
        }

        return mainConditionString + " AND " + Utils.metadata().childRegister.tableName + "." + Constants.KEY.DATE_REMOVED + " IS NULL AND " + Utils.metadata().childRegister.tableName + "." + Constants.KEY.DOD + " IS NULL";
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