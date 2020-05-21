package org.smartregister.child.domain;

import org.smartregister.domain.form.FormLocation;

import java.util.List;

/**
 * Created by ndegwamartin on 16/05/2020.
 */
public class FormLocationTree {
    private String formLocationString;
    private List<FormLocation> formLocations;

    public FormLocationTree(String formLocationString, List<FormLocation> formLocations) {
        this.formLocationString = formLocationString;
        this.formLocations = formLocations;
    }

    public String getFormLocationString() {
        return formLocationString;
    }

    public List<FormLocation> getFormLocations() {
        return formLocations;
    }
}
