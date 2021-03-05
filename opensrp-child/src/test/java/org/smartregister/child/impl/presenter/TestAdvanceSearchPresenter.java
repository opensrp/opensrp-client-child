package org.smartregister.child.impl.presenter;

import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.model.BaseChildAdvancedSearchModel;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;

public class TestAdvanceSearchPresenter extends BaseChildAdvancedSearchPresenter {

    public TestAdvanceSearchPresenter(ChildAdvancedSearchContract.View view, String viewConfigurationIdentifier, BaseChildAdvancedSearchModel advancedSearchModel) {
        super(view, viewConfigurationIdentifier, advancedSearchModel);
    }

    @Override
    protected AdvancedMatrixCursor getRemoteLocalMatrixCursor(AdvancedMatrixCursor remoteCursor) {
        return null;
    }

    @Override
    public String getMainCondition() {
        return null;
    }
}
