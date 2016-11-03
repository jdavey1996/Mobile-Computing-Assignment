package com.josh_davey.mobile_computing_assignment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

//https://developer.android.com/reference/android/os/AsyncTask.html
public class GetData extends AsyncTask<String,String,JSONArray>{
    Context ctx;
    public GetData(Context ctx) {
        this.ctx = ctx;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected JSONArray doInBackground(String... params) {
        //String filter = params[0];
        //String search = params[1];

        try {
            HttpConnection httpConnection = new HttpConnection();

            //http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
            Uri uri = Uri.parse("https://api.themoviedb.org/3/search/movie")
                    .buildUpon()
                    .appendQueryParameter("api_key", "7b0de9fe7070866e986294d27c2960db")
                    .appendQueryParameter("language", "en-US")
                    .appendQueryParameter("query", "fight")
                    .appendQueryParameter("page", "1").build();
            Log.i("uri", uri.toString());

            URL url = new URL(uri.toString());

            JSONArray data = new JSONObject(httpConnection.httpGet(url)).getJSONArray("results");

            return data;
        }catch (Exception e)
        {
e.printStackTrace();
            Log.i("ER","eerrrr");
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(JSONArray result) {
        //super.onPostExecute(result);
        try {
            for (int i = 0; i < result.length(); i++) {
                Log.i("Downloaded data", result.getJSONObject(i).getString("original_title"));
            }
           // Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {

        }
    }


}
