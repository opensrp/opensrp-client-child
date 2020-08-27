package org.smartregister.child.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.util.Constants;

import timber.log.Timber;

import static org.smartregister.child.util.JsonFormUtils.getJsonObject;
import static org.smartregister.child.util.JsonFormUtils.getJsonString;

public class ChildMotherDetailsModel implements Comparable<ChildMotherDetailsModel> {

    private String id;
    public String childBaseEntityId;
    private String relationalId;
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String zeirId;
    private String motherBaseEntityId;
    private String fatherBaseEntityId;
    private String motherFirstName;
    private String motherLastName;
    private String inActive;
    private String lostFollowUp;
    private JSONObject childJson;
    private JSONObject motherJson;

    public ChildMotherDetailsModel(JSONObject childJson, JSONObject motherJson) {
        this.motherJson = motherJson;
        setChildJson(childJson);
        mapJsonToField();
    }

    public Object[] getColumnValuesFromJson() {
        return new Object[]{
                getChildBaseEntityId(), getRelationalId(), getFirstName(), getLastName(), getGender(), getDateOfBirth(),
                getZeirId(), getMotherBaseEntityId(), getFatherBaseEntityId(), getMotherFirstName(), getMotherLastName(), getInActive(), getLostFollowUp()
        };
    }

    private void mapJsonToField() {
        try {

            if (childJson.has(Constants.KEY.ID_LOWER_CASE)) {
                setId(childJson.getString(Constants.Client.ID_LOWER_CASE));
            }
            if (childJson.has(Constants.Client.BASE_ENTITY_ID)) {
                setChildBaseEntityId(childJson.getString(Constants.Client.BASE_ENTITY_ID));
            }
            if (childJson.has(Constants.Client.RELATIONSHIPS)) {
                JSONObject relationships = childJson.getJSONObject(Constants.Client.RELATIONSHIPS);
                if (relationships != null && relationships.has(Constants.KEY.MOTHER)) {
                    setRelationalId(relationships.getJSONArray(Constants.KEY.MOTHER).getString(0));
                }
            }
            if (childJson.has(Constants.Client.FIRST_NAME)) {
                setFirstName(childJson.getString(Constants.Client.FIRST_NAME));
            }
            if (childJson.has(Constants.Client.LAST_NAME)) {
                setLastName(childJson.getString(Constants.Client.LAST_NAME));
            }
            if (childJson.has(Constants.Client.GENDER)) {
                setGender(childJson.getString(Constants.Client.GENDER));
            }
            if (childJson.has(Constants.Client.BIRTHDATE)) {
                setDateOfBirth(childJson.getString(Constants.Client.BIRTHDATE));
            }
            if (childJson.has(Constants.Client.IDENTIFIERS)) {
                setZeirId(childJson.getJSONObject(Constants.Client.IDENTIFIERS).getString(Constants.KEY.ZEIR_ID));
            }
            if (motherJson.has(Constants.Client.BASE_ENTITY_ID)) {
                setMotherBaseEntityId(motherJson.getString(Constants.Client.BASE_ENTITY_ID));
            }
            if (childJson.has(Constants.Client.RELATIONSHIPS)) {
                JSONObject relationships = childJson.getJSONObject(Constants.Client.RELATIONSHIPS);
                if (relationships != null && relationships.has(Constants.KEY.FATHER)) {
                    setFatherBaseEntityId(relationships.getJSONArray(Constants.KEY.FATHER).getString(0));
                }
            }
            if (motherJson.has(Constants.Client.FIRST_NAME)) {
                setMotherFirstName(motherJson.getString(Constants.Client.FIRST_NAME));
            }
            if (motherJson.has(Constants.Client.LAST_NAME)) {
                setMotherLastName(motherJson.getString(Constants.Client.LAST_NAME));
            }
            if (childJson.has(Constants.Client.ATTRIBUTES)) {
                setInActive(getJsonString(getJsonObject(childJson, Constants.Client.ATTRIBUTES), Constants.Client.INACTIVE));
                setLostFollowUp(getJsonString(getJsonObject(childJson, Constants.Client.ATTRIBUTES), Constants.Client.LOST_TO_FOLLOW_UP));
            }

        } catch (JSONException e) {
            Timber.e(e, "Error parsing Advanced Search Client JSON");
        }
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

    public String getMotherBaseEntityId() {
        return motherBaseEntityId;
    }

    public void setMotherBaseEntityId(String motherBaseEntityId) {
        this.motherBaseEntityId = motherBaseEntityId;
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

    @Override
    public int compareTo(ChildMotherDetailsModel childMotherDetailsModel) {
        return this.getZeirId().compareTo(childMotherDetailsModel.getZeirId());
    }

    public String getFatherBaseEntityId() {
        return fatherBaseEntityId;
    }

    public void setFatherBaseEntityId(String fatherBaseEntityId) {
        this.fatherBaseEntityId = fatherBaseEntityId;
    }
}
