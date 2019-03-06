package org.smartregister.child.sample.repository;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.child.sample.BuildConfig;
import org.smartregister.child.sample.application.SampleApplication;
import org.smartregister.child.util.Constants;
import org.smartregister.child.util.DBConstants;
import org.smartregister.child.util.Utils;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.domain.db.Column;
import org.smartregister.growthmonitoring.repository.WeightRepository;
import org.smartregister.growthmonitoring.repository.ZScoreRepository;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.RecurringServiceTypeRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.IMDatabaseUtils;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Hia2ReportRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.util.DatabaseMigrationUtils;

import java.util.ArrayList;

/**
 * Created by keyman on 28/07/2017.
 */
public class SampleRepository extends Repository {


    private static final String TAG = SampleRepository.class.getCanonicalName();
    protected SQLiteDatabase readableDatabase;
    protected SQLiteDatabase writableDatabase;
    private Context context;
    public static String PASSWORD = "Sample_PASS";

    public SampleRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), SampleApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository.createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository.createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());

        ConfigurableViewsRepository.createTable(database);
        UniqueIdRepository.createTable(database);

        SettingsRepository.onUpgrade(database);

        WeightRepository.createTable(database);
        VaccineRepository.createTable(database);

        runLegacyUpgrades(database);

        onUpgrade(database, 1, BuildConfig.DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SampleRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    //upgradeToVersion2(db);
                    break;
                default:
                    break;
            }
            upgradeTo++;
        }
    }


    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(PASSWORD);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(PASSWORD);
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Log.e(TAG, "Database Error. " + e.getMessage());
            return null;
        }

    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }


    /**
     * Version 2 added some columns to the ec_child table
     *
     * @param database
     */
    private void upgradeToVersion2(SQLiteDatabase database) {
        try {
            // Run insert query
            ArrayList<String> newlyAddedFields = new ArrayList<>();
            newlyAddedFields.add("BCG_2");
            newlyAddedFields.add("inactive");
            newlyAddedFields.add("lost_to_follow_up");

            DatabaseMigrationUtils.addFieldsToFTSTable(database, commonFtsObject, Utils.metadata().childRegister.tableName, newlyAddedFields);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion2 " + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion3(SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
            db.execSQL(VaccineRepository.EVENT_ID_INDEX);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
            db.execSQL(WeightRepository.EVENT_ID_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
            db.execSQL(VaccineRepository.FORMSUBMISSION_INDEX);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
            db.execSQL(WeightRepository.FORMSUBMISSION_INDEX);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion3 " + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion4(SQLiteDatabase db) {
        try {
            db.execSQL(AlertRepository.ALTER_ADD_OFFLINE_COLUMN);
            db.execSQL(AlertRepository.OFFLINE_INDEX);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion4" + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion5(SQLiteDatabase db) {
        try {
            RecurringServiceTypeRepository.createTable(db);
            RecurringServiceRecordRepository.createTable(db);

            RecurringServiceTypeRepository recurringServiceTypeRepository = SampleApplication.getInstance().recurringServiceTypeRepository();
            IMDatabaseUtils.populateRecurringServices(context, db, recurringServiceTypeRepository);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion5 " + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion6(SQLiteDatabase db) {
        try {
            ZScoreRepository.createTable(db);
            db.execSQL(WeightRepository.ALTER_ADD_Z_SCORE_COLUMN);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion6" + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion7OutOfArea(SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_HIA2_STATUS_COL);

        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion7Hia2 " + e.getMessage());
        }
    }

    private void upgradeToVersion8RecurringServiceUpdate(SQLiteDatabase db) {
        try {

            // Recurring service json changed. update
            RecurringServiceTypeRepository recurringServiceTypeRepository = SampleApplication.getInstance().recurringServiceTypeRepository();
            IMDatabaseUtils.populateRecurringServices(context, db, recurringServiceTypeRepository);

        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion8RecurringServiceUpdate " + Log.getStackTraceString(e));
        }
    }

    private void upgradeToVersion8ReportDeceased(SQLiteDatabase database) {
        try {

            String ALTER_ADD_DEATHDATE_COLUMN = "ALTER TABLE " + Utils.metadata().childRegister.tableName + " ADD COLUMN " + DBConstants.KEY.DOD + " VARCHAR";
            database.execSQL(ALTER_ADD_DEATHDATE_COLUMN);

            ArrayList<String> newlyAddedFields = new ArrayList<>();
            newlyAddedFields.add(DBConstants.KEY.DOD);

            DatabaseMigrationUtils.addFieldsToFTSTable(database, commonFtsObject, Utils.metadata().childRegister.tableName, newlyAddedFields);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion8ReportDeceased " + e.getMessage());
        }
    }

    private void upgradeToVersion9(SQLiteDatabase database) {
        try {
            String ALTER_EVENT_TABLE_VALIDATE_COLUMN = "ALTER TABLE " + EventClientRepository.Table.event + " ADD COLUMN " + EventClientRepository.event_column.validationStatus + " VARCHAR";
            database.execSQL(ALTER_EVENT_TABLE_VALIDATE_COLUMN);

            String ALTER_CLIENT_TABLE_VALIDATE_COLUMN = "ALTER TABLE " + EventClientRepository.Table.client + " ADD COLUMN " + EventClientRepository.client_column.validationStatus + " VARCHAR";
            database.execSQL(ALTER_CLIENT_TABLE_VALIDATE_COLUMN);

            String ALTER_REPORT_TABLE_VALIDATE_COLUMN = "ALTER TABLE " + Hia2ReportRepository.Table.hia2_report + " ADD COLUMN " + Hia2ReportRepository.report_column.validationStatus + " VARCHAR";
            database.execSQL(ALTER_REPORT_TABLE_VALIDATE_COLUMN);

            EventClientRepository.createIndex(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());
            EventClientRepository.createIndex(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
            EventClientRepository.createIndex(database, Hia2ReportRepository.Table.hia2_report, Hia2ReportRepository.report_column.values());

        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion9 " + e.getMessage());
        }
    }


    private void upgradeToVersion12(SQLiteDatabase db) {
        try {
            Column[] columns = {EventClientRepository.event_column.formSubmissionId};
            EventClientRepository.createIndex(db, EventClientRepository.Table.event, columns);

            db.execSQL(WeightRepository.ALTER_ADD_CREATED_AT_COLUMN);
            WeightRepository.migrateCreatedAt(db);

            db.execSQL(VaccineRepository.ALTER_ADD_CREATED_AT_COLUMN);
            VaccineRepository.migrateCreatedAt(db);

            db.execSQL(RecurringServiceRecordRepository.ALTER_ADD_CREATED_AT_COLUMN);
            RecurringServiceRecordRepository.migrateCreatedAt(db);

        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion12 " + e.getMessage());
        }
    }

    private void upgradeToVersion13(SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_COL);

            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_COL);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion13 " + e.getMessage());
        }
    }

    private void upgradeToVersion14(SQLiteDatabase db) {
        try {

            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_TEAM_COL);

            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);

            db.execSQL(WeightRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);

            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion14 " + e.getMessage());
        }
    }

    private void upgradeToVersion15RemoveUnnecessaryTables(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS address");
            db.execSQL("DROP TABLE IF EXISTS obs");
            if (DatabaseMigrationUtils.isColumnExists(db, "path_reports", Hia2ReportRepository.report_column.json.name()))
                db.execSQL("ALTER TABLE path_reports RENAME TO " + Hia2ReportRepository.Table.hia2_report.name() + ";");
            if (DatabaseMigrationUtils.isColumnExists(db, EventClientRepository.Table.client.name(), "firstName"))
                DatabaseMigrationUtils.recreateSyncTableWithExistingColumnsOnly(db, EventClientRepository.Table.client);
            if (DatabaseMigrationUtils.isColumnExists(db, EventClientRepository.Table.event.name(), "locationId"))
                DatabaseMigrationUtils.recreateSyncTableWithExistingColumnsOnly(db, EventClientRepository.Table.event);


        } catch (Exception e) {
            Log.e(TAG, "upgradeToVersion15RemoveUnnecessaryTables " + e.getMessage());
        }
    }

    private void runLegacyUpgrades(SQLiteDatabase database) {
        upgradeToVersion2(database);
        upgradeToVersion3(database);
        upgradeToVersion4(database);
        upgradeToVersion5(database);
        upgradeToVersion6(database);
        upgradeToVersion7OutOfArea(database);
        upgradeToVersion8RecurringServiceUpdate(database);
        upgradeToVersion8ReportDeceased(database);
        upgradeToVersion9(database);
        upgradeToVersion12(database);
        upgradeToVersion13(database);
        upgradeToVersion14(database);
        upgradeToVersion15RemoveUnnecessaryTables(database);

    }

}
