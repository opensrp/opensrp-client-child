package org.smartregister.child.util;


import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import timber.log.Timber;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class ChildDbMigrations {

    public static boolean addShowBcg2ReminderAndBcgScarColumnsToEcChildDetails(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE " + Utils.metadata().getRegisterQueryProvider().getChildDetailsTable() + " ADD COLUMN " + Constants.SHOW_BCG2_REMINDER + " TEXT default null");
            db.execSQL("ALTER TABLE " + Utils.metadata().getRegisterQueryProvider().getChildDetailsTable() + " ADD COLUMN " + Constants.SHOW_BCG_SCAR + " TEXT default null");
            return true;
        } catch (SQLException | IllegalArgumentException e) {
            FirebaseCrashlytics.getInstance().recordException(e); Timber.e(e);
            return false;
        }
    }
}
