package org.smartregister.child.util;

import org.smartregister.AllConstants;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public final class Constants extends AllConstants {

    public static final String SQLITE_DATE_TIME_FORMAT = "yyyy-MM-dd";
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
    public static final String CHILD_REGISTER_CARD_NUMBER = "Child_Register_Card_Number";
    public static final String FALSE = "false";
    public static final String RESIDENTIAL_AREA = "Residential_Area";
    public static final String RESIDENTIAL_AREA_OTHER = "Residential_Area_Other";
    public static final String RESIDENTIAL_ADDRESS = "Residential_Address";
    public static final String PREFERRED_LANGUAGE = "Preferred_Language";
    public static final String MOTHER_LOOKUP_SHOW_RESULTS_DEFAULT_DURATION = "30000";
    public static final String MOTHER_LOOKUP_UNDO_DEFAULT_DURATION = "10000";
    public static final String SHOW_BCG_SCAR = "show_bcg_scar";
    public static final String SHOW_BCG2_REMINDER = "show_bcg2_reminder";
    public static final String DISABLE_CHILD_HEIGHT_METRIC = "disable_child_height_metric";
    public static final String CLIENT_RELATIONSHIP = "client_relationship";
    public static final String ENCOUNTER = "encounter";
    public static final String ADVANCED_DATA_CAPTURE_STRATEGY_PREFIX = "ADCS_";

    public enum RECORD_ACTION {GROWTH, VACCINATION, NONE}

    public static class JSON_FORM_KEY {
        public static final String OPTIONS = "options";
        public static final String DEATH_DATE_APPROX = "deathdateApprox";
        public static final String UNIQUE_ID = "unique_id";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String DATE_BIRTH = "Date_Birth";
        public static final String DATE_DEATH = "Date_of_Death";
        public static final String DATE_BIRTH_UNKNOWN = "Date_Birth_Unknown";
        public static final String AGE = "age";
        public static final String MOTHER_GUARDIAN_DATE_BIRTH = "Mother_Guardian_Date_Birth";
        public static final String MOTHER_GUARDIAN_DATE_BIRTH_UNKNOWN = "Mother_Guardian_Date_Birth_Unknown";
        public static final String MOTHER_GUARDIAN_AGE = "Mother_Guardian_Age";
        public static final String HIERARCHY = "hierarchy";
        public static final String SELECTABLE = "selectable";
        public static final String SUB_TYPE = "sub_type";
        public static final String LOCATION_SUB_TYPE = "location";
        public static final String VALUE_FIELD = "value_field";
        public static final String RELATIONSHIPS = "relationships";
        public static final String SERVVICES = "services";
        public static final String EXCLUSION_KEYS = "exclusion_keys";
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
        public static final String BIRTH_TETANUS_PROTECTION = "Birth_Tetanus_Protection";
        public static final String LOOK_UP = "look_up";
        public static final String ENTITY_ID = "entity_id";
        public static final String MOTHER = "mother";
        public static final String FATHER = "father";
        public static final String FIRST_NAME = "first_name";
        public static final String MIDDLE_NAME = "middle_name";
        public static final String LAST_NAME = "last_name";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String RELATIONAL_ID = "relational_id";
        public static final String DOB = "dob";//Date Of Birth
        public static final String DOD = "dod";
        public static final String GENDER = "gender";
        public static final String ZEIR_ID = "zeir_id";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String MOTHER_FIRST_NAME = "mother_first_name";
        public static final String MOTHER_LAST_NAME = "mother_last_name";
        public static final String DOB_UNKNOWN = "dob_unknown";
        public static final String MOTHER_DOB_UNKNOWN = "mother_dob_unknown";
        public static final String MOTHER_BASE_ENTITY_ID = "mother_base_entity_id";
        public static final String FATHER_BASE_ENTITY_ID = "father_base_entity_id";
        public static final String EPI_CARD_NUMBER = "epi_card_number";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";
        public static final String DATE_REMOVED = "date_removed";
        public static final String NFC_CARD_IDENTIFIER = "nfc_card_identifier";
        public static final String NFC_CARDS_ARCHIVE = "nfc_cards_archive";
        public static final String NFC_CARD_BLACKLISTED = "nfc_card_blacklisted";
        public static final String NFC_CARD_BLACKLISTED_DATE = "nfc_card_blacklisted_date";
        public static final String ID_LOWER_CASE = "_id";
        public static final String RELATIONALID = "relationalid";
        public static final String MOTHER_GUARDIAN_PHONE_NUMBER = "mother_guardian_phone_number";
        public static final String MOTHER_GUARDIAN_NUMBER = "mother_guardian_number";
        public static final String ID = "id";
        public static final String NRC_NUMBER = "nrc_number";
        public static final String FATHER_NAME = "father_name";
        public static final String CLIENT_REG_DATE = "client_reg_date";
        public static final String HAS_PROFILE_IMAGE = "has_profile_image";
        public static final String MOTHER_DOB = "mother_dob";
        public static final String MOTHER_NRC_NUMBER = "mother_nrc_number";
        public static final String FIRST_HEALTH_FACILITY_CONTACT = "first_health_facility_contact";
        public static final String FATHER_RELATIONAL_ID = "father_relational_id";
        public static final String CHILD_HIV_STATUS = "child_hiv_status";
        public static final String CHILD_TREATMENT = "child_treatment";
        public static final String IS_CLOSED = "is_closed";
        public static final String BIRTH_DATE = "birth_date";
        public static final String MOTHER_PHONE_NUMBER = "mother_phone_number";
        public static final String IS_REMOTE_CLIENT = "is_remote_client";
        public static final String OPENSRP_ID = "opensrp_id";
        public static final String OA_SERVICE_DATE = "OA_Service_Date";
        public static final String WEIGHT_KG = "Weight_Kg";
        public static final String PRIVATE_SECTOR_VACCINE = "private_sector_vaccine";
        public static final String VACCINE_DATE = "vaccine_date";
        public static final String DYNAMIC_FIELD = "dynamic_field";
        public static final String SELECTED_VACCINES = "selected_vaccines";
        public static final String SELECTED_VACCINES_COUNTER = "selected_vaccines_counter";
        public static final String CONCEPT = "concept";
        public static final String TEXT = "text";
        public static final String CHILD_REGISTER_CARD_NUMBER = "child_register_card_number";
        public static final String STATUS = "status";
        public static final String DONE = "done";

        public static final String RECURRING_SERVICE_TYPES = "recurring_service_types";
        public static final String BOOSTER_VACCINE = "booster_vaccine";
        public static final String BCG_SCAR = "BCG: scar";
        public static final String DETAILS = "details";
        public static final String SERVICE_DATE = "service_date";

        public static final String IS_CHILD_DATA_ON_DEVICE = "is_child_data_on_device";
        public static final String NFC_CARD_LAST_UPDATED_TIMESTAMP = "nfc_card_last_updated_timestamp";
        public static final String NFC_LAST_PROCESSED_TIMESTAMP = "nfc_last_processed_timestamp";
    }

    public static final class INTENT_KEY {
        public static final String JSON = "json";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String EXTRA_CHILD_DETAILS = "child_details";
        public static final String EXTRA_REGISTER_CLICKABLES = "register_clickables";
        public static final String LOCATION_ID = "location_id";
        public static final String PROVIDER_ID = "provider_id";
        public static final String NEXT_APPOINTMENT_DATE = "next_appointment_date";
    }

    public static class OPENMRS_ENTITY {
        public static final String PERSON = "person";
    }

    public interface Tables {
        String EC_DYNAMIC_VACCINES = "ec_dynamic_vaccines";
        String EC_BOOSTER_VACCINES = "ec_booster_vaccines";
    }

    public static class ENTITY {

        public static final String CHILD = "child";
        public static final String MOTHER = "mother";
        public static final String FATHER = "father";
    }

    public static class BOOLEAN_INT {
        public static final int TRUE = 1;
    }

    public interface BOOLEAN_STRING {
        String TRUE = "true";
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
        public static final String FATHER_REGISTRATION = "Father Registration";
        public static final String DEATH = "Death";
        public static final String UPDATE_FATHER_DETAILS = "Update Father Details";
        public static final String UPDATE_MOTHER_DETAILS = "Update Mother Details";
        public static final String ARCHIVE_CHILD_RECORD = "archive_child_record";
        public static final String DYNAMIC_VACCINES = "dynamic_vaccines";
        public static final String OUT_OF_AREA_RECURRING_SERVICE = "out_of_area_service_recurring_service";
        public static final String BOOSTER_VACCINES = "booster_vaccines";
        public static final String UPDATE_DYNAMIC_VACCINES = "update_dynamic_vaccines";
        public static final String DELETE_DYNAMIC_VACCINES = "delete_dynamic_vaccines";
        public static final String NEXT_APPOINTMENT = "next_appointment";
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
        ;

        public static final String HOME_NEXT_VISIT_DATE_ENABLED = "home.next.visit.date.enabled";
        public static final String HOME_RECORD_WEIGHT_ENABLED = "home.record.weight.enabled";
        public static final String HOME_TOOLBAR_SCAN_CARD_ENABLED = "home.toolbar.scan.card.enabled";
        public static final String HOME_TOOLBAR_SCAN_QR_ENABLED = "home.toolbar.scan.qr.enabled";
        public static final String HOME_COMPLIANCE_ENABLED = "home.compliance.enabled";

        public static final String HOME_ZEIR_ID_COL_ENABLED = "home.zeir.id.column.enabled";

        public static final String FEATURE_BOTTOM_NAVIGATION_ENABLED = "feature.bottom.navigation.enabled";
        public static final String FEATURE_SCAN_QR_ENABLED = "feature.scan.qr.enabled";

        public static final String DETAILS_SIDE_NAVIGATION_ENABLED = "details.side.navigation.enabled";

        public static final String MOTHER_LOOKUP_SHOW_RESULTS_DURATION = "mother.lookup.show.results.duration";
        public static final String MOTHER_LOOKUP_UNDO_DURATION = "mother.lookup.undo.duration";
    }

    public static class VACCINE_GROUP {
        public static final String BIRTH = "Birth";
    }

    public static class VACCINE {
        public static final String BCG2 = "bcg2";
        public static final String BCG = "bcg";
    }

    public static final class LOCAL_DATE_TIME {

        public static final String YEAR = "year";
        public static final String MONTH_OF_YEAR = "monthOfYear";
        public static final String DAY_OF_MONTH = "dayOfMonth";
        public static final String HOUR_OF_DAY = "hourOfDay";
        public static final String MINUTE_OF_HOUR = "minuteOfHour";
        public static final String SECOND_OF_MINUTE = "secondOfMinute";
    }

    public interface Client {
        String DATE_CREATED = "dateCreated";
        String FIRST_NAME = "firstName";
        String LAST_NAME = "lastName";
        String SYSTEM_OF_REGISTRATION = "system_of_registration";
        String BIRTHDATE = "birthdate";
        String DEATHDATE = "deathdate";
        String ID_LOWER_CASE = "_id";
        String IDENTIFIERS = "identifiers";
        String GENDER = "gender";
        String RELATIONSHIPS = "relationships";
        String INACTIVE = "inactive";
        String LOST_TO_FOLLOW_UP = "lost_to_follow_up";
        String BASE_ENTITY_ID = "baseEntityId";
        String ATTRIBUTES = "attributes";
        String MANAGING_ORG_LOCATION_ID = "managing_organization_location_id";
        String IS_OUT_OF_CATCHMENT = "is_out_of_catchment";
    }

    public interface JsonForm {
        String OUT_OF_CATCHMENT_SERVICE = "out_of_catchment_service";
        String DYNAMIC_VACCINES = "dynamic_vaccines";
        String BOOSTER_VACCINES = "booster_vaccines";
    }

    public interface VACCINE_CODE {
        String TETANUS = "epnt";
    }

    public interface NEXT_APPOINTMENT_OBSERVATION_FIELD {
        String TREATMENT_PROVIDED = "Treatment_Provided";
        String NEXT_APPOINTMENT_DATE = "Next_Appointment_Date";
        String NEXT_SERVICE_EXPECTED = "Next_Service_Expected";
        String IS_OUT_OF_CATCHMENT = "Is_Out_Of_Catchment";

    }
}
