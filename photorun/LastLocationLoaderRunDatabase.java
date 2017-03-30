package com.application.dissertation.photorun;

/**
 * Created by Mizan on 20/03/2015.
 */
import android.content.Context;
import android.location.Location;

public class LastLocationLoaderRunDatabase extends RunDatabaseDataLoader<Location> {
    private long mRunId;

    public LastLocationLoaderRunDatabase(Context context, long runId) {
        super(context);
        mRunId = runId;
    }

    @Override
    public Location loadInBackground() {
        return DatabaseManager.get(getContext()).getLastLocationForRun(mRunId);
    }

}
