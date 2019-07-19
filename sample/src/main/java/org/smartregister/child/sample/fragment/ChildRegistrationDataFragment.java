package org.smartregister.child.sample.fragment;

import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.child.sample.R;
import org.smartregister.child.sample.util.SampleConstants;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.growthmonitoring.util.AppProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-05-29.
 */
public class ChildRegistrationDataFragment extends BaseChildRegistrationDataFragment {
    private  boolean monitorGrowth = false;
    /*
     * The map is such that key is the key defined in the registration form json while the value is the strings resource id
     * e.g. Key "First_Name" and Value "R.string.first_name"
     *
     * At runtime, the correct language string will be loaded
     *
     * Values will only show up if you add them here
     */

    @Override
    protected Map<String, Integer> getDataRowLabelResourceIds() {
        Map<String, Integer> resourceIds = new HashMap<>();
        boolean hasProperty = GrowthMonitoringLibrary.getInstance().getAppProperties().hasProperty(AppProperties.KEY.MONITOR_GROWTH);
        if (hasProperty) {
            monitorGrowth = GrowthMonitoringLibrary.getInstance().getAppProperties().getPropertyBoolean(AppProperties.KEY.MONITOR_GROWTH);
        }

        resourceIds.put("First_Name", R.string.first_name);
        resourceIds.put("Last_Name", R.string.last_name);
        resourceIds.put("Sex", R.string.sex);
        resourceIds.put("Date_Birth", R.string.child_dob);
        resourceIds.put("First_Health_Facility_Contact", R.string.date_first_seen);
        resourceIds.put("Birth_Weight", R.string.birth_weight);
        if (hasProperty && monitorGrowth) {
            resourceIds.put("Birth_Height", R.string.birth_height);
        }
        resourceIds.put("Birth_Tetanus_Protection", R.string.birth_tetanus_protection);
        resourceIds.put("Child_Register_Card_Number", R.string.child_register_card_number);
        resourceIds.put("Home_Facility", R.string.child_home_health_facility);
        resourceIds.put("Mother_Guardian_First_Name", R.string.mother_guardian_name);
        resourceIds.put("Mother_Guardian_Last_Name", R.string.mother_second_name);
        resourceIds.put("Mother_Guardian_Date_Birth", R.string.mother_guardian_dob);
        resourceIds.put("Mother_Guardian_Phone_Number", R.string.mother_guardian_phone_number);
        resourceIds.put("Mother_Guardian_NRC", R.string.mother_guardian_nrc);
        resourceIds.put("Father_Guardian_Name", R.string.father_guardian_full_name);
        resourceIds.put("Second_Guardian_Phone_Number", R.string.father_guardian_phone_number);
        //resourceIds.put("Father_Guardian_NRC", R.string.);
        resourceIds.put("Place_Birth", R.string.place_of_birth);
        resourceIds.put("Birth_Facility_Name", R.string.health_facility_the_child_was_in);
        resourceIds.put("Birth_Facility_Name_Other", R.string.other_birth_facility);
        resourceIds.put("Residential_Area", R.string.residential_area);
        resourceIds.put("Residential_Area_Other", R.string.residential_area_other);
        resourceIds.put("Residential_Address", R.string.home_address);
        resourceIds.put("Physical_Landmark", R.string.physical_landmark);

        //resourceIds.put("CHW_Name", R.string.);
        //resourceIds.put("CHW_Phone_Number", R.string.);
        //resourceIds.put("PMTCT_Status", R.string.);

        return resourceIds;
    }

    @Override
    public String getRegistrationForm() {
        return SampleConstants.JSON_FORM.CHILD_ENROLLMENT;
    }

}