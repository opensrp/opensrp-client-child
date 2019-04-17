package org.smartregister.child.sample.util;

/**
 * Created by ndegwamartin on 01/03/2019.
 */
public class SampleConstants {

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String CHILD_REGISTER = "child_register";

    }

    public static final class EventType {
        public static final String CHILD_REGISTRATION = "Birth Registration";

        public static final String UPDATE_CHILD_REGISTRATION = "Update Birth Registration";
    }

    public static class JSON_FORM {

        public static String CHILD_ENROLLMENT = "child_enrollment";
        public static String OUT_OF_CATCHMENT_SERVICE = "out_of_catchment_service";

    }

    public static class RELATIONSHIP {
        public static final String MOTHER = "mother";

    }

    public static class TABLE_NAME {
        public static final String CHILD = "ec_child";
        public static final String MOTHER_TABLE_NAME = "ec_mother";
    }

    public static final class VACCINE {

        public static final String CHILD = "child";
    }
}
