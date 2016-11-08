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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

//https://developers.google.com/places/web-service/search
//https://developers.google.com/places/web-service/supported_types
public class GooglePlacesAsync extends AsyncTask<String,String,JSONArray>
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
        Toast.makeText(ctx, "GPA startd", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected JSONArray doInBackground(String... params) {
        String latitude = "53.230688";
        String longitude = "-0.540579";
        try {
            HttpConnection httpConnection = new HttpConnection();
            Thread.sleep(10000);
            //http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
            Uri uri = Uri.parse("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
                    .buildUpon()
                    .appendQueryParameter("location", latitude+","+longitude)
                    .appendQueryParameter("radius", "2000") //~1.2 miles
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
        ProgressBar locationMarkersProgress = (ProgressBar)activity.findViewById(R.id.locationMarkersProgress);
        TextView locationMarkersProgressTxt = (TextView)activity.findViewById(R.id.locationMarkersProgressTxt);


        locationMarkersProgressTxt.setVisibility(View.GONE);
        locationMarkersProgress.setVisibility(View.GONE);


        if (data == null)
        {
            Toast.makeText(ctx, "Unable to get nearest supermarkets, check your network connection.", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                if(data.length() >0) {
                    for (int i = 0; i < data.length(); i++) {
                        String title = data.getJSONObject(i).getString("name");
                        String latitude = data.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat");
                        String longitude = data.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng");
                        String openclosed = data.getJSONObject(i).getJSONObject("opening_hours").getString("open_now");

                        LatLng latlng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                        googleMap.addMarker(new MarkerOptions()
                                .position(latlng)
                                .title(title)
                                .snippet("Open now: "+openclosed));

                        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(lincoln));
                    }
                }
                else
                {
                    Toast.makeText(ctx, "No supermarkets near your location.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                //This would call if the string "name" couldn't be found in the downloaded data - would only occur if Google changed their JSON file structure.
                Toast.makeText(ctx, "Unknown Google API error. Please try again later.", Toast.LENGTH_SHORT).show();
            }

        }
    }

}