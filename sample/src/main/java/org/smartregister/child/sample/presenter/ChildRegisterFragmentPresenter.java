package org.smartregister.child.sample.presenter;

import org.smartregister.child.contract.ChildRegisterFragmentContract;
import org.smartregister.child.cursor.AdvancedMatrixCursor;
import org.smartregister.child.presenter.BaseChildRegisterFragmentPresenter;
import org.smartregister.child.sample.contract.AdvancedSearchContract;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;

/**
 * Created by ndegwamartin on 01/03/2019.
 */
public class ChildRegisterFragmentPresenter extends BaseChildRegisterFragmentPresenter {

    protected AdvancedSearchContract.Interactor interactor;

    protected AdvancedMatrixCursor matrixCursor;

    public ChildRegisterFragmentPresenter(ChildRegisterFragmentContract.View view, ChildRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    public String getMainCondition() {
        return String.format(" %s is null ", DBConstants.KEY.DATE_REMOVED);
    }

    @Override
    public String getDefaultSortQuery() {
        return "";//To Remove DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ";
    }

}
