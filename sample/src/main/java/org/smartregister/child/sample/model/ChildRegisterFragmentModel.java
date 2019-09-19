package org.smartregister.child.sample.model;

import org.smartregister.AllConstants;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildRegisterFragmentModel;
import org.smartregister.child.sample.util.DBConstants;
import org.smartregister.domain.Response;

/**
 * Created by ndegwamartin on 2019-05-27.
 */
public class ChildRegisterFragmentModel extends BaseChildRegisterFragmentModel {

    @Override
    public AdvancedMatrixCursor createMatrixCursor(Response<String> response) {
        //Just overriddenn
        return null;
    }

    @Override
    protected String[] mainColumns(String tableName, String parentTableName) {
        String[] columns = new String[] {

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
                parentTableName + "." + DBConstants.KEY.FATHER_NAME,
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
                tableName + "." + DBConstants.KEY.LOST_TO_FOLLOW_UP,
        };
        return columns;
    }
}
