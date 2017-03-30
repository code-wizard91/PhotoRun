package com.application.dissertation.photorun;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;


public class cameraFragment extends android.support.v4.app.Fragment {

    private GoogleMap mMap;
    private static Context context;
    private ImageView camera,info,save;
    private static final int CAMERA_REQUEST = 1888;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraFragment.context = getActivity();


    }

    public static cameraFragment newInstance() {
        cameraFragment fragment = new cameraFragment();

        return fragment;
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        info = (ImageView) rootView.findViewById(R.id.infobutton);
        camera = (ImageView) rootView.findViewById(R.id.camerabutton);
        save=(ImageView) rootView.findViewById(R.id.savebutton);

        RunMiniatureMap();

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new RunFragment())
                        .commit();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takeSnap();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                        "content://media/internal/images/media"));
                startActivity(intent);

            }
        });


        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CAMERA_REQUEST){



                        Bitmap photo = (Bitmap) data.getExtras().get("data");
            LocationManager lManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = lManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = lManager.getLastKnownLocation(provider);

            LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(currentPosition)
                    .snippet("You are Here")
                    .icon(BitmapDescriptorFactory.fromBitmap(photo))
                    .title("ME"));


            //zooming into the location
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 13.9f));





        }



    }



    public void takeSnap(){



        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                try {
                    FileOutputStream out = new FileOutputStream("/mnt/sdcard/Download/TeleSensors.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mMap.snapshot(callback);


    }

    public void RunMiniatureMap() {


        // Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment fm = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        // Getting GoogleMap object from the fragment
        mMap = fm.getMap();

        // Enabling MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);


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
                //mMap.clear();
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
        //onLocationChanged(location);


    }




    private void drawMarker(Location location){
        // Remove any existing markers on the map
        //mMap.clear();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);




    }
}


