package org.smartregister.child.util;


import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import timber.log.Timber;

public class DbMigrations {

    public static boolean AddShowBcg2ReminderAndBcgScarColumnsToEcChildDetails(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE " + Utils.metadata().getRegisterQueryProvider().getChildDetailsTable() + " ADD COLUMN ? TEXT default null",
                    new String[]{Constants.SHOW_BCG2_REMINDER});
            db.execSQL("ALTER TABLE " + Utils.metadata().getRegisterQueryProvider().getChildDetailsTable() + " ADD COLUMN ? TEXT default null",
                    new String[]{Constants.SHOW_BCG_SCAR});
            return true;
        } catch (SQLException | IllegalArgumentException e) {
            Timber.e(e);
            return false;
        }
    }
}
