package org.smartregister.child.model;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.util.ConfigHelper;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class BaseChildRegisterFragmentModel implements ChildRegisterFragmentContract.Model {

    @Override
    public RegisterConfiguration defaultRegisterConfiguration() {
        return ConfigHelper.defaultRegisterConfiguration(Utils.context().applicationContext());
    }

    @Override
    public ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier) {
        return ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().getViewConfiguration(viewConfigurationIdentifier);
    }

    @Override
    public Set<View> getRegisterActiveColumns(String viewConfigurationIdentifier) {
        return ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().getRegisterActiveColumns(viewConfigurationIdentifier);
    }

    @Override
    public String countSelect(String tableName, String mainCondition, String parentTableName) {
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.SelectInitiateMainTableCounts(tableName);
        countQueryBuilder.customJoin("LEFT JOIN " + parentTableName + " ON  " + tableName + ".relational_id =  " + parentTableName + ".id");
        return countQueryBuilder.mainCondition(mainCondition);
    }

    @Override
    public String mainSelect(String tableName, String mainCondition, String parentTableName) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        queryBUilder.SelectInitiateMainTable(tableName, mainColumns(tableName, parentTableName));
        queryBUilder.customJoin("LEFT JOIN " + parentTableName + " ON  " + tableName + ".relational_id =  " + parentTableName + ".id");
        return queryBUilder.mainCondition(mainCondition);
    }

    protected String[] mainColumns(String tableName, String parentTableName) {
        String[] columns = new String[]{

                tableName + "." + DBConstants.KEY.RELATIONALID,
                tableName + "." + DBConstants.KEY.DETAILS,
                tableName + "." + DBConstants.KEY.ZEIR_ID,
                tableName + "." + DBConstants.KEY.RELATIONAL_ID,
                tableName + "." + DBConstants.KEY.FIRST_NAME,
                tableName + "." + DBConstants.KEY.LAST_NAME,
                tableName + "." + AllConstants.ChildRegistrationFields.GENDER,
                tableName + "." + DBConstants.KEY.BASE_ENTITY_ID,
                parentTableName + "." + DBConstants.KEY.FIRST_NAME + " as mother_first_name",
                parentTableName + "." + DBConstants.KEY.LAST_NAME + " as mother_last_name",
                parentTableName + "." + DBConstants.KEY.DOB + " as mother_dob",
                parentTableName + "." + DBConstants.KEY.NRC_NUMBER + " as mother_nrc_number",
                tableName + "." + DBConstants.KEY.FATHER_FIRST_NAME,
                tableName + "." + DBConstants.KEY.DOB,
                tableName + "." + DBConstants.KEY.EPI_CARD_NUMBER,
                tableName + "." + DBConstants.KEY.CONTACT_PHONE_NUMBER,
                tableName + "." + DBConstants.KEY.PMTCT_STATUS,
                tableName + "." + DBConstants.KEY.PROVIDER_UC,
                tableName + "." + DBConstants.KEY.PROVIDER_TOWN,
                tableName + "." + DBConstants.KEY.PROVIDER_ID,
                tableName + "." + DBConstants.KEY.PROVIDER_LOCATION_ID,
                tableName + "." + DBConstants.KEY.CLIENT_REG_DATE,
                tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                tableName + "." + DBConstants.KEY.INACTIVE,
                tableName + "." + DBConstants.KEY.NFC_CARD_IDENTIFIER,
        };
        return columns;
    }

    @Override
    public String getFilterText(List<Field> list, String filterTitle) {
        List<Field> filterList = list;
        if (filterList == null) {
            filterList = new ArrayList<>();
        }

        String filter = filterTitle;
        if (filter == null) {
            filter = "";
        }
        return "<font color=#727272>" + filter + "</font> <font color=#f0ab41>(" + filterList.size() + ")</font>";
    }

    @Override
    public String getSortText(Field sortField) {
        String sortText = "";
        if (sortField != null) {
            if (StringUtils.isNotBlank(sortField.getDisplayName())) {
                sortText = "(Sort: " + sortField.getDisplayName() + ")";
            } else if (StringUtils.isNotBlank(sortField.getDbAlias())) {
                sortText = "(Sort: " + sortField.getDbAlias() + ")";
            }
        }
        return sortText;
    }

    @Override
    public Map<String, String> createEditMap(String opensrpID) {
        return null;
    }

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {
        String[] columns = new String[]{"_id", "relationalid", DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME, DBConstants.KEY.DOB, DBConstants.KEY.ZEIR_ID};
        AdvancedMatrixCursor matrixCursor = new AdvancedMatrixCursor(columns);

        if (response == null || response.isFailure() || StringUtils.isBlank(response.payload())) {
            return matrixCursor;
        }

        JSONArray jsonArray = getJsonArray(response);
        if (jsonArray != null) {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject client = getJsonObject(jsonArray, i);
                String entityId;
                String firstName;
                String lastName;
                String dob;
                String ancId;
                String phoneNumber;
                String altContactName;
                if (client == null) {
                    continue;
                }

                // Skip deceased children
                if (StringUtils.isNotBlank(getJsonString(client, "deathdate"))) {
                    continue;
                }

                entityId = getJsonString(client, "baseEntityId");
                firstName = getJsonString(client, "firstName");
                lastName = getJsonString(client, "lastName");

                dob = getJsonString(client, "birthdate");
                if (StringUtils.isNotBlank(dob) && StringUtils.isNumeric(dob)) {
                    try {
                        Long dobLong = Long.valueOf(dob);
                        Date date = new Date(dobLong);
                        dob = DateUtil.yyyyMMddTHHmmssSSSZ.format(date);
                    } catch (Exception e) {
                        Log.e(getClass().getName(), e.toString(), e);
                    }
                }

                ancId = getJsonString(getJsonObject(client, "identifiers"), DBConstants.KEY.ZEIR_ID);
                if (StringUtils.isNotBlank(ancId)) {
                    ancId = ancId.replace("-", "");
                }

                phoneNumber = getJsonString(getJsonObject(client, "attributes"), "phone_number");

                altContactName = getJsonString(getJsonObject(client, "attributes"), "alt_name");


                matrixCursor.addRow(new Object[]{entityId, null, firstName, lastName, dob, ancId});
            }
        }
        return matrixCursor;
    }

    private String getJsonString(JSONObject jsonObject, String field) {
        try {
            if (jsonObject != null && jsonObject.has(field)) {
                String string = jsonObject.getString(field);
                if (StringUtils.isBlank(string)) {
                    return "";
                } else {
                    return string;
                }
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }
        return "";

    }

    private JSONObject getJsonObject(JSONObject jsonObject, String field) {
        try {
            if (jsonObject != null && jsonObject.has(field)) {
                return jsonObject.getJSONObject(field);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }
        return null;

    }

    private JSONObject getJsonObject(JSONArray jsonArray, int position) {
        try {
            if (jsonArray != null && jsonArray.length() > 0) {
                return jsonArray.getJSONObject(position);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }
        return null;

    }

    @Override
    public JSONArray getJsonArray(Response<String> response) {
        try {
            if (response.status().equals(ResponseStatus.success)) {
                return new JSONArray(response.payload());
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "", e);
        }
        return null;
    }
}
