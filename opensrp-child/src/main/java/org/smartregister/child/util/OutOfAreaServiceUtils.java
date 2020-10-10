package org.smartregister.child.util;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.ChildRegisterContract;
import org.smartregister.child.task.SaveOutOfAreaServiceTask;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.util.VaccinatorUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ndegwamartin on 10/10/2020.
 */
public class OutOfAreaServiceUtils {

    /**
     * Constructs a weight object using the out of service area form
     *
     * @param openSrpContext The context to work with
     * @param outOfAreaForm  Out of area form to extract the weight form
     * @return A weight object if weight recorded in form, or {@code null} if weight not recorded
     * @throws Exception
     */
    public static Weight getRecordedWeight(org.smartregister.Context openSrpContext, JSONObject outOfAreaForm, String locationId, Map<String, String> metadata)
            throws JSONException, ParseException {
        Weight weight = null;
        JSONArray fields = outOfAreaForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);


        for (int i = 0; i < fields.length(); i++) {
            JSONObject curField = fields.getJSONObject(i);
            if (curField.getString(JsonFormConstants.KEY).equals(Constants.KEY.WEIGHT_KG)) {
                if (StringUtils.isNotEmpty(curField.getString(JsonFormConstants.VALUE))) {
                    weight = new Weight();
                    weight.setBaseEntityId("");
                    weight.setOutOfCatchment(1);
                    weight.setKg(Float.parseFloat(curField.getString(JsonFormConstants.VALUE)));
                    weight.setAnmId(openSrpContext.allSharedPreferences().fetchRegisteredANM());
                    weight.setLocationId(locationId);
                    weight.setUpdatedAt(null);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN, Locale.ENGLISH);
                    weight.setDate(dateFormat.parse(metadata.get(Constants.KEY.OA_SERVICE_DATE)));
                    weight.setProgramClientId(metadata.containsKey(Constants.KEY.NFC_CARD_IDENTIFIER) ? metadata.get(Constants.KEY.NFC_CARD_IDENTIFIER) : metadata.get(Constants.KEY.OPENSRP_ID));
                }
            }
        }

        return weight;
    }

    /**
     * Constructs a list of recorded vaccines from the out of area form provided
     *
     * @param openSrpContext The context to use
     * @param outOfAreaForm  Out of area form to extract recorded vaccines from
     * @return A list of recorded vaccines
     */
    public static ArrayList<Vaccine> getRecordedVaccines(org.smartregister.Context openSrpContext, JSONObject outOfAreaForm, String locationId, Map<String, String> metadata) throws Exception {
        ArrayList<Vaccine> vaccines = new ArrayList<>();
        JSONArray fields = outOfAreaForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);

        for (int i = 0; i < fields.length(); i++) {
            JSONObject curField = fields.getJSONObject(i);
            if (curField.has(Constants.IS_VACCINE_GROUP) && curField.getBoolean(Constants.IS_VACCINE_GROUP) &&
                    curField.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX)) {
                JSONArray options = curField.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                addSingleVaccine(openSrpContext, vaccines, options, locationId, metadata);
            }
        }

        return vaccines;
    }

    private static void addSingleVaccine(org.smartregister.Context openSrpContext, ArrayList<Vaccine> vaccines, JSONArray options, String locationId, Map<String, String> metadata) throws JSONException, ParseException {
        for (int j = 0; j < options.length(); j++) {
            JSONObject curOption = options.getJSONObject(j);
            if (curOption.getString(JsonFormConstants.VALUE).equalsIgnoreCase(Boolean.TRUE.toString())) {
                Vaccine curVaccine = new Vaccine();
                curVaccine.setBaseEntityId("");
                curVaccine.setOutOfCatchment(1);
                curVaccine.setName(curOption.getString(JsonFormConstants.KEY));
                curVaccine.setAnmId(openSrpContext.allSharedPreferences().fetchRegisteredANM());
                curVaccine.setLocationId(locationId);
                curVaccine.setCalculation(VaccinatorUtils.getVaccineCalculation(openSrpContext.applicationContext(), curVaccine.getName()));
                curVaccine.setUpdatedAt(null);

                SimpleDateFormat dateFormat = new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN, Locale.ENGLISH);
                curVaccine.setDate(dateFormat.parse(metadata.get(Constants.KEY.OA_SERVICE_DATE)));
                curVaccine.setProgramClientId(metadata.containsKey(Constants.KEY.NFC_CARD_IDENTIFIER) ? metadata.get(Constants.KEY.NFC_CARD_IDENTIFIER) : metadata.get(Constants.KEY.OPENSRP_ID));

                vaccines.add(curVaccine);
            }
        }
    }

    public static void processOutOfAreaService(String outOfAreaJsonFormString, ChildRegisterContract.ProgressDialogCallback progressDialogCallback) {
        SaveOutOfAreaServiceTask saveOutOfAreaServiceTask = new SaveOutOfAreaServiceTask(ChildLibrary.getInstance().context().applicationContext(), outOfAreaJsonFormString, progressDialogCallback);
        Utils.startAsyncTask(saveOutOfAreaServiceTask, null);
    }

    public static Map<String, String> getOutOfAreaMetadata(JSONObject outOfAreaForm) throws JSONException {

        Map<String, String> metadata = new HashMap<>();
        JSONArray fields = outOfAreaForm.getJSONObject(JsonFormConstants.STEP1).getJSONArray(JsonFormConstants.FIELDS);

        int foundFields = 0;
        for (int i = 0; i < fields.length(); i++) {

            JSONObject curField = fields.getJSONObject(i);

            if (curField.getString(JsonFormConstants.KEY).equals(Constants.KEY.OA_SERVICE_DATE)) {

                foundFields++;
                metadata.put(Constants.KEY.OA_SERVICE_DATE, curField.getString(JsonFormConstants.VALUE));

            } else if (curField.getString(JsonFormConstants.KEY).equalsIgnoreCase(Constants.KEY.ZEIR_ID)
                    || curField.getString(JsonFormConstants.KEY).equalsIgnoreCase(Constants.KEY.OPENSRP_ID)) {

                foundFields++;
                metadata.put(Constants.KEY.OPENSRP_ID, curField.getString(JsonFormConstants.VALUE));

            } else if (curField.getString(JsonFormConstants.KEY).equals(Constants.KEY.NFC_CARD_IDENTIFIER)) {

                metadata.put(Constants.KEY.NFC_CARD_IDENTIFIER, "c_0" + curField.getString(JsonFormConstants.VALUE));
                foundFields++;
            }

            if (foundFields == 2) {//we expect 2 metadata items, break early
                break;
            }
        }
        return metadata;
    }
}
