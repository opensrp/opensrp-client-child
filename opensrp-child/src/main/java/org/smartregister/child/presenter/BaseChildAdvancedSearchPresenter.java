package org.smartregister.child.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.R;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.interactor.ChildAdvancedSearchInteractor;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.domain.Response;

import java.lang.ref.WeakReference;
import java.util.Map;

import static org.smartregister.child.fragment.BaseAdvancedSearchFragment.END_DATE;
import static org.smartregister.child.fragment.BaseAdvancedSearchFragment.START_DATE;

/**
 * Created by ndegwamartin on 11/04/2019.
 */
public abstract class BaseChildAdvancedSearchPresenter extends BaseChildRegisterFragmentPresenter
        implements ChildAdvancedSearchContract.Presenter, ChildAdvancedSearchContract.InteractorCallBack {

    public static final String TABLE_NAME = Utils.metadata().getRegisterQueryProvider().getDemographicTable();
    private static final String BIRTH_DATE = "birth_date";
    protected ChildAdvancedSearchContract.Model model;
    private final WeakReference<ChildAdvancedSearchContract.View> viewReference;
    private String currentCondition;

    public BaseChildAdvancedSearchPresenter(ChildAdvancedSearchContract.View view, String viewConfigurationIdentifier,
                                            BaseChildAdvancedSearchModel advancedSearchModel) {
        super(view, advancedSearchModel, viewConfigurationIdentifier);
        this.viewReference = new WeakReference<>(view);
        interactor = new ChildAdvancedSearchInteractor();
        model = advancedSearchModel;
    }

    public void search(Map<String, String> searchMap, boolean isLocal) {

        String searchCriteria = String.format("%s %s", getView().getString(R.string.search_criteria_includes) + Utils.bold(getView().getString(isLocal ? R.string.my_catchment_area : R.string.out_and_inside)), model.createSearchString(searchMap));

        if (StringUtils.isBlank(searchCriteria)) {
            return;
        }

        getView().updateSearchCriteria(searchCriteria);

        Map<String, String> editMap = model.createEditMap(searchMap);
        if (editMap == null || editMap.isEmpty()) {
            return;
        }

        if (isLocal) {
            getView().showProgressView();
            getView().switchViews(true);
            localQueryInitialize(editMap);
            getView().countExecute();
            getView().filterandSortInInitializeQueries();
            getView().hideProgressView();
        } else {
            getView().showProgressView();
            getView().switchViews(true);
            if (editMap.size() > 0) {
                Map<String, String> localMap = model.createEditMap(searchMap);
                if (localMap != null && !localMap.isEmpty()) {
                    localQueryInitialize(localMap);
                }
            }
            interactor.search(cleanMapForAdvancedSearch(editMap), this, searchMap.get(Constants.KEY.ZEIR_ID));
        }
    }

    @Override
    public String getCountQuery() {
        return model.countSelect(currentCondition);
    }

    protected Map<String, String> cleanMapForAdvancedSearch(Map<String, String> editMap) {
        if (editMap.containsKey(START_DATE) && editMap.containsKey(END_DATE)) {
            String startDate = editMap.remove(START_DATE);
            String endDate = editMap.remove(END_DATE);
            if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                String birthDate = startDate + ":" + endDate;
                editMap.put(BIRTH_DATE, birthDate);
            }
        }
        return editMap;
    }

    protected ChildAdvancedSearchContract.View getView() {
        if (viewReference != null) return viewReference.get();
        else return null;
    }

    private void localQueryInitialize(Map<String, String> editMap) {
        currentCondition = model.getMainConditionString(editMap);
        String countSelect = model.countSelect(currentCondition);
        String mainSelect = model.mainSelect(currentCondition);
        getView().initializeQueryParams(TABLE_NAME, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);
    }

    @Override
    public String getDefaultSortQuery() {
        return TABLE_NAME + "." + Constants.KEY.LAST_INTERACTED_WITH + " DESC";
    }

    @Override
    public void onResultsFound(Response<String> response, String opensrpID) {
        AdvancedMatrixCursor advancedMatrixCursor = getRemoteLocalMatrixCursor(model.createMatrixCursor(response));
        setMatrixCursor(advancedMatrixCursor);
        getMatrixCursor().moveToFirst();
        getView().recalculatePagination(advancedMatrixCursor);
        getView().filterandSortInInitializeQueries();
        getView().hideProgressView();
    }

    protected abstract AdvancedMatrixCursor getRemoteLocalMatrixCursor(AdvancedMatrixCursor remoteCursor);

    public void setModel(ChildAdvancedSearchContract.Model model) {
        this.model = model;
    }

    public void setInteractor(ChildAdvancedSearchContract.Interactor interactor) {
        this.interactor = interactor;
    }

    public String getCurrentCondition() {
        return currentCondition;
    }
}
