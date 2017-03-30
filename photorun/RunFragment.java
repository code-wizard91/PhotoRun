package com.application.dissertation.photorun;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * Created by Mizan on 24/02/2015.
 */
public class RunFragment extends Fragment{

    private GoogleMap mMap;
    private static final String ID_RUN = "RUN_ID";
    private static final int LOAD_RUN_ID = 0;
    private static final int LOAD_LOCATION_ID = 1;
    private DatabaseManager myDatabaseManager;
    private static Context context;

    private Run mRun;
    private Location mLastLocation;
    private Button Start, Stop, mMapButton;

    private TextView Started, LatitudeValues, LongtitudeValues, AltitudeValues, mDurationTextView;


    // Create a LocationReceiver and override the methods.
    private BroadcastReceiver mLocationReceiver = new Location_Finder() {

        @Override
        protected void onLocationReceived(Context context, Location location) {

            if (!myDatabaseManager.isTrackingRun(mRun)) return;
            mLastLocation = location;

            if (isVisible()) updateUI();

        }


        @Override
        protected void onProviderEnabledChanged(boolean enabled) {

            int toastText = enabled ? R.string.gps_enabled : R.string.gps_enabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
        }
    };




    private class LocationLoaderCallbacks implements LoaderCallbacks<Location> {
        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args) {
            return new LastLocationLoaderRunDatabase(getActivity(),
                    args.getLong(ID_RUN));
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location location) {
            mLastLocation = location;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Location> loader) {
            // Do nothing
        }
    }


    /**
     * newInstance method that puts Run ID in RunFragment's arguments.
     *
     * @param runId
     * @return RunFragment with Run ID bundled.
     */
    public static RunFragment newInstance(long runId) {

        Bundle args = new Bundle();
        args.putLong(ID_RUN, runId);
        RunFragment frag = new RunFragment();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        RunFragment.context = getActivity();
        myDatabaseManager = DatabaseManager.get(getActivity());

        // Check if there is a valid Run ID in arguments, if so, use it to load the
        // specific run
        Bundle args = getArguments();
        if (args != null) {
            long runId = args.getLong(ID_RUN, -1);

            if (runId != -1) {

                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_RUN_ID, args, new RunLoaderCallbacks());
                lm.initLoader(LOAD_LOCATION_ID, args, new LocationLoaderCallbacks());

            }
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_run, container, false);

        RunMiniatureMap();

        Started = (TextView) v.findViewById(R.id.startedTextView);
        LatitudeValues = (TextView) v.findViewById(R.id.latitudeTextView);
        LongtitudeValues = (TextView) v.findViewById(R.id.longitudeTextView);
        AltitudeValues = (TextView) v.findViewById(R.id.altitudeTextView);
        mDurationTextView = (TextView) v.findViewById(R.id.durationTextView);

        Start = (Button) v.findViewById(R.id.startButton);
        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // IF we didn't find a Run in onCreate(), create a new one and start
                // tracking it. ELSE start tracking the found Run.
                if (mRun == null) {
                    mRun = myDatabaseManager.startNewRun();
                } else {
                    myDatabaseManager.startTrackingRun(mRun);
                }
                updateUI();
            }
        });
        Stop = (Button) v.findViewById(R.id.stopButton);
        Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDatabaseManager.stopRun();
                updateUI();
            }
        });
        mMapButton = (Button) v.findViewById(R.id.run_mapButton);
        mMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ActivityMapRun.class);
                i.putExtra(ActivityMapRun.EXTRA_RUN_ID, mRun.getRunId());
                startActivity(i);
            }
        });
        updateUI();
        return v;
    }

    public void RunMiniatureMap() {
        // Getting reference to the SupportMapFragment
        SupportMapFragment fm = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // Initialising google maps
        mMap = fm.getMap();
        // Enabling location button
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Creating a criteria object in order to get the provider
        Criteria criteria = new Criteria();
        // Getting the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        //below we perform actions depending on how the location status changes
        android.location.LocationListener myLocationChangeDetector = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();
                drawMarker(location);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Toast.makeText(context.getApplicationContext(),"The Provider was Changed",Toast.LENGTH_LONG);
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
        mMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet("You are Here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("ME"));


        //zooming into the location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 13.9f));



    }



    @Override
    public void onStart() {
        super.onStart();

        // Register host activity as receiver for intents DatabaseManager.ACTION_LOCATION.
        getActivity().registerReceiver(
                mLocationReceiver, new IntentFilter(DatabaseManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {

        // Unregister host activity as receiver.
        getActivity().unregisterReceiver(mLocationReceiver);
        super.onStop();
    }

    /**
     *  This method is used to keep UI elements updated on the main screen.
     */
    private void updateUI() {


        // Enable Disable Start Stop buttons depending on run status
        boolean started = myDatabaseManager.isTracking();
        boolean trackingThisRun = myDatabaseManager.isTrackingRun(mRun);

        if (mRun != null){
            Started.setText(mRun.getStartDate().toString());
        }

        int durationSeconds = 0;
        if (mRun != null && mLastLocation != null){

            durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
            LatitudeValues.setText(String.valueOf(mLastLocation.getLatitude()));
            LongtitudeValues.setText(String.valueOf(mLastLocation.getLongitude()));
            AltitudeValues.setText(String.valueOf(mLastLocation.getAltitude()));
            mMapButton.setEnabled(true);
        }else{

            mMapButton.setEnabled(false);
        }

        mDurationTextView.setText(Run.formatDuration(durationSeconds));
        Start.setEnabled(!started);
        Stop.setEnabled(started && trackingThisRun);

    }


    private class RunLoaderCallbacks implements LoaderCallbacks<Run> {
        @Override
        public Loader<Run> onCreateLoader(int id, Bundle args) {
            return new Run_List_Loader(getActivity(), args.getLong(ID_RUN));
        }

        @Override
        public void onLoadFinished(Loader<Run> loader, Run run) {
            mRun = run;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Run> loader) {
            // Do nothing
        }
    }




}



