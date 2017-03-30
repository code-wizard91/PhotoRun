package com.application.dissertation.photorun;

/**
 * Created by Mizan on 20/03/2015.
 */

import java.util.ArrayList;

import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.application.dissertation.photorun.RunListDatabaseHelper.LocationCursor;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class RunMainMapFragment extends SupportMapFragment implements LoaderCallbacks<Cursor>{


    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_LOCATIONS = 0;
    private GoogleMap mGoogleMap;
    private LocationCursor mLocationCursor;
    private static Context context;
    private boolean mDelayed;
    ArrayList<LatLng> runlist = new ArrayList<>();


    private Location_Finder mLocationReceiver = new Location_Finder(){
        @Override
        protected void onLocationReceived(Context context, Location loc) {
            if(!mDelayed) {    //The delay is used to make sure the location is loaded
                mDelayed = true;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isVisible())   //Check of the fragment is visible to the user this is to avoid crashes
                            return;
                        mGoogleMap.clear();//clears the map of objects when loaded
                        restartLoader();
                        mDelayed = false;
                    }
                }, 5000);                     //number of seconds to delay the loading of the map
            }
        }
    };

    public static RunMainMapFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMainMapFragment rf = new RunMainMapFragment();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RunMainMapFragment.context = getActivity();
        // Check for a Run ID as an argument, and find the run
        Bundle args = getArguments();
        if (args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
                if (runId != -1) {
                    LoaderManager lm = getLoaderManager();
                        lm.initLoader(LOAD_LOCATIONS, args, this);
                }
            }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);


        // Stash a reference to the GoogleMap
        mGoogleMap = getMap();
        // Show the user's location
        mGoogleMap.setMyLocationEnabled(true);


        return v;
    }






    private void updateUI(){

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        android.location.LocationListener myLocationChangeDetector = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                drawMarker(location);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(context.getApplicationContext(), "The Provider was Changed", Toast.LENGTH_LONG);
            }
            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(context.getApplicationContext(),"The Provider was Enabled",Toast.LENGTH_LONG);
            }
            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(context.getApplicationContext(),"The Provider was Disabled",Toast.LENGTH_LONG);
            }
        };

        if(location!=null)
        {
            //PLACE THE INITIAL MARKER
            drawMarker(location);
        }
        locationManager.requestLocationUpdates(provider,10000,10,myLocationChangeDetector);
    }



    private void drawMarker(Location location){

        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
        runlist.add(currentPosition);

        for(int i = 0; i<runlist.size();i++){

            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(runlist.get(i))
                    .title("TRACKING")
                    .snippet("Real Time Locations"));
        }
        AddLines();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(runlist.get(0), 13.9f));

    }
    public void AddLines(){

        for(int i = 0; i<runlist.size()-1;i++) {

            LatLng source = runlist.get(i);
            LatLng destinationLine = runlist.get(i+1);

            Polyline line = mGoogleMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(source.latitude, source.longitude),
                            new LatLng(destinationLine.latitude,destinationLine.longitude)
                    ).width(7).color(Color.BLUE).geodesic(true)
            );

        }


    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long runId = args.getLong(ARG_RUN_ID, -1);
        return new LocationListDatabaseLoader(getActivity(), runId);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mLocationCursor = (LocationCursor)cursor;
       updateUI();
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Stop using the data
        mLocationCursor.close();
        mLocationCursor = null;
    }

    private void restartLoader(){
        Bundle args = getArguments();
        if (args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
            if (runId != -1) {
                LoaderManager lm = getLoaderManager();
                lm.restartLoader(LOAD_LOCATIONS, args, this); //method used to force loader to restart
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(mLocationReceiver,
                new IntentFilter(DatabaseManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }





}
