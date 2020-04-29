package org.smartregister.child.util;

/**
 * Created by ndegwamartin on 26/02/2019.
 */
public class DBConstants {
    public static final class KEY {
        public static final String ID_LOWER_CASE = "_id";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String RELATIONAL_ID = "relational_id";
        public static final String DOB = "dob";//Date Of Birth
        public static final String DOD = "dod";
        public static final String GENDER = "gender";
        public static final String UNIQUE_ID = "unique_id";
        public static final String ZEIR_ID = "zeir_id";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String CONTACT_STATUS = "contact_status";
        public static final String VILLAGE_TOWN = "village_town";
        public static final String STREET = "street";
        public static final String LANDMARK = "landmark";
        public static final String GPS = "gps";
        public static final String DATE_REMOVED = "date_removed";
        public static final String FATHER_NAME = "father_name";
        public static final String NRC_NUMBER = "nrc_number";
        public static final String DETAILS = "details";
        public static final String RELATIONALID = "relationalid";
        public static final String EPI_CARD_NUMBER = "epi_card_number";
        public static final String MOTHER_GUARDIAN_PHONE_NUMBER = "mother_guardian_phone_number";
        public static final String PMTCT_STATUS = "pmtct_status";
        public static final String PROVIDER_UC = "provider_uc";
        public static final String PROVIDER_TOWN = "provider_town";
        public static final String PROVIDER_ID = "provider_id";
        public static final String PROVIDER_LOCATION_ID = "provider_location_id";
        public static final String CLIENT_REG_DATE = "client_reg_date";
        public static final String INACTIVE = "inactive";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";
        public static final String OBJECT_ID = "object_id";
        public static final String OBJECT_RELATIONAL_ID = "object_relational_id";
        public static final String ENTITY_TYPE = "entity_type";
        public static final String MOTHER_FIRST_NAME = "mother_first_name";
        public static final String MOTHER_LAST_NAME = "mother_last_name";
    }

    public interface RegisterTable {
        String CHILD_DETAILS = "ec_child_details";
        String MOTHER_DETAILS = "ec_mother_details";
        String CLIENT = "ec_client";
    }
}
