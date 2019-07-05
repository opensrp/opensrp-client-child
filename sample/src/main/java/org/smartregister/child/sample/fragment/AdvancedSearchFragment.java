package org.smartregister.child.sample.fragment;

import android.text.TextUtils;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.fragment.BaseAdvancedSearchFragment;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.child.sample.R;
import org.smartregister.child.sample.presenter.AdvancedSearchPresenter;
import org.smartregister.child.sample.util.DBConstants;
import org.smartregister.child.sample.util.DBQueryHelper;
import org.smartregister.child.util.Utils;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;
import java.util.Map;

import static org.smartregister.child.util.Constants.CHILD_STATUS.ACTIVE;
import static org.smartregister.child.util.Constants.CHILD_STATUS.INACTIVE;
import static org.smartregister.child.util.Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP;

public class AdvancedSearchFragment extends BaseAdvancedSearchFragment {

    AdvancedSearchPresenter presenter;
    private MaterialEditText firstName;
    private MaterialEditText lastName;
    protected MaterialEditText openSrpId;
    protected MaterialEditText motherGuardianName;
    protected MaterialEditText motherGuardianNrc;
    protected MaterialEditText motherGuardianPhoneNumber;

    @Override
    protected BaseChildAdvancedSearchPresenter getPresenter() {

        if (presenter == null) {
            String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
            presenter = new AdvancedSearchPresenter(this, viewConfigurationIdentifier);
        }

        return presenter;
    }

    @Override
    public boolean onBackPressed() {
        ((BaseRegisterActivity) getActivity()).setSelectedBottomBarMenuItem(R.id.action_home);
        return super.onBackPressed();
    }

    @Override
    public void populateSearchableFields(View view) {

        firstName = view.findViewById(org.smartregister.child.R.id.first_name);
        firstName.addTextChangedListener(advancedSearchTextwatcher);

        lastName = view.findViewById(org.smartregister.child.R.id.last_name);
        lastName.addTextChangedListener(advancedSearchTextwatcher);

        openSrpId = view.findViewById(org.smartregister.child.R.id.opensrp_id);
        openSrpId.addTextChangedListener(advancedSearchTextwatcher);

        motherGuardianName = view.findViewById(org.smartregister.child.R.id.mother_guardian_name);
        motherGuardianName.addTextChangedListener(advancedSearchTextwatcher);

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
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianName);
        advancedFormSearchableFields.put(DBConstants.KEY.NRC_NUMBER, motherGuardianNrc);
        advancedFormSearchableFields.put(DBConstants.KEY.CONTACT_PHONE_NUMBER, motherGuardianPhoneNumber);
        advancedFormSearchableFields.put(START_DATE, startDate);
        advancedFormSearchableFields.put(END_DATE, endDate);
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
    public void setAdvancedSearchFormData(HashMap<String, String> formData) {

        this.searchFormData = formData;

    }

    @Override
    public void onClick(View view) {
        view.toString();
    }

    @Override
    public void assignedValuesBeforeBarcode() {
        if (searchFormData.size() > 0) {
            firstName.setText(searchFormData.get(DBConstants.KEY.FIRST_NAME));
            lastName.setText(searchFormData.get(DBConstants.KEY.LAST_NAME));
            motherGuardianName.setText(searchFormData.get(DBConstants.KEY.MOTHER_FIRST_NAME));
            motherGuardianNrc.setText(searchFormData.get(DBConstants.KEY.NRC_NUMBER));
            motherGuardianPhoneNumber.setText(searchFormData.get(DBConstants.KEY.CONTACT_PHONE_NUMBER));
            openSrpId.setText(searchFormData.get(DBConstants.KEY.ZEIR_ID));
        }
    }

    @Override
    protected HashMap<String, String> createSelectedFieldMap() {
        HashMap<String, String> fields = new HashMap<>();
        fields.put(DBConstants.KEY.FIRST_NAME, firstName.getText().toString());
        fields.put(DBConstants.KEY.LAST_NAME, lastName.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianName.getText().toString());
        fields.put(DBConstants.KEY.NRC_NUMBER, motherGuardianNrc.getText().toString());
        fields.put(DBConstants.KEY.CONTACT_PHONE_NUMBER, motherGuardianPhoneNumber.getText().toString());
        fields.put(DBConstants.KEY.ZEIR_ID, openSrpId.getText().toString());
        return fields;
    }

    @Override
    protected void clearFormFields() {
        super.clearFormFields();

        openSrpId.setText("");
        firstName.setText("");
        lastName.setText("");
        motherGuardianName.setText("");
        motherGuardianNrc.setText("");
        motherGuardianPhoneNumber.setText("");

    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomeRegisterCondition();
    }

    @Override
    protected Map<String, String> getSearchMap(boolean outOfArea) {

        Map<String, String> searchParams = new HashMap<>();


        String fn = firstName.getText().toString();
        String ln = lastName.getText().toString();


        String motherGuardianNameString = motherGuardianName.getText().toString();

        String motherGuardianNrcString = motherGuardianNrc.getText().toString();

        String motherGuardianPhoneNumberString = motherGuardianPhoneNumber.getText().toString();

        String zeir = openSrpId.getText().toString();


        if (StringUtils.isNotBlank(motherGuardianNameString)) {
            searchParams.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianNameString);
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


        String tableName = Utils.metadata().childRegister.tableName;
        String parentTableName = Utils.metadata().childRegister.motherTableName;


        //Inactive
        boolean isInactive = inactive.isChecked();
        if (isInactive) {
            String inActiveKey = INACTIVE;
            if (!outOfArea) {
                inActiveKey = tableName + "." + INACTIVE;
            }
            searchParams.put(inActiveKey, Boolean.toString(isInactive));
        }
        //Active
        boolean isActive = active.isChecked();
        if (isActive) {
            String activeKey = ACTIVE;
            if (!outOfArea) {
                activeKey = tableName + "." + ACTIVE;
            }
            searchParams.put(activeKey, Boolean.toString(isActive));
        }

        //Lost To Follow Up
        boolean isLostToFollowUp = lostToFollowUp.isChecked();
        if (isLostToFollowUp) {
            String lostToFollowUpKey = LOST_TO_FOLLOW_UP;
            if (!outOfArea) {
                lostToFollowUpKey = tableName + "." + LOST_TO_FOLLOW_UP;
            }
            searchParams.put(lostToFollowUpKey, Boolean.toString(isLostToFollowUp));
        }


        if (isActive == isInactive && isActive == isLostToFollowUp) {

            if (searchParams.containsKey(INACTIVE)) {
                searchParams.remove(INACTIVE);
            }

            if (searchParams.containsKey(tableName + "." + INACTIVE)) {
                searchParams.remove(tableName + "." + INACTIVE);
            }

            if (searchParams.containsKey(ACTIVE)) {
                searchParams.remove(ACTIVE);
            }

            if (searchParams.containsKey(tableName + "." + ACTIVE)) {
                searchParams.remove(tableName + "." + ACTIVE);
            }

            if (searchParams.containsKey(LOST_TO_FOLLOW_UP)) {
                searchParams.remove(LOST_TO_FOLLOW_UP);
            }

            if (searchParams.containsKey(tableName + "." + LOST_TO_FOLLOW_UP)) {
                searchParams.remove(tableName + "." + LOST_TO_FOLLOW_UP);
            }

        }

        String startDateString = startDate.getText().toString();
        if (StringUtils.isNotBlank(startDateString)) {
            searchParams.put(START_DATE, startDateString.trim());
        }

        String endDateString = endDate.getText().toString();
        if (StringUtils.isNotBlank(endDateString)) {
            searchParams.put(END_DATE, endDateString.trim());
        }

        /*


        if (StringUtils.isNotBlank(startDateString) && StringUtils.isNotBlank(endDateString)) {
            String dateFormat = "yyyy-MM-dd";
            Date startDate = Utils.getDateFromString(startDateString, dateFormat);
            Date endDate = Utils.getDateFromString(endDateString, dateFormat);

            if (startDate.compareTo(endDate) > 0) {
                Utils.showToast(getActivity(),"For birth range please select an End Date which IS NOT earlier than the Start Date");
                return;
            }
        }

        */

        return searchParams;
    }

}