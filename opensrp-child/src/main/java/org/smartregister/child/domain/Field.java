package org.smartregister.child.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ndegwamartin on 2019-06-11.
 */
public class Field {

    private String key;

    private String type;

    @SerializedName("openmrs_entity")
    private String openmrsEntity;

    @SerializedName("openmrs_entity_id")
    private String openmrsEntityId;

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getOpenmrsEntity() {
        return openmrsEntity;
    }

    public String getOpenmrsEntityId() {
        return openmrsEntityId;
    }
}
