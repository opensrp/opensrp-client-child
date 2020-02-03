package org.smartregister.child.util;

public interface ChildDbConstants {
    interface Table {
        String REGISTER_TYPE = "register_type";
    }

    interface Column {
        interface REGISTER_TYPE {
            String BASE_ENTITY_ID = "baseEntityId";
            String REGISTER_TYPE = "register_type";
            String DATE_CREATED = "date_created";
            String DATE_REMOVED = "date_removed";
        }
    }
}
