package org.smartregister.child.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ndegwamartin on 2019-06-11.
 */
public class Field {

    private String key;

    private String type;

    private List<String> keys;

    private List<String> values;

    private String hint;
    
    @SerializedName("entity_id")
    private String entityId;

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


    public String getEntityId() {
        return entityId;
    }

    public String getOpenmrsEntity() {
        return openmrsEntity;
    }

    public String getOpenmrsEntityId() {
        return openmrsEntityId;
    }

    public List<String> getKeys() {
        return keys;
    }

    public List<String> getValues() {
        return values;
    }
    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
