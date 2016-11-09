package com.josh_davey.mobile_computing_assignment;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class GoogleLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    //Variables.
    Context ctx;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Activity activity;
    GoogleMap googleMap;

    public GoogleLocation(Context ctx, Activity activity, GoogleMap googleMap)
    {
        this.ctx = ctx;
        this.activity = activity;
        this.googleMap = googleMap;
    }

    public void startLocation()
    {
        GooglePlayServices googlePlayServices = new GooglePlayServices(ctx,activity);

        if(googlePlayServices.checkAvailable()) {
            if (buildGoogleApiClient()) {
                googleApiClient.connect();
            }
        }
    }

    public boolean buildGoogleApiClient() {
        try {
            googleApiClient = new GoogleApiClient.Builder(ctx)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates()
    {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        else {
            //Requesting permission to access device location.
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void stopLocationUpdates()
    {
        //If the googleApiClient is connected, stop location updates and disconnect (Save battery).
        if (googleApiClient != null && googleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(ctx, location.getLatitude()+", "+location.getLongitude(), Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Shows location on map.
            googleMap.setMyLocationEnabled(true);
            //Set map to show location and set zoom level.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),13));

            //Get places near location once location has changed.
            GooglePlacesAsync googlePlacesAsync = new GooglePlacesAsync(ctx,activity,googleMap);
            googlePlacesAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            //Enables visibility for a progressbar and accompanying text http://www.materialdoc.com/linear-progress/
            ProgressBar locationMarkersProgress = (ProgressBar)activity.findViewById(R.id.placesProgress);
            locationMarkersProgress.setVisibility(View.VISIBLE);
            TextView locationMarkersProgressTxt = (TextView)activity.findViewById(R.id.placesProgressTxt);
            locationMarkersProgressTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
