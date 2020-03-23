package org.smartregister.child.util;


import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import timber.log.Timber;

public class DbMigrations {

    public static boolean addShowBcg2ReminderAndBcgScarColumnsToEcChildDetails(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE " + Utils.metadata().getRegisterQueryProvider().getChildDetailsTable() + " ADD COLUMN " + Constants.SHOW_BCG2_REMINDER + " TEXT default null");
            db.execSQL("ALTER TABLE " + Utils.metadata().getRegisterQueryProvider().getChildDetailsTable() + " ADD COLUMN " + Constants.SHOW_BCG_SCAR + " TEXT default 0");
            return true;
        } catch (SQLException | IllegalArgumentException e) {
            Timber.e(e);
            return false;
        }
    }
}
