package org.smartregister.child.presenter;

import android.widget.LinearLayout;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.utils.ValidationStatus;

import org.smartregister.child.fragment.ChildFormFragment;
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

    @Override
    public void onNextClick(LinearLayout mainView) {
        ValidationStatus validationStatus = this.writeValuesAndValidate(mainView);
        if (validationStatus.isValid()) {
            JsonFormFragment next = ChildFormFragment.getFormFragment(mStepDetails.optString(Constants.JSON_FORM_EXTRA.NEXT));
            getView().hideKeyBoard();
            getView().transactThis(next);
        } else {
            validationStatus.requestAttention();
            getView().showToast(validationStatus.getErrorMessage());
        }
    }

    public boolean intermediatePage() {
        return this.mStepDetails != null && this.mStepDetails.has(Constants.JSON_FORM_EXTRA.NEXT);
    }
}