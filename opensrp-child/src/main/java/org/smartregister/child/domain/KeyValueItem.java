package org.smartregister.child.domain;

/**
 * Created by ndegwamartin on 2019-06-11.
 */
public class KeyValueItem {

    private String key;
    private String value;

    public KeyValueItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
