package org.smartregister.child.util;

import android.content.res.Resources;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.api.constants.Gender;
import org.smartregister.Context;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.EditWrapper;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.immunization.db.VaccineRepo;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ndegwamartin on 25/02/2019.
 */
public class Utils extends org.smartregister.util.Utils {
    public static final SimpleDateFormat DB_DF = new SimpleDateFormat(Constants.SQLITE_DATE_TIME_FORMAT);
    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String DEFAULT_LOCATION_LEVEL = "Health Facility";
    public static final String FACILITY = "Facility";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(FACILITY);
    }

    public static int getProfileImageResourceIDentifier() {
        return R.mipmap.ic_child;
    }

    public static TableRow getDataRow(android.content.Context context, String label, String value, TableRow row) {
        TableRow tr = row;
        if (row == null) {
            tr = new TableRow(context);
            TableRow.LayoutParams trlp =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(trlp);
            tr.setPadding(10, 5, 10, 5);
        }

        TextView l = new TextView(context);
        l.setText(label + ": ");
        l.setPadding(20, 2, 20, 2);
        l.setTextColor(Color.BLACK);
        l.setTextSize(14);
        l.setBackgroundColor(Color.WHITE);
        tr.addView(l);

        TextView v = new TextView(context);
        v.setText(value);
        v.setPadding(20, 2, 20, 2);
        v.setTextColor(Color.BLACK);
        v.setTextSize(14);
        v.setBackgroundColor(Color.WHITE);
        tr.addView(v);

        return tr;
    }

    public static TableRow getDataRow(android.content.Context context, String label, String value, String field,
                                      TableRow row) {
        TableRow tr = row;
        if (row == null) {
            tr = new TableRow(context);
            TableRow.LayoutParams trlp =
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(trlp);
            tr.setPadding(10, 5, 10, 5);
        }

        TextView l = new TextView(context);
        l.setText(label + ": ");
        l.setPadding(20, 2, 20, 2);
        l.setTextColor(Color.BLACK);
        l.setTextSize(14);
        l.setBackgroundColor(Color.WHITE);
        tr.addView(l);

        EditWrapper editWrapper = new EditWrapper();
        editWrapper.setCurrentValue(value);
        editWrapper.setField(field);

        EditText e = new EditText(context);
        e.setTag(editWrapper);
        e.setText(value);
        e.setPadding(20, 2, 20, 2);
        e.setTextColor(Color.BLACK);
        e.setTextSize(14);
        e.setBackgroundColor(Color.WHITE);
        e.setInputType(InputType.TYPE_NULL);
        tr.addView(e);

        return tr;
    }

    public static TableRow getDataRow(android.content.Context context) {
        TableRow tr = new TableRow(context);
        TableRow.LayoutParams trlp =
                new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tr.setLayoutParams(trlp);
        tr.setPadding(0, 0, 0, 0);
        // tr.setBackgroundColor(Color.BLUE);
        return tr;
    }

    public static int addAsInts(boolean ignoreEmpty, String... vals) {
        int i = 0;
        for (String v : vals) {
            i += ignoreEmpty && StringUtils.isBlank(v) ? 0 : Integer.parseInt(v);
        }
        return i;
    }

    public static TableRow addToRow(android.content.Context context, String value, TableRow row) {
        return addToRow(context, value, row, false, 1);
    }

    public static TableRow addToRow(android.content.Context context, String value, TableRow row, int weight) {
        return addToRow(context, value, row, false, weight);
    }

    public static TableRow addToRow(android.content.Context context, String value, TableRow row, boolean compact) {
        return addToRow(context, value, row, compact, 1);
    }

    public static void putAll(Map<String, String> map, Map<String, String> extend) {
        Collection<String> values = extend.values();
        while (true) {
            if (!(values.remove(null))) break;
        }
        map.putAll(extend);
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
            Log.e(Utils.class.getCanonicalName(), Log.getStackTraceString(e));
        }

    }

    public static Date getDateFromString(String date, String dateFormatPattern) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
            return dateFormat.parse(date);
        } catch (ParseException e) {
            Log.e(Utils.class.getCanonicalName(), e.getMessage());
            return null;
        }
    }

    public static int yearFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static Date getCohortEndDate(VaccineRepo.Vaccine vaccine, Date startDate) {
        if (vaccine == null || startDate == null) {
            return null;
        }
        String vaccineName = VaccineRepository.addHyphen(vaccine.display().toLowerCase());
        return getCohortEndDate(vaccineName, startDate);

    }

    public static Date getCohortEndDate(String vaccine, Date startDate) {

        if (StringUtils.isBlank(vaccine) || startDate == null) {
            return null;
        }

        Calendar endDateCalendar = Calendar.getInstance();
        endDateCalendar.setTime(startDate);

        final String opv0 = VaccineRepository.addHyphen(VaccineRepo.Vaccine.opv0.display().toLowerCase());

        final String rota1 = VaccineRepository.addHyphen(VaccineRepo.Vaccine.rota1.display().toLowerCase());
        final String rota2 = VaccineRepository.addHyphen(VaccineRepo.Vaccine.rota2.display().toLowerCase());

        final String measles2 = VaccineRepository.addHyphen(VaccineRepo.Vaccine.measles2.display().toLowerCase());
        final String mr2 = VaccineRepository.addHyphen(VaccineRepo.Vaccine.mr2.display().toLowerCase());

        if (vaccine.equals(opv0)) {
            endDateCalendar.add(Calendar.DATE, 13);
        } else if (vaccine.equals(rota1) || vaccine.equals(rota2)) {
            endDateCalendar.add(Calendar.MONTH, 8);
        } else if (vaccine.equals(measles2) || vaccine.equals(mr2)) {
            endDateCalendar.add(Calendar.YEAR, 2);
        } else {
            endDateCalendar.add(Calendar.YEAR, 1);
        }

        return endDateCalendar.getTime();
    }


    public static Date dobStringToDate(String dobString) {
        DateTime dateTime = dobStringToDateTime(dobString);
        if (dateTime != null) {
            return dateTime.toDate();
        }
        return null;
    }

    public static DateTime dobStringToDateTime(String dobString) {
        try {
            if (StringUtils.isBlank(dobString)) {
                return null;
            }
            return new DateTime(dobString);

        } catch (Exception e) {
            return null;
        }
    }

    public static String getTodaysDate() {
        return convertDateFormat(Calendar.getInstance().getTime(), DB_DF);
    }

    public static String convertDateFormat(Date date, SimpleDateFormat formatter) {

        return formatter.format(date);
    }

    public static void recordWeight(WeightRepository weightRepository, WeightWrapper weightWrapper, String dobString,
                                    String syncStatus) {

        Weight weight = new Weight();
        if (weightWrapper.getDbKey() != null) {
            weight = weightRepository.find(weightWrapper.getDbKey());
        }
        weight.setBaseEntityId(weightWrapper.getId());
        weight.setKg(weightWrapper.getWeight());
        weight.setDate(weightWrapper.getUpdatedWeightDate().toDate());
        weight.setAnmId(ChildLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM());
        weight.setSyncStatus(syncStatus);

        Gender gender = Gender.UNKNOWN;
        String genderString = weightWrapper.getGender();
        if (genderString != null && genderString.toLowerCase().equals(Constants.GENDER.FEMALE)) {
            gender = Gender.FEMALE;
        } else if (genderString != null && genderString.toLowerCase().equals(Constants.GENDER.MALE)) {
            gender = Gender.MALE;
        }

        Date dob = Utils.dobStringToDate(dobString);

        if (dob != null && gender != Gender.UNKNOWN) {
            weightRepository.add(dob, gender, weight);
        } else {
            weightRepository.add(weight);
        }

        weightWrapper.setDbKey(weight.getId());
    }

    public static void recordHeight(HeightRepository heightRepository, HeightWrapper heightWrapper, String dobString,
                                    String syncStatus) {
        if (heightWrapper != null && heightWrapper.getHeight() != null && heightWrapper.getId() != null) {
            Height height = new Height();
            if (heightWrapper.getDbKey() != null) {
                height = heightRepository.find(heightWrapper.getDbKey());
            }
            height.setBaseEntityId(heightWrapper.getId());
            height.setCm(heightWrapper.getHeight());
            height.setDate(heightWrapper.getUpdatedHeightDate().toDate());
            height.setAnmId(ChildLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM());
            height.setSyncStatus(syncStatus);

            Gender gender = Gender.UNKNOWN;
            String genderString = heightWrapper.getGender();
            if (genderString != null && genderString.toLowerCase().equals(Constants.GENDER.FEMALE)) {
                gender = Gender.FEMALE;
            } else if (genderString != null && genderString.toLowerCase().equals(Constants.GENDER.MALE)) {
                gender = Gender.MALE;
            }

            Date dob = Utils.dobStringToDate(dobString);

            if (dob != null && gender != Gender.UNKNOWN) {
                heightRepository.add(dob, gender, height);
            } else {
                heightRepository.add(height);
            }

            heightWrapper.setDbKey(height.getId());
        }
    }

    public static Context context() {
        return ChildLibrary.getInstance().context();
    }

    public static ChildMetadata metadata() {
        return ChildLibrary.getInstance().metadata();
    }

    public static String updateGrowthValue(String value) {
        String growthValue = value;
        if (!value.contains(".")) {
            growthValue = value + ".0";
        }
        return growthValue;
    }

    public static Map<String, String> getCleanMap(Map<String, String> rawDetails) {
        Map<String, String> clean = new HashMap<>();

        try {
            //    Map<String, String> old = CoreLibrary.getInstance().context().detailsRepository().getAllDetailsForClient
            //    (getChildDetails().getCaseId());

            Map<String, String> old = rawDetails;
            for (Map.Entry<String, String> entry : old.entrySet()) {
                String val = entry.getValue();
                if (!TextUtils.isEmpty(val) && !"null".equalsIgnoreCase(val.toLowerCase())) {
                    clean.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            Log.e(Utils.class.getCanonicalName(), e.getMessage(), e);
        }

        return clean;

    }

    public static String formatNumber(String raw) {
        try {

            NumberFormat nf = NumberFormat.getInstance();
            nf.setGroupingUsed(false);
            return nf.format(NumberFormat.getInstance(Locale.ENGLISH).parse(raw));

        } catch (ParseException e) {
            return raw;
        }
    }

    public static String getTranslatedIdentifier(String key) {

        String myKey;
        try {
            myKey = ChildLibrary.getInstance().context().applicationContext().getString(ChildLibrary.getInstance().context().applicationContext().getResources().getIdentifier(key.toLowerCase(), "string", ChildLibrary.getInstance().context().applicationContext().getPackageName()));

        } catch (Resources.NotFoundException resourceNotFoundException) {
            myKey = key;
        }
        return myKey;
    }

    public static String bold(String textToBold) {
        return "<b>" + textToBold + "</b>";
    }
}
