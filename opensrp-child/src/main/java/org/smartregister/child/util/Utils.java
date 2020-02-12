package org.smartregister.child.util;

import android.content.ContentValues;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.constants.Gender;
import org.smartregister.Context;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.R;
import org.smartregister.child.domain.ChildMetadata;
import org.smartregister.child.domain.EditWrapper;
import org.smartregister.child.event.BaseEvent;
import org.smartregister.child.event.ClientDirtyFlagEvent;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.commonregistry.AllCommonsRepository;
import org.smartregister.growthmonitoring.domain.Height;
import org.smartregister.growthmonitoring.domain.HeightWrapper;
import org.smartregister.growthmonitoring.domain.Weight;
import org.smartregister.growthmonitoring.domain.WeightWrapper;
import org.smartregister.growthmonitoring.repository.HeightRepository;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.service.intent.HeightIntentService;
import org.smartregister.growthmonitoring.service.intent.WeightIntentService;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.immunization.domain.Vaccine;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.service.intent.VaccineIntentService;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.UniqueIdRepository;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

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
            vaccine.setName(vaccine.getName().trim());
            // Add the vaccine
            vaccineRepository.add(vaccine);

            String name = vaccine.getName();
            if (!StringUtils.isBlank(name) && name.contains("/")) {

                updateFTSForCombinedVaccineAlternatives(vaccineRepository, vaccine);
            }

            if (vaccine != null && !BaseRepository.TYPE_Synced.equals(vaccine.getSyncStatus()))
                Utils.postEvent(new ClientDirtyFlagEvent(vaccine.getBaseEntityId(), VaccineIntentService.EVENT_TYPE));

        } catch (Exception e) {
            Timber.e(e);
        }

    }

    /**
     * Update vaccines in the same group where either can be given. For example measles 1 / mr 1
     *
     * @param vaccineRepository
     * @param vaccine
     */
    public static void updateFTSForCombinedVaccineAlternatives(VaccineRepository vaccineRepository, Vaccine vaccine) {

        List<String> ftsVaccineNames = getAlternativeCombinedVaccines(VaccineRepository.removeHyphen(vaccine.getName()), ImmunizationLibrary.getInstance().COMBINED_VACCINES_MAP);

        if (ftsVaccineNames != null) {

            for (String ftsVaccineName : ftsVaccineNames) {
                ftsVaccineName = VaccineRepository.addHyphen(ftsVaccineName.toLowerCase());
                Vaccine ftsVaccine = new Vaccine();
                ftsVaccine.setBaseEntityId(vaccine.getBaseEntityId());
                ftsVaccine.setName(ftsVaccineName);
                vaccineRepository.updateFtsSearch(ftsVaccine);
            }

        }
    }

    /**
     * @param vaccineName_       Vaccine whos alternative vaccines names must be found
     * @param combinedVaccineMap Combined vaccine map
     * @return list of alternative vaccines to {@code vaccineName_}
     */

    public static List<String> getAlternativeCombinedVaccines(String vaccineName_, Map<String, String> combinedVaccineMap) {

        List<String> comboVaccineList = null;

        String vaccineName = VaccineRepository.removeHyphen(vaccineName_);
        String comboVaccinesValue = combinedVaccineMap.get(vaccineName_);
        if (comboVaccinesValue != null) {

            String[] comboVaccines = StringUtils.stripAll(comboVaccinesValue.split("/"));

            comboVaccineList = Lists.newArrayList(comboVaccines);

            comboVaccineList.remove(vaccineName);
        }
        return comboVaccineList;

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

    public static void recordWeight(WeightRepository weightRepository, WeightWrapper weightWrapper, String syncStatus) {

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

        Date dob = Utils.dobStringToDate(weightWrapper.getDob());

        if (dob != null && gender != Gender.UNKNOWN) {
            weightRepository.add(dob, gender, weight);
        } else {
            weightRepository.add(weight);
        }

        weightWrapper.setDbKey(weight.getId());

        if (weight != null && !BaseRepository.TYPE_Synced.equals(syncStatus))
            Utils.postEvent(new ClientDirtyFlagEvent(weight.getBaseEntityId(), WeightIntentService.EVENT_TYPE));

    }

    public static void recordHeight(HeightRepository heightRepository, HeightWrapper heightWrapper, String syncStatus) {
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
            if (genderString != null && Constants.GENDER.FEMALE.equalsIgnoreCase(genderString)) {
                gender = Gender.FEMALE;
            } else if (genderString != null && Constants.GENDER.MALE.equalsIgnoreCase(genderString)) {
                gender = Gender.MALE;
            }

            Date dob = Utils.dobStringToDate(heightWrapper.getDob());

            if (dob != null && gender != Gender.UNKNOWN) {
                heightRepository.add(dob, gender, height);
            } else {
                heightRepository.add(height);
            }

            heightWrapper.setDbKey(height.getId());

            if (height != null && !BaseRepository.TYPE_Synced.equals(syncStatus))
                Utils.postEvent(new ClientDirtyFlagEvent(height.getBaseEntityId(), HeightIntentService.EVENT_TYPE));
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
        if (NumberUtils.isNumber(value) && Double.parseDouble(value) > 0) {
            if (!value.contains(".")) {
                growthValue = value + ".0";
            }
            return growthValue;
        }

        throw new IllegalArgumentException(value + "is not a positive number");
    }

    public static Map<String, String> getCleanMap(Map<String, String> rawDetails) {
        Map<String, String> clean = new HashMap<>();

        try {
            for (Map.Entry<String, String> entry : rawDetails.entrySet()) {
                String val = entry.getValue();
                if (!TextUtils.isEmpty(val) && !"null".equalsIgnoreCase(val.toLowerCase())) {
                    clean.put(entry.getKey(), entry.getValue());
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return clean;

    }

    public static String formatNumber(String raw) {
        try {

            NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
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

    public static Integer getWeeksDue(DateTime dueDate) {

        return dueDate != null ? Math.abs(Weeks.weeksBetween(new DateTime(), dueDate).getWeeks()) : null;
    }

    @NonNull
    public static String getChildBirthDate(@Nullable JSONObject jsonObject) throws JSONException {
        String childBirthDate = "";

        if (jsonObject != null && jsonObject.has(FormEntityConstants.Person.birthdate.toString())) {
            childBirthDate = jsonObject.getString(FormEntityConstants.Person.birthdate.toString());
        }

        return childBirthDate.contains("T") ? childBirthDate.substring(0, childBirthDate.indexOf('T')) : childBirthDate;
    }

    public static void updateLastInteractionWith(String baseEntityId, String tableName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constants.KEY.LAST_INTERACTED_WITH, Calendar.getInstance().getTimeInMillis());
        updateLastInteractionWith(baseEntityId, tableName, contentValues);
    }

    public static void updateLastInteractionWith(String baseEntityId, String tableName, ContentValues contentValues) {
        AllCommonsRepository allCommonsRepository = ChildLibrary.getInstance().context().allCommonsRepositoryobjects(tableName);
        allCommonsRepository.update(tableName, contentValues, baseEntityId);
        allCommonsRepository.updateSearch(baseEntityId);
    }

    public static String reverseHyphenatedString(String date) {

        String[] dateArray = date.split("-");
        ArrayUtils.reverse(dateArray);

        return StringUtils.join(dateArray, "-");
    }

    public static void postEvent(BaseEvent baseEvent) {

        EventBus eventBus = ChildLibrary.getInstance().getEventBus();

        if (eventBus != null && baseEvent != null)
            eventBus.post(baseEvent);
    }

    public static Date getDate(String eventDateStr) {
        Date date = null;
        if (StringUtils.isNotBlank(eventDateStr)) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH);
                date = dateFormat.parse(eventDateStr);
            } catch (ParseException e) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
                    date = dateFormat.parse(eventDateStr);
                } catch (ParseException pe) {
                    try {
                        date = DateUtil.parseDate(eventDateStr);
                    } catch (ParseException pee) {
                        Timber.e(pee);
                    }
                }
            }
        }
        return date;
    }

    public static String getNextOpenMrsId() {
        UniqueIdRepository uniqueIdRepo = ChildLibrary.getInstance().getUniqueIdRepository();
        return uniqueIdRepo.getNextUniqueId() != null ? uniqueIdRepo.getNextUniqueId().getOpenmrsId() : "";
    }

    public static boolean isVaccineDue(@NonNull List<Vaccine> vaccineList, @NonNull Date dob, @NonNull org.smartregister.immunization.domain.jsonmapping.Vaccine vaccine, boolean allowedExpiredVaccineEntry) {
        Date dueDate = VaccineCalculator.getVaccineDueDate(vaccine, dob, vaccineList);
        Date expiryDate = VaccineCalculator.getVaccineExpiryDate(dob, vaccine);
        return (dueDate != null && (expiryDate == null || allowedExpiredVaccineEntry || expiryDate.after(Calendar.getInstance().getTime())));
    }
}