package org.smartregister.child.domain;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.smartregister.view.activity.BaseProfileActivity;

/**
 * Created by ndegwamartin on 01/03/2019.
 */
public class ChildMetadata {
    public final Class childFormActivity;
    public final Class profileActivity;
    public final boolean formWizardValidateRequiredFieldsBefore;

    public ChildRegister childRegister;

    public ChildMetadata(Class<? extends JsonFormActivity> childFormActivity, Class<? extends BaseProfileActivity> profileActivity, boolean formWizardValidateRequiredFieldsBefore) {
        this.childFormActivity = childFormActivity;
        this.profileActivity = profileActivity;
        this.formWizardValidateRequiredFieldsBefore = formWizardValidateRequiredFieldsBefore;
    }

    public class ChildRegister {

        public final String formName;

        public final String tableName;

        public final String registerEventType;

        public final String updateEventType;

        public final String config;

        public final String childCareGiverRelationKey;

        public final String outOfCatchmentFormName;


        public ChildRegister(String formName, String tableName, String registerEventType, String updateEventType, String config, String childCareGiverRelationKey, String outOfCatchmentFormName) {
            this.formName = formName;
            this.tableName = tableName;
            this.registerEventType = registerEventType;
            this.updateEventType = updateEventType;
            this.config = config;
            this.childCareGiverRelationKey = childCareGiverRelationKey;
            this.outOfCatchmentFormName = outOfCatchmentFormName;
        }
    }

    public void updateChildRegister(String formName, String tableName, String registerEventType, String updateEventType, String config, String childCareGiverRelationKey, String outOfCatchmentFormName) {
        this.childRegister = new ChildRegister(formName, tableName, registerEventType, updateEventType, config, childCareGiverRelationKey, outOfCatchmentFormName);
    }

    public class ChildActivityRegister {

        public final String tableName;
        public final int currentLimit;
        public final boolean showPagination;

        public ChildActivityRegister(String tableName, int currentLimit, boolean showPagination) {
            this.tableName = tableName;
            if (currentLimit <= 0) {
                this.currentLimit = 20;
            } else {
                this.currentLimit = currentLimit;
            }
            this.showPagination = showPagination;
        }
    }
}

