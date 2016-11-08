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

        //Check for location permissions enabled.
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startMap();
        }
        else
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMap();
                }
                else
                {
                    Toast.makeText(getContext(), "Location permission not granted. Please grant this permission to view supermarkets nearby.", Toast.LENGTH_SHORT).show();
                }
        return;
        }
    }


    public void startMap()
    {
        GooglePlayServices googlePlayServices = new GooglePlayServices(getContext(), getActivity());
        if (googlePlayServices.checkAvailable()) {
            mapView.getMapAsync(this);
        } else {
            Toast.makeText(getContext(), "Unable to load map.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            googleMap.setMyLocationEnabled(true);

            //http://www.materialdoc.com/linear-progress/
            ProgressBar locationMarkersProgress = (ProgressBar)getView().findViewById(R.id.locationMarkersProgress);
            locationMarkersProgress.setVisibility(View.VISIBLE);

            TextView locationMarkersProgressTxt = (TextView)getView().findViewById(R.id.locationMarkersProgressTxt);
            locationMarkersProgressTxt.setVisibility(View.VISIBLE);

            GooglePlacesAsync googlePlacesAsync = new GooglePlacesAsync(getContext(),getActivity(),googleMap);
            googlePlacesAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
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
