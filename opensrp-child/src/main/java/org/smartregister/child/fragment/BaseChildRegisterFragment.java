package org.smartregister.child.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.child.R;
import org.smartregister.child.activity.BaseChildRegisterActivity;
import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.domain.RegisterClickables;
import org.smartregister.child.domain.RepositoryHolder;
import org.smartregister.child.provider.ChildRegisterProvider;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.service.AlertService;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;
import org.smartregister.view.customcontrols.FontVariant;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public abstract class BaseChildRegisterFragment extends BaseRegisterFragment implements ChildRegisterFragmentContract.View {

    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_DOSAGE_STATUS = "click_view_dosage_status";

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {

        RepositoryHolder repositoryHolder = new RepositoryHolder();
        repositoryHolder.setCommonRepository(commonRepository());
        repositoryHolder.setVaccineRepository(getVaccineRepository());
        repositoryHolder.setWeightRepository(getWeightRepository());


        ChildRegisterProvider childRegisterProvider = new ChildRegisterProvider(getActivity(), repositoryHolder, visibleColumns, registerActionHandler, paginationViewHandler, getAlertService());
        clientAdapter = new RecyclerViewPaginatedAdapter(null, childRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);

        // Update top left icon
        qrCodeScanImageView = view.findViewById(R.id.scanQrCode);
        if (qrCodeScanImageView != null) {
            qrCodeScanImageView.setVisibility(View.GONE);
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

    @Override
    protected String getMainCondition() {
        return null;
    }

    @Override
    protected String getDefaultSortQuery() {
        return null;
    }

    @Override
    protected void refreshSyncProgressSpinner() {
        super.refreshSyncProgressSpinner();
        if (syncButton != null) {
            syncButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void startRegistration() {
        ((BaseChildRegisterActivity) getActivity()).startFormActivity(Utils.metadata().childRegister.formName, null, null);
    }


    @Override
    public void showNotFoundPopup(String uniqueId) {
        if (getActivity() == null) {
            return;
        }
        NoMatchDialogFragment.launchDialog((BaseRegisterActivity) getActivity(), DIALOG_TAG, uniqueId);
    }

    @Override
    protected void initializePresenter() {

    }

    @Override
    public void setUniqueID(String s) {
        if (getSearchView() != null) {
            getSearchView().setText(s);
        }
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> hashMap) {

    }

    @Override
    protected void onViewClicked(View view) {

        if (getActivity() == null) {
            return;
        }

        if (view.getTag() != null && view.getTag(R.id.record_action) != null) {
            goToChildImmunizationActivity((CommonPersonObjectClient) view.getTag(), (Constants.RECORD_ACTION) view.getTag(R.id.record_action));
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

    private void goToChildDetailActivity(CommonPersonObjectClient patient, Constants.RECORD_ACTION record_action) {

        Intent intent = new Intent(getActivity(), Utils.metadata().profileActivity);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(Constants.INTENT_KEY.RECORD_ACTION, record_action);

        startActivity(intent);
    }

    private void goToChildImmunizationActivity(CommonPersonObjectClient patient, Constants.RECORD_ACTION record_action) {

        Intent intent = new Intent(getActivity(), Utils.metadata().childImmunizationActivity);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(Constants.INTENT_KEY.RECORD_ACTION, record_action);

        startActivity(intent);
    }

    @Override
    public ChildRegisterFragmentContract.Presenter presenter() {
        return (ChildRegisterFragmentContract.Presenter) presenter;
    }

    protected abstract WeightRepository getWeightRepository();

    protected abstract VaccineRepository getVaccineRepository();

    protected abstract AlertService getAlertService();
}
