package com.application.dissertation.photorun;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.Date;

/**
 * Created by Mizan on 03/03/2015.
 */
public class RunListDatabaseHelper extends SQLiteOpenHelper {

    private static final String Database_Id = "runs.sqlite";
    private static final int Version_number_id = 1;
    private static final String RUNTable = "run";
    private static final String RUN_ID_Column = "_id";
    private static final String RUNSTARTDATE_Column = "start_date";
    private static final String LOCATIONINFO_Table = "location";
    private static final String LATITUDE_COLUMN = "latitude";
    private static final String LONGITUDE_COLUMN = "longitude";
    private static final String ALTITUDE_COLUMN = "altitude";
    private static final String LOCATION_TIME_COLUMN = "timestamp";
    private static final String LOCATION_PROVIDER_COLUMN = "provider";
    private static final String LOCATIONRUN_ID_COLUMN = "run_id";

    public LocationCursor getlocationForRun(long runId) {
        Cursor mywrappedcursor = getReadableDatabase().query(LOCATIONINFO_Table, null,
                LOCATIONRUN_ID_COLUMN + " = ?", // Limit to the given run
                new String[] { String.valueOf(runId) }, null, // group by
                null, // having
                LOCATION_TIME_COLUMN + " asc"); // order by timestamp
        return new LocationCursor(mywrappedcursor);
    }

    public LocationCursor getLastLocationForEachRun(long runId) {
        Cursor wrappedCursor = getReadableDatabase().query(LOCATIONINFO_Table, null, // All
                // columns
                LOCATIONRUN_ID_COLUMN + " = ?", // limit to the given run
                new String[] { String.valueOf(runId) }, null, // group by
                null, // having
                LOCATION_TIME_COLUMN + " desc", // order by latest first
                "1"); // limit 1
        return new LocationCursor(wrappedCursor);
    }

    public long insertLocationIntoDB(long runId, Location location) {
        ContentValues cv = new ContentValues();
        cv.put(LATITUDE_COLUMN, location.getLatitude());
        cv.put(LONGITUDE_COLUMN, location.getLongitude());
        cv.put(ALTITUDE_COLUMN, location.getAltitude());
        cv.put(LOCATION_TIME_COLUMN, location.getTime());
        cv.put(LOCATION_PROVIDER_COLUMN, location.getProvider());
        cv.put(LOCATIONRUN_ID_COLUMN, runId);
        return getWritableDatabase().insert(LOCATIONINFO_Table, null, cv);
    }

    public RunCursor queryRunID(long id) {
        Cursor wrapped = getReadableDatabase().query(RUNTable, null, // All
                // columns
                RUN_ID_Column + " = ?", // Look for a run ID
                new String[] { String.valueOf(id) }, // with this value
                null, // group by
                null, // order by
                null, // having
                "1"); // limit 1 row
        return new RunCursor(wrapped);
    }

    public RunListDatabaseHelper(Context context) {
        super(context, Database_Id, null, Version_number_id);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the "run" table
        db.execSQL("create table run ("
                + "_id integer primary key autoincrement, start_date integer)");
        // Create the "location" table
        db.execSQL("create table location ("
                + " timestamp integer, latitude real, longitude real, altitude real,"
                + " provider varchar(100), run_id integer references run(_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement schema changes and data massage here when upgrading
    }

    public long AddRunToDB(Run run) {
        ContentValues contentVal = new ContentValues();
        contentVal.put(RUNSTARTDATE_Column, run.getStartDate().getTime());
        return getWritableDatabase().insert(RUNTable, null, contentVal);
    }

    public RunCursor queryDatabaseRuns() {
        // Equivalent to "select * from run order by start_date asc"
        Cursor wrapped = getReadableDatabase().query(RUNTable, null, null,
                null, null, null, RUNSTARTDATE_Column + " asc");
        return new RunCursor(wrapped);
    }

    public static class LocationCursor extends CursorWrapper {
        public LocationCursor(Cursor c) {
            super(c);
        }
        public Location getLocation() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            // get provider in order to use the constructor
            String provider = getString(getColumnIndex(LOCATION_PROVIDER_COLUMN));
            Location location = new Location(provider);
            // Populate each specific properties
            location.setLongitude(getDouble(getColumnIndex(LONGITUDE_COLUMN)));
            location.setLatitude(getDouble(getColumnIndex(LATITUDE_COLUMN)));
            location.setAltitude(getDouble(getColumnIndex(ALTITUDE_COLUMN)));
            location.setTime(getLong(getColumnIndex(LOCATION_TIME_COLUMN)));
            return location;
        }
    }

    /**
     * A convenience class to wrap a cursor that returns rows from the "run"
     * table. The {@link //getRun()} method will give you a Run instance
     * representing the current row.
     */
    public static class RunCursor extends CursorWrapper {
        public RunCursor(Cursor c) {
            super(c);
        }

        /**
         * Returns a Run object configured for the current row, or null if the
         * current row is invalid.
         */
        public Run getRun() {
            if (isBeforeFirst() || isAfterLast())
                return null;
            Run run = new Run();
            long runId = getLong(getColumnIndex(RUN_ID_Column));
            run.setRunId(runId);
            long startDate = getLong(getColumnIndex(RUNSTARTDATE_Column));
            run.setStartDate(new Date(startDate));
            return run;
        }
    }

}