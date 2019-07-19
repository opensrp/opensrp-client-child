package org.smartregister.child.domain;

/**
 * Created by keyman on 16/11/2016.
 */
public class EditWrapper {
    private String currentValue;
    private String newValue;
    private String field;

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
