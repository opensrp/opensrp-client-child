package org.smartregister.child.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.R;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.util.Utils;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by ndegwamartin on 28/02/2019.
 */
public abstract class BaseChildRegisterFragmentPresenter implements ChildRegisterFragmentContract.Presenter {

    protected WeakReference<ChildRegisterFragmentContract.View> viewReference;

    protected ChildRegisterFragmentContract.Model model;

    protected RegisterConfiguration config;

    protected Set<org.smartregister.configurableviews.model.View> visibleColumns = new TreeSet<>();

    protected String viewConfigurationIdentifier;

    protected ChildAdvancedSearchContract.Interactor interactor;

    protected AdvancedMatrixCursor matrixCursor;

    public BaseChildRegisterFragmentPresenter(ChildRegisterFragmentContract.View view,
                                              ChildRegisterFragmentContract.Model model,
                                              String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(view);
        this.model = model;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = model.defaultRegisterConfiguration();
    }

    @Override
    public void processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = model.getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            config = (RegisterConfiguration) viewConfiguration.getMetadata();
            setVisibleColumns(model.getRegisterActiveColumns(viewConfigurationIdentifier));
        }

        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(config.getSearchBarText());
        }
    }

    @Override
    public void initializeQueries(String mainCondition) {
        String tableName = Utils.metadata().getRegisterQueryProvider().getDemographicTable();
        String parentTableName = Utils.metadata().getRegisterQueryProvider().getDemographicTable();

        String countSelect = model.countSelect(Utils.metadata().getRegisterQueryProvider().getDemographicTable(), mainCondition, parentTableName);
        String mainSelect = model.mainSelect(mainCondition);

        getView().initializeQueryParams(tableName, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().countExecute();
        getView().filterandSortInInitializeQueries();
    }

    @Override
    public void startSync() {
        //ServiceTools.startSyncService(getActivity());
    }

    @Override
    public void searchGlobally(String uniqueId) {
        // TODO implement search global
    }

    private void setVisibleColumns(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

    protected ChildRegisterFragmentContract.View getView() {
        if (viewReference != null) return viewReference.get();
        else return null;
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = model.getFilterText(filterList, getView().getString(R.string.filter));
        String sortText = model.getSortText(sortField);

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    @Override
    public abstract String getMainCondition();

    @Override
    public abstract String getDefaultSortQuery();

    public void setModel(ChildRegisterFragmentContract.Model model) {
        this.model = model;
    }

    public AdvancedMatrixCursor getMatrixCursor() {
        return matrixCursor;
    }

    public void setMatrixCursor(AdvancedMatrixCursor matrixCursor) {
        this.matrixCursor = matrixCursor;
    }
}