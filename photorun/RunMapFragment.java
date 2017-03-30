package com.application.dissertation.photorun;

/**
 * Created by Mizan on 20/03/2015.
 */

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.dissertation.photorun.RunDatabaseHelper.LocationCursor;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.achartengine.renderer.SimpleSeriesRenderer;

public class RunMapFragment extends SupportMapFragment implements LoaderCallbacks<Cursor>{


    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_LOCATIONS = 0;
    private GoogleMap mGoogleMap;
    private LocationCursor mLocationCursor;
    private static Context context;
    private boolean mDelayed;
    ArrayList<LatLng> runlist = new ArrayList<>();


    private LocationReciever mLocationReceiver = new LocationReciever(){
        @Override
        protected void onLocationReceived(Context context, Location loc) {
            if(!mDelayed) {               //checks if it is delayed and in countdown
                mDelayed = true;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isVisible())   //Check fragment is visible to user, if we didn't use this, the activity would
                            return;        // crash when pressing back as the code below would still run after the delay
                        mGoogleMap.clear();                         //removes overlays of map
                        restartLoader();
                        mDelayed = false;
                    }
                }, 5000);                     //number of seconds to delay
            }
        }
    };

    public static RunMapFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment rf = new RunMapFragment();
        rf.setArguments(args);
        return rf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RunMapFragment.context = getActivity();
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

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

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
        // Remove any existing markers on the map
        //mMap.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
        runlist.add(currentPosition);


        for(int i = 0; i<runlist.size();i++){



            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(runlist.get(i))
                    .title("TRACKING")
                    .snippet("Real Time Locations"));



        }

        AddLines();
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(runlist.get(0),13));
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
        return new LocationListCursorLoader(getActivity(), runId);
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
                new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }





}
