package org.smartregister.child.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.R;
import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.interactor.ChildAdvancedSearchInteractor;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.domain.Response;

import java.lang.ref.WeakReference;
import java.util.Date;
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
    private WeakReference<ChildAdvancedSearchContract.View> viewReference;

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

    protected Map<String, String> cleanMapForAdvancedSearch(Map<String, String> editMap) {

        if (editMap.containsKey(START_DATE) || editMap.containsKey(END_DATE)) {

            Date date0 = new Date(0);
            String startDate = DateUtil.yyyyMMdd.format(date0);

            Date now = new Date();
            String endDate = DateUtil.yyyyMMdd.format(now);

            if (editMap.containsKey(START_DATE)) {
                startDate = editMap.remove(START_DATE);
            }
            if (editMap.containsKey(END_DATE)) {
                endDate = editMap.remove(END_DATE);
            }

            String bDate = startDate + ":" + endDate;
            editMap.put(BIRTH_DATE, bDate);
        }

        return editMap;
    }

    protected ChildAdvancedSearchContract.View getView() {
        if (viewReference != null) return viewReference.get();
        else return null;
    }

    private void localQueryInitialize(Map<String, String> editMap) {

        String mainCondition = model.getMainConditionString(editMap);

        String countSelect = model.countSelect(mainCondition);
        String mainSelect = model.mainSelect(mainCondition);

        getView().initializeQueryParams(TABLE_NAME, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);
    }

    @Override
    public String getDefaultSortQuery() {
        return TABLE_NAME + "." + Constants.KEY.LAST_INTERACTED_WITH + " DESC";
    }

    @Override
    public void onResultsFound(Response<String> response, String opensrpID) {
        matrixCursor = model.createMatrixCursor(response);//To Do magic cursors
        AdvancedMatrixCursor advancedMatrixCursor = getRemoteLocalMatrixCursor(matrixCursor);
        setMatrixCursor(advancedMatrixCursor);

        advancedMatrixCursor.moveToFirst();
        getView().recalculatePagination(advancedMatrixCursor);

        getView().filterandSortInInitializeQueries();
        getView().hideProgressView();

    }

    protected abstract AdvancedMatrixCursor getRemoteLocalMatrixCursor(AdvancedMatrixCursor matrixCursor);

    public void setModel(ChildAdvancedSearchContract.Model model) {
        this.model = model;
    }

    public void setInteractor(ChildAdvancedSearchContract.Interactor interactor) {
        this.interactor = interactor;
    }
}
