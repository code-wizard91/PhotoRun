package com.application.dissertation.photorun;

/**
 * Created by Mizan on 21/03/2015.
 */
import android.content.Context;
import android.database.Cursor;

public class LocationListDatabaseLoader extends SQLiteDatabaseLoader {

    private long mRunId;

    public LocationListDatabaseLoader(Context c, long runId) {
        super(c);
        mRunId = runId;
    }

    @Override
    protected Cursor loadCursor() {
        return DatabaseManager.get(getContext()).queryLocationsForRun(mRunId);
    }

}