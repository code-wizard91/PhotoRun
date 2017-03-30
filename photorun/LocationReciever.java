package com.application.dissertation.photorun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

/**
 * Created by Mizan on 24/02/2015.
 */
public class LocationReciever extends BroadcastReceiver {

    public static final String TAG = "LocationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // Try and retrieve location
        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);

        // If got location, call onLocationReceived() with it and return
        if (location != null){

            onLocationReceived(context, location);
            return;
        }

        // If this code runs, someone else has happened. Maybe provider was changed.
        if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)){
            boolean enabled = intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
            onProviderEnabledChanged(enabled);
        }

    }

    /**
     * Log the details of the location we got. (Should be overridden)
     *
     * @param context
     * @param location
     */
    protected void onLocationReceived(Context context, Location location){

        Log.d(TAG, this + " Got location from " + location.getProvider() + ": " +
                location.getLatitude() + ", " + location.getLongitude());
    }

    /**
     * Log details of provider change. (Should be overridden)
     *
     * @param enabled
     */
    protected void onProviderEnabledChanged(boolean enabled){
        Log.d(TAG, "Provider " + (enabled ? "enabled" : "disabled"));
    }
}
