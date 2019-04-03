package org.smartregister.child.sample.presenter;

import android.database.Cursor;
import android.database.CursorJoiner;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.cursor.CreateRemoteLocalCursor;
import org.smartregister.child.sample.contract.AdvancedSearchContract;
import org.smartregister.child.sample.interactor.AdvancedSearchInteractor;
import org.smartregister.child.sample.model.AdvancedSearchModel;
import org.smartregister.child.sample.util.SampleConstants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.domain.Response;

import java.lang.ref.WeakReference;
import java.util.Map;

public class AdvancedSearchPresenter extends ChildRegisterFragmentPresenter
        implements AdvancedSearchContract.Presenter, AdvancedSearchContract.InteractorCallBack {

    private WeakReference<AdvancedSearchContract.View> viewReference;

    private AdvancedSearchContract.Model model;

    public static final String TABLE_NAME = SampleConstants.TABLE_NAME.CHILD;

    public AdvancedSearchPresenter(AdvancedSearchContract.View view, String viewConfigurationIdentifier) {
        super(view, new AdvancedSearchModel(), viewConfigurationIdentifier);
        this.viewReference = new WeakReference<>(view);
        interactor = new AdvancedSearchInteractor();
        model = new AdvancedSearchModel();
    }

    public void search(String firstName, String lastName, String opensrpID, String edd, String dob, String phoneNumber,
                       String alternateContact, boolean isLocal) {
        String searchCriteria = model
                .createSearchString(firstName, lastName, opensrpID, edd, dob, phoneNumber, alternateContact);
        if (StringUtils.isBlank(searchCriteria)) {
            return;
        }

        getView().updateSearchCriteria(searchCriteria);

        Map<String, String> editMap = model
                .createEditMap(firstName, lastName, opensrpID, edd, dob, phoneNumber, alternateContact, isLocal);
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
                Map<String, String> localMap = model
                        .createEditMap(firstName, lastName, opensrpID, edd, dob, phoneNumber, alternateContact, true);
                if (localMap != null && !localMap.isEmpty()) {
                    localQueryInitialize(localMap);
                }
            }
            interactor.search(editMap, this, opensrpID);

        }
    }

    private void localQueryInitialize(Map<String, String> editMap) {
        String mainCondition = model.getMainConditionString(editMap);

        String countSelect = model.countSelect(TABLE_NAME, mainCondition, SampleConstants.TABLE_NAME.MOTHER_TABLE_NAME);
        String mainSelect = model.mainSelect(TABLE_NAME, mainCondition, SampleConstants.TABLE_NAME.MOTHER_TABLE_NAME);

        getView().initializeQueryParams(TABLE_NAME, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);
    }

    @Override
    public void onResultsFound(Response<String> response, String opensrpID) {
        matrixCursor = model.createMatrixCursor(response);
        AdvancedMatrixCursor advancedMatrixCursor = getRemoteLocalMatrixCursor(matrixCursor);

        advancedMatrixCursor.moveToFirst();
        getView().recalculatePagination(advancedMatrixCursor);

        getView().filterandSortInInitializeQueries();
        getView().hideProgressView();

    }

    private AdvancedMatrixCursor getRemoteLocalMatrixCursor(AdvancedMatrixCursor matrixCursor) {
        String query = getView().filterAndSortQuery();
        Cursor cursor = getView().getRawCustomQueryForAdapter(query);
        if (cursor != null && cursor.getCount() > 0 || true) {
            AdvancedMatrixCursor remoteLocalCursor = new AdvancedMatrixCursor(new String[]{DBConstants.KEY.ID_LOWER_CASE,
                    DBConstants.KEY.RELATIONAL_ID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME});

            CursorJoiner joiner = new CursorJoiner(matrixCursor, new String[]{DBConstants.KEY.ZEIR_ID,
                    DBConstants.KEY.ID_LOWER_CASE}, cursor,
                    new String[]{DBConstants.KEY.ZEIR_ID, DBConstants.KEY.ID_LOWER_CASE});
            for (CursorJoiner.Result joinerResult : joiner) {
                switch (joinerResult) {
                    case BOTH:
                        CreateRemoteLocalCursor createRemoteLocalCursor = new CreateRemoteLocalCursor(matrixCursor, true);
                        remoteLocalCursor
                                .addRow(new Object[]{createRemoteLocalCursor.getId(), createRemoteLocalCursor.getRelationalId(),
                                        createRemoteLocalCursor.getFirstName(), createRemoteLocalCursor.getLastName()});
                        break;
                    case RIGHT:
                        CreateRemoteLocalCursor localCreateRemoteLocalCursor = new CreateRemoteLocalCursor(cursor, false);
                        remoteLocalCursor
                                .addRow(new Object[]{localCreateRemoteLocalCursor.getId(), localCreateRemoteLocalCursor.getRelationalId(),
                                        localCreateRemoteLocalCursor.getFirstName(), localCreateRemoteLocalCursor.getLastName()});

                        break;
                    case LEFT:
                        createRemoteLocalCursor = new CreateRemoteLocalCursor(matrixCursor, true);
                        remoteLocalCursor
                                .addRow(new Object[]{createRemoteLocalCursor.getId(), createRemoteLocalCursor.getRelationalId(),
                                        createRemoteLocalCursor.getFirstName(), createRemoteLocalCursor.getLastName()});
                        break;
                    default:
                        break;
                }
            }

            cursor.close();
            matrixCursor.close();
            return remoteLocalCursor;
        } else {
            return matrixCursor;
        }
    }


    protected AdvancedSearchContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    public void setModel(AdvancedSearchContract.Model model) {
        this.model = model;
    }

    public void setInteractor(AdvancedSearchContract.Interactor interactor) {
        this.interactor = interactor;
    }
}
