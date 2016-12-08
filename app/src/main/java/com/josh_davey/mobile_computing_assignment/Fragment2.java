package com.josh_davey.mobile_computing_assignment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

/*References:
    https://developer.android.com/guide/components/fragments.html
    https://developers.google.com/android/reference/com/google/android/gms/maps/MapView*/
public class Fragment2 extends Fragment implements OnMapReadyCallback {
    //Variables.
    MapView mapView;
    GoogleLocation googleLocation;
    Button getPlacesBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialise and create map, loading the container regardless of connectivity status and location access status.
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        //Call startMap method, loading google map.
        startMap();

        //Get instance of getPlacesBtn
        getPlacesBtn = (Button) getActivity().findViewById(R.id.getNearbyBtn);
    }

    public void startMap() {
        //If google play services is available, load map.
        GooglePlayServices googlePlayServices = new GooglePlayServices(getContext(), getActivity());
        if (googlePlayServices.checkAvailable()) {
            mapView.getMapAsync(this);
        } else {
            Toast.makeText(getContext(), "Unable to load google map.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Create instance of GoogleLocation class to use when checking if location is being requested, and to start location requests.
        googleLocation = new GoogleLocation(getContext(), getActivity(), googleMap);

        //OnClick listener for when button is pressed for getting nearby places.
        getPlacesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if permissions are enabled, if not, request them.
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    //Check if the location listener is already on - if true, toast to tell user.
                    if (googleLocation.requestingLocation) {
                        Toast.makeText(getContext(), "We're still trying to get your location, please wait.", Toast.LENGTH_SHORT).show();
                    }
                    //Check if places are being downloaded still - if true, toast to tell user.
                    else if (googleLocation.googlePlacesAsync != null && googleLocation.googlePlacesAsync.getStatus() == AsyncTask.Status.RUNNING) {
                        Toast.makeText(getContext(), "We're still trying to download places, please wait.", Toast.LENGTH_SHORT).show();
                    }
                    //Start location listening to get current location and get places.
                    else {
                        googleLocation.startLocation();
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });
    }

    //Functionality for when permisions are requested.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                //If user grants permissions, toast and perform click on getPlacesBtn. Else, toast error.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Permissions are now granted.", Toast.LENGTH_SHORT).show();
                    getPlacesBtn.performClick();
                } else {
                    Toast.makeText(getContext(), "Location permission not granted. Please grant this permission to view supermarkets nearby.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //Google maps docs requirement.
        mapView.onStop();
        //Call stopLocationUpdates method. This checks if the app is still listening for location updates, stopping them if true.
        if(googleLocation != null) {
            googleLocation.stopLocationUpdates();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //Google maps docs requirement.
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //Google maps docs requirement.
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Google maps docs requirement.
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Google maps docs requirement.
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //Google maps docs requirement.
        mapView.onLowMemory();
    }
}
