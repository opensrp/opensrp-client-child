package org.smartregister.child.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.RadioButton;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.child.listener.DatePickerListener;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.child.provider.AdvancedSearchClientsProvider;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.DBQueryHelper;
import org.smartregister.child.util.Utils;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public abstract class BaseAdvancedSearchFragment extends BaseChildRegisterFragment
        implements ChildAdvancedSearchContract.View, ChildRegisterFragmentContract.View {

    private View listViewLayout;
    private View advancedSearchForm;
    private ImageButton backButton;
    private Button searchButton;
    private Button search;

    private RadioButton outsideInside;
    private RadioButton myCatchment;

    private MaterialEditText firstName;
    private MaterialEditText lastName;

    private TextView searchCriteria;
    private TextView matchingResults;


    private boolean listMode = false;
    private boolean isLocal = false;

    private BroadcastReceiver connectionChangeReciever;
    private boolean registeredConnectionChangeReceiver = false;
    private AdvancedSearchTextWatcher advancedSearchTextwatcher = new AdvancedSearchTextWatcher();
    private HashMap<String, String> searchFormData = new HashMap<>();

    protected CheckBox active;
    protected CheckBox inactive;
    protected CheckBox lostToFollowUp;
    protected MaterialEditText zeirId;
    protected MaterialEditText motherGuardianName;
    protected MaterialEditText motherGuardianNrc;
    protected MaterialEditText motherGuardianPhoneNumber;
    protected EditText startDate;
    protected EditText endDate;

    private Button qrCodeButton;
    private Map<String, View> advancedFormSearchableFields = new HashMap<>();

    @Override
    protected void initializePresenter() {
        presenter = getPresenter();

    }

    protected abstract BaseChildAdvancedSearchPresenter getPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advanced_search, container, false);
        rootView = view;//handle to the root

        setupViews(view);
        onResumption();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            switchViews(false);
            updateSearchLimits();
            resetForm();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (connectionChangeReciever != null && registeredConnectionChangeReceiver) {
            getActivity().unregisterReceiver(connectionChangeReciever);
            registeredConnectionChangeReceiver = false;
        }
    }

    @Override
    public boolean onBackPressed() {
        goBack();
        return true;
    }

    @Override
    protected void goBack() {
        if (listMode) {
            switchViews(false);
        } else {
            ((BaseRegisterActivity) getActivity()).switchToBaseFragment();
        }
    }

    @Override
    protected void onViewClicked(View view) {
        if (view.getId() == R.id.search) {
            search();
        } else if (view.getId() == R.id.advanced_form_search_btn) {
            search();
        } else if (view.getId() == R.id.back_button) {
            switchViews(false);
        } /*else if (view.getId() == R.id.undo_button) {
            ((BaseRegisterActivity) getActivity()).switchToBaseFragment();
            ((BaseRegisterActivity) getActivity()).setSelectedBottomBarMenuItem(R.id.action_clients);
            ((BaseRegisterActivity) getActivity()).setSearchTerm("");
        } else if ((view.getId() == R.id.patient_column || view.getId() == R.id.profile) && view.getTag() != null) {
            Utils.navigateToProfile(getActivity(), (HashMap<String, String>) ((CommonPersonObjectClient) view.getTag()).getColumnmaps());
        } else if (view.getId() == R.id.sync) {
            SyncServiceJob.scheduleJobImmediately(SyncServiceJob.TAG);
            SyncSettingsServiceJob.scheduleJobImmediately(SyncSettingsServiceJob.TAG);
            //Todo add the move to catchment area
        }*/
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        listViewLayout = view.findViewById(R.id.advanced_search_list);
        listViewLayout.setVisibility(View.GONE);

        advancedSearchForm = view.findViewById(R.id.advanced_search_form);
        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(registerActionHandler);

        searchCriteria = view.findViewById(R.id.search_criteria);
        matchingResults = view.findViewById(R.id.matching_results);
        search = view.findViewById(R.id.search);
        searchButton = view.findViewById(R.id.advanced_form_search_btn);
        outsideInside = view.findViewById(R.id.out_and_inside);
        myCatchment = view.findViewById(R.id.my_catchment);

        firstName = view.findViewById(R.id.first_name);
        advancedFormSearchableFields.put(DBConstants.KEY.FIRST_NAME, firstName);

        lastName = view.findViewById(R.id.last_name);
        advancedFormSearchableFields.put(DBConstants.KEY.LAST_NAME, lastName);

        zeirId = view.findViewById(R.id.zeir_id);
        advancedFormSearchableFields.put(DBConstants.KEY.ZEIR_ID, zeirId);

        motherGuardianName = view.findViewById(R.id.mother_guardian_name);
        advancedFormSearchableFields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianName);

        motherGuardianNrc = view.findViewById(R.id.mother_guardian_nrc);
        advancedFormSearchableFields.put(DBConstants.KEY.NRC_NUMBER, motherGuardianNrc);

        motherGuardianPhoneNumber = view.findViewById(R.id.mother_guardian_phone_number);
        advancedFormSearchableFields.put(DBConstants.KEY.CONTACT_PHONE_NUMBER, motherGuardianPhoneNumber);

        populateFormViews(view);

    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        RepositoryHolder repoHolder = new RepositoryHolder();
        BaseChildRegisterActivity activity = (BaseChildRegisterActivity) getActivity();

        repoHolder.setWeightRepository(GrowthMonitoringLibrary.getInstance().weightRepository());
        repoHolder.setVaccineRepository(ImmunizationLibrary.getInstance().vaccineRepository());
        repoHolder.setCommonRepository(commonRepository());

        AdvancedSearchClientsProvider advancedSearchProvider = new AdvancedSearchClientsProvider(getActivity(), repoHolder,
                visibleColumns, registerActionHandler, paginationViewHandler, ChildLibrary.getInstance().context().alertService());
        clientAdapter = new RecyclerViewPaginatedAdapter(null, advancedSearchProvider,
                context().commonrepository(this.tablename));
        clientsView.setAdapter(clientAdapter);
    }

    private void populateFormViews(View view) {
        search.setEnabled(false);
        search.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));
        search.setOnClickListener(registerActionHandler);


        searchButton.setEnabled(false);
        searchButton.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));
        searchButton.setOnClickListener(registerActionHandler);


        outsideInside.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!Utils.isConnectedToNetwork(getActivity())) {
                    myCatchment.setChecked(true);
                    outsideInside.setChecked(false);
                } else {
                    myCatchment.setChecked(!isChecked);
                }
            }
        });

        myCatchment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!Utils.isConnectedToNetwork(getActivity())) {
                    myCatchment.setChecked(true);
                    outsideInside.setChecked(false);
                } else {
                    outsideInside.setChecked(!isChecked);
                }
            }
        });

        View outsideInsideLayout = view.findViewById(R.id.out_and_inside_layout);
        outsideInsideLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outsideInside.toggle();
            }
        });

        View myCatchmentLayout = view.findViewById(R.id.my_catchment_layout);
        myCatchmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCatchment.toggle();
            }
        });


        firstName.addTextChangedListener(advancedSearchTextwatcher);

        lastName.addTextChangedListener(advancedSearchTextwatcher);


        zeirId.addTextChangedListener(advancedSearchTextwatcher);

        motherGuardianName.addTextChangedListener(advancedSearchTextwatcher);

        motherGuardianNrc.addTextChangedListener(advancedSearchTextwatcher);

        motherGuardianPhoneNumber.addTextChangedListener(advancedSearchTextwatcher);

        startDate = view.findViewById(R.id.start_date);
        endDate = view.findViewById(R.id.end_date);

        setDatePicker(startDate);
        setDatePicker(endDate);

        qrCodeButton = view.findViewById(R.id.qrCodeButton);
        qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() == null) {
                    return;
                }
                BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) getActivity();
                baseRegisterActivity.startQrCodeScanner();

                ((BaseChildRegisterActivity) getActivity()).setAdvancedSearch(true);
                ((BaseChildRegisterActivity) getActivity()).setAdvancedSearchFormData(createSelectedFieldMap());
            }
        });

        Button scanCardButton = view.findViewById(R.id.scanCardButton);
        scanCardButton.setVisibility(View.GONE);

        outsideInside = view.findViewById(R.id.out_and_inside);
        myCatchment = view.findViewById(R.id.my_catchment);

        outsideInside.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!Utils.isConnectedToNetwork(getActivity())) {
                    myCatchment.setChecked(true);
                    outsideInside.setChecked(false);
                } else {
                    myCatchment.setChecked(!isChecked);
                }
            }
        });

        myCatchment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!Utils.isConnectedToNetwork(getActivity())) {
                    myCatchment.setChecked(true);
                    outsideInside.setChecked(false);
                } else {
                    outsideInside.setChecked(!isChecked);
                }
            }
        });


        View mycatchmentLayout = view.findViewById(R.id.my_catchment_layout);
        mycatchmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCatchment.toggle();
            }
        });

        active = view.findViewById(R.id.active);
        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked && !inactive.isChecked() && !lostToFollowUp.isChecked()) {
                    active.setChecked(true);
                }
            }
        });
        inactive = view.findViewById(R.id.inactive);
        inactive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked && !active.isChecked() && !lostToFollowUp.isChecked()) {
                    inactive.setChecked(true);
                }
            }
        });
        lostToFollowUp = view.findViewById(R.id.lost_to_follow_up);
        lostToFollowUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked && !active.isChecked() && !inactive.isChecked()) {
                    lostToFollowUp.setChecked(true);
                }
            }
        });

        final View activeLayout = view.findViewById(R.id.active_layout);
        activeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active.toggle();
            }
        });

        final View inactiveLayout = view.findViewById(R.id.inactive_layout);
        inactiveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inactive.toggle();
            }
        });

        final View lostToFollowUpLayout = view.findViewById(R.id.lost_to_follow_up_layout);
        lostToFollowUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lostToFollowUp.toggle();
            }
        });

        resetForm();
    }

    private void assignedValuesBeforeBarcode() {
        if (searchFormData.size() > 0) {
            firstName.setText(searchFormData.get(DBConstants.KEY.FIRST_NAME));
            lastName.setText(searchFormData.get(DBConstants.KEY.LAST_NAME));
            motherGuardianName.setText(searchFormData.get(DBConstants.KEY.MOTHER_FIRST_NAME));
            motherGuardianNrc.setText(searchFormData.get(DBConstants.KEY.NRC_NUMBER));
            motherGuardianPhoneNumber.setText(searchFormData.get(DBConstants.KEY.CONTACT_PHONE_NUMBER));
            zeirId.setText(searchFormData.get(DBConstants.KEY.ZEIR_ID));
        }
    }

    private HashMap<String, String> createSelectedFieldMap() {
        HashMap<String, String> fields = new HashMap<>();
        fields.put(DBConstants.KEY.FIRST_NAME, firstName.getText().toString());
        fields.put(DBConstants.KEY.LAST_NAME, lastName.getText().toString());
        fields.put(DBConstants.KEY.MOTHER_FIRST_NAME, motherGuardianName.getText().toString());
        fields.put(DBConstants.KEY.NRC_NUMBER, motherGuardianNrc.getText().toString());
        fields.put(DBConstants.KEY.CONTACT_PHONE_NUMBER, motherGuardianPhoneNumber.getText().toString());
        fields.put(DBConstants.KEY.ZEIR_ID, zeirId.getText().toString());
        return fields;
    }

    private void checkTextFields() {
        if (!TextUtils.isEmpty(firstName.getText()) || !TextUtils.isEmpty(lastName.getText()) || !TextUtils.isEmpty(motherGuardianName.getText()) || !TextUtils.isEmpty(motherGuardianNrc.getText()) || !TextUtils.isEmpty(motherGuardianPhoneNumber.getText()) || !TextUtils.isEmpty(zeirId.getText())) {
            search.setEnabled(true);
            search.setTextColor(getResources().getColor(R.color.white));


            searchButton.setEnabled(true);
            searchButton.setTextColor(getResources().getColor(R.color.white));
        } else {
            search.setEnabled(false);
            search.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));

            searchButton.setEnabled(false);
            searchButton.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));
        }
    }

    @Override
    public void switchViews(boolean showList) {
        if (showList) {
            Utils.hideKeyboard(getActivity());

            advancedSearchForm.setVisibility(View.GONE);
            listViewLayout.setVisibility(View.VISIBLE);
            clientsView.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.GONE);

            if (titleLabelView != null) {
                titleLabelView.setText(getString(R.string.search_results));
            }

            updateMatchingResults(0);
            showProgressView();
            listMode = true;
        } else {
            clearSearchCriteria();
            advancedSearchForm.setVisibility(View.VISIBLE);
            listViewLayout.setVisibility(View.GONE);
            clientsView.setVisibility(View.INVISIBLE);
            backButton.setVisibility(View.GONE);
            searchButton.setVisibility(View.VISIBLE);

            if (titleLabelView != null) {
                titleLabelView.setText(getString(R.string.advanced_search));
            }


            listMode = false;
        }
    }

    private void updateSearchLimits() {
        if (Utils.isConnectedToNetwork(getActivity())) {
            outsideInside.setChecked(true);
            myCatchment.setChecked(false);
        } else {
            myCatchment.setChecked(true);
            outsideInside.setChecked(false);
        }

        if (connectionChangeReciever == null) {
            connectionChangeReciever = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!Utils.isConnectedToNetwork(getActivity())) {
                        myCatchment.setChecked(true);
                        outsideInside.setChecked(false);
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            getActivity().registerReceiver(connectionChangeReciever, intentFilter);
            registeredConnectionChangeReceiver = true;
        }

    }

    private void resetForm() {
        clearSearchCriteria();
        clearMatchingResults();
        clearFormFields();
    }

    private void clearFormFields() {
        active.setChecked(true);
        inactive.setChecked(false);
        lostToFollowUp.setChecked(false);

        zeirId.setText("");
        firstName.setText("");
        lastName.setText("");
        motherGuardianName.setText("");
        motherGuardianNrc.setText("");
        motherGuardianPhoneNumber.setText("");

        startDate.setText("");
        endDate.setText("");
    }

    private void clearSearchCriteria() {
        if (searchCriteria != null) {
            searchCriteria.setVisibility(View.GONE);
            searchCriteria.setText("");
        }
    }

    private void clearMatchingResults() {
        if (matchingResults != null) {
            matchingResults.setVisibility(View.GONE);
            matchingResults.setText("");
        }
    }


    public void updateMatchingResults(int count) {
        if (matchingResults != null) {
            matchingResults.setText(String.format(getString(R.string.matching_results), String.valueOf(count)));
            matchingResults.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateSearchCriteria(String searchCriteriaString) {
        if (searchCriteria != null) {
            searchCriteria.setText(searchCriteriaString);
            searchCriteria.setVisibility(View.VISIBLE);
        }
    }


    private void setDatePicker(final EditText editText) {
        editText.setOnClickListener(new DatePickerListener(getActivity(), editText, true));
    }

    @Override
    public void setupSearchView(View view) {
        // TODO implement this
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomePatientRegisterCondition();
    }

    private void search() {

        if (myCatchment.isChecked()) {
            isLocal = true;
        } else if (outsideInside.isChecked()) {
            isLocal = false;
        }


        ((ChildAdvancedSearchContract.Presenter) presenter).search(getSearchMap(), isLocal);
    }

    public Map<String, String> getSearchMap() {

        Map<String, String> searchParams = new HashMap<>();

        String fn = firstName.getText().toString();
        String ln = lastName.getText().toString();


        String motherGuardianNameString = motherGuardianName.getText().toString();

        String motherGuardianNrcString = motherGuardianNrc.getText().toString();

        String motherGuardianPhoneNumberString = motherGuardianPhoneNumber.getText().toString();

        String zeir = zeirId.getText().toString();


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

    @Override
    public void recalculatePagination(AdvancedMatrixCursor matrixCursor) {
        super.recalculatePagination(matrixCursor);
        updateMatchingResults(clientAdapter.getTotalcount());
    }

    @Override
    public void showNotFoundPopup(String opensrpID) {
        //Todo implement this
    }

    @Override
    public void countExecute() {
        Cursor cursor = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            String query = "";

            sqb.addCondition(filters);
            query = sqb.orderbyCondition(Sortqueries);
            query = sqb.Endquery(query);

            Log.i(getClass().getName(), query);
            cursor = commonRepository().rawCustomQueryForAdapter(query);
            cursor.moveToFirst();
            clientAdapter.setTotalcount(cursor.getInt(0));
            Log.v("total count here", "" + clientAdapter.getTotalcount());

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);

        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        updateMatchingResults(clientAdapter.getTotalcount());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                // Returns a new CursorLoader
                return new CursorLoader(getActivity()) {
                    @Override
                    public Cursor loadInBackground() {
                        AdvancedMatrixCursor matrixCursor = ((BaseChildAdvancedSearchPresenter) presenter).getMatrixCursor();
                        if (isLocal || matrixCursor == null) {
                            String query = filterAndSortQuery();
                            Cursor cursor = commonRepository().rawCustomQueryForAdapter(query);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressView();
                                }
                            });

                            return cursor;
                        } else {
                            return matrixCursor;
                        }
                    }
                };
            default:
                // An invalid id was passed in
                return null;
        }
    }


    @Override
    public String filterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try {
            sqb.addCondition(filters);
            query = sqb.orderbyCondition(Sortqueries);
            query = sqb.Endquery(
                    sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));
        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString(), e);
        }

        return query;
    }

    @Override
    public Cursor getRawCustomQueryForAdapter(String query) {
        return commonRepository().rawCustomQueryForAdapter(query);
    }

    @Override
    public void onResume() {
        super.onResume();
        assignedValuesBeforeBarcode();
    }

    private class AdvancedSearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Todo later
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkTextFields();
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkTextFields();
        }
    }

}
