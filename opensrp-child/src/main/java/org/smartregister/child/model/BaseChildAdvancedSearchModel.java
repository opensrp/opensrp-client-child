package org.smartregister.child.model;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

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
        convertDateToDesiredFormat(editMap);
        final String table = Utils.metadata().getRegisterQueryProvider().getDemographicTable();
        final String childDetailsTable = Utils.metadata().getRegisterQueryProvider().getChildDetailsTable();

        final String motherFirstNameKey = Constants.KEY.MOTHER_FIRST_NAME;
        final String motherFirstNameSelect = Constants.KEY.MOTHER + "." + Constants.KEY.FIRST_NAME;
        final String motherLastNameKey = Constants.KEY.MOTHER_LAST_NAME;
        final String motherLastNameSelect = Constants.KEY.MOTHER + "." + Constants.KEY.LAST_NAME;

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
                mainConditionString += motherFirstNameSelect + " Like '%" + editMap
                        .get(motherFirstNameKey) + "%' AND " + motherLastNameSelect + " Like '%" + editMap
                        .get(motherLastNameKey) + "%'";
            } else {
                mainConditionString += " AND  (" + motherFirstNameSelect + " Like '%" + editMap
                        .get(motherFirstNameKey) + "%' AND " + motherLastNameSelect + " Like '%" + editMap
                        .get(motherLastNameKey) + "%' ) ";
            }
        } else if (editMap.containsKey(motherFirstNameKey) && !editMap.containsKey(motherLastNameKey)) {
            if (StringUtils.isBlank(mainConditionString)) {
                mainConditionString += " " + motherFirstNameSelect + " Like '%" + editMap
                        .get(motherFirstNameKey) + "%'";
            } else {
                mainConditionString += " AND  (" + motherFirstNameSelect + " Like '%" + editMap
                        .get(motherFirstNameKey) + "%') ";
            }
        } else if (!editMap.containsKey(motherFirstNameKey) && editMap.containsKey(motherLastNameKey)) {
            if (StringUtils.isBlank(mainConditionString)) {
                mainConditionString += " " + motherLastNameSelect + " Like '%" + editMap
                        .get(motherLastNameKey) + "%'";
            } else {
                mainConditionString += " AND  (" + motherLastNameSelect + " Like '%" + editMap
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

                boolean isActive = key.contains(Constants.CHILD_STATUS.ACTIVE) && !key.contains(Constants.CHILD_STATUS.INACTIVE);
                if (StringUtils.isBlank(statusConditionString)) {
                    if (isActive) {
                        statusConditionString += " ( ( " + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + " IS NULL OR " + childDetailsTable + "." + Constants.CHILD_STATUS.INACTIVE + " != '" + Boolean.TRUE
                                .toString() + "' ) " +
                                " AND ( " + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " IS NULL OR " + childDetailsTable + "." + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " != '" + Boolean.TRUE
                                .toString() + "' ) ) ";
                    } else {
                        statusConditionString += " " + key + " = '" + value + "'";
                    }
                } else {
                    if (isActive) {
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

        return String.format("%s AND (%s is null AND %s == '0')", mainConditionString,
                Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.DATE_REMOVED,
                Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.IS_CLOSED);
    }

    private void convertDateToDesiredFormat(Map<String, String> editMap) {
        try {
            if (editMap.containsKey(START_DATE) && editMap.containsKey(END_DATE)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault().toString().startsWith("ar") ? Locale.ENGLISH : Locale.getDefault()
                );
                SimpleDateFormat desiredDateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault().toString().startsWith("ar") ? Locale.ENGLISH : Locale.getDefault()
                );
                Date parsedStartDate = simpleDateFormat.parse(editMap.get(START_DATE));
                Date parsedEndDate = simpleDateFormat.parse(editMap.get(END_DATE));
                editMap.put(START_DATE, desiredDateFormat.format(parsedStartDate));
                editMap.put(END_DATE, desiredDateFormat.format(parsedEndDate));
            }
        } catch (ParseException e) {
            Timber.e(e, "Error converting dates to right format");
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

    @Override
    public List<ChildMotherDetailModel> getChildMotherDetailModels(Response<String> response) {
        return ChildJsonFormUtils.processReturnedAdvanceSearchResults(response);
    }

}
