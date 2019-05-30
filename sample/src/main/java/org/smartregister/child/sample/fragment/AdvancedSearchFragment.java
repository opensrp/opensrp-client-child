package org.smartregister.child.sample.fragment;

import android.text.TextUtils;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.fragment.BaseAdvancedSearchFragment;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.child.sample.presenter.AdvancedSearchPresenter;
import org.smartregister.child.sample.util.DBConstants;
import org.smartregister.child.sample.util.DBQueryHelper;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.HashMap;
import java.util.Map;

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
    public void populateSearchableFields(View view) {

        firstName = view.findViewById(org.smartregister.child.R.id.first_name);
        advancedFormSearchableFields.put(DBConstants.KEY.FIRST_NAME, firstName);

        lastName = view.findViewById(org.smartregister.child.R.id.last_name);
        advancedFormSearchableFields.put(DBConstants.KEY.LAST_NAME, lastName);

        openSrpId = view.findViewById(org.smartregister.child.R.id.zeir_id);
        advancedFormSearchableFields.put(DBConstants.KEY.ZEIR_ID, openSrpId);

        motherGuardianName = view.findViewById(org.smartregister.child.R.id.mother_guardian_name);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianName);

        motherGuardianNrc = view.findViewById(org.smartregister.child.R.id.mother_guardian_nrc);
        advancedFormSearchableFields.put(DBConstants.KEY.NRC_NUMBER, motherGuardianNrc);

        motherGuardianPhoneNumber = view.findViewById(org.smartregister.child.R.id.mother_guardian_phone_number);
        advancedFormSearchableFields.put(DBConstants.KEY.CONTACT_PHONE_NUMBER, motherGuardianPhoneNumber);


        firstName.addTextChangedListener(advancedSearchTextwatcher);

        lastName.addTextChangedListener(advancedSearchTextwatcher);


        openSrpId.addTextChangedListener(advancedSearchTextwatcher);

        motherGuardianName.addTextChangedListener(advancedSearchTextwatcher);

        motherGuardianNrc.addTextChangedListener(advancedSearchTextwatcher);

        motherGuardianPhoneNumber.addTextChangedListener(advancedSearchTextwatcher);
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
    protected void assignedValuesBeforeBarcode() {
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
        return DBQueryHelper.getHomePatientRegisterCondition();
    }

    @Override
    protected Map<String, String> getSearchMap() {

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

        return searchParams;
    }

}