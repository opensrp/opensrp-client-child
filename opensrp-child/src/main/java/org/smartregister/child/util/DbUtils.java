package org.smartregister.child.util;

import android.content.ContentValues;

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
