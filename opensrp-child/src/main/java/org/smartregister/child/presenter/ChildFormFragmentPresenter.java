package org.smartregister.child.presenter;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.child.util.Constants;

/**
 * Created by ndegwamartin on 01/03/2019.
 */
public class ChildFormFragmentPresenter extends JsonFormFragmentPresenter {

    public static final String TAG = ChildFormFragmentPresenter.class.getName();

    public ChildFormFragmentPresenter(JsonFormFragment formFragment, JsonFormInteractor jsonFormInteractor) {
        super(formFragment, jsonFormInteractor);
    }

    @Override
    public void setUpToolBar() {
        super.setUpToolBar();
    }


    public boolean intermediatePage() {
        return this.mStepDetails != null && this.mStepDetails.has(Constants.JSON_FORM_EXTRA.NEXT);
    }
}