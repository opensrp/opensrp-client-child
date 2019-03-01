package org.smartregister.child.util;

import org.smartregister.AllConstants;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class Constants extends AllConstants {


    public static class JSON_FORM_KEY {
        public static final String ENTITY_ID = "entity_id";
        public static final String OPTIONS = "options";
        public static final String ENCOUNTER_LOCATION = "encounter_location";
        public static final String ATTRIBUTES = "attributes";
        public static final String DEATH_DATE = "deathdate";
        public static final String DEATH_DATE_APPROX = "deathdateApprox";
        public static final String UNIQUE_ID = "unique_id";
        public static final String FAMILY_NAME = "fam_name";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String DOB = "dob";
        public static final String DOB_UNKNOWN = "dob_unknown";
        public static final String AGE = "age";

    }

    public static class JSON_FORM_EXTRA {
        public static final String JSON = "json";
        public static final String NEXT = "next";

    }

    public static class OPENMRS {
        public static final String ENTITY = "openmrs_entity";
        public static final String ENTITY_ID = "openmrs_entity_id";
    }

    public static final class KEY {
        public static final String KEY = "key";
        public static final String VALUE = "value";
        public static final String TREE = "tree";
        public static final String DEFAULT = "default";
        public static final String PHOTO = "photo";
        public static final String TYPE = "type";
    }

    public static final class INTENT_KEY {
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String FAMILY_BASE_ENTITY_ID = "child_base_entity_id";
        public static final String FAMILY_HEAD = "child_head";
        public static final String PRIMARY_CAREGIVER = "primary_caregiver";
        public static final String VILLAGE_TOWN = "village_town";
        public static final String FAMILY_NAME = "child_name";
        public static final String JSON = "json";
        public static final String TO_RESCHEDULE = "to_reschedule";
        public static final String IS_REMOTE_LOGIN = "is_remote_login";
        public static final String GO_TO_DUE_PAGE = "go_to_due_page";
    }

    public static class ENTITY {
        public static final String PERSON = "person";
    }

    public static class BOOLEAN_INT {
        public static final int TRUE = 1;
    }

    public static class WizardFormActivity {
        public static final String EnableOnCloseDialog = "EnableOnCloseDialog";
    }
    public static final class EntityType {

        public static final String CHILD = "child";
    }

    public static final class EC_CHILD_TABLE {

        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String DOB = "dob";
        public static final String DOD = "dod";
        public static final String REG_DATE = "client_reg_date";
        public static final String INACTIVE = "inactive";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";


    }

    public static final class GENDER {

        public static final String MALE = "male";
        public static final String FEMALE = "female";
        public static final String TRANSGENDER = "transgender";
    }
}
