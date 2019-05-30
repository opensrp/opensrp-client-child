package org.smartregister.child.sample.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TableRow;

import org.smartregister.child.fragment.BaseChildRegistrationDataFragment;
import org.smartregister.child.sample.R;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.DateUtil;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by ndegwamartin on 2019-05-29.
 */
public class ChildRegistrationDataFragment extends BaseChildRegistrationDataFragment {


    @Override
    public void loadData(Map<String, String> detailsMap) {
        if (fragmentView != null) {

            CustomFontTextView tvChildsHomeHealthFacility = fragmentView.findViewById(R.id.value_childs_home_health_facility);
            CustomFontTextView tvChildsOpenSrpId = fragmentView.findViewById(R.id.value_childs_zeir_id);
            CustomFontTextView tvChildsRegisterCardNumber = fragmentView.findViewById(R.id.value_childs_register_card_number);
            CustomFontTextView tvChildsFirstName = fragmentView.findViewById(R.id.value_first_name);
            CustomFontTextView tvChildsLastName = fragmentView.findViewById(R.id.value_last_name);
            CustomFontTextView tvChildsSex = fragmentView.findViewById(R.id.value_sex);
            CustomFontTextView tvChildsDOB = fragmentView.findViewById(R.id.value_childs_dob);
            CustomFontTextView tvChildsAge = fragmentView.findViewById(R.id.value_age);
            CustomFontTextView tvChildDateFirstSeen = fragmentView.findViewById(R.id.value_date_first_seen);
            CustomFontTextView tvChildsBirthWeight = fragmentView.findViewById(R.id.value_birth_weight);
            CustomFontTextView tvBirthTetanusProtection = fragmentView.findViewById(R.id.value_birth_tetanus_protection);
            ((View) tvBirthTetanusProtection.getParent()).setVisibility(View.GONE);
            CustomFontTextView tvMotherFirstName = fragmentView.findViewById(R.id.value_mother_guardian_first_name);
            CustomFontTextView tvMotherLastName = fragmentView.findViewById(R.id.value_mother_guardian_last_name);
            CustomFontTextView tvMotherDOB = fragmentView.findViewById(R.id.value_mother_guardian_dob);
            CustomFontTextView tvMotherPhoneNumber = fragmentView.findViewById(R.id.value_mother_guardian_phone_number);
            CustomFontTextView tvFatherPhoneNumber = fragmentView.findViewById(R.id.father_guardian_phone_number);
            CustomFontTextView tvFatherFullName = fragmentView.findViewById(R.id.value_father_guardian_full_name);
            CustomFontTextView tvFatherDOB = fragmentView.findViewById(R.id.value_father_guardian_nrc_number);
            CustomFontTextView tvChildsPlaceOfBirth = fragmentView.findViewById(R.id.value_place_of_birth);
            CustomFontTextView tvChildsBirthHealthFacility = fragmentView.findViewById(R.id.value_childs_birth_health_facility);
            CustomFontTextView tvChildsOtherBirthFacility = fragmentView.findViewById(R.id.value_other_birth_facility);
            CustomFontTextView tvChildsResidentialArea = fragmentView.findViewById(R.id.value_childs_residential_area);
            CustomFontTextView tvChildsOtherResidentialArea = fragmentView.findViewById(R.id.value_other_childs_residential_area);
            CustomFontTextView tvChildsHomeAddress = fragmentView.findViewById(R.id.value_home_address);
            CustomFontTextView tvLandmark = fragmentView.findViewById(R.id.value_landmark);
            CustomFontTextView tvPreferredLanguage = fragmentView.findViewById(R.id.value_preferred_language);

            TableRow tableRowChildsOtherBirthFacility = fragmentView.findViewById(R.id.tableRow_childRegDataFragment_childsOtherBirthFacility);
            TableRow tableRowChildsOtherResidentialArea = fragmentView.findViewById(R.id.tableRow_childRegDataFragment_childsOtherResidentialArea);

            tvChildsHomeHealthFacility.setText(LocationHelper.getInstance().getOpenMrsReadableName(LocationHelper.getInstance().getOpenMrsLocationName(Utils.getValue(detailsMap, "Home_Facility", false))));
            tvChildsOpenSrpId.setText(Utils.getValue(detailsMap, "zeir_id", false));
            tvChildsRegisterCardNumber.setText(Utils.getValue(detailsMap, "Child_Register_Card_Number", false));
            tvChildsFirstName.setText(Utils.getValue(detailsMap, "first_name", true));
            tvChildsLastName.setText(Utils.getValue(detailsMap, "last_name", true));
            tvChildsSex.setText(Utils.getValue(detailsMap, "gender", true));

            String formattedAge = "";
            String dobString = Utils.getValue(detailsMap, Constants.KEY.DOB, false);
            Date dob = Utils.dobStringToDate(dobString);
            if (dob != null) {
                String childsDateOfBirth = Constants.DATE_FORMAT.format(dob);
                tvChildsDOB.setText(childsDateOfBirth);

                long timeDiff = Calendar.getInstance().getTimeInMillis() - dob.getTime();
                if (timeDiff >= 0) {
                    formattedAge = DateUtil.getDuration(timeDiff);
                }
            }

            tvChildsAge.setText(formattedAge);

            String dateString = Utils.getValue(detailsMap, "First_Health_Facility_Contact", false);
            if (!TextUtils.isEmpty(dateString)) {
                Date date = JsonFormUtils.formatDate(dateString, false);
                if (date != null) {
                    dateString = Constants.DATE_FORMAT.format(date);
                }
            }

            tvChildDateFirstSeen.setText(dateString);
            tvChildsBirthWeight.setText(Utils.kgStringSuffix(Utils.getValue(detailsMap, "Birth_Weight", true)));
            tvBirthTetanusProtection.setText(Utils.getValue(detailsMap, "Birth_Tetanus_Protection", true).isEmpty() ? Utils.getValue(childDetails, "Birth_Tetanus_Protection", true) : Utils.getValue(detailsMap, "Birth_Tetanus_Protection", true));
            tvMotherFirstName.setText(Utils.getValue(detailsMap, "mother_first_name", true).isEmpty() ? Utils.getValue(childDetails, "mother_first_name", true) : Utils.getValue(detailsMap, "mother_first_name", true));
            tvMotherLastName.setText(Utils.getValue(detailsMap, "mother_last_name", true).isEmpty() ? Utils.getValue(childDetails, "mother_last_name", true) : Utils.getValue(detailsMap, "mother_last_name", true));

            String motherDobString = Utils.getValue(childDetails, "mother_dob", true);
            Date motherDob = Utils.dobStringToDate(motherDobString);
            if (motherDob != null) {
                motherDobString = Constants.DATE_FORMAT.format(motherDob);
            }


            // If default mother dob ... set it as blank
            if (motherDobString != null && motherDobString.equals(JsonFormUtils.MOTHER_DEFAULT_DOB)) {
                motherDobString = "";
            }

            tvMotherDOB.setText(motherDobString);
            if (detailsMap.containsKey("Mother_Guardian_Phone_Number")) {
                tvMotherPhoneNumber.setText(Utils.getValue(detailsMap, "Mother_Guardian_Phone_Number", true));
            } else {
                tvMotherPhoneNumber.setText(Utils.getValue(detailsMap, "Mother_Guardian_Number", true));
            }

            String fatherName = Utils.getValue(detailsMap, "Father_Guardian_First_Name", true);
            String fatherNameLast = Utils.getValue(detailsMap, "Father_Guardian_Last_Name", true);
            fatherNameLast = !TextUtils.isEmpty(fatherNameLast) ? fatherNameLast : "";

            tvFatherFullName.setText(fatherName + " " + fatherNameLast);


            if (detailsMap.containsKey("Father_Guardian_Phone_Number")) {
                tvFatherPhoneNumber.setText(Utils.getValue(detailsMap, "Father_Guardian_Phone_Number", true));
            } else {

                tvFatherPhoneNumber.setText(Utils.getValue(detailsMap, "Father_Guardian_Number", true));
            }

            tvFatherDOB.setText(Utils.getValue(detailsMap, "Father_Guardian_Date_Birth", true));

            String placeOfBirthChoice = Utils.getValue(detailsMap, "Place_Birth", true);
            if (placeOfBirthChoice.equalsIgnoreCase("1588AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
                placeOfBirthChoice = "Health facility";
            }

            if (placeOfBirthChoice.equalsIgnoreCase("1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")) {
                placeOfBirthChoice = "Home";
            }

            tvChildsPlaceOfBirth.setText(placeOfBirthChoice);
            String childsBirthHealthFacility = Utils.getValue(detailsMap, "Home_Facility", false);
            childsBirthHealthFacility = TextUtils.isEmpty(childsBirthHealthFacility) ? Utils.getValue(detailsMap, "isPlace_Birth", false) : childsBirthHealthFacility;
            tvChildsBirthHealthFacility.setText(LocationHelper.getInstance().getOpenMrsReadableName(LocationHelper.getInstance().getOpenMrsLocationName(childsBirthHealthFacility)));

            if (LocationHelper.getInstance().getOpenMrsReadableName(LocationHelper.getInstance().getOpenMrsLocationName(childsBirthHealthFacility)).equalsIgnoreCase("other")) {
                tableRowChildsOtherBirthFacility.setVisibility(View.VISIBLE);
                tvChildsOtherBirthFacility.setText(Utils.getValue(detailsMap, "Birth_Facility_Name_Other", true));
            }

            String childsResidentialArea = Utils.getValue(detailsMap, "address3", false);
            tvChildsResidentialArea.setText(LocationHelper.getInstance().getOpenMrsReadableName(LocationHelper.getInstance().getOpenMrsLocationName(childsResidentialArea)));
            if (LocationHelper.getInstance().getOpenMrsReadableName(LocationHelper.getInstance().getOpenMrsLocationName(childsResidentialArea)).equalsIgnoreCase("other")) {
                tableRowChildsOtherResidentialArea.setVisibility(View.VISIBLE);
                tvChildsOtherResidentialArea.setText(Utils.getValue(detailsMap, "address5", true));
            }

            tvChildsHomeAddress.setText(Utils.getValue(detailsMap, "address2", true));
            tvLandmark.setText(Utils.getValue(detailsMap, "address1", true));

            tvPreferredLanguage.setText(Utils.getValue(detailsMap, "Preferred_Language", true).isEmpty() ? Utils.getValue(childDetails, "Preferred_Language", true) : Utils.getValue(detailsMap, "Preferred_Language", true));

            // remove any empty fields
            removeEmptyValueFields();
        }
    }
}
