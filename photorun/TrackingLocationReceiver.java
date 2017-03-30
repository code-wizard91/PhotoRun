package com.application.dissertation.photorun;

import android.content.Context;
import android.location.Location;

/**
 * Created by Mizan on 03/03/2015.
 */
public class TrackingLocationReceiver extends Location_Finder {

    @Override
    protected void onLocationReceived(Context context, Location location) {
        DatabaseManager.get(context).insertLocation(location);
    }
}
