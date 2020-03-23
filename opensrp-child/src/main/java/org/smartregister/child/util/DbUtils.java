package org.smartregister.child.util;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.child.ChildLibrary;

import java.util.HashMap;

public class DbUtils {
    public static HashMap<String, String> fetchChildDetails(String baseEntityId) {
        return ChildLibrary.getInstance()
                .eventClientRepository()
                .rawQuery(ChildLibrary.getInstance().getRepository().getReadableDatabase(),
                        Utils.metadata().getRegisterQueryProvider().mainRegisterQuery() +
                                " where " + Utils.metadata().getRegisterQueryProvider().getDemographicTable() + ".id = '" + baseEntityId + "' limit 1").get(0);
    }

    public static HashMap<String, String> fetchChildFirstGrowthAndMonitoring(String baseEntityId) {
        HashMap<String, String> hashMap = new HashMap<>();
        SQLiteDatabase sqLiteDatabase = ChildLibrary.getInstance().getRepository().getReadableDatabase();
        Cursor weightCursor = sqLiteDatabase.query("weights", new String[]{"kg", "created_at"},
                "base_entity_id = ?",
                new String[]{baseEntityId}, null, null, "created_at asc", "1");
        String dateCreated = "";
        String weight = null;
        String height = null;
        if (weightCursor != null && weightCursor.getCount() > 0 && weightCursor.moveToNext()) {
            weight = weightCursor.getString(0);
            dateCreated = weightCursor.getString(1);
            hashMap.put("birth_weight", weight);
        }

        Cursor heightCursor = sqLiteDatabase.query("heights", new String[]{"cm", "created_at"},
                "base_entity_id = ? and created_at = ?",
                new String[]{baseEntityId, dateCreated}, null, null, null, "1");
        if (heightCursor != null && heightCursor.getCount() > 0 && heightCursor.moveToNext()) {
            height = heightCursor.getString(0);
            hashMap.put("birth_height", height);

        }
        return hashMap;
    }

    public static boolean updateChildDetailsValue(String columnName, String value, String baseEntityId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, value);
        int i = ChildLibrary.getInstance()
                .getRepository().getWritableDatabase()
                .update(ChildLibrary.getInstance().metadata().getRegisterQueryProvider().getChildDetailsTable(),
                        contentValues, Constants.KEY.BASE_ENTITY_ID + "= ?", new String[]{baseEntityId});
        return i != 0;
    }
}
