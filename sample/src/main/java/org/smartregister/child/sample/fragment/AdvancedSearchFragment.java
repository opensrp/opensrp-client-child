package org.smartregister.child.sample.fragment;

import android.text.TextUtils;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.fragment.BaseAdvancedSearchFragment;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.child.sample.R;
import org.smartregister.child.sample.presenter.AdvancedSearchPresenter;
import org.smartregister.child.sample.util.DBQueryHelper;
import org.smartregister.child.util.DBConstants;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;
import java.util.Map;

import static org.smartregister.child.util.Constants.CHILD_STATUS.ACTIVE;
import static org.smartregister.child.util.Constants.CHILD_STATUS.INACTIVE;
import static org.smartregister.child.util.Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP;

public class AdvancedSearchFragment extends BaseAdvancedSearchFragment {
    protected MaterialEditText openSrpId;
    protected MaterialEditText motherGuardianFirstName;
    protected MaterialEditText motherGuardianLastName;
    protected MaterialEditText motherGuardianNrc;
    protected MaterialEditText motherGuardianPhoneNumber;
    AdvancedSearchPresenter presenter;
    private MaterialEditText firstName;
    private MaterialEditText lastName;

    @Override
    protected BaseChildAdvancedSearchPresenter getPresenter() {

        if (presenter == null) {
            String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
            presenter = new AdvancedSearchPresenter(this, viewConfigurationIdentifier);
        }

        return presenter;
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> formData) {

        this.searchFormData = formData;

    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomeRegisterCondition();
    }

    @Override
    public void assignedValuesBeforeBarcode() {
        if (searchFormData.size() > 0) {
            firstName.setText(searchFormData.get(DBConstants.KEY.FIRST_NAME));
            lastName.setText(searchFormData.get(DBConstants.KEY.LAST_NAME));
            motherGuardianFirstName.setText(searchFormData.get(DBConstants.KEY.MOTHER_FIRST_NAME));
            motherGuardianLastName.setText(searchFormData.get(DBConstants.KEY.MOTHER_LAST_NAME));
            motherGuardianNrc.setText(searchFormData.get(DBConstants.KEY.NRC_NUMBER));
            motherGuardianPhoneNumber.setText(searchFormData.get(DBConstants.KEY.CONTACT_PHONE_NUMBER));
            openSrpId.setText(searchFormData.get(DBConstants.KEY.ZEIR_ID));
        }
    }

    @Override
    public void populateSearchableFields(View view) {

        firstName = view.findViewById(org.smartregister.child.R.id.first_name);
        firstName.addTextChangedListener(advancedSearchTextwatcher);

        lastName = view.findViewById(org.smartregister.child.R.id.last_name);
        lastName.addTextChangedListener(advancedSearchTextwatcher);

        openSrpId = view.findViewById(org.smartregister.child.R.id.opensrp_id);
        openSrpId.addTextChangedListener(advancedSearchTextwatcher);

        motherGuardianFirstName = view.findViewById(org.smartregister.child.R.id.mother_guardian_first_name);
        motherGuardianFirstName.addTextChangedListener(advancedSearchTextwatcher);

        motherGuardianLastName = view.findViewById(org.smartregister.child.R.id.mother_guardian_last_name);
        motherGuardianLastName.addTextChangedListener(advancedSearchTextwatcher);

        motherGuardianNrc = view.findViewById(org.smartregister.child.R.id.mother_guardian_nrc);
        motherGuardianNrc.addTextChangedListener(advancedSearchTextwatcher);

        motherGuardianPhoneNumber = view.findViewById(org.smartregister.child.R.id.mother_guardian_phone_number);
        motherGuardianPhoneNumber.addTextChangedListener(advancedSearchTextwatcher);

        //Defaults
        startDate.addTextChangedListener(advancedSearchTextwatcher);
        endDate.addTextChangedListener(advancedSearchTextwatcher);

        advancedFormSearchableFields.put(DBConstants.KEY.FIRST_NAME, firstName);
        advancedFormSearchableFields.put(DBConstants.KEY.LAST_NAME, lastName);
        advancedFormSearchableFields.put(DBConstants.KEY.ZEIR_ID, openSrpId);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstName);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastName);
        advancedFormSearchableFields.put(DBConstants.KEY.NRC_NUMBER, motherGuardianNrc);
        advancedFormSearchableFields.put(DBConstants.KEY.CONTACT_PHONE_NUMBER, motherGuardianPhoneNumber);
        advancedFormSearchableFields.put(START_DATE, startDate);
        advancedFormSearchableFields.put(END_DATE, endDate);
    }

    @Override
    protected HashMap<String, String> createSelectedFieldMap() {
        HashMap<String, String> fields = new HashMap<>();
        fields.put(DBConstants.KEY.FIRST_NAME, firstName.getText().toString());
        fields.put(DBConstants.KEY.LAST_NAME, lastName.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstName.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastName.getText().toString());
        fields.put(DBConstants.KEY.NRC_NUMBER, motherGuardianNrc.getText().toString());
        fields.put(DBConstants.KEY.CONTACT_PHONE_NUMBER, motherGuardianPhoneNumber.getText().toString());
        fields.put(DBConstants.KEY.ZEIR_ID, openSrpId.getText().toString());
        fields.put(START_DATE, startDate.getText().toString());
        fields.put(END_DATE, endDate.getText().toString());
        return fields;
    }

    @Override
    public boolean onBackPressed() {
        ((BaseRegisterActivity) getActivity()).setSelectedBottomBarMenuItem(R.id.action_home);
        return super.onBackPressed();
    }

    @Override
    protected String getDefaultSortQuery() {
        return presenter.getDefaultSortQuery();
    }

    @Override
    protected String filterSelectionCondition(boolean urgentOnly) {
        return DBQueryHelper.getFilterSelectionCondition(urgentOnly);
    }

    @Override
    public void onClick(View view) {
        view.toString();
    }

    @Override
    protected void clearFormFields() {
        super.clearFormFields();

        openSrpId.setText("");
        firstName.setText("");
        lastName.setText("");
        motherGuardianFirstName.setText("");
        motherGuardianLastName.setText("");
        motherGuardianNrc.setText("");
        motherGuardianPhoneNumber.setText("");

    }

    @Override
    protected Map<String, String> getSearchMap(boolean outOfArea) {
        Map<String, String> searchParams = new HashMap<>();

        String fn = firstName.getText().toString();
        String ln = lastName.getText().toString();

        String motherGuardianFirstNameString = motherGuardianFirstName.getText().toString();
        String motherGuardianLastNameString = motherGuardianLastName.getText().toString();
        String motherGuardianNrcString = motherGuardianNrc.getText().toString();
        String motherGuardianPhoneNumberString = motherGuardianPhoneNumber.getText().toString();
        String zeir = openSrpId.getText().toString();


        if (StringUtils.isNotBlank(motherGuardianFirstNameString)) {
            searchParams.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianFirstNameString);
        }

        if (StringUtils.isNotBlank(motherGuardianLastNameString)) {
            searchParams.put(DBConstants.KEY.MOTHER_LAST_NAME, motherGuardianLastNameString);
        }

        if (StringUtils.isNotBlank(motherGuardianNrcString)) {

            searchParams.put(DBConstants.KEY.NRC_NUMBER, motherGuardianNrcString);
        }

        if (StringUtils.isNotBlank(motherGuardianPhoneNumberString)) {
            searchParams.put(DBConstants.KEY.CONTACT_PHONE_NUMBER, motherGuardianPhoneNumberString);
        }


        if (!TextUtils.isEmpty(fn)) {
            searchParams.put(DBConstants.KEY.FIRST_NAME, fn);
        }

        if (!TextUtils.isEmpty(ln)) {
            searchParams.put(DBConstants.KEY.LAST_NAME, ln);
        }

        if (!TextUtils.isEmpty(zeir)) {
            searchParams.put(DBConstants.KEY.ZEIR_ID, zeir);
        }


        //Inactive
        boolean isInactive = inactive.isChecked();
        if (isInactive) {
            searchParams.put(INACTIVE, Boolean.toString(true));
        }
        //Active
        boolean isActive = active.isChecked();
        if (isActive) {
            searchParams.put(ACTIVE, Boolean.toString(true));
        }

        //Lost To Follow Up
        boolean isLostToFollowUp = lostToFollowUp.isChecked();
        if (isLostToFollowUp) {
            searchParams.put(LOST_TO_FOLLOW_UP, Boolean.toString(true));
        }


        if (isActive == isInactive && isActive == isLostToFollowUp) {

            searchParams.remove(INACTIVE);

            searchParams.remove(ACTIVE);

            searchParams.remove(LOST_TO_FOLLOW_UP);

        }

        String startDateString = startDate.getText().toString();
        if (StringUtils.isNotBlank(startDateString)) {
            searchParams.put(START_DATE, startDateString.trim());
        }

        String endDateString = endDate.getText().toString();
        if (StringUtils.isNotBlank(endDateString)) {
            searchParams.put(END_DATE, endDateString.trim());
        }

        return searchParams;
    }

}