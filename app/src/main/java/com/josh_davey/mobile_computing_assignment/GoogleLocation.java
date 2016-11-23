package com.josh_davey.mobile_computing_assignment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/*References:
    https://www.toptal.com/android/android-developers-guide-to-google-location-services-api*/
public class GoogleLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    //Variables.
    Context ctx;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Activity activity;
    GoogleMap googleMap;
    GooglePlacesAsync googlePlacesAsync;
    Boolean requestingLocation= false; //Used to let calling fragment know whether location updates are still being requested.

    public GoogleLocation(Context ctx, Activity activity, GoogleMap googleMap)
    {
        this.ctx = ctx;
        this.activity = activity;
        this.googleMap = googleMap;
    }

    public void startLocation()
    {
        //Checks if Google play services is available.
        GooglePlayServices googlePlayServices = new GooglePlayServices(ctx,activity);
        if(googlePlayServices.checkAvailable()) {
            //Runs buildGoogleApiClient method. If built successfully and returned true, connect to it.
            if (buildGoogleApiClient()) {
                googleApiClient.connect();
            }
        }
    }

    public boolean buildGoogleApiClient() {
        try {
            //Build google API client for location services. Added connectionCallbacks and onConnectionFailedListener, implemented later.
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

    @Override
    public void onConnected(Bundle bundle) {
        //Call followng methods once connected to GoogleApiClient for Location services.
        createLocationRequest();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast if unable to connect to GoogleApiClient for Location services.
        Toast.makeText(ctx, "Connection to Google Location failed, please try again later.", Toast.LENGTH_SHORT).show();
    }

    //Create location request, This should't matter too much as this app only requests location once then stops listening.
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates()
    {
        //If permissions are accepted
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Request location updates using the location request previously created.
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            //Sets boolean variable to true, location updates are being listened for.
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
            //Remove location updates.
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            //Disconnect from GoogleApiClient location services.
            googleApiClient.disconnect();

            //Sets boolean value to false, location is no longer being requested.
            requestingLocation = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Check permissions.
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
        }
    }
}
