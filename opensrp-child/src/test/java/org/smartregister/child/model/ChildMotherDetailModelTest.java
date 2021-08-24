package org.smartregister.child.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.child.util.ChildJsonFormUtils;
import org.smartregister.child.util.Constants;

@RunWith(PowerMockRunner.class)
public class ChildMotherDetailModelTest {

    private String searchResponse = "[{\"type\":\"Client\",\"dateCreated\":\"2020-10-13T07:57:11.105+01:00\",\"system_of_registration\":\"MVACC\",\"serverVersion\":1600329284434,\"clientApplicationVersion\":1,\"clientDatabaseVersion\":11,\"baseEntityId\":\"20770b0a-f8e9-4ed5-bf25-bb10c4a2cfbc\",\"identifiers\":{\"zeir_id\":\"1000017\"},\"addresses\":[{\"addressType\":\"\",\"addressFields\":{\"address1\":\"Small villa\",\"address2\":\"Illinois\"}}],\"attributes\":{\"age\":\"0.18\",\"child_reg\":\"19012990192\",\"ga_at_birth\":\"39\",\"sms_recipient\":\"mother\",\"place_of_birth\":\"hospital\",\"birth_registration_number\":\"2020/0809\",\"inactive\":\"inactive\",\"lost_to_follow_up\":\"yes\"},\"firstName\":\"Benjamin\",\"lastName\":\"Franklin\",\"birthdate\":\"2020-08-09T13:00:00.000+01:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"Male\",\"relationships\":{\"father\":[\"ef052dab-564e-47c6-8550-dbedc50ae06f\"],\"mother\":[\"29a28f93-779d-4936-aee2-1fdb02eee9b9\"]},\"teamId\":\"8c1112a5-7d17-41b3-b8fa-e1dafa87e9e4\",\"_id\":\"c0e7fd14-308a-4475-bc1b-1b651f5bf105\",\"_rev\":\"v1\"},{\"type\":\"Client\",\"dateCreated\":\"2020-10-13T07:57:11.144+01:00\",\"serverVersion\":1600329284435,\"baseEntityId\":\"29a28f93-779d-4936-aee2-1fdb02eee9b9\",\"identifiers\":{\"M_ZEIR_ID\":\"100003-3\"},\"addresses\":[{\"addressType\":\"\",\"addressFields\":{\"address1\":\"Small villa\",\"address2\":\"Illinois\"}}],\"attributes\":{\"first_birth\":\"yes\",\"mother_rubella\":\"yes\",\"mother_tdv_doses\":\"2_plus_tdv_doses\",\"rubella_serology\":\"yes\",\"serology_results\":\"negative\",\"mother_nationality\":\"other\",\"second_phone_number\":\"23233232\",\"mother_guardian_number\":\"07456566\",\"mother_nationality_other\":\"American\"},\"firstName\":\"Gates\",\"lastName\":\"Belinda\",\"birthdate\":\"1973-01-01T13:00:00.000+01:00\",\"birthdateApprox\":false,\"deathdateApprox\":false,\"gender\":\"female\",\"_id\":\"95c4951a-aadb-4b41-8e50-8f7566fea40b\",\"_rev\":\"v1\"}]";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void testChildMotherDetailModelThrowsNullPointerExceptionWhenChildJsonObjectIsNull() {
        ChildMotherDetailModel model = new ChildMotherDetailModel(null, new JSONObject());
        Assert.assertNull(model);
    }

    @Test(expected = NullPointerException.class)
    public void testChildMotherDetailModelThrowsNullPointerExceptionWhenMotherJsonObjectIsNull() {
        ChildMotherDetailModel model = new ChildMotherDetailModel(new JSONObject(), null);
        Assert.assertNull(model);
    }

    @Test(expected = NullPointerException.class)
    public void testChildMotherDetailModelThrowsNullPointerExceptionWhenJsonObjectsAreNull() {
        ChildMotherDetailModel model = new ChildMotherDetailModel(null, null);
        Assert.assertNull(model);
    }

    @Test()
    public void testChildMotherDetailModelSetsAllFieldsToNullWhenJsonObjectsAreEmpty() {
        JSONObject childJson = new JSONObject();
        JSONObject motherJson = new JSONObject();

        ChildMotherDetailModel model = new ChildMotherDetailModel(childJson, motherJson);
        Assert.assertNull(model.getId());
        Assert.assertNull(model.getChildBaseEntityId());
        Assert.assertNull(model.getFirstName());
        Assert.assertNull(model.getLastName());
        Assert.assertNull(model.getGender());
        Assert.assertNull(model.getDateOfBirth());
        Assert.assertNull(model.getRelationalId());
        Assert.assertNull(model.getMotherBaseEntityId());
        Assert.assertNull(model.getFatherBaseEntityId());
        Assert.assertNull(model.getZeirId());
        Assert.assertNull(model.getInActive());
        Assert.assertNull(model.getLostFollowUp());
        Assert.assertNull(model.getMotherFirstName());
        Assert.assertNull(model.getMotherLastName());
    }

    @Test
    public void testChildMotherDetailModelSetsAllFieldsCorrectlyWhenMotherAndChildJsonObjectsAreSet() throws JSONException {
        JSONArray searchResults = new JSONArray(searchResponse);

        for (int i = 0; i < searchResults.length(); i++) {
            JSONObject childJson = searchResults.getJSONObject(i);
            JSONObject identifiers = childJson.getJSONObject(Constants.Client.IDENTIFIERS);

            if (identifiers.has(Constants.KEY.ZEIR_ID)) {
                JSONObject attributes = childJson.getJSONObject(Constants.Client.ATTRIBUTES);
                JSONObject relationships = childJson.getJSONObject(Constants.Client.RELATIONSHIPS);
                JSONObject motherJson = ChildJsonFormUtils.getRelationshipJson(searchResults, relationships.getJSONArray(Constants.KEY.MOTHER).getString(0));

                ChildMotherDetailModel model = new ChildMotherDetailModel(childJson, motherJson);
                Assert.assertEquals(childJson.getString(Constants.Client.ID_LOWER_CASE), model.getId());
                Assert.assertEquals(childJson.getString(Constants.Client.BASE_ENTITY_ID), model.getChildBaseEntityId());
                Assert.assertEquals(childJson.getString(Constants.Client.FIRST_NAME), model.getFirstName());
                Assert.assertEquals(childJson.getString(Constants.Client.LAST_NAME), model.getLastName());
                Assert.assertEquals(childJson.getString(Constants.Client.GENDER), model.getGender());
                Assert.assertEquals(childJson.getString(Constants.Client.BIRTHDATE), model.getDateOfBirth());
                Assert.assertEquals(relationships.getJSONArray(Constants.KEY.MOTHER).getString(0), model.getRelationalId());
                Assert.assertEquals(relationships.getJSONArray(Constants.KEY.MOTHER).getString(0), model.getMotherBaseEntityId());
                Assert.assertEquals(relationships.getJSONArray(Constants.KEY.FATHER).getString(0), model.getFatherBaseEntityId());
                Assert.assertEquals(identifiers.getString(Constants.KEY.ZEIR_ID), model.getZeirId());
                Assert.assertEquals(attributes.getString(Constants.Client.INACTIVE), model.getInActive());
                Assert.assertEquals(attributes.getString(Constants.Client.LOST_TO_FOLLOW_UP), model.getLostFollowUp());
                Assert.assertEquals(motherJson.getString(Constants.Client.FIRST_NAME), model.getMotherFirstName());
                Assert.assertEquals(motherJson.getString(Constants.Client.LAST_NAME), model.getMotherLastName());
                Assert.assertEquals(childJson.getString(Constants.Client.SYSTEM_OF_REGISTRATION),model.getSystemOfRegistration());
            }
        }
    }

    @Test
    public void testChildModelComparision() throws JSONException
    {
        JSONArray searchResults = new JSONArray(searchResponse);

            JSONObject childJson = searchResults.getJSONObject(0);
            JSONObject identifiers = childJson.getJSONObject(Constants.Client.IDENTIFIERS);

            if (identifiers.has(Constants.KEY.ZEIR_ID)) {
                JSONObject relationships = childJson.getJSONObject(Constants.Client.RELATIONSHIPS);
                JSONObject motherJson = ChildJsonFormUtils.getRelationshipJson(searchResults, relationships.getJSONArray(Constants.KEY.MOTHER).getString(0));


                ChildMotherDetailModel motherDetailModel = new ChildMotherDetailModel(childJson,motherJson );
                ChildMotherDetailModel motherDetailModelwithNull = new ChildMotherDetailModel(childJson, motherJson);
                motherDetailModel.setZeirId("12345");
                motherDetailModelwithNull.setZeirId(null);

                Assert.assertEquals(0, motherDetailModel.compareTo(motherDetailModelwithNull));
                ChildMotherDetailModel motherDetailModel2 = new ChildMotherDetailModel(childJson, motherJson);
                motherDetailModel2.setZeirId("123456");

                Assert.assertEquals(-1, motherDetailModel.compareTo(motherDetailModel2));

            }


    }
}
