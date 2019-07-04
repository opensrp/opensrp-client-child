package org.smartregister.child.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.VaccinatorUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ndegwamartin on 05/03/2019.
 */
public class SaveOutOfAreaServiceTask extends AsyncTask<Void, Void, Void> {

    private final Context context;
    private final org.smartregister.Context openSrpContext;
    private final String formString;
    private WeightRepository weightRepository;
    private VaccineRepository vaccineRepository;

    public SaveOutOfAreaServiceTask(Context context, org.smartregister.Context openSrpContext, String formString, WeightRepository weightRepository, VaccineRepository vaccineRepository) {
        this.context = context;
        this.openSrpContext = openSrpContext;
        this.formString = formString;
        this.weightRepository = weightRepository;
        this.vaccineRepository = vaccineRepository;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            JSONObject form = new JSONObject(formString);

            // Create a weight object if weight was recorded
            Weight weight = getWeightObject(openSrpContext, form);
            if (weight != null) {
                weightRepository.add(weight);
            }

            // Create a vaccine object for all recorded vaccines
            ArrayList<Vaccine> vaccines = getVaccineObjects(context, openSrpContext, form);
            if (vaccines.size() > 0) {
                for (Vaccine curVaccine : vaccines) {
                    addVaccine(vaccineRepository, curVaccine);
                }
            }
        } catch (Exception e) {
            Log.e(SaveOutOfAreaServiceTask.class.getCanonicalName(), Log.getStackTraceString(e));
        }
        return null;
    }


    /**
     * Constructs a weight object using the out of service area form
     *
     * @param openSrpContext The context to work with
     * @param outOfAreaForm  Out of area form to extract the weight form
     * @return A weight object if weight recorded in form, or {@code null} if weight not recorded
     * @throws Exception
     */
    private static Weight getWeightObject(org.smartregister.Context openSrpContext, JSONObject outOfAreaForm) throws Exception {
        Weight weight = null;
        JSONArray fields = outOfAreaForm.getJSONObject("step1").getJSONArray("fields");
        String serviceDate = null;
        String openSrpId = null;

        int foundFields = 0;
        for (int i = 0; i < fields.length(); i++) {
            JSONObject curField = fields.getJSONObject(i);
            if (curField.getString("key").equals("Weight_Kg")) {
                foundFields++;
                if (StringUtils.isNotEmpty(curField.getString("value"))) {
                    weight = new Weight();
                    weight.setBaseEntityId("");
                    weight.setKg(Float.parseFloat(curField.getString("value")));
                    weight.setAnmId(openSrpContext.allSharedPreferences().fetchRegisteredANM());
                    weight.setLocationId(outOfAreaForm.getJSONObject("metadata")
                            .getString("encounter_location"));
                    weight.setUpdatedAt(null);
                }
            } else if (curField.getString("key").equals("OA_Service_Date")) {
                foundFields++;
                serviceDate = curField.getString("value");
            } else if (curField.getString("key").equals("ZEIR_ID")) {
                foundFields++;
                openSrpId = formatChildUniqueId(curField.getString("value"));
            }

            if (foundFields == 3) {
                break;
            }
        }

        if (weight != null && serviceDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN);
            weight.setDate(dateFormat.parse(serviceDate));
        }

        if (weight != null && openSrpId != null) {
            weight.setProgramClientId(openSrpId);
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
    private static ArrayList<Vaccine> getVaccineObjects(Context context,
                                                        org.smartregister.Context openSrpContext,
                                                        JSONObject outOfAreaForm) throws Exception {
        ArrayList<Vaccine> vaccines = new ArrayList<>();
        JSONArray fields = outOfAreaForm.getJSONObject("step1").getJSONArray("fields");
        String serviceDate = null;
        String openSrpId = null;

        for (int i = 0; i < fields.length(); i++) {
            JSONObject curField = fields.getJSONObject(i);
            if (curField.has("is_vaccine_group")
                    && curField.getBoolean("is_vaccine_group")
                    && curField.getString("type").equals("check_box")) {
                JSONArray options = curField.getJSONArray("options");
                for (int j = 0; j < options.length(); j++) {
                    JSONObject curOption = options.getJSONObject(j);
                    if (curOption.getString("value").equalsIgnoreCase(Boolean.TRUE.toString())) {
                        Vaccine curVaccine = new Vaccine();
                        curVaccine.setBaseEntityId("");
                        curVaccine.setName(curOption.getString("key"));
                        curVaccine.setAnmId(openSrpContext.allSharedPreferences().fetchRegisteredANM());
                        curVaccine.setLocationId(outOfAreaForm.getJSONObject("metadata")
                                .getString("encounter_location"));
                        curVaccine.setCalculation(VaccinatorUtils.getVaccineCalculation(context,
                                curVaccine.getName()));
                        curVaccine.setUpdatedAt(null);

                        vaccines.add(curVaccine);
                    }
                }
            } else if (curField.getString("key").equals("OA_Service_Date")) {
                serviceDate = curField.getString("value");
            } else if (curField.getString("key").equals("ZEIR_ID")) {
                openSrpId = formatChildUniqueId(curField.getString("value"));
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(com.vijay.jsonwizard.utils.FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN);
        for (Vaccine curVaccine : vaccines) {
            if (serviceDate != null) {
                curVaccine.setDate(dateFormat.parse(serviceDate));
            }

            if (openSrpId != null) {
                curVaccine.setProgramClientId(openSrpId);
            }
        }

        return vaccines;
    }

    public static void addVaccine(VaccineRepository vaccineRepository, Vaccine vaccine) {
        try {
            if (vaccineRepository == null || vaccine == null) {
                return;
            }

            // Add the vaccine
            vaccineRepository.add(vaccine);

            String name = vaccine.getName();
            if (StringUtils.isBlank(name)) {
                return;
            }

            // Update vaccines in the same group where either can be given
            // For example measles 1 / mr 1
            name = VaccineRepository.removeHyphen(name);
            String ftsVaccineName = null;

            if (VaccineRepo.Vaccine.measles1.display().equalsIgnoreCase(name)) {
                ftsVaccineName = VaccineRepo.Vaccine.mr1.display();
            } else if (VaccineRepo.Vaccine.mr1.display().equalsIgnoreCase(name)) {
                ftsVaccineName = VaccineRepo.Vaccine.measles1.display();
            } else if (VaccineRepo.Vaccine.measles2.display().equalsIgnoreCase(name)) {
                ftsVaccineName = VaccineRepo.Vaccine.mr2.display();
            } else if (VaccineRepo.Vaccine.mr2.display().equalsIgnoreCase(name)) {
                ftsVaccineName = VaccineRepo.Vaccine.measles2.display();
            }

            if (ftsVaccineName != null) {
                ftsVaccineName = VaccineRepository.addHyphen(ftsVaccineName.toLowerCase());
                Vaccine ftsVaccine = new Vaccine();
                ftsVaccine.setBaseEntityId(vaccine.getBaseEntityId());
                ftsVaccine.setName(ftsVaccineName);
                vaccineRepository.updateFtsSearch(ftsVaccine);
            }

        } catch (Exception e) {
            Log.e(SaveOutOfAreaServiceTask.class.getCanonicalName(), Log.getStackTraceString(e));
        }

    }

    /**
     * This method formats the child unique id obtained from a JSON Form to something that is useable
     *
     * @param unformattedId The unformatted unique identifier
     * @return A formatted ID or the original id if method is unable to format
     */
    private static String formatChildUniqueId(String unformattedId) {
        if (StringUtils.isNotBlank(unformattedId) && !unformattedId.contains("-")) {
            StringBuilder stringBuilder = new StringBuilder(unformattedId);
            stringBuilder.insert(unformattedId.length() - 1, '-');
            unformattedId = stringBuilder.toString();
        }

        return unformattedId;
    }
}
