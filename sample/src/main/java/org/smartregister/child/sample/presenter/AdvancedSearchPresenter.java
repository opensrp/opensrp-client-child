package org.smartregister.child.sample.presenter;

import android.database.Cursor;
import android.database.CursorJoiner;

import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;
import org.smartregister.child.sample.cursor.CreateRemoteLocalCursor;
import org.smartregister.child.sample.model.AdvancedSearchModel;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.sample.util.DBQueryHelper;

public class AdvancedSearchPresenter extends BaseChildAdvancedSearchPresenter {
    public AdvancedSearchPresenter(ChildAdvancedSearchContract.View view, String viewConfigurationIdentifier) {
        super(view, viewConfigurationIdentifier, new AdvancedSearchModel());
    }

    @Override
    protected AdvancedMatrixCursor getRemoteLocalMatrixCursor(AdvancedMatrixCursor matrixCursor) {
        String query = getView().filterAndSortQuery();
        Cursor cursor = getView().getRawCustomQueryForAdapter(query);
        if (cursor != null && cursor.getCount() > 0) {
            AdvancedMatrixCursor remoteLocalCursor = new AdvancedMatrixCursor(new String[]{DBConstants.KEY.ID_LOWER_CASE, DBConstants.KEY.RELATIONALID, DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME, DBConstants.KEY.MOTHER_FIRST_NAME, DBConstants.KEY.MOTHER_LAST_NAME, DBConstants.KEY.GENDER, DBConstants.KEY.DOB, DBConstants.KEY.ZEIR_ID, DBConstants.KEY.INACTIVE, DBConstants.KEY.LOST_TO_FOLLOW_UP});

            CursorJoiner joiner = new CursorJoiner(matrixCursor, new String[]{DBConstants.KEY.ZEIR_ID}, cursor, new String[]{DBConstants.KEY.ZEIR_ID});
            for (CursorJoiner.Result joinerResult : joiner) {
                switch (joinerResult) {
                    case BOTH:
                        CreateRemoteLocalCursor createRemoteLocalCursor = new CreateRemoteLocalCursor(cursor, true);
                        remoteLocalCursor
                                .addRow(new Object[]{createRemoteLocalCursor.getId(), createRemoteLocalCursor.getRelationalId(),
                                        createRemoteLocalCursor.getFirstName(), createRemoteLocalCursor.getLastName(),
                                        createRemoteLocalCursor.getMotherFirstName(), createRemoteLocalCursor.getMotherLastName(), createRemoteLocalCursor.getGender(), createRemoteLocalCursor.getDob(), createRemoteLocalCursor.getOpenSrpId(), createRemoteLocalCursor.getInactive(), createRemoteLocalCursor.getLostToFollowUp()});
                        break;
                    case RIGHT:
                        CreateRemoteLocalCursor localCreateRemoteLocalCursor = new CreateRemoteLocalCursor(cursor, false);
                        remoteLocalCursor
                                .addRow(new Object[]{localCreateRemoteLocalCursor.getId(), localCreateRemoteLocalCursor.getRelationalId(),
                                        localCreateRemoteLocalCursor.getFirstName(), localCreateRemoteLocalCursor.getLastName(),
                                        localCreateRemoteLocalCursor.getMotherFirstName(), localCreateRemoteLocalCursor.getMotherLastName(), localCreateRemoteLocalCursor.getGender(), localCreateRemoteLocalCursor.getDob(), localCreateRemoteLocalCursor.getOpenSrpId(), localCreateRemoteLocalCursor.getInactive(), localCreateRemoteLocalCursor.getLostToFollowUp()});

                        break;
                    case LEFT:
                        createRemoteLocalCursor = new CreateRemoteLocalCursor(matrixCursor, true);
                        remoteLocalCursor
                                .addRow(new Object[]{createRemoteLocalCursor.getId(), createRemoteLocalCursor.getRelationalId(),
                                        createRemoteLocalCursor.getFirstName(), createRemoteLocalCursor.getLastName(),
                                        createRemoteLocalCursor.getMotherFirstName(), createRemoteLocalCursor.getMotherLastName(), createRemoteLocalCursor.getGender(), createRemoteLocalCursor.getDob(), createRemoteLocalCursor.getOpenSrpId(), createRemoteLocalCursor.getInactive(), createRemoteLocalCursor.getLostToFollowUp()});
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

    @Override
    public String getMainCondition() {
        return DBQueryHelper.getHomeRegisterCondition();
    }
}