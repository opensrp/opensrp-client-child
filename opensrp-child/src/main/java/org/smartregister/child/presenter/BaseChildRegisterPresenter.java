package org.smartregister.child.presenter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.child.R;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.interactor.ChildRegisterInteractor;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.domain.FetchStatus;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class BaseChildRegisterPresenter
        implements ChildRegisterContract.Presenter, ChildRegisterContract.InteractorCallBack {

    public static final String TAG = BaseChildRegisterPresenter.class.getName();

    protected WeakReference<ChildRegisterContract.View> viewReference;
    protected ChildRegisterContract.Interactor interactor;
    protected ChildRegisterContract.Model model;

    public BaseChildRegisterPresenter(ChildRegisterContract.View view, ChildRegisterContract.Model model) {
        viewReference = new WeakReference<>(view);
        interactor = new ChildRegisterInteractor();
        this.model = model;
    }

    public void setModel(ChildRegisterContract.Model model) {
        this.model = model;
    }

    public void setInteractor(ChildRegisterContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        model.registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        model.unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        viewReference = null;//set to null on destroy
        // Inform interactor
        interactor.onDestroy(isChangingConfiguration);
        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            interactor = null;
            model = null;
        }
    }

    @Override
    public void updateInitials() {
        String initials = model.getInitials();
        if (initials != null) {
            getView().updateInitialsText(initials);
        }
    }

    private ChildRegisterContract.View getView() {
        if (viewReference != null) return viewReference.get();
        else return null;
    }

    @Override
    public void saveLanguage(String language) {
        model.saveLanguage(language);
        getView().displayToast(language + " selected");
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {

        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this);
            return;
        }

        JSONObject form = model.getFormAsJson(formName, entityId, currentLocationId);
        getView().startFormActivity(form);

    }

    @Override
    public void saveForm(String jsonString, UpdateRegisterParams updateRegisterParams) {

        try {

            List<ChildEventClient> childEventClientList = model.processRegistration(jsonString, updateRegisterParams.getFormTag());
            if (childEventClientList == null || childEventClientList.isEmpty()) {
                return;
            }

            interactor.saveRegistration(childEventClientList, jsonString, updateRegisterParams, this);

        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    @Override
    public void saveOutOfCatchmentService(String jsonString, ChildRegisterContract.ProgressDialogCallback progressDialogCallback) {
        JsonFormUtils.processOutOfAreaService(jsonString, progressDialogCallback);
    }

    @Override
    public void closeChildRecord(String jsonString) {

        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

            Timber.d(jsonString);
            //getView().showProgressDialog(jsonString.contains(Constants.EventType.CLOSE) ? R.string.removing_dialog_title
            // : R.string.saving_dialog_title);

            interactor.removeChildFromRegister(jsonString, allSharedPreferences.fetchRegisteredANM());

        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));

        }
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        try {
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight());
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
            getView().displayToast(R.string.error_unable_to_start_form);
        }
    }

    @Override
    public void onNoUniqueId() {
        getView().displayShortToast(R.string.no_unique_id);
    }

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        getView().refreshList(FetchStatus.fetched);
        getView().hideProgressDialog();
    }
}
