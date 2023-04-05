package org.smartregister.child.fragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildImmunizationActivity;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.domain.MoveToCatchmentEvent;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.child.provider.AdvancedSearchClientsProvider;
import org.smartregister.child.util.AppExecutors;
import org.smartregister.child.util.ChildAppProperties;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.MoveToMyCatchmentUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.child.widgets.AdvanceSearchDatePickerDialog;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.FetchStatus;
import org.smartregister.event.Listener;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

public abstract class BaseAdvancedSearchFragment extends BaseChildRegisterFragment
        implements ChildAdvancedSearchContract.View, ChildRegisterFragmentContract.View, View.OnClickListener {
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    private final Listener<MoveToCatchmentEvent> moveToMyCatchmentListener = moveToCatchmentEvent -> {
        if (moveToCatchmentEvent != null) {
            if (ChildJsonFormUtils.processMoveToCatchment(context(), moveToCatchmentEvent)) {
                clientAdapter.notifyDataSetChanged();
                ((BaseRegisterActivity) requireActivity()).refreshList(FetchStatus.fetched);
                ((BaseRegisterActivity) requireActivity()).switchToBaseFragment();

                Utils.showToast(requireActivity(), requireActivity().getString(R.string.move_to_catchment_success_message));
            } else {
                Utils.showShortToast(requireActivity(), requireActivity().getString(R.string.an_error_occured));
            }
        } else {
            Utils.showShortToast(requireActivity(), requireActivity().getString(R.string.unable_to_move_to_my_catchment));
        }
    };
    protected AdvancedSearchTextWatcher advancedSearchTextwatcher = new AdvancedSearchTextWatcher();
    protected HashMap<String, String> searchFormData = new HashMap<>();
    protected CheckBox active;
    protected CheckBox inactive;
    protected CheckBox lostToFollowUp;
    protected EditText startDate;
    protected EditText endDate;
    protected Button qrCodeButton;
    protected Map<String, View> advancedFormSearchableFields = new HashMap<>();
    private View listViewLayout;
    private View advancedSearchForm;
    private ImageButton backButton;
    private Button searchButton;
    private Button advancedSearchToolbarSearchButton;
    private RadioButton outsideInside;
    private RadioButton myCatchment;
    private TextView searchCriteria;
    private TextView matchingResults;
    private boolean listMode = false;
    private boolean isLocal = false;
    private BroadcastReceiver connectionChangeReciever;
    private boolean registeredConnectionChangeReceiver = false;
    private ProgressDialog progressDialog;
    private AdvanceSearchDatePickerDialog startDateDatePicker;
    private AdvanceSearchDatePickerDialog endDateDatePicker;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(
            FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN,
            Locale.getDefault().toString().startsWith("ar") ? Locale.ENGLISH : Locale.getDefault()
    );

    @Override
    protected void initializePresenter() {
        presenter = getPresenter();
        initProgressDialog();
    }

    private void initProgressDialog() {
        progressDialog = new ProgressDialog(requireActivity());
        progressDialog.setCancelable(false);
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
            requireActivity().unregisterReceiver(connectionChangeReciever);
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
            ((BaseRegisterActivity) requireActivity()).switchToBaseFragment();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onViewClicked(View view) {
        if (view.getId() == R.id.record_growth && view.getTag() != null) {
            if (view.getTag() instanceof String) {
                recordService((String) view.getTag());
            } else if (view.getTag() instanceof CommonPersonObjectClient) {
                recordGrowth(view);
            }
        } else if (view.getId() == R.id.search) {
            search();
        } else if (view.getId() == R.id.advanced_form_search_btn) {
            search();
        } else if (view.getId() == R.id.back_button) {
            switchViews(false);
        } else if ((view.getId() == R.id.patient_column || view.getId() == R.id.child_profile_info_layout)
                && view.getTag() != null) {
            recordGrowth(view);
        } else if (view.getId() == R.id.move_to_catchment && view.getTag(R.id.move_to_catchment_ids) != null && view.getTag(R.id.move_to_catchment_ids) instanceof List) {
            List<String> ids = (List<String>) view.getTag(R.id.move_to_catchment_ids);
            moveToMyCatchmentArea(ids);
        }
    }

    private void recordGrowth(View view) {
        RegisterClickables registerClickables = new RegisterClickables();
        if (view.getTag(org.smartregister.child.R.id.record_action) != null) {
            registerClickables.setRecordWeight(Constants.RECORD_ACTION.GROWTH.equals(view.getTag(org.smartregister.child.R.id.record_action)));
            registerClickables.setRecordAll(Constants.RECORD_ACTION.VACCINATION.equals(view.getTag(org.smartregister.child.R.id.record_action)));
            registerClickables.setNextAppointmentDate(view.getTag(R.id.next_appointment_date) != null ? String.valueOf(view.getTag(R.id.next_appointment_date)) : "");
        }
        BaseChildImmunizationActivity.launchActivity(requireActivity(), (CommonPersonObjectClient) view.getTag(), registerClickables);
    }

    protected void recordService(String openSrpId) {
        try {
            ChildJsonFormUtils.startForm(requireActivity(), ChildJsonFormUtils.REQUEST_CODE_GET_JSON, getOutOfCatchmentServiceFormName(), openSrpId,
                    ChildJsonFormUtils.getProviderLocationId(requireContext()));
        } catch (Exception e) {
            Utils.showShortToast(requireActivity(), getString(R.string.error_recording_out_of_catchment_service));
            Timber.e(e, "Error recording Out of Catchment Service");
        }
    }

    protected String getOutOfCatchmentServiceFormName() {
        return Constants.JsonForm.OUT_OF_CATCHMENT_SERVICE;
    }

    private void moveToMyCatchmentArea(final List<String> ids) {
        if (ChildLibrary.getInstance().getProperties().isTrue(ChildAppProperties.KEY.NOVEL.OUT_OF_CATCHMENT)) {

            showMoveToCatchmentChoiceDialog(ids);

        } else {

            showMoveToCatchmentDialog(ids, true);
        }
    }

    private void showMoveToCatchmentChoiceDialog(final List<String> ids) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(requireActivity().getString(R.string.choose_how));

        String[] choices = {requireActivity().getString(R.string.permanently), requireActivity().getString(R.string.temporarily)};
        builder.setItems(choices, (dialog, which) -> showMoveToCatchmentDialog(ids, which == 0));
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void showMoveToCatchmentDialog(final List<String> ids, final boolean isPermanent) {
        AlertDialog dialog = new AlertDialog.Builder(requireActivity(), R.style.PathAlertDialog)
                .setMessage(R.string.move_to_catchment_confirm_dialog_message)
                .setTitle(ChildLibrary.getInstance().getProperties().isTrue(ChildAppProperties.KEY.NOVEL.OUT_OF_CATCHMENT) ? requireActivity().getString(R.string.move_to_catchment_confirm_dialog_title_, isPermanent ? requireActivity().getString(R.string.permanently) : requireActivity().getString(R.string.temporarily)) : requireActivity().getString(R.string.move_to_catchment_confirm_dialog_title))
                .setCancelable(false)
                .setPositiveButton(R.string.no_button_label, null)
                .setNegativeButton(R.string.yes_button_label,
                        (dialog1, whichButton) -> {
                            progressDialog.setTitle(R.string.move_to_catchment_dialog_title);
                            progressDialog.setMessage(getString(R.string.move_to_catchment_dialog_message));
                            MoveToMyCatchmentUtils.moveToMyCatchment(ids, moveToMyCatchmentListener, progressDialog, isPermanent);
                        }).create();

        dialog.show();
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        listViewLayout = view.findViewById(R.id.advanced_search_list);
        listViewLayout.setVisibility(View.GONE);

        advancedSearchForm = view.findViewById(R.id.advanced_search_form);
        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        searchCriteria = view.findViewById(R.id.search_criteria);
        matchingResults = view.findViewById(R.id.matching_results);
        advancedSearchToolbarSearchButton = view.findViewById(R.id.search);
        searchButton = view.findViewById(R.id.advanced_form_search_btn);
        outsideInside = view.findViewById(R.id.out_and_inside);
        myCatchment = view.findViewById(R.id.my_catchment);

        populateFormViews(view);

        populateSearchableFields(view);

        resetForm();

    }

    public abstract void populateSearchableFields(View view);

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {

        RepositoryHolder repoHolder = new RepositoryHolder();

        repoHolder.setWeightRepository(GrowthMonitoringLibrary.getInstance().weightRepository());
        repoHolder.setHeightRepository(GrowthMonitoringLibrary.getInstance().heightRepository());
        repoHolder.setVaccineRepository(ImmunizationLibrary.getInstance().vaccineRepository());
        repoHolder.setCommonRepository(commonRepository());

        AdvancedSearchClientsProvider advancedSearchProvider = new AdvancedSearchClientsProvider(requireActivity(), repoHolder, visibleColumns, registerActionHandler, paginationViewHandler, ChildLibrary.getInstance().context().alertService());

        clientAdapter = new RecyclerViewPaginatedAdapter(null, advancedSearchProvider, context().commonrepository(this.tablename));
        clientsView.setAdapter(clientAdapter);
    }

    protected void populateFormViews(View view) {

        setUpSearchButtons();

        setUpMyCatchmentControls(view, outsideInside, myCatchment, R.id.out_and_inside_layout);

        setUpMyCatchmentControls(view, myCatchment, outsideInside, R.id.my_catchment_layout);

        startDate = view.findViewById(R.id.start_date);
        startDate.setTag(R.id.type, START_DATE);
        startDate.setOnClickListener(this);
        startDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Overridden
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Overridden
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (StringUtils.isNoneBlank(editable))
                        endDateDatePicker.getDatePickerDialog().setMinDate(dateFormatter.parse(editable.toString()).getTime());
                } catch (ParseException e) {
                    Timber.e(e, "Error setting end date minimum to start date");
                }
                startDate.setError(null);
            }
        });
        startDateDatePicker = new AdvanceSearchDatePickerDialog(startDate);
        startDateDatePicker.setDateFormatter(dateFormatter);

        endDate = view.findViewById(R.id.end_date);
        endDate.setTag(R.id.type, END_DATE);
        endDate.setOnClickListener(this);
        endDate.setEnabled(false);
        endDateDatePicker = new AdvanceSearchDatePickerDialog(endDate);
        endDateDatePicker.setDateFormatter(dateFormatter);

        setUpQRCodeButton(view);

        setUpScanCardButton(view);

        setUpMyCatchmentControls(view, myCatchment, outsideInside, R.id.my_catchment_layout);

        active = view.findViewById(R.id.active);
        active.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!isChecked && !inactive.isChecked() && !lostToFollowUp.isChecked()) {
                active.setChecked(true);
            }
        });
        inactive = view.findViewById(R.id.inactive);
        inactive.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!isChecked && !active.isChecked() && !lostToFollowUp.isChecked()) {
                inactive.setChecked(true);
            }
        });
        lostToFollowUp = view.findViewById(R.id.lost_to_follow_up);
        lostToFollowUp.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!isChecked && !active.isChecked() && !inactive.isChecked()) {
                lostToFollowUp.setChecked(true);
            }
        });

        final View activeLayout = view.findViewById(R.id.active_layout);
        activeLayout.setOnClickListener(v -> active.toggle());

        final View inactiveLayout = view.findViewById(R.id.inactive_layout);
        inactiveLayout.setOnClickListener(v -> inactive.toggle());

        final View lostToFollowUpLayout = view.findViewById(R.id.lost_to_follow_up_layout);
        lostToFollowUpLayout.setOnClickListener(v -> lostToFollowUp.toggle());
    }

    private void setUpMyCatchmentControls(View view, final RadioButton myCatchment,
                                          final RadioButton outsideInside, int p) {
        myCatchment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Trigger for myCatchment only, when there is no internet connectivity
            if (!Utils.isConnectedToNetwork(requireActivity()) && myCatchment.getId() == R.id.my_catchment) {
                outsideInside.setChecked(false);
                myCatchment.setChecked(true);
            } else {
                outsideInside.setChecked(!isChecked);
            }
        });

        View myCatchmentLayout = view.findViewById(p);
        myCatchmentLayout.setOnClickListener(v -> myCatchment.toggle());
    }

    private void setUpSearchButtons() {
        advancedSearchToolbarSearchButton.setEnabled(false);
        advancedSearchToolbarSearchButton.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));
        advancedSearchToolbarSearchButton.setOnClickListener(registerActionHandler);

        searchButton.setEnabled(false);
        searchButton.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));
        searchButton.setOnClickListener(registerActionHandler);
    }

    private void setUpQRCodeButton(View view) {
        qrCodeButton = view.findViewById(R.id.qrCodeButton);
        if (!ChildLibrary.getInstance().getProperties().hasProperty(ChildAppProperties.KEY.FEATURE_SCAN_QR_ENABLED) || ChildLibrary.getInstance().getProperties().getPropertyBoolean(ChildAppProperties.KEY.FEATURE_SCAN_QR_ENABLED)) {
            qrCodeButton.setOnClickListener(view1 -> {
                if (requireActivity() == null) {
                    return;
                }
                BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) requireActivity();
                baseRegisterActivity.startQrCodeScanner();

                ((BaseChildRegisterActivity) requireActivity()).setAdvancedSearch(true);
                ((BaseChildRegisterActivity) requireActivity()).setAdvancedSearchFormData(createSelectedFieldMap());
            });
        } else {
            qrCodeButton.setVisibility(View.GONE);
        }
    }

    private void setUpScanCardButton(View view) {
        Button scanCardButton = view.findViewById(R.id.scanCardButton);

        if (ChildLibrary.getInstance().getProperties().getPropertyBoolean(ChildAppProperties.KEY.FEATURE_NFC_CARD_ENABLED)) {
            scanCardButton.setVisibility(View.VISIBLE);//should be visible

            ((View) view.findViewById(R.id.card_id).getParent()).setVisibility(View.VISIBLE);
        }
    }

    public abstract void assignedValuesBeforeBarcode();

    protected abstract HashMap<String, String> createSelectedFieldMap();

    private void checkTextFields() {
        if (anySearchableFieldHasValue()) {
            advancedSearchToolbarSearchButton.setEnabled(true);
            advancedSearchToolbarSearchButton.setTextColor(getResources().getColor(R.color.white));

            searchButton.setEnabled(true);
            searchButton.setTextColor(getResources().getColor(R.color.white));
        } else {
            advancedSearchToolbarSearchButton.setEnabled(false);
            advancedSearchToolbarSearchButton.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));

            searchButton.setEnabled(false);
            searchButton.setTextColor(getResources().getColor(R.color.contact_complete_grey_border));
        }
    }

    private boolean anySearchableFieldHasValue() {
        for (Map.Entry<String, View> entry : advancedFormSearchableFields.entrySet()) {
            if (entry.getValue() instanceof TextView && !TextUtils.isEmpty(((TextView) entry.getValue()).getText())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void switchViews(boolean showList) {
        if (showList) {
            Utils.hideKeyboard(requireActivity());

            advancedSearchForm.setVisibility(View.GONE);
            listViewLayout.setVisibility(View.VISIBLE);
            clientsView.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
            searchButton.setVisibility(View.GONE);
            advancedSearchToolbarSearchButton.setVisibility(View.GONE);

            if (titleLabelView != null) {
                titleLabelView.setText(getString(R.string.search_results));
            }

            // hide result count , should be dynamic
            if (matchingResults != null) {
                matchingResults.setVisibility(View.GONE);
            }

            showProgressView();
            listMode = true;
        } else {
            clearSearchCriteria();
            advancedSearchForm.setVisibility(View.VISIBLE);
            listViewLayout.setVisibility(View.GONE);
            clientsView.setVisibility(View.INVISIBLE);
            searchButton.setVisibility(View.VISIBLE);
            advancedSearchToolbarSearchButton.setVisibility(View.VISIBLE);

            if (titleLabelView != null) {
                titleLabelView.setText(getString(R.string.advanced_search));
            }

            listMode = false;
        }
    }

    private void updateSearchLimits() {
        if (Utils.isConnectedToNetwork(requireActivity())) {
            outsideInside.setChecked(true);
            myCatchment.setChecked(false);
        } else {
            outsideInside.setChecked(false);
            myCatchment.setChecked(true);
        }

        if (connectionChangeReciever == null) {
            connectionChangeReciever = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (!Utils.isConnectedToNetwork(requireActivity())) {
                        outsideInside.setChecked(false);
                        myCatchment.setChecked(true);
                    }
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            requireActivity().registerReceiver(connectionChangeReciever, intentFilter);
            registeredConnectionChangeReceiver = true;
        }

    }

    private void resetForm() {
        clearSearchCriteria();
        clearMatchingResults();
        clearFormFields();
    }


    protected void clearFormFields() {
        active.setChecked(true);
        inactive.setChecked(false);
        lostToFollowUp.setChecked(false);
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

    @Override
    public void updateMatchingResults(int count) {
        if (matchingResults != null) {
            matchingResults.setText(getString(R.string.matching_results, String.valueOf(count)));
            matchingResults.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateSearchCriteria(String searchCriteriaString) {
        if (searchCriteria != null) {
            searchCriteria.setText(Html.fromHtml(searchCriteriaString));
            searchCriteria.setVisibility(View.VISIBLE);
        }
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
        return ((BaseChildAdvancedSearchPresenter) presenter).getCurrentCondition();
    }

    public void search() {
        if (myCatchment.isChecked()) {
            isLocal = true;
        } else if (outsideInside.isChecked()) {
            isLocal = false;
        }

        Map<String, String> editMap = getSearchMap(!isLocal);

        //Do not search when only one of the birth dates are provided
        if (editMap.containsKey(START_DATE) && !editMap.containsKey(END_DATE)) {
            endDate.setError(getString(R.string.end_date_required));
            return;
        }
        if (editMap.containsKey(END_DATE) && !editMap.containsKey(START_DATE)) {
            startDate.setError(getString(R.string.start_date_required));
            return;
        }

        ((ChildAdvancedSearchContract.Presenter) presenter).search(editMap, isLocal);
    }

    protected abstract Map<String, String> getSearchMap(boolean outOfArea);

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
    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {
        this.searchFormData = advancedSearchFormData;
    }

    @Override
    public void countExecute() {
        try {
            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    String sql = ((BaseChildAdvancedSearchPresenter) presenter).getCountQuery();
                    Timber.i(sql);
                    int totalCount = commonRepository().countSearchIds(sql);
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            clientAdapter.setTotalcount(totalCount);
                            Timber.i("Total Register Count %d", clientAdapter.getTotalcount());
                            updateMatchingResults(totalCount);

                            clientAdapter.setCurrentlimit(getPageLimit());
                            clientAdapter.setCurrentoffset(0);
                        }
                    });
                }
            });


        } catch (Exception e) {
            Timber.e(e);
        }

    }

    @Override
    public @NotNull Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            return new AdvanceSearchCursorLoader(this);
        }
        return new CursorLoader(requireContext());
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
            Timber.e(e);
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
        //Do not show for sync status on advance search screen
        SyncStatusBroadcastReceiver.getInstance().removeSyncStatusListener(this);
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

    static class AdvanceSearchCursorLoader extends CursorLoader {

        private final BaseAdvancedSearchFragment advancedSearchFragment;

        public AdvanceSearchCursorLoader(BaseAdvancedSearchFragment advancedSearchFragment) {
            super(advancedSearchFragment.requireActivity());
            this.advancedSearchFragment = advancedSearchFragment;
        }

        @Override
        public Cursor loadInBackground() {
            AdvancedMatrixCursor matrixCursor = ((BaseChildAdvancedSearchPresenter) advancedSearchFragment.presenter).getMatrixCursor();
            advancedSearchFragment.requireActivity();
            if (advancedSearchFragment.isLocal || matrixCursor == null) {
                String query = advancedSearchFragment.filterAndSortQuery();
                Cursor cursor = advancedSearchFragment.commonRepository().rawCustomQueryForAdapter(query);
                advancedSearchFragment.requireActivity().runOnUiThread(advancedSearchFragment::hideProgressView);
                return cursor;
            } else {
                return matrixCursor;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof EditText) {
            Calendar currentDate = Calendar.getInstance();
            final EditText editText = (EditText) view;
            String previousDateString = editText.getText().toString();
            if (StringUtils.isNoneBlank(previousDateString)) {
                try {
                    currentDate.setTime(dateFormatter.parse(previousDateString));
                } catch (ParseException e) {
                    Timber.e(e, "Error parsing Advance Search Date: %s", e.getMessage());
                }
            }

            if (editText.getTag(R.id.type).equals(START_DATE)) {
                startDateDatePicker.setCurrentDate(currentDate);
                startDateDatePicker.showDialog();
                endDate.setEnabled(true);
                startDate.setError(null);
            } else if (editText.getTag(R.id.type).equals(END_DATE)) {
                endDate.setError(null);
                endDateDatePicker.setCurrentDate(currentDate);
                endDateDatePicker.showDialog();
            }
        }
    }

}
