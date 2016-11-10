package com.josh_davey.mobile_computing_assignment;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

//https://developer.android.com/guide/components/fragments.html
//https://developers.google.com/android/reference/com/google/android/gms/maps/MapView
public class Fragment3 extends Fragment implements OnMapReadyCallback {
    MapView mapView;
    GoogleLocation googleLocation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment3_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialise and create map, loading the container regardless of connectivity status and location access status.
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        startMap();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "permission granted", Toast.LENGTH_SHORT).show();
                    Button b = (Button)getActivity().findViewById(R.id.getNearbyBtn);
                    b.performClick();
                }
                else
                {
                    Toast.makeText(getContext(), "Location permission not granted. Please grant this permission to view supermarkets nearby.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void startMap()
    {
        GooglePlayServices googlePlayServices = new GooglePlayServices(getContext(), getActivity());
        //If google play services is available, load map.
        if (googlePlayServices.checkAvailable()) {
            mapView.getMapAsync(this);
        } else {
            Toast.makeText(getContext(), "Unable to load map.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleLocation = new GoogleLocation(getContext(),getActivity(),googleMap);

        Button btn = (Button)getActivity().findViewById(R.id.getNearbyBtn);
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Check if the location listener is already on - if true, toast to tell user.
                    if(googleLocation.requestingLocation)
                    {
                        Toast.makeText(getContext(), "We're still trying to get your location, please wait.", Toast.LENGTH_SHORT).show();
                    }
                    //Check if places are being downloaded still - if true, toast to tell user.
                    else if (googleLocation.googlePlacesAsync != null && googleLocation.googlePlacesAsync.getStatus() == AsyncTask.Status.RUNNING)
                    {
                        Toast.makeText(getContext(), "We're still trying to download places, please wait.", Toast.LENGTH_SHORT).show();
                    }
                    //Start location listening to get current location and get places.
                    else
                    {
                        googleLocation.startLocation();
                    }
                }
                else
                {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        //Checking if the app is still listening for location updates, stopping them if true.
        if(googleLocation != null && googleLocation.requestingLocation)
        {
            googleLocation.stopLocationUpdates();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
