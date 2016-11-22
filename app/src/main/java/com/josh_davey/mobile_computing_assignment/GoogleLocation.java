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
    Boolean requestingLocation= false;
    Activity activity;
    GoogleMap googleMap;
    GooglePlacesAsync googlePlacesAsync;

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
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates()
    {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            requestingLocation = true;
            Toast.makeText(ctx, "Attempting to find your location...", Toast.LENGTH_SHORT).show();
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
            requestingLocation = false;
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
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Stop location updates, location has been acquired.
            stopLocationUpdates();
            //Shows location on map.
            googleMap.setMyLocationEnabled(true);
            //Set map to show location and set zoom level.
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),13));
            //Get places near location once location has been found.
            googlePlacesAsync = new GooglePlacesAsync(ctx, activity, googleMap);
            googlePlacesAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location.getLatitude(), location.getLongitude());

            //Checks if device is connected to either a wifi network or mobile network. Toast if not.
            NetworkStatus networkStatus = new NetworkStatus(ctx);
            if(!networkStatus.checkConnection()) {
                Toast.makeText(ctx, "Nearby places will be downloaded once an internet connection becomes available.", Toast.LENGTH_SHORT).show();
            }
       
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
