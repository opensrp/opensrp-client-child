package org.smartregister.child.model;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;

import java.util.Map;

import static org.smartregister.child.util.Constants.CHILD_STATUS.ACTIVE;
import static org.smartregister.child.util.Constants.KEY.FIRST_NAME;
import static org.smartregister.child.util.Constants.KEY.INACTIVE;
import static org.smartregister.child.util.Constants.KEY.LAST_NAME;
import static org.smartregister.child.util.Constants.KEY.LOST_TO_FOLLOW_UP;

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
        {
            final String parentTableName = Utils.metadata().childRegister.motherTableName;
            String tableName = Utils.metadata().childRegister.tableName;

            final String motherFirstNameKey = Constants.KEY.MOTHER_FIRST_NAME;
            final String motherLastNameKey = Constants.KEY.MOTHER_LAST_NAME;

            final String startDateKey = START_DATE;
            final String endDateKey = END_DATE;

            String mainConditionString = "";
            for (Map.Entry<String, String> entry : editMap.entrySet()) {
                String key = entry.getKey();
                key = key.startsWith("mother_") ? parentTableName + "." + key : tableName + "." + key;
                String value = entry.getValue();
                if (!key.contains(startDateKey) && !key.contains(endDateKey) && !key.contains(ACTIVE) && !key
                        .contains(INACTIVE) && !key.contains(LOST_TO_FOLLOW_UP) && !key.contains(motherFirstNameKey) && !key
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
                    mainConditionString += " " + tableName + ".dob BETWEEN '" + editMap
                            .get(startDateKey) + "' AND '" + editMap.get(endDateKey) + "'";
                } else if (editMap.containsKey(startDateKey)) {
                    mainConditionString += " " + tableName + ".dob >= '" + editMap.get(startDateKey) + "'";

                } else if (editMap.containsKey(endDateKey)) {
                    mainConditionString += " " + tableName + ".dob <= '" + editMap.get(endDateKey) + "'";
                }
            } else {
                if (editMap.containsKey(startDateKey) && editMap.containsKey(endDateKey)) {
                    mainConditionString += " AND " + tableName + ".dob BETWEEN '" + editMap
                            .get(startDateKey) + "' AND '" + editMap.get(endDateKey) + "'";
                } else if (editMap.containsKey(startDateKey)) {
                    mainConditionString += " AND " + tableName + ".dob >= '" + editMap.get(startDateKey) + "'";

                } else if (editMap.containsKey(endDateKey)) {
                    mainConditionString += " AND " + tableName + ".dob <= '" + editMap.get(endDateKey) + "'";
                }
            }

            if (editMap.containsKey(motherFirstNameKey) && editMap.containsKey(motherLastNameKey)) {
                if (StringUtils.isBlank(mainConditionString)) {
                    mainConditionString += " " + parentTableName + "." + FIRST_NAME + " Like '%" + editMap
                            .get(motherFirstNameKey) + "%' AND " + parentTableName + "." + LAST_NAME + " Like '%" + editMap
                            .get(motherLastNameKey) + "%'";
                } else {
                    mainConditionString += " AND  (" + parentTableName + "." + FIRST_NAME + " Like '%" + editMap
                            .get(motherFirstNameKey) + "%' AND " + parentTableName + "." + LAST_NAME + " Like '%" + editMap
                            .get(motherLastNameKey) + "%' ) ";
                }
            } else if (editMap.containsKey(motherFirstNameKey) && !editMap.containsKey(motherLastNameKey)) {
                if (StringUtils.isBlank(mainConditionString)) {
                    mainConditionString += " " + parentTableName + "." + FIRST_NAME + " Like '%" + editMap
                            .get(motherFirstNameKey) + "%'";
                } else {
                    mainConditionString += " AND  (" + parentTableName + "." + FIRST_NAME + " Like '%" + editMap
                            .get(motherFirstNameKey) + "%') ";
                }
            } else if (!editMap.containsKey(motherFirstNameKey) && editMap.containsKey(motherLastNameKey)) {
                if (StringUtils.isBlank(mainConditionString)) {
                    mainConditionString += " " + parentTableName + "." + LAST_NAME + " Like '%" + editMap
                            .get(motherLastNameKey) + "%'";
                } else {
                    mainConditionString += " AND  (" + parentTableName + "." + LAST_NAME + " Like '%" + editMap
                            .get(motherLastNameKey) + "%' ) ";
                }
            }

            String statusConditionString = "";
            for (Map.Entry<String, String> entry : editMap.entrySet()) {
                String key = entry.getKey();
                key = key.startsWith("mother_") ? parentTableName + "." + key : tableName + "." + key;
                String value = entry.getValue();
                if (key.contains(ACTIVE) || key.contains(INACTIVE) || key.contains(LOST_TO_FOLLOW_UP)) {

                    if (StringUtils.isBlank(statusConditionString)) {
                        if (key.contains(ACTIVE) && !key.contains(INACTIVE)) {
                            statusConditionString += " ( ( " + tableName + "." + INACTIVE + " IS NULL OR " + tableName + "." + INACTIVE + " != '" + Boolean.TRUE
                                    .toString() + "' ) " +
                                    " AND ( " + tableName + "." + LOST_TO_FOLLOW_UP + " IS NULL OR " + tableName + "." + LOST_TO_FOLLOW_UP + " != '" + Boolean.TRUE
                                    .toString() + "' ) ) ";
                        } else {
                            statusConditionString += " " + key + " = '" + value + "'";
                        }
                    } else {
                        if (key.contains(ACTIVE) && !key.contains(INACTIVE)) {
                            statusConditionString += " OR ( ( " + tableName + "." + INACTIVE + " IS NULL OR " + tableName + "." + INACTIVE + " != '" + Boolean.TRUE
                                    .toString() + "' ) " +
                                    " AND ( " + tableName + "." + LOST_TO_FOLLOW_UP + " IS NULL OR " + tableName + "." + LOST_TO_FOLLOW_UP + " != '" + Boolean.TRUE
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

            return mainConditionString;

        }

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