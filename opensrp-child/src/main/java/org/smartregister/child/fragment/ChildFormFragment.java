package org.smartregister.child.fragment;

import android.os.Bundle;
import android.view.View;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.utils.ValidationStatus;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.smartregister.child.ChildLibrary;
import org.smartregister.child.presenter.ChildFormFragmentPresenter;

/**
 * Created by ndegwamartin on 01/03/2019.
 */
public class ChildFormFragment extends JsonWizardFormFragment {

    public static final String TAG = ChildFormFragment.class.getName();

    public static ChildFormFragment getFormFragment(String stepName) {
        ChildFormFragment jsonFormFragment = new ChildFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    @Override
    protected ChildFormFragmentPresenter createPresenter() {
        return new ChildFormFragmentPresenter(this, JsonFormInteractor.getInstance());
    }

    @Override
    public void updateVisibilityOfNextAndSave(boolean next, boolean save) {
        super.updateVisibilityOfNextAndSave(next, save);
        Form form = getForm();
        if (form != null && form.isWizard() && !ChildLibrary.getInstance().metadata().formWizardValidateRequiredFieldsBefore) {
            this.getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(save);
        }
    }

    public void validateActivateNext() {
        if (!isVisible()) { //form fragment is initializing or not the last page
            return;
        }

        Form form = getForm();
        if (form == null || !form.isWizard()) {
            return;
        }

        ValidationStatus validationStatus = null;
        for (View dataView : getJsonApi().getFormDataViews()) {

            validationStatus = getPresenter().validate(this, dataView, false);
            if (!validationStatus.isValid()) {
                break;
            }
        }

        if (validationStatus != null && validationStatus.isValid()) {
            if (getPresenter().intermediatePage()) {
                //getMenu().findItem(com.vijay.jsonwizard.R.id.action_next).setVisible(true);
            } else {
                getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(true);
            }
        } else {
            if (getPresenter().intermediatePage()) {
                //getMenu().findItem(com.vijay.jsonwizard.R.id.action_next).setVisible(false);
            } else {
                getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(false);
            }
        }
    }

    public ChildFormFragmentPresenter getPresenter() {
        return (ChildFormFragmentPresenter) presenter;
    }

    private Form getForm() {
        return this.getActivity() != null && this.getActivity() instanceof JsonFormActivity ? ((JsonFormActivity) this.getActivity()).getForm() : null;
    }
}
