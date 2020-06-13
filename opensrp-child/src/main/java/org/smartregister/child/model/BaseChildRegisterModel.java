package org.smartregister.child.model;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONObject;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.domain.ChildEventClient;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.JsonFormUtils;
import org.smartregister.child.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.util.FormUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class BaseChildRegisterModel implements ChildRegisterContract.Model {
    private FormUtils formUtils;

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void saveLanguage(String language) {
        // TODO Save Language
        //Map<String, String> langs = getAvailableLanguagesMap();
        //Utils.saveLanguage(Utils.getKeyByValue(langs, language));
    }

    /*private Map<String, String> getAvailableLanguagesMap() {
        return null;
        //return AncApplication.getJsonSpecHelper().getAvailableLanguagesMap();
    }*/

    @Override
    public String getLocationId(String locationName) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationName);
    }

    @Override
    public List<ChildEventClient> processRegistration(String jsonString, FormTag formTag) {
        List<ChildEventClient> childEventClientList = new ArrayList<>();
        ChildEventClient childEventClient = JsonFormUtils.processChildDetailsForm(jsonString, formTag);
        if (childEventClient == null) {
            return null;
        }

        childEventClientList.add(childEventClient);

        ChildEventClient childHeadEventClient = JsonFormUtils.processMotherRegistrationForm(jsonString, childEventClient.getClient().getRelationalBaseEntityId(),
                childEventClient);
        if (childHeadEventClient == null) {
            return childEventClientList;
        }

        // Update the child mother
        Client childClient = childEventClient.getClient();

        childClient.addRelationship(Utils.metadata().childRegister.childCareGiverRelationKey, childHeadEventClient.getClient().getBaseEntityId());

        // Add search by mother

        ContentValues values = new ContentValues();

        values.put(Constants.KEY.LAST_INTERACTED_WITH, Calendar.getInstance().getTimeInMillis());

        String tableName = Utils.metadata().getRegisterQueryProvider().getDemographicTable();
        Utils.updateLastInteractionWith(childClient.getBaseEntityId(), tableName, values);

        childEventClientList.add(childHeadEventClient);

        return childEventClientList;
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        JSONObject form = getFormUtils().getFormJson(formName);
        if (form == null) {
            return null;
        }
        return JsonFormUtils.getFormAsJson(form, formName, entityId, currentLocationId);
    }

    private FormUtils getFormUtils() {

        try {
            formUtils = new FormUtils(Utils.context().applicationContext());
        } catch (Exception e) {
            Log.e(BaseChildRegisterModel.class.getCanonicalName(), e.getMessage(), e);
        }

        return formUtils;
    }

    public void setFormUtils(FormUtils formUtils) {
        this.formUtils = formUtils;
    }


    @Override
    public String getInitials() {
        return Utils.getUserInitials();
    }
}
