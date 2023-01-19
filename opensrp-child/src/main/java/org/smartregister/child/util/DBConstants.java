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
        public static final String DOB = "dob";//Date Of Birth
        public static final String DOD = "dod";
        public static final String GENDER = "gender";
        public static final String ZEIR_ID = "zeir_id";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String DATE_REMOVED = "date_removed";
        public static final String NRC_NUMBER = "nrc_number";
        public static final String RELATIONALID = "relationalid";
        public static final String EPI_CARD_NUMBER = "epi_card_number";
        public static final String MOTHER_GUARDIAN_PHONE_NUMBER = "mother_guardian_phone_number";
        public static final String INACTIVE = "inactive";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";
        public static final String MOTHER_FIRST_NAME = "mother_first_name";
        public static final String MOTHER_LAST_NAME = "mother_last_name";
    }

    public interface RegisterTable {
        String CHILD_DETAILS = "ec_child_details";
        String MOTHER_DETAILS = "ec_mother_details";
        String CLIENT = "ec_client";
        String FATHER_DETAILS = "ec_father_details";
    }
}
