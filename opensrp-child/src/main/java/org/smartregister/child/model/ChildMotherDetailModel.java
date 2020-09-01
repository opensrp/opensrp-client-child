package org.smartregister.child.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.util.Constants;

import timber.log.Timber;

import static org.smartregister.child.util.JsonFormUtils.getJsonObject;
import static org.smartregister.child.util.JsonFormUtils.getJsonString;

public class ChildMotherDetailModel implements Comparable<ChildMotherDetailModel> {

    private String id;
    public String childBaseEntityId;
    private String relationalId;
    private String motherBaseEntityId;
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String zeirId;
    private String motherFirstName;
    private String motherLastName;
    private String inActive;
    private String lostFollowUp;
    private JSONObject childJson;
    private JSONObject motherJson;

    public ChildMotherDetailModel(JSONObject childJson, JSONObject motherJson) {
        setMotherJson(motherJson);
        setChildJson(childJson);
        mapJsonToField();
    }

    public Object[] getColumnValuesFromJson() {
        return new Object[]{
                getChildBaseEntityId(), getRelationalId(), getMotherBaseEntityId(), getFirstName(), getLastName(), getGender(), getDateOfBirth(),
                getZeirId(), getMotherFirstName(), getMotherLastName(), getInActive(), getLostFollowUp()
        };
    }

    private void mapJsonToField() {
        try {
            setId(getStringValue(Constants.Client.ID_LOWER_CASE, true));
            setChildBaseEntityId(getStringValue(Constants.Client.BASE_ENTITY_ID, true));
            setFirstName(getStringValue(Constants.Client.FIRST_NAME, true));
            setLastName(getStringValue(Constants.Client.LAST_NAME, true));
            setGender(getStringValue(Constants.Client.GENDER, true));
            setDateOfBirth(getStringValue(Constants.Client.BIRTHDATE, true));

            if (childJson.has(Constants.Client.RELATIONSHIPS)) {
                JSONObject relationships = childJson.getJSONObject(Constants.Client.RELATIONSHIPS);
                if (relationships != null && relationships.has(Constants.KEY.MOTHER)) {
                    String mothersId = relationships.getJSONArray(Constants.KEY.MOTHER).getString(0);
                    setRelationalId(mothersId);
                    setMotherBaseEntityId(mothersId);
                }
            }

            setZeirId(getStringFromJson(Constants.Client.IDENTIFIERS, Constants.KEY.ZEIR_ID));
            setInActive(getStringFromJson(Constants.Client.ATTRIBUTES, Constants.Client.INACTIVE));
            setLostFollowUp(getStringFromJson(Constants.Client.ATTRIBUTES, Constants.Client.LOST_TO_FOLLOW_UP));
            setMotherFirstName(getStringValue(Constants.Client.FIRST_NAME, false));
            setMotherLastName(getStringValue(Constants.Client.LAST_NAME, false));

        } catch (JSONException e) {
            Timber.e(e, "Error parsing Advanced Search Client JSON");
        }
    }

    private String getStringValue(String key, boolean isChild) throws JSONException {
        if (isChild && childJson.has(key)) {
            return childJson.getString(key);
        } else if (!isChild && motherJson.has(key)) {
            return motherJson.getString(key);
        }
        return null;
    }

    private String getStringFromJson(String jsonKey, String actualKey) {
        if (childJson.has(jsonKey)) {
            return getJsonString(getJsonObject(childJson, jsonKey), actualKey);
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChildBaseEntityId() {
        return childBaseEntityId;
    }

    public void setChildBaseEntityId(String childBaseEntityId) {
        this.childBaseEntityId = childBaseEntityId;
    }

    public String getRelationalId() {
        return relationalId;
    }

    public void setRelationalId(String relationalId) {
        this.relationalId = relationalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getZeirId() {
        return zeirId;
    }

    public void setZeirId(String zeirId) {
        this.zeirId = zeirId;
    }

    public String getMotherFirstName() {
        return motherFirstName;
    }

    public void setMotherFirstName(String motherFirstName) {
        this.motherFirstName = motherFirstName;
    }

    public String getMotherLastName() {
        return motherLastName;
    }

    public void setMotherLastName(String motherLastName) {
        this.motherLastName = motherLastName;
    }

    public void setChildJson(JSONObject ClientJson) {
        this.childJson = ClientJson;
    }

    public String getInActive() {
        return inActive;
    }

    public void setInActive(String inActive) {
        this.inActive = inActive;
    }

    public String getLostFollowUp() {
        return lostFollowUp;
    }

    public void setLostFollowUp(String lostFollowUp) {
        this.lostFollowUp = lostFollowUp;
    }

    public String getMotherBaseEntityId() {
        return motherBaseEntityId;
    }

    public void setMotherBaseEntityId(String motherBaseEntityId) {
        this.motherBaseEntityId = motherBaseEntityId;
    }

    @Override
    public int compareTo(ChildMotherDetailModel childMotherDetailModel) {
        return this.getZeirId().compareTo(childMotherDetailModel.getZeirId());
    }

    public void setMotherJson(JSONObject motherJson) {
        this.motherJson = motherJson;
    }
}
