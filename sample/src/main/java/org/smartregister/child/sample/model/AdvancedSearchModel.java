package org.smartregister.child.sample.model;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.domain.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 2019-05-27.
 */
public class AdvancedSearchModel extends BaseChildAdvancedSearchModel {

    private static final String MOTHER_BASE_ENTITY_ID = "mother_base_entity_id";
    private static final String MOTHER_GUARDIAN_FIRST_NAME = "mother_first_name";
    private static final String MOTHER_GUARDIAN_LAST_NAME = "mother_last_name";

    @Override
    public Map<String, String> createEditMap(Map<String, String> editMap) {
        return editMap;
    }

    @Override
    public String[] getColumns() {
        return new String[0];
    }

    protected String[] mainColumns() {
        return Utils.metadata().getRegisterQueryProvider().mainColumns();
    }

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {

        String[] columns = new String[]{
                Constants.KEY.ID_LOWER_CASE,
                Constants.KEY.RELATIONALID,
                Constants.KEY.FIRST_NAME,
                Constants.KEY.MIDDLE_NAME,
                Constants.KEY.LAST_NAME,
                Constants.KEY.GENDER,
                Constants.KEY.DOB,
                Constants.KEY.ZEIR_ID,
                Constants.KEY.EPI_CARD_NUMBER,
                Constants.KEY.NFC_CARD_IDENTIFIER,
                MOTHER_BASE_ENTITY_ID,
                MOTHER_GUARDIAN_FIRST_NAME,
                MOTHER_GUARDIAN_LAST_NAME,
                org.smartregister.child.util.Constants.CHILD_STATUS.INACTIVE,
                org.smartregister.child.util.Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP
        };
        AdvancedMatrixCursor matrixCursor = new AdvancedMatrixCursor(columns);

        if (response == null || response.isFailure() || StringUtils.isBlank(response.payload())) {
            return matrixCursor;
        }

        JSONArray jsonArray = getJsonArray(response);
        if (jsonArray != null) {
            List<JSONObject> jsonValues = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonValues.add(getJsonObject(jsonArray, i));
            }

            Collections.sort(jsonValues, (lhs, rhs) -> {

                if (!lhs.has(Constants.ENTITY.CHILD) || !rhs.has(Constants.ENTITY.CHILD)) {
                    return 0;
                }

                JSONObject lhsChild = getJsonObject(lhs, Constants.ENTITY.CHILD);
                JSONObject rhsChild = getJsonObject(rhs, Constants.ENTITY.CHILD);

                String lhsZeirId = getJsonString(getJsonObject(lhsChild, Constants.Client.IDENTIFIERS), Constants.KEY.ZEIR_ID.toUpperCase());
                String rhsZeirId = getJsonString(getJsonObject(rhsChild, Constants.Client.IDENTIFIERS), Constants.KEY.ZEIR_ID.toUpperCase());

                return lhsZeirId.compareTo(rhsZeirId);

            });

            for (JSONObject client : jsonValues) {
                String entityId = "";
                String firstName = "";
                String middleName = "";
                String lastName = "";
                String gender = "";
                String dob = "";
                String zeirId = "";
                String epiCardNumber = "";
                String inactive = "";
                String lostToFollowUp = "";
                String nfcCardId = "";

                if (client == null) {
                    continue;
                }

                if (client.has(Constants.ENTITY.CHILD)) {
                    JSONObject child = getJsonObject(client, Constants.ENTITY.CHILD);

                    // Skip deceased children
                    if (StringUtils.isNotBlank(getJsonString(child, Constants.Client.DEATHDATE))) {
                        continue;
                    }

                    entityId = getJsonString(child, Constants.Client.BASE_ENTITY_ID);
                    firstName = getJsonString(child, Constants.FIRST_NAME);
                    middleName = getJsonString(child, Constants.MIDDLE_NAME);
                    lastName = getJsonString(child, Constants.LAST_NAME);

                    gender = getJsonString(child, Constants.KEY.GENDER);
                    dob = getJsonString(child, Constants.Client.BIRTHDATE);
                    if (StringUtils.isNotBlank(dob) && StringUtils.isNumeric(dob)) {
                        try {
                            long dobLong = Long.valueOf(dob);
                            Date date = new Date(dobLong);
                            dob = DateUtil.yyyyMMddTHHmmssSSSZ.format(date);
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    } else if (dob.startsWith("{")) {
                        Date date = processJsonFormatLocalDate(dob);
                        if (date != null) {
                            dob = DateUtil.yyyyMMddTHHmmssSSSZ.format(date);
                        }
                    }

                    zeirId = getJsonString(getJsonObject(child, Constants.Client.IDENTIFIERS), Constants.KEY.ZEIR_ID.toUpperCase());
                    if (StringUtils.isNotBlank(zeirId)) {
                        zeirId = zeirId.replace("-", "");
                    }

                    epiCardNumber = getJsonString(getJsonObject(child, Constants.Client.ATTRIBUTES), Constants.CHILD_REGISTER_CARD_NUMBER);

                    inactive = getJsonString(getJsonObject(child, Constants.Client.ATTRIBUTES), Constants.CHILD_STATUS.INACTIVE);
                    lostToFollowUp = getJsonString(getJsonObject(child, Constants.Client.ATTRIBUTES), Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP);
                    nfcCardId = getJsonString(getJsonObject(child, Constants.Client.ATTRIBUTES), Constants.KEY.NFC_CARD_IDENTIFIER);
                }

                String motherBaseEntityId = "";
                String motherFirstName = "";
                String motherLastName = "";

                if (client.has(Constants.ENTITY.MOTHER)) {
                    JSONObject mother = getJsonObject(client, Constants.ENTITY.MOTHER);
                    motherFirstName = getJsonString(mother, Constants.FIRST_NAME);
                    motherLastName = getJsonString(mother, Constants.LAST_NAME);
                    motherBaseEntityId = getJsonString(mother, Constants.Client.BASE_ENTITY_ID);
                }

                matrixCursor.addRow(new Object[]{
                        entityId,
                        null,
                        firstName,
                        middleName,
                        lastName,
                        gender,
                        dob,
                        zeirId,
                        epiCardNumber,
                        nfcCardId,
                        motherBaseEntityId,
                        motherFirstName,
                        motherLastName,
                        motherLastName,
                        inactive,
                        lostToFollowUp
                });
            }

            return matrixCursor;
        } else {
            return matrixCursor;
        }
    }

    private Date processJsonFormatLocalDate(String dateString) {
        Date date = null;
        try {
            JSONObject jsonObject = new JSONObject(dateString);
            date = (new LocalDateTime(
                    jsonObject.getInt(Constants.LOCAL_DATE_TIME.YEAR),
                    jsonObject.getInt(Constants.LOCAL_DATE_TIME.MONTH_OF_YEAR),
                    jsonObject.getInt(Constants.LOCAL_DATE_TIME.DAY_OF_MONTH),
                    jsonObject.getInt(Constants.LOCAL_DATE_TIME.HOUR_OF_DAY),
                    jsonObject.getInt(Constants.LOCAL_DATE_TIME.MINUTE_OF_HOUR),
                    jsonObject.getInt(Constants.LOCAL_DATE_TIME.SECOND_OF_MINUTE)
            )).toDate();
        } catch (Exception e) {
            Timber.e(e);
        }
        return date;
    }
}
