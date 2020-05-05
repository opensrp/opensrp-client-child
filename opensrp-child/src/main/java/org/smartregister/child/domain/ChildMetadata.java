package org.smartregister.child.domain;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.smartregister.child.BuildConfig;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ndegwamartin on 01/03/2019.
 */
public class ChildMetadata {
    public final Class childFormActivity;
    public final Class childImmunizationActivity;
    public final Class profileActivity;
    public final boolean formWizardValidateRequiredFieldsBefore;
    private ArrayList<String> locationLevels;
    private ArrayList<String> healthFacilityLevels;
    private List<String> fieldsWithLocationHierarchy;
    public ChildRegister childRegister;

    public ChildMetadata(Class<? extends JsonFormActivity> childFormActivity,
                         Class<? extends BaseProfileActivity> profileActivity,
                         Class<? extends BaseChildImmunizationActivity> childImmunizationActivity,
                         boolean formWizardValidateRequiredFieldsBefore) {
        this.childFormActivity = childFormActivity;
        this.profileActivity = profileActivity;
        this.childImmunizationActivity = childImmunizationActivity;
        this.formWizardValidateRequiredFieldsBefore = formWizardValidateRequiredFieldsBefore;
        locationLevels = new ArrayList<>(Arrays.asList(BuildConfig.LOCATION_LEVELS));
        healthFacilityLevels = new ArrayList<>(Arrays.asList(BuildConfig.HEALTH_FACILITY_LEVELS));
    }

    public void updateChildRegister(String formName, String tableName, String parentTableName, String registerEventType,
                                    String updateEventType, String outOfCatchmentServiceEventType, String config, String childCareGiverRelationKey,
                                    String outOfCatchmentFormName) {
        this.childRegister =
                new ChildRegister(formName, tableName, parentTableName, registerEventType, updateEventType, outOfCatchmentServiceEventType, config,
                        childCareGiverRelationKey, outOfCatchmentFormName);
    }

    public class ChildRegister {

        public final String formName;

        public final String tableName;

        public final String motherTableName;

        public final String registerEventType;

        public final String updateEventType;

        public final String outOfCatchmentServiceEventType;

        public final String config;

        public final String childCareGiverRelationKey;

        public final String outOfCatchmentFormName;


        public ChildRegister(String formName, String tableName, String parentTableName, String registerEventType,
                             String updateEventType, String outOfCatchmentServiceEventType, String config, String childCareGiverRelationKey,
                             String outOfCatchmentFormName) {
            this.formName = formName;
            this.tableName = tableName;
            this.motherTableName = parentTableName;
            this.registerEventType = registerEventType;
            this.updateEventType = updateEventType;
            this.outOfCatchmentServiceEventType = outOfCatchmentServiceEventType;
            this.config = config;
            this.childCareGiverRelationKey = childCareGiverRelationKey;
            this.outOfCatchmentFormName = outOfCatchmentFormName;
        }
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

    public ArrayList<String> getLocationLevels() {
        return locationLevels;
    }

    public void setLocationLevels(ArrayList<String> locationLevels) {
        this.locationLevels = locationLevels;
    }

    public ArrayList<String> getHealthFacilityLevels() {
        return healthFacilityLevels;
    }

    public void setHealthFacilityLevels(ArrayList<String> healthFacilityLevels) {
        this.healthFacilityLevels = healthFacilityLevels;
    }

    public List<String> getFieldsWithLocationHierarchy() {
        return fieldsWithLocationHierarchy;
    }

    public void setFieldsWithLocationHierarchy(List<String> fieldsWithLocationHierarchy) {
        this.fieldsWithLocationHierarchy = fieldsWithLocationHierarchy;
    }
}

