package com.josh_davey.mobile_computing_assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    ProgressDialog progress;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(ctx,R.style.ProgressDialogTheme);
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);

        progress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismisses dialog
                dialog.dismiss();
                //Cancels async task
                cancel(true);
            }
        });
    }
    @Override
    protected JSONArray doInBackground(String... params) {
        //String filter = params[0];
        //String search = params[1];

        try {
            publishProgress();

            //http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
            Uri uri = Uri.parse("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/findByIngredients?fillIngredients=false")
                    .buildUpon()
                    .appendQueryParameter("ingredients", "apples,flour,sugar")
                    .appendQueryParameter("limitLicense", "false")
                    .appendQueryParameter("number", "20")
                    .appendQueryParameter("ranking", "1")
                    .build();

            URL url = new URL(uri.toString());

            HttpConnection httpConnection = new HttpConnection(url);
            httpConnection.addHeader("X-Mashape-Key", "1q0nIfwEzpmshsRYUrOjcTOg1u07p13tuq7jsn0HSdAdoMC7p2");
            httpConnection.addHeader("Accept", "application/json");


           JSONArray data = new JSONArray(httpConnection.httpGet());

            return data;
        }catch (Exception e)
        {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
            progress.setMessage("test");
            progress.show();

    }

    @Override
    protected void onPostExecute(JSONArray result) {
        progress.dismiss();
        if(result == null)
        {
            Toast.makeText(ctx,"Error occurred. Unable to get recipes",Toast.LENGTH_LONG).show();
        }
        else {
            try {
                for (int i = 0; i < result.length(); i++) {
                    Log.i("Downloaded data", result.getJSONObject(i).getString("title"));
                }
                // Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        Toast.makeText(ctx, "cancelled", Toast.LENGTH_SHORT).show();
    }
}
