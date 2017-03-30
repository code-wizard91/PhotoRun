package com.application.dissertation.photorun;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.application.dissertation.photorun.RunListDatabaseHelper.LocationCursor;

/**
 * Created by Mizan on 24/02/2015.
 */
public class DatabaseManager {

    public static final String TAG = "DatabaseManager";

    public static final String PREFS_FILE = "runs";
    public static final String PREF_CURRENT_RUN_ID = "DatabaseManager.currentRunId";

    public static final String ACTION_LOCATION = "gary.tracker.ACTION_LOCATION";

    public static final String TEST_PROVIDER = "TEST_PROVIDER";

    private static DatabaseManager sDatabaseManager;
    private Context mAppContext;
    private LocationManager mLocationManager;
    private RunListDatabaseHelper mHelper;
    private SharedPreferences mPrefs;
    private long mCurrentRunId;


    public LocationCursor queryLocationsForRun(long runId) {
        return mHelper.getlocationForRun(runId);
    }


    private DatabaseManager(Context appContext){
        mAppContext = appContext;
        mLocationManager = (LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);
        mHelper = new RunListDatabaseHelper(mAppContext);
        mPrefs = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentRunId = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);
    }

    /**
     * Make DatabaseManager a singleton
     *
     * @param context - Context
     * @return DatabaseManager
     */
    public static DatabaseManager get(Context context){

        if (sDatabaseManager == null){
            sDatabaseManager = new DatabaseManager(context.getApplicationContext());
        }

        return sDatabaseManager;
    }

    /**
     * Creates Intent to be broadcast when location updates happen.
     *
     * @param shouldCreate boolean telling PendingIntent.getBroadcast() whether it
     *                     should create a new PendingIntent or not
     * @return PendingIntent or null.
     */
    private PendingIntent getLocationPendingIntent(boolean shouldCreate){

        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;

        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    /**
     * Starts location tracking by telling LocationManager to give location updates via
     * GPS_PROVIDER as frequently as possible.
     *
     * *Currently uses TestProvider that provides mock locations.
     */
    public void startLocationUpdates(){

        String provider = LocationManager.GPS_PROVIDER;

        // If we have the test provider and it's enabled, use it.
        if (mLocationManager.getProvider(TEST_PROVIDER) != null &&
                mLocationManager.isProviderEnabled(TEST_PROVIDER)){
            provider = TEST_PROVIDER;
        }

        Toast.makeText(mAppContext, "Using Provider: " + provider.toUpperCase(), Toast.LENGTH_SHORT).show();

        // Start updates from LocationManager
        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
    }

    /**
     * Stops location tracking
     */
    public void stopLocationUpdates(){

        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null){

            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    /**
     * Checks whether location tracking is on by checking whether a PendingIntent
     * exists using getLocationPendingIntent()
     *
     * @return boolean whether location tracking is on
     */
    public boolean isTracking(){
        return getLocationPendingIntent(false) != null;
    }

    /**
     * Checks whether passed in run is currently being tracked.
     * @param run
     * @return boolean whether current run is being tracked
     */
    public boolean isTrackingRun(Run run){
        return run != null && run.getRunId() == mCurrentRunId;
    }

    /**
     * Get a RunCursor pointing to run with passed in ID. If there are any results,
     * set that as our run and return it.
     *
     * @param id
     * @return Run that matches passed in ID
     */
    public Run getRun(long id){

        Run run = null;
        RunListDatabaseHelper.RunCursor cursor = mHelper.queryRunID(id);
        cursor.moveToFirst();

        if (!cursor.isAfterLast()){
            run = cursor.getRun();
        }

        cursor.close();

        return run;

    }

    /**
     * Get a LocationCursor pointing to last location of run with passed in ID. If
     * there are any results, set that as our location and return it.
     *
     * @param runId
     * @return Last Location of run with passed in ID
     */
    public Location getLastLocationForRun(long runId){

        Location location = null;

        RunListDatabaseHelper.LocationCursor cursor = mHelper.getLastLocationForEachRun(runId);
        cursor.moveToFirst();

        if (!cursor.isAfterLast()){
            location = cursor.getLocation();
        }

        cursor.close();

        return location;
    }

    /**
     * Calls createAndInsertRun() to create a new Run and insert it into the db. Then
     * starts tracking using that Run by calling startTrackingRun() on it.
     *
     * @return run
     */
    public Run startNewRun(){

        // Retrieve a new run by calling createAndInsertRun()
        Run run = createAndInsertRun();

        // Start tracking the run
        startTrackingRun(run);

        return run;
    }

    /**
     * Gets the ID of passed in Run and stores it in an instance variable and in
     * SharedPrefs.
     *
     * @param run
     */
    public void startTrackingRun(Run run){

        // Get the ID and store it in an instance variable
        mCurrentRunId = run.getRunId();

        // Also store it in SharedPrefs so we can retrieve in constructor app killed
        mPrefs.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentRunId).commit();

        startLocationUpdates();

    }

    /**
     * Creates a new run and inserts it into the db using RunListDatabaseHelper.AddRunToDB().
     * Then sets the ID of the Run to that returned by AddRunToDB().
     *
     * @return Run
     */
    private Run createAndInsertRun(){

        Run run = new Run();
        run.setRunId(mHelper.AddRunToDB(run));

        return run;
    }

    public RunListDatabaseHelper.RunCursor queryRuns(){

        return mHelper.queryDatabaseRuns();
    }

    /**
     * IF there is a Run being tracked currently, insert the passed in location into db.
     *
     * @param location
     */
    public void insertLocation(Location location){

        if (mCurrentRunId != -1){
            mHelper.insertLocationIntoDB(mCurrentRunId, location);
        }
        else{
            Log.e(TAG, "Location received with no currently tracking run; ignoring.");
        }
    }




    /**
     * Stops location updates and clears ID of current run.
     */
    public void stopRun(){

        stopLocationUpdates();;
        mCurrentRunId = -1;
        mPrefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }
}
