package org.smartregister.child.sample;

import org.smartregister.child.provider.RegisterQueryProvider;
import org.smartregister.child.util.Constants;

/**
 * Created by ndegwamartin on 12/06/2020.
 */
public class SampleAppRegisterQueryProvider extends RegisterQueryProvider {

    @Override
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
                "mother." + Constants.KEY.DOB_UNKNOWN + " as " + Constants.KEY.MOTHER_DOB_UNKNOWN,
                "mother" + "." + Constants.KEY.DOB + " as mother_dob",
                getMotherDetailsTable() + "." + Constants.KEY.NRC_NUMBER + " as mother_nrc_number",
                getMotherDetailsTable() + "." + Constants.KEY.FATHER_NAME,
                getMotherDetailsTable() + "." + Constants.KEY.EPI_CARD_NUMBER,
                getDemographicTable() + "." + Constants.KEY.CLIENT_REG_DATE,
                getChildDetailsTable() + "." + Constants.KEY.PMTCT_STATUS,
                getDemographicTable() + "." + Constants.KEY.LAST_INTERACTED_WITH,
                getChildDetailsTable() + "." + Constants.CHILD_STATUS.INACTIVE,
                getChildDetailsTable() + "." + Constants.KEY.LOST_TO_FOLLOW_UP,
                getChildDetailsTable() + "." + Constants.KEY.BIRTH_WEIGHT,
                getChildDetailsTable() + "." + Constants.KEY.BIRTH_HEIGHT,
                getChildDetailsTable() + "." + Constants.KEY.FIRST_HEALTH_FACILITY_CONTACT,
                getChildDetailsTable() + "." + Constants.KEY.MOTHER_GUARDIAN_PHONE_NUMBER,
                getDemographicTable() + "." + "address1",
        };
    }
}
