package org.smartregister.child.util;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.smartregister.Context;
import org.smartregister.child.ChildLibrary;
import org.smartregister.child.contract.IMotherLookup;
import org.smartregister.child.domain.EntityLookUp;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.event.Listener;

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
    public static final String IS_CONSENTED = "is_consented";
    public static final String RELATIONAL_ID = "relational_id";
    public static final String NRC_NUMBER = "nrc_number";
    public static final String DETAILS = "details";
    public static final String RELATIONALID = "relationalid";
    public static final String MOTHER_NATIONALITY = "mother_nationality";
    public static final String MOTHER_NATIONALITY_OTHER = "mother_nationality_other";

    public static void motherLookUp(final Context context, final Activity activityContext, final EntityLookUp entityLookUp,
                                    final Listener<Map<CommonPersonObject, List<CommonPersonObject>>> listener,
                                    final ProgressBar progressBar) {

        org.smartregister.util.Utils
                .startAsyncTask(new AsyncTask<Void, Void, Map<CommonPersonObject, List<CommonPersonObject>>>() {
                    @Override
                    protected Map<CommonPersonObject, List<CommonPersonObject>> doInBackground(Void... params) {
                        publishProgress();
                        return lookUp(context, activityContext, entityLookUp);
                    }

                    @Override
                    protected void onPostExecute(Map<CommonPersonObject, List<CommonPersonObject>> result) {
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

    private static Map<CommonPersonObject, List<CommonPersonObject>> lookUp(Context context, Activity activityContext, EntityLookUp entityLookUp) {
        Map<CommonPersonObject, List<CommonPersonObject>> results = new HashMap<>();
        if (context == null) {
            return results;
        }


        if (entityLookUp == null || entityLookUp.isEmpty()) {
            return results;
        }

        String tableName = Utils.metadata().getRegisterQueryProvider().getDemographicTable();


        List<String> ids = new ArrayList<>();
        List<CommonPersonObject> motherList = new ArrayList<>();

        CommonRepository commonRepository = context.commonrepository(tableName);
        String query = ((IMotherLookup) activityContext).lookUpQuery(entityLookUp.getMap(), tableName);

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


        for (int i = 0; i < ids.size(); i++) {

            relationalIds.append("'").append(ids.get(i)).append("'");

            if (i != ids.size() - 1) {
                relationalIds.append(",");
            }
        }

        List<HashMap<String, String>> childList = ChildLibrary.getInstance()
                .eventClientRepository()
                .rawQuery(ChildLibrary.getInstance().getRepository().getReadableDatabase(),
                        Utils.metadata().getRegisterQueryProvider().mainRegisterQuery()
                                + " where " + Utils.metadata().getRegisterQueryProvider().getDemographicTable() + "." + Constants.KEY.IS_CLOSED + " == 0 and " + Utils.metadata().getRegisterQueryProvider().getChildDetailsTable() + ".relational_id IN (" + relationalIds + ")");
        for (CommonPersonObject mother : motherList) {
            results.put(mother, findChildren(childList, mother.getCaseId()));
        }


        return results;

    }

    private static List<CommonPersonObject> findChildren
            (List<HashMap<String, String>> childList, String motherBaseEnityId) {
        List<CommonPersonObject> foundChildren = new ArrayList<>();
        for (Map<String, String> child : childList) {
            CommonPersonObject commonPersonObject = new CommonPersonObject(child.get(baseEntityId), child.get(RELATIONALID), child, "child");
            commonPersonObject.setColumnmaps(child);
            String relationalID = getValue(commonPersonObject.getDetails(), RELATIONAL_ID, false);
            if (!foundChildren.contains(commonPersonObject) && relationalID.equals(motherBaseEnityId)) {
                foundChildren.add(commonPersonObject);
            }
        }

        return foundChildren;

    }

}
