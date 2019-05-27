package org.smartregister.child.fragment;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.child.presenter.BaseChildRegisterFragmentPresenter;
import org.smartregister.child.provider.ChildRegisterProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBQueryHelper;
import org.smartregister.child.util.Utils;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.AlertStatus;
import org.smartregister.domain.FetchStatus;
import org.smartregister.growthmonitoring.GrowthMonitoringLibrary;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.util.VaccinateActionUtils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public abstract class BaseChildRegisterFragment extends BaseRegisterFragment implements ChildRegisterFragmentContract.View, SyncStatusBroadcastReceiver.SyncStatusListener, View.OnClickListener {

    private static String DOD_MAIN_CONDITION = " ( " + Constants.KEY.DOD + " is NULL OR " + Constants.KEY.DOD + " = '' ) ";
    private View filterSection;
    private int dueOverdueCount = 0;
    private LocationPickerView clinicSelection;
    private TextView overdueCountTV;

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomePatientRegisterCondition();
    }

    @Override
    protected abstract String getDefaultSortQuery();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View view = inflater.inflate(R.layout.smart_register_activity_customized, container, false);
        mView = view;
        onInitialization();
        setupViews(view);
        onResumption();
        return view;
    }

    @Override
    protected void onResumption() {
        super.onResumption();

        if (filterMode()) {
            toggleFilterSelection();
        }

        updateSearchView();

        updateLocationText();

    }

    @Override
    public void onResume() {
        super.onResume();

        AllSharedPreferences allSharedPreferences = context().allSharedPreferences();
        if (!allSharedPreferences.fetchIsSyncInitial() || !SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            org.smartregister.util.Utils.startAsyncTask(new CountDueAndOverDue(), null);
        }
    }

    private boolean filterMode() {
        return filterSection != null && filterSection.getTag() != null;
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        CustomFontTextView buttonReportMonth = view.findViewById(R.id.btn_report_month);
        if (buttonReportMonth != null) {
            buttonReportMonth.setVisibility(View.INVISIBLE);
            view.findViewById(R.id.service_mode_selection).setVisibility(View.INVISIBLE);

            // Update top left icon
            FrameLayout qrCodeScanImageView = view.findViewById(R.id.scan_qr_code);
            if (qrCodeScanImageView != null) {
                qrCodeScanImageView.setOnClickListener(this);
            }

            FrameLayout scanCardView = view.findViewById(R.id.scan_card);
            if (scanCardView != null) {
                scanCardView.setOnClickListener(this);
                scanCardView.setVisibility(View.GONE);
            }

            // Update title name
            ImageView logo = view.findViewById(R.id.opensrp_logo_image_view);
            if (logo != null) {
                logo.setVisibility(View.GONE);
            }

            CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
            if (titleView != null) {
                titleView.setVisibility(View.GONE);
            }

            filterSection = view.findViewById(R.id.filter_selection);
            filterSection.setOnClickListener(this);

            clinicSelection = view.findViewById(R.id.clinic_selection);
            clinicSelection.init();


            clientsView.setVisibility(View.VISIBLE);
            clientsProgressView.setVisibility(View.INVISIBLE);
            setServiceModeViewDrawableRight(null);


            TextView nameInitials = view.findViewById(R.id.name_inits);
            nameInitials.setVisibility(View.GONE);
            LinearLayout btnBackToHome = view.findViewById(R.id.btn_back_to_home);
            btnBackToHome.setOnClickListener(this);
            syncButton = view.findViewById(R.id.back_button);
            syncButton.setVisibility(View.VISIBLE);
            syncButton.setImageResource(R.drawable.ic_action_menu);
            syncButton.setOnClickListener(this);


            View globalSearchButton = view.findViewById(R.id.global_search);
            globalSearchButton.setOnClickListener(this);

            overdueCountTV = view.findViewById(R.id.filter_count);
            overdueCountTV.setVisibility(View.GONE);
        }

    }

    public void updateDueOverdueCountText(int overDueCount) {
        if (overdueCountTV != null) {
            if (overDueCount > 0) {
                overdueCountTV.setText(String.valueOf(overDueCount));
                overdueCountTV.setVisibility(View.VISIBLE);
                overdueCountTV.setClickable(true);
            } else {
                overdueCountTV.setVisibility(View.GONE);
                overdueCountTV.setClickable(false);
            }
        } else {
            Log.e(BaseChildRegisterFragment.class.getCanonicalName(), "Over Due Count Text View (overdueCountTV) is NULL ...whyyy?");
        }
    }


    protected void toggleFilterSelection() {
        if (filterSection != null) {
            String tagString = "PRESSED";
            if (filterSection.getTag() == null) {
                filter("", "", filterSelectionCondition(false), false);
                filterSection.setTag(tagString);
                filterSection.setBackgroundResource(R.drawable.transparent_clicked_background);
            } else if (filterSection.getTag().toString().equals(tagString)) {
                updateSortAndFilter(null, null);
                filterSection.setTag(null);
                filterSection.setBackgroundResource(R.drawable.transparent_gray_background);
            }
        }
    }

    private String filterSelectionCondition(boolean urgentOnly) {
        final String AND = " AND ";
        final String OR = " OR ";
        final String IS_NULL_OR = " IS NULL OR ";
        final String TRUE = "'true'";

        String mainCondition = DOD_MAIN_CONDITION +
                AND + " (" + Constants.CHILD_STATUS.INACTIVE + IS_NULL_OR + Constants.CHILD_STATUS.INACTIVE + " != " + TRUE + " ) " +
                AND + " (" + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + IS_NULL_OR + Constants.CHILD_STATUS.LOST_TO_FOLLOW_UP + " != " + TRUE + " ) " +
                AND + " ( ";
        ArrayList<VaccineRepo.Vaccine> vaccines = VaccineRepo.getVaccines("child");

        vaccines.remove(VaccineRepo.Vaccine.bcg2);
        vaccines.remove(VaccineRepo.Vaccine.ipv);
        vaccines.remove(VaccineRepo.Vaccine.opv0);
        vaccines.remove(VaccineRepo.Vaccine.opv4);
        vaccines.remove(VaccineRepo.Vaccine.measles1);
        vaccines.remove(VaccineRepo.Vaccine.mr1);
        vaccines.remove(VaccineRepo.Vaccine.measles2);
        vaccines.remove(VaccineRepo.Vaccine.mr2);

        final String URGENT = "'" + AlertStatus.urgent.value() + "'";
        final String NORMAL = "'" + AlertStatus.normal.value() + "'";
        final String COMPLETE = "'" + AlertStatus.complete.value() + "'";


        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + URGENT + " ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + URGENT + OR;
            }
        }

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " = " + URGENT +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " != " + COMPLETE + " ) ";

        if (urgentOnly) {
            return mainCondition + " ) ";
        }

        mainCondition += OR;
        for (int i = 0; i < vaccines.size(); i++) {
            VaccineRepo.Vaccine vaccine = vaccines.get(i);
            if (i == vaccines.size() - 1) {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + NORMAL + " ";
            } else {
                mainCondition += " " + VaccinateActionUtils.addHyphen(vaccine.display()) + " = " + NORMAL + OR;
            }
        }

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv4.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.opv0.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr1.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles1.display()) + " != " + COMPLETE + " ) ";

        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " != " + COMPLETE + " ) ";
        mainCondition += OR + " ( " + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.mr2.display()) + " = " + NORMAL +
                AND + VaccinateActionUtils.addHyphen(VaccineRepo.Vaccine.measles2.display()) + " != " + COMPLETE + " ) ";

        return mainCondition + " ) ";
    }

    @SuppressLint("NewApi")
    @Override
    public void showNotFoundPopup(String opensrpID) {
        NoMatchDialogFragment
                .launchDialog((BaseRegisterActivity) Objects.requireNonNull(getActivity()), DIALOG_TAG, opensrpID);
    }


    @Override
    public void setUniqueID(String s) {
        if (getSearchView() != null) {
            getSearchView().setText(s);
        }
    }


    @Override

    public void setAdvancedSearchFormData(HashMap<String, String> formData) {
        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) getActivity();
        if (baseRegisterActivity != null) {
            android.support.v4.app.Fragment currentFragment = baseRegisterActivity
                    .findFragmentByPosition(BaseRegisterActivity
                            .ADVANCED_SEARCH_POSITION);
            ((BaseAdvancedSearchFragment) currentFragment).setAdvancedSearchFormData(formData);
        }
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {

        RepositoryHolder repositoryHolder = new RepositoryHolder();
        repositoryHolder.setCommonRepository(commonRepository());
        repositoryHolder.setVaccineRepository(ImmunizationLibrary.getInstance().vaccineRepository());
        repositoryHolder.setWeightRepository(GrowthMonitoringLibrary.getInstance().weightRepository());


        ChildRegisterProvider childRegisterProvider = new ChildRegisterProvider(getActivity(), repositoryHolder, visibleColumns, registerActionHandler, paginationViewHandler, context().alertService());
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }


    @Override
    protected void startRegistration() {
        ((BaseChildRegisterActivity) getActivity()).startFormActivity(Utils.metadata().childRegister.formName, null, null);
    }


    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        ((BaseChildRegisterFragmentPresenter) presenter).updateSortAndFilter(filterList, sortField);
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        // do we need to post progress?
    }

    @Override
    public void onSyncComplete(FetchStatus fetchStatus) {
        super.onSyncComplete(fetchStatus);
        Utils.startAsyncTask(new CountDueAndOverDue(), null);
    }

    @Override
    public void recalculatePagination(AdvancedMatrixCursor matrixCursor) {
        clientAdapter.setTotalcount(matrixCursor.getCount());
        Log.v("total count here", "" + clientAdapter.getTotalcount());
        clientAdapter.setCurrentlimit(20);
        if (clientAdapter.getTotalcount() > 0) {
            clientAdapter.setCurrentlimit(clientAdapter.getTotalcount());
        }
        clientAdapter.setCurrentoffset(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final AdvancedMatrixCursor matrixCursor = ((BaseChildRegisterFragmentPresenter) presenter).getMatrixCursor();
        if (!globalQrSearch || matrixCursor == null) {
            return super.onCreateLoader(id, args);
        } else {
            globalQrSearch = false;
            switch (id) {
                case LOADER_ID:
                    // Returns a new CursorLoader
                    return new CursorLoader(getActivity()) {
                        @Override
                        public Cursor loadInBackground() {
                            return matrixCursor;
                        }
                    };
                default:
                    // An invalid id was passed in
                    return null;
            }
        }
    }

    @Override
    protected void onViewClicked(View view) {

        if (getActivity() == null) {
            return;
        }

    }

    protected void updateLocationText() {
        if (clinicSelection != null) {
            clinicSelection.setText(LocationHelper.getInstance().getOpenMrsReadableName(
                    clinicSelection.getSelectedItem()));
            String locationId = LocationHelper.getInstance().getOpenMrsLocationId(clinicSelection.getSelectedItem());
            context().allSharedPreferences().savePreference(Constants.CURRENT_LOCATION_ID, locationId);
        }
    }


    @Override
    public ChildRegisterFragmentContract.Presenter presenter() {
        return (ChildRegisterFragmentContract.Presenter) presenter;
    }

    private int count(String mainConditionString) {

        int count = 0;

        Cursor c = null;

        try {
            SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(countSelect);
            String query = "";
            if (isValidFilterForFts(commonRepository())) {
                String sql = sqb.countQueryFts(tablename, "", mainConditionString, "");
                Log.i(getClass().getName(), query);

                count = commonRepository().countSearchIds(sql);
            } else {
                sqb.addCondition(filters);
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(query);

                Log.i(getClass().getName(), query);
                c = commonRepository().rawCustomQueryForAdapter(query);
                c.moveToFirst();
                count = c.getInt(0);
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), e.toString(), e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return count;
    }

    public void triggerFilterSelection() {
        if (filterSection != null && !filterMode()) {
            filterSection.performClick();
        }
    }

    private class CountDueAndOverDue extends AsyncTask<Void, Void, Pair<Integer, Integer>> {
        @Override
        protected Pair<Integer, Integer> doInBackground(Void... params) {
            int overdueCount = count(filterSelectionCondition(true));

            dueOverdueCount = count(filterSelectionCondition(false));
            return Pair.create(overdueCount, dueOverdueCount);
        }

        @Override
        protected void onPostExecute(Pair<Integer, Integer> pair) {
            int overDue = pair.first;
            dueOverdueCount = pair.second;

            updateDueOverdueCountText(overDue);

        }
    }
}
