package org.smartregister.child.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.child.R;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.domain.UpdateRegisterParams;
import org.smartregister.child.interactor.ChildRegisterInteractor;
import org.smartregister.child.util.OutOfAreaServiceUtils;
import org.smartregister.domain.FetchStatus;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, String> metadataMap = StringUtils.isBlank(metadata) ? new HashMap<String, String>() : (Map<String, String>) new Gson()
                .fromJson(metadata, new TypeToken<Map<String, String>>() {
                }.getType());

        startForm(formName, entityId, metadataMap, currentLocationId);
    }

    @Override
    public void startForm(String formName, String entityId, Map<String, String> metadata, String currentLocationId) throws Exception {
        if (StringUtils.isBlank(entityId)) {
            Triple<String, Map<String, String>, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this);
            return;
        }

        JSONObject form = model.getFormAsJson(formName, entityId, currentLocationId, metadata);

        getView().startFormActivity(form);
    }

    @Override
    public void saveForm(String jsonString, UpdateRegisterParams updateRegisterParams) {

        try {

            List<ChildEventClient> childEventClientList = model.processRegistration(jsonString, updateRegisterParams.getFormTag(), updateRegisterParams.isEditMode());
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
        OutOfAreaServiceUtils.processOutOfAreaService(jsonString, progressDialogCallback);
    }

    @Override
    public void closeChildRecord(String jsonString) {
        try {
            AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
            interactor.removeChildFromRegister(jsonString, allSharedPreferences.fetchRegisteredANM());
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    @Override
    public void onUniqueIdFetched(Triple<String, Map<String, String>, String> triple, String entityId) {
        try {
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight());
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
            if (getView() != null) {
                getView().displayToast(R.string.error_unable_to_start_form);
            }
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
