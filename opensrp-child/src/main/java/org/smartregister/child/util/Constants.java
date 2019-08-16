package org.smartregister.child.util;

import org.smartregister.AllConstants;

import java.text.SimpleDateFormat;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class Constants extends AllConstants {

    public static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN);
    public static final String SQLITE_DATE_TIME_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_STRING = "1970-1-1";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String BALANCE = "Balance";
    public static final String NEW_BALANCE = "New balance";
    public static final String NEW_BALANCE_ = "New balance:";
    public static final String STEP_1 = "step1";
    public static final String TITLE = "title";
    public static final String FIELDS = "fields";
    public static final String IS_VACCINE_GROUP = "is_vaccine_group";
    public static final String VALUE = "value";
    public static final String WEIGHT_KG = "Weight_Kg";
    public static final String DATE_BIRTH = "Date_Birth";
    public static final String BIRTH_WEIGHT = "Birth_Weight";
    public static final String BIRTH_HEIGHT = "Birth_Height";
    public static final String SEX = "Sex";
    public static final String DATE_REACTION = "Date_Reaction";
    public static final String ENTITY_ID = "entityId";
    public static final String FIRST_NAME = "firstName";
    public static final String MIDDLE_NAME = "middleName";
    public static final String LAST_NAME = "lastName";
    public static final String BIND_TYPE = "bindType";
    public static final String NO_OF_EVENTS = "no_of_events";
    public static final String EVENTS = "events";
    public static final String CLIENTS = "clients";
    public static final String HOME_FACILITY = "Home_Facility";
    public static final String CARD_ID = "CARD_ID";

    public enum RECORD_ACTION {GROWTH, VACCINATION, NONE}

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
        public static final String BIRTH_HEIGHT = "Birth_Height";
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
        public static final String PROVIDER_ID = "provider_id";
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

    public static class PROPERTY {
        public static final String NOTIFICATIONS_BCG_ENABLED = "notifications.bcg.enabled";
        public static final String POPUP_WEIGHT_ENABLED = "popup.weight.enabled";

        public static final String FEATURE_IMAGES_ENABLED = "feature.images.enabled";
        public static final String FEATURE_NFC_CARD_ENABLED = "feature.nfc.card.enabled";

        public static final String HOME_NEXT_VISIT_DATE_ENABLED = "home.next.visit.date.enabled";
        public static final String HOME_RECORD_WEIGHT_ENABLED = "home.record.weight.enabled";
        public static final String HOME_TOOLBAR_SCAN_CARD_ENABLED = "home.toolbar.scan.card.enabled";
        public static final String HOME_TOOLBAR_SCAN_QR_ENABLED = "home.toolbar.scan.qr.enabled";

        public static final String FEATURE_BOTTOM_NAVIGATION_ENABLED = "feature.bottom.navigation.enabled";
        public static final String FEATURE_SCAN_QR_ENABLED = "feature.scan.qr.enabled";

        public static final String DETAILS_SIDE_NAVIGATION_ENABLED = "details.side.navigation.enabled";


    }

}
