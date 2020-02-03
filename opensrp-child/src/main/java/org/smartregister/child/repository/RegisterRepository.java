package org.smartregister.child.repository;

import org.smartregister.child.util.DBConstants;

public class RegisterRepository {

    public String mainRegisterQuery() {
        return "select " + getDemographicTable() + ".id as _id, " + getDemographicTable() + ".relationalid, " + getDemographicTable() + ".zeir_id, child_details.relational_id, " + getDemographicTable() +
                ".gender, " + getDemographicTable() + ".base_entity_id, " + getDemographicTable() + ".first_name, " + getDemographicTable() + ".last_name, mother.first_name as mother_first_name, " +
                "mother.last_name as mother_last_name," + getDemographicTable() + ".dob, mother.dob as mother_dob, mother_details.nrc_number as mother_nrc_number, mother_details.father_name, mother_details.epi_card_number," +
                "" + getDemographicTable() + ".client_reg_date,child_details.pmtct_status, " + getDemographicTable() + ".last_interacted_with,\n" +
                " child_details.inactive, child_details.lost_to_follow_up, child_details.contact_phone_number from " + getChildDetailsTable() + " child_details\n" +
                "join " + getMotherDetailsTable() + " mother_details on child_details.relational_id = mother_details.base_entity_id join " + getDemographicTable() + " on " +
                "" + getDemographicTable() + ".base_entity_id=child_details.base_entity_id join " + getDemographicTable() + " mother\n" +
                "on mother.base_entity_id = mother_details.base_entity_id";
    }

    public String getChildDetailsTable() {
        return DBConstants.RegisterTable.CHILD_DETAILS;
    }

    public String getMotherDetailsTable() {
        return DBConstants.RegisterTable.MOTHER_DETAILS;
    }

    public String getDemographicTable() {
        return DBConstants.RegisterTable.CLIENT;
    }


}
