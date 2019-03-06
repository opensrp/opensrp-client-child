package org.smartregister.child.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.child.presenter.BaseChildRegisterFragmentPresenter;
import org.smartregister.child.provider.ChildRegisterProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.DBQueryHelper;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public abstract class BaseChildRegisterFragment extends BaseRegisterFragment
        implements ChildRegisterFragmentContract.View, SyncStatusBroadcastReceiver
        .SyncStatusListener {

    private static final String TAG = BaseChildRegisterFragment.class.getCanonicalName();

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        // presenter = new ChildBaseChildRegisterFragmentPresenter(this, viewConfigurationIdentifier);
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomePatientRegisterCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return DBConstants.KEY.LAST_INTERACTED_WITH + " DESC";
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        // Update top left icon
        qrCodeScanImageView = view.findViewById(R.id.scanQrCode);
        if (qrCodeScanImageView != null) {
            //qrCodeScanImageView.setVisibility(View.GONE);
        }

        // Update Search bar
        View searchBarLayout = view.findViewById(R.id.search_bar_layout);
        searchBarLayout.setBackgroundResource(R.color.customAppThemeBlue);

        if (getSearchView() != null) {
            getSearchView().setBackgroundResource(R.color.white);
            getSearchView().setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_search, 0, 0, 0);
        }

        // Update sort filter
        TextView filterView = view.findViewById(R.id.filter_text_view);
        if (filterView != null) {
            filterView.setText(getString(R.string.sort));
        }

        // Update title name
        ImageView logo = view.findViewById(R.id.opensrp_logo_image_view);
        if (logo != null) {
            logo.setVisibility(View.GONE);
        }

        CustomFontTextView titleView = view.findViewById(R.id.txt_title_label);
        if (titleView != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(getString(R.string.header_children));
            titleView.setFontVariant(FontVariant.REGULAR);
        }
    }


    @SuppressLint("NewApi")
    @Override
    public void showNotFoundPopup(String whoAncId) {
        NoMatchDialogFragment
                .launchDialog((BaseRegisterActivity) Objects.requireNonNull(getActivity()), DIALOG_TAG, whoAncId);
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
            ((AdvancedSearchFragment) currentFragment).setSearchFormData(formData);
        }
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {

        RepositoryHolder repositoryHolder = new RepositoryHolder();
        repositoryHolder.setCommonRepository(commonRepository());
        repositoryHolder.setVaccineRepository(((BaseChildRegisterActivity) getActivity()).getVaccineRepository());
        repositoryHolder.setWeightRepository(((BaseChildRegisterActivity) getActivity()).getWeightRepository());


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

        if (view.getTag() != null && view.getTag(R.id.record_action) != null) {
            goToChildImmunizationActivity((CommonPersonObjectClient) view.getTag(), (Constants.RECORD_ACTION) view.getTag(R.id.record_action));
            // TODO Move to catchment
        } else if (view.getId() == R.id.filter_text_view) {
            ((BaseChildRegisterActivity) getActivity()).switchToFragment(BaseRegisterActivity.SORT_FILTER_POSITION);
        }

        //starto
        CommonPersonObjectClient client = null;
        if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
            client = (CommonPersonObjectClient) view.getTag();
        }
        RegisterClickables registerClickables = new RegisterClickables();
/*
        switch (view.getId()) {
            case R.id.child_profile_info_layout:

                ChildImmunizationActivity.launchActivity(getActivity(), client, null);
                break;
            case R.id.record_weight:
                registerClickables.setRecordWeight(true);
                ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                break;

            case R.id.record_vaccination:
                registerClickables.setRecordAll(true);
                ChildImmunizationActivity.launchActivity(getActivity(), client, registerClickables);
                break;
            case R.id.filter_selection:
                toggleFilterSelection();
                break;

            case R.id.global_search:
                ((ChildSmartRegisterActivity) getActivity()).startAdvancedSearch();
                break;

            case R.id.scan_qr_code:
                ((ChildSmartRegisterActivity) getActivity()).startQrCodeScanner();
                break;
            default:
                break;
        }*/

    }

    private void goToChildImmunizationActivity(CommonPersonObjectClient patient, Constants.RECORD_ACTION record_action) {

        Intent intent = new Intent(getActivity(), Utils.metadata().childImmunizationActivity);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(Constants.INTENT_KEY.RECORD_ACTION, record_action);
        intent.putExtra(Constants.INTENT_KEY.EXTRA_CHILD_DETAILS,patient);

        startActivity(intent);
    }

    @Override
    public ChildRegisterFragmentContract.Presenter presenter() {
        return (ChildRegisterFragmentContract.Presenter) presenter;
    }
}
