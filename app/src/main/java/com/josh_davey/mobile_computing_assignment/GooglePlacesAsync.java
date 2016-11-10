package com.josh_davey.mobile_computing_assignment;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

//https://developers.google.com/places/web-service/search
//https://developers.google.com/places/web-service/supported_types
public class GooglePlacesAsync extends AsyncTask<Double,String,JSONArray>
{
    //Variables.
    Context ctx;
    Activity activity;
    GoogleMap googleMap;

    public GooglePlacesAsync(Context ctx, Activity activity, GoogleMap googleMap) {
        this.ctx = ctx;
        this.activity = activity;
        this.googleMap = googleMap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(ctx, "Location acquired, attempting to find nearby supermarkets.", Toast.LENGTH_SHORT).show();
        //Enables visibility for a progressbar and accompanying text http://www.materialdoc.com/linear-progress/
        ProgressBar locationMarkersProgress = (ProgressBar)activity.findViewById(R.id.placesProgress);
        locationMarkersProgress.setVisibility(View.VISIBLE);
    }


    @Override
    protected JSONArray doInBackground(Double... params) {
        Double latitude = params[0];
        Double longitude = params[1];
        try {
            HttpConnection httpConnection = new HttpConnection();
            //Thread.sleep(10000);
            //http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
            Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
                    .buildUpon()
                    .appendQueryParameter("location", latitude+","+longitude)
                    .appendQueryParameter("radius", "4000")
                    .appendQueryParameter("type", "grocery_or_supermarket")
                    .appendQueryParameter("key", "AIzaSyDS9PUfBF9KJnAxcIOE42oUEAGJZEgdti0").build();

            URL url = new URL(uri.toString());

            JSONArray data = new JSONObject(httpConnection.httpGet(url)).getJSONArray("results");

            return data;
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }


    @Override
    protected void onPostExecute(JSONArray data) {
        //Disables visibility for a progressbar and accompanying text - data has finished loading.
        ProgressBar placesProgress = (ProgressBar)activity.findViewById(R.id.placesProgress);
        placesProgress.setVisibility(View.GONE);

        if (data == null)
        {
            Toast.makeText(ctx, "Unable to get nearest supermarkets, check your network connection.", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                if(data.length() >0) {
                    //Remove existing markers on the map.
                    googleMap.clear();
                    for (int i = 0; i < data.length(); i++) {
                        //Create new marker.
                        MarkerOptions marker = new MarkerOptions();

                        //Get place title and set marker title.
                        String title = data.getJSONObject(i).getString("name");
                        marker.title(title);

                        //Get place location and set marker positioning.
                        String latitude = data.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat");
                        String longitude = data.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng");
                        LatLng latlng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                        marker.position(latlng);

                        //Check if place has opening hours information recorded and add to marker snippet if so.
                        if (data.getJSONObject(i).has("opening_hours")) {
                            String openclosed = data.getJSONObject(i).getJSONObject("opening_hours").getString("open_now");
                            marker.snippet("Open now: " + openclosed);
                        }

                        //Add marker to the map.
                        googleMap.addMarker(marker);
                    }
                }
                else
                {
                    Toast.makeText(ctx, "No supermarkets near your location.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                //This would call if the string "name" couldn't be found in the downloaded data - would only occur if Google changed their JSON file structure.
                Toast.makeText(ctx, "Unknown Google API error. Please try again later.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

}