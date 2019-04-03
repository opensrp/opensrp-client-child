package org.smartregister.child.sample.fragment;

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

import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.child.provider.AdvancedSearchClientsProvider;
import org.smartregister.child.sample.R;
import org.smartregister.child.sample.activity.ChildRegisterActivity;
import org.smartregister.child.sample.contract.AdvancedSearchContract;
import org.smartregister.child.sample.listener.DatePickerListener;
import org.smartregister.child.sample.presenter.AdvancedSearchPresenter;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.DBQueryHelper;
import org.smartregister.child.util.Utils;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import java.util.HashMap;
import java.util.Set;

public class AdvancedSearchFragment extends ChildRegisterFragment
        implements AdvancedSearchContract.View, ChildRegisterFragmentContract.View {

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

    @Override
    protected void initializePresenter() {
        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new AdvancedSearchPresenter(this, viewConfigurationIdentifier);

    }

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
        } /*else if (view.getId() == R.id.undo_button) {
            ((BaseRegisterActivity) getActivity()).switchToBaseFragment();
            ((BaseRegisterActivity) getActivity()).setSelectedBottomBarMenuItem(R.id.action_clients);
            ((BaseRegisterActivity) getActivity()).setSearchTerm("");
        } else if (view.getId() == R.id.back_button) {
            switchViews(false);
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
        searchButton = view.findViewById(R.id.search);
        searchButton.setOnClickListener(this);

        searchCriteria = view.findViewById(R.id.search_criteria);
        matchingResults = view.findViewById(R.id.matching_results);

        populateFormViews(view);

    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        RepositoryHolder repoHolder = new RepositoryHolder();
        ChildRegisterActivity activity = (ChildRegisterActivity) getActivity();

        repoHolder.setWeightRepository(activity.getWeightRepository());
        repoHolder.setVaccineRepository(activity.getVaccineRepository());
        repoHolder.setCommonRepository(commonRepository());

        AdvancedSearchClientsProvider advancedSearchProvider = new AdvancedSearchClientsProvider(getActivity(), repoHolder,
                visibleColumns, registerActionHandler, paginationViewHandler, ChildLibrary.getInstance().context().alertService());
        clientAdapter = new RecyclerViewPaginatedAdapter(null, advancedSearchProvider,
                context().commonrepository(this.tablename));
        clientsView.setAdapter(clientAdapter);
    }

    private void populateFormViews(View view) {
        search = view.findViewById(R.id.search);
        search.setEnabled(false);
        search.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));

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


        firstName = view.findViewById(R.id.first_name);
        firstName.addTextChangedListener(advancedSearchTextwatcher);

        lastName = view.findViewById(R.id.last_name);
        lastName.addTextChangedListener(advancedSearchTextwatcher);


        zeirId = view.findViewById(org.smartregister.child.R.id.zeir_id);
        firstName = view.findViewById(org.smartregister.child.R.id.first_name);
        lastName = view.findViewById(org.smartregister.child.R.id.last_name);
        motherGuardianName = view.findViewById(org.smartregister.child.R.id.mother_guardian_name);
        motherGuardianNrc = view.findViewById(org.smartregister.child.R.id.mother_guardian_nrc);
        motherGuardianPhoneNumber = view.findViewById(org.smartregister.child.R.id.mother_guardian_phone_number);

        startDate = view.findViewById(org.smartregister.child.R.id.start_date);
        endDate = view.findViewById(org.smartregister.child.R.id.end_date);

        search.setOnClickListener(this);

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

                ((ChildRegisterActivity) getActivity()).setAdvancedSearch(true);
                ((ChildRegisterActivity) getActivity()).setAdvancedSearchFormData(createSelectedFieldMap());
            }
        });


        outsideInside = view.findViewById(org.smartregister.child.R.id.out_and_inside);
        myCatchment = view.findViewById(org.smartregister.child.R.id.my_catchment);

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


        View mycatchmentLayout = view.findViewById(org.smartregister.child.R.id.my_catchment_layout);
        mycatchmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCatchment.toggle();
            }
        });

        active = view.findViewById(org.smartregister.child.R.id.active);
        active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked && !inactive.isChecked() && !lostToFollowUp.isChecked()) {
                    active.setChecked(true);
                }
            }
        });
        inactive = view.findViewById(org.smartregister.child.R.id.inactive);
        inactive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked && !active.isChecked() && !lostToFollowUp.isChecked()) {
                    inactive.setChecked(true);
                }
            }
        });
        lostToFollowUp = view.findViewById(org.smartregister.child.R.id.lost_to_follow_up);
        lostToFollowUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked && !active.isChecked() && !inactive.isChecked()) {
                    lostToFollowUp.setChecked(true);
                }
            }
        });

        final View activeLayout = view.findViewById(org.smartregister.child.R.id.active_layout);
        activeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active.toggle();
            }
        });

        final View inactiveLayout = view.findViewById(org.smartregister.child.R.id.inactive_layout);
        inactiveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inactive.toggle();
            }
        });

        final View lostToFollowUpLayout = view.findViewById(org.smartregister.child.R.id.lost_to_follow_up_layout);
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
        }
    }

    private HashMap<String, String> createSelectedFieldMap() {
        HashMap<String, String> fields = new HashMap<>();
        fields.put(DBConstants.KEY.FIRST_NAME, firstName.getText().toString());
        fields.put(DBConstants.KEY.LAST_NAME, lastName.getText().toString());
        return fields;
    }


    private void checkTextFields() {
        if (!TextUtils.isEmpty(firstName.getText()) || !TextUtils
                .isEmpty(lastName.getText())) {
            search.setEnabled(true);
            search.setTextColor(getResources().getColor(R.color.white));
            search.setOnClickListener(registerActionHandler);
        } else {
            search.setEnabled(false);
            search.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));
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

        firstName.setText("");
        lastName.setText("");
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

        String fn = firstName.getText().toString();
        String ln = lastName.getText().toString();

        if (myCatchment.isChecked()) {
            isLocal = true;
        } else if (outsideInside.isChecked()) {
            isLocal = false;
        }

        ((AdvancedSearchContract.Presenter) presenter).search(fn, ln, "", "", "", "", "", isLocal);
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
                        AdvancedMatrixCursor matrixCursor = ((AdvancedSearchPresenter) presenter).getMatrixCursor();
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