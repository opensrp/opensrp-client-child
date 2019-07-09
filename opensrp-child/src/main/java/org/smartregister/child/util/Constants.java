package org.smartregister.child.util;

import org.smartregister.AllConstants;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class Constants extends AllConstants {

    public static final String SQLITE_DATE_TIME_FORMAT = "yyyy-MM-dd";

    public enum RECORD_ACTION {WEIGHT, VACCINATION, NONE}

    public static class JSON_FORM_KEY {
        public static final String OPTIONS = "options";
        public static final String DEATH_DATE_APPROX = "deathdateApprox";
        public static final String UNIQUE_ID = "unique_id";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String DOB = "dob";
        public static final String DOB_UNKNOWN = "dob_unknown";
        public static final String AGE = "age";

    }

    public static class JSON_FORM_EXTRA {
        public static final String NEXT = "next";

    }

    public static class OPENMRS {
        public static final String ENTITY = "openmrs_entity";
        public static final String ENTITY_ID = "openmrs_entity_id";
    }

    public static final class KEY {
        public static final String KEY = "key";
        public static final String VALUE = "value";
        public static final String PHOTO = "photo";
        public static final String VACCINE = "vaccine";
        public static final String ALERT = "alert";
        public static final String WEEK = "week";
        public static final String MONTH = "month";
        public static final String DATE = "date";
        public static final String CHILD = "child";
        public static final String PMTCT_STATUS = "pmtct_status";
        public static final String BIRTH_WEIGHT = "Birth_Weight";
        public static final String LOOK_UP = "look_up";
        public static final String ENTITY_ID = "entity_id";
        public static final String MOTHER = "mother";
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
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";
        public static final String MOTHER_FIRST_NAME = "mother_first_name";
        public static final String MOTHER_LAST_NAME = "mother_last_name";
        public static final String EPI_CARD_NUMBER = "epi_card_number";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";
        public static final String DATE_REMOVED = "date_removed";
        public static final String NFC_CARD_IDENTIFIER = "nfc_card_identifier";
        public static final String ID_LOWER_CASE = "_id";
        public static final String RELATIONALID = "relationalid";
    }

    public static final class INTENT_KEY {
        public static final String JSON = "json";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String RECORD_ACTION = "record_action";
        public static final String EXTRA_CHILD_DETAILS = "child_details";
        public static final String EXTRA_REGISTER_CLICKABLES = "register_clickables";
        public static final String LOCATION_ID = "location_id";
        public static final String NEXT_APPOINTMENT_DATE = "next_appointment_date";
    }

    public static class ENTITY {
        public static final String PERSON = "person";
    }

    public static class BOOLEAN_INT {
        public static final int TRUE = 1;
    }

    public static class FormActivity {
        public static final String EnableOnCloseDialog = "EnableOnCloseDialog";
    }

    public static final class GENDER {

        public static final String MALE = "male";
        public static final String FEMALE = "female";
    }

    //Temporary to move implementation
    public static final class EventType {

        public static final String AEFI = "AEFI";
        public static final String BITRH_REGISTRATION = "Birth Registration";
        public static final String UPDATE_BITRH_REGISTRATION = "Update Birth Registration";
        public static final String NEW_WOMAN_REGISTRATION = "New Woman Registration";
        public static final String DEATH = "Death";
        public static final String OUT_OF_CATCHMENT_SERVICE = "Out of Catchment Service";
        public static final String VACCINATION = "Vaccination";
    }

    public static final class CONCEPT {
        public final static String VACCINE_DATE = "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    }

    public static final class CHILD_STATUS {
        public static final String ACTIVE = "active";
        public static final String INACTIVE = "inactive";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";

    }

}
