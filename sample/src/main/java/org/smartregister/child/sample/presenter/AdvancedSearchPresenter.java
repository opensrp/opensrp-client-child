package org.smartregister.child.sample.presenter;

import org.smartregister.child.contract.ChildAdvancedSearchContract;
import org.smartregister.child.presenter.BaseChildAdvancedSearchPresenter;

public class AdvancedSearchPresenter extends BaseChildAdvancedSearchPresenter {
    public AdvancedSearchPresenter(ChildAdvancedSearchContract.View view, String viewConfigurationIdentifier) {
        super(view, viewConfigurationIdentifier);
    }

}
