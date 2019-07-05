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

/**
 * Created by ndegwamartin on 11/04/2019.
 */
public abstract class BaseChildAdvancedSearchPresenter extends BaseChildRegisterFragmentPresenter
        implements ChildAdvancedSearchContract.Presenter, ChildAdvancedSearchContract.InteractorCallBack {

    private WeakReference<ChildAdvancedSearchContract.View> viewReference;

    private ChildAdvancedSearchContract.Model model;

    public static final String TABLE_NAME = Utils.metadata().childRegister.tableName;

    public BaseChildAdvancedSearchPresenter(ChildAdvancedSearchContract.View view, String viewConfigurationIdentifier, BaseChildAdvancedSearchModel advancedSearchModel) {
        super(view, advancedSearchModel, viewConfigurationIdentifier);
        this.viewReference = new WeakReference<>(view);
        interactor = new ChildAdvancedSearchInteractor();
        model = advancedSearchModel;
    }


    public void search(Map<String, String> searchMap, boolean isLocal) {
        String searchCriteria = getView().getString(R.string.search_criteria_includes) + model.createSearchString(searchMap);
        if (StringUtils.isBlank(searchCriteria)) {
            return;
        }

        getView().updateSearchCriteria(searchCriteria);

        Map<String, String> editMap = model.createEditMap(searchMap, isLocal);
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
                Map<String, String> localMap = model.createEditMap(searchMap, true);
                if (localMap != null && !localMap.isEmpty()) {
                    localQueryInitialize(localMap);
                }
            }
            interactor.search(editMap, this, searchMap.get(Constants.KEY.ZEIR_ID));

        }
    }

    private void localQueryInitialize(Map<String, String> editMap) {
        String mainCondition = model.getMainConditionString(editMap);

        String countSelect = model.countSelect(TABLE_NAME, mainCondition, Utils.metadata().childRegister.motherTableName);
        String mainSelect = model.mainSelect(TABLE_NAME, mainCondition, Utils.metadata().childRegister.motherTableName);

        getView().initializeQueryParams(TABLE_NAME, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);
    }

    @Override
    public void onResultsFound(Response<String> response, String opensrpID) {
        matrixCursor = model.createMatrixCursor(response);
        AdvancedMatrixCursor advancedMatrixCursor = getRemoteLocalMatrixCursor(matrixCursor);
        setMatrixCursor(advancedMatrixCursor);

        advancedMatrixCursor.moveToFirst();
        getView().recalculatePagination(advancedMatrixCursor);

        getView().filterandSortInInitializeQueries();
        getView().hideProgressView();

    }

    protected abstract AdvancedMatrixCursor getRemoteLocalMatrixCursor(AdvancedMatrixCursor matrixCursor);


    protected ChildAdvancedSearchContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    public void setModel(ChildAdvancedSearchContract.Model model) {
        this.model = model;
    }

    public void setInteractor(ChildAdvancedSearchContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Override
    public String getDefaultSortQuery() {
        return TABLE_NAME + "." + Constants.KEY.LAST_INTERACTED_WITH + " DESC";
    }


}
