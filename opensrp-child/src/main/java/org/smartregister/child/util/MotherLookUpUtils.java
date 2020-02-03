package org.smartregister.child.util;

import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.domain.EntityLookUp;
import org.smartregister.child.repository.RegisterRepository;
import org.smartregister.clientandeventmodel.DateUtil;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.event.Listener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static android.view.View.VISIBLE;
import static org.smartregister.util.Utils.getValue;

/**
 * Created by keyman on 26/01/2017.
 */
public class MotherLookUpUtils {
    public static final String firstName = "first_name";
    public static final String lastName = "last_name";
    public static final String birthDate = "date_birth";
    public static final String dob = "dob";
    public static final String baseEntityId = "base_entity_id";
    public static final String MOTHER_GUARDIAN_NRC = "Mother_Guardian_NRC";
    public static final String MOTHER_GUARDIAN_PHONE_NUMBER = "Mother_Guardian_Phone_Number";
    public static final String RELATIONAL_ID = "relational_id";
    public static final String CONTACT_PHONE_NUMBER = "contact_phone_number";
    public static final String NRC_NUMBER = "nrc_number";
    public static final String DETAILS = "details";
    public static final String RELATIONALID = "relationalid";

    public static void motherLookUp(final Context context, final EntityLookUp entityLookUp,
                                    final Listener<HashMap<CommonPersonObject, List<CommonPersonObject>>> listener,
                                    final ProgressBar progressBar) {

        org.smartregister.util.Utils
                .startAsyncTask(new AsyncTask<Void, Void, HashMap<CommonPersonObject, List<CommonPersonObject>>>() {
                    @Override
                    protected HashMap<CommonPersonObject, List<CommonPersonObject>> doInBackground(Void... params) {
                        publishProgress();
                        return lookUp(context, entityLookUp);
                    }

                    @Override
                    protected void onPostExecute(HashMap<CommonPersonObject, List<CommonPersonObject>> result) {
                        listener.onEvent(result);
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    protected void onProgressUpdate(Void... values) {
                        if (progressBar != null) {
                            progressBar.setVisibility(VISIBLE);
                        }
                    }
                }, null);
    }

    private static HashMap<CommonPersonObject, List<CommonPersonObject>> lookUp(Context context, EntityLookUp entityLookUp) {
        HashMap<CommonPersonObject, List<CommonPersonObject>> results = new HashMap<>();
        if (context == null) {
            return results;
        }


        if (entityLookUp.isEmpty()) {
            return results;
        }

        String tableName = Utils.metadata().childRegister.motherTableName;


        List<String> ids = new ArrayList<>();
        List<CommonPersonObject> motherList = new ArrayList<>();

        CommonRepository commonRepository = context.commonrepository(tableName);
        String query = lookUpQuery(entityLookUp.getMap(), tableName);

        Cursor cursor = null;
        try {

            cursor = commonRepository.rawCustomQueryForAdapter(query);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    CommonPersonObject commonPersonObject = commonRepository.readAllcommonforCursorAdapter(cursor);
                    motherList.add(commonPersonObject);


                    ids.add(commonPersonObject.getCaseId());
                    cursor.moveToNext();
                }
            }


        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (motherList.isEmpty()) {
            return results;
        }

        StringBuilder relationalIds = new StringBuilder();
        for (String id : ids) {
            relationalIds.append("'" + id + "'");
        }


        List<HashMap<String, String>> childList = ChildLibrary.getInstance()
                .eventClientRepository()
                .rawQuery(ChildLibrary.getInstance().getRepository().getReadableDatabase(),
                        RegisterRepository.mainRegisterQuery()
                                + " where child_details.relational_id IN (" + relationalIds + ")");
        for (CommonPersonObject mother : motherList) {
            results.put(mother, findChildren(childList, mother.getCaseId()));
        }


        return results;

    }

    private static String lookUpQuery(Map<String, String> entityMap, String tableName) {

        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.SelectInitiateMainTable(tableName,
                new String[]{RegisterRepository.getDemographicTable() + "." + RELATIONALID, RegisterRepository.getDemographicTable() + "." + DETAILS, Constants.KEY.ZEIR_ID, Constants.KEY.FIRST_NAME, Constants.KEY.LAST_NAME,
                        AllConstants.ChildRegistrationFields.GENDER, Constants.KEY.DOB, NRC_NUMBER, CONTACT_PHONE_NUMBER,
                        RegisterRepository.getDemographicTable() + "." + Constants.KEY.BASE_ENTITY_ID}

        );
        queryBuilder.customJoin(" join ec_child_details on ec_child_details.relational_id=ec_mother_details.base_entity_id join ec_mother_details on ec_mother_details.base_entity_id = ec_client.base_entity_id ");
        String query = queryBuilder.mainCondition(getMainConditionString(entityMap));
        return queryBuilder.Endquery(query);
    }

    private static List<CommonPersonObject> findChildren(List<HashMap<String, String>> childList, String motherBaseEnityId) {
        List<CommonPersonObject> foundChildren = new ArrayList<>();
        for (Map<String, String> child : childList) {
            CommonPersonObject commonPersonObject = new CommonPersonObject(child.get(baseEntityId), child.get(RELATIONALID), child, "child");
            String relationalID = getValue(commonPersonObject.getDetails(), RELATIONAL_ID, false);
            if (!foundChildren.contains(child) && relationalID.equals(motherBaseEnityId)) {
                foundChildren.add(commonPersonObject);
            }
        }

        return foundChildren;

    }

    private static String getMainConditionString(Map<String, String> entityMap) {

        String mainConditionString = "";
        for (Map.Entry<String, String> entry : entityMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (StringUtils.containsIgnoreCase(key, firstName)) {
                key = firstName;
            }

            if (StringUtils.containsIgnoreCase(key, lastName)) {
                key = lastName;
            }

            if (StringUtils.equalsIgnoreCase(key, MOTHER_GUARDIAN_PHONE_NUMBER)) {
                key = CONTACT_PHONE_NUMBER;
            }

            if (StringUtils.equalsIgnoreCase(key, MOTHER_GUARDIAN_NRC)) {
                key = NRC_NUMBER;
            }


            if (StringUtils.containsIgnoreCase(key, birthDate)) {
                if (!isDate(value)) {
                    continue;
                }
                key = dob;
            }

            if (!key.equals(dob)) {
                if (StringUtils.isBlank(mainConditionString)) {
                    mainConditionString += " " + key + " Like '%" + value + "%'";
                } else {
                    mainConditionString += " AND " + key + " Like '%" + value + "%'";

                }
            } else {
                if (StringUtils.isBlank(mainConditionString)) {
                    mainConditionString += " cast(" + key + " as date) " + " =  cast('" + value + "'as date) ";
                } else {
                    mainConditionString += " AND cast(" + key + " as date) " + " =  cast('" + value + "'as date) ";

                }
            }
        }

        return mainConditionString;

    }

    private static boolean isDate(String dobString) {
        try {
            DateUtil.yyyyMMdd.parse(dobString);
            return true;
        } catch (ParseException e) {
            return false;
        }

    }
}
