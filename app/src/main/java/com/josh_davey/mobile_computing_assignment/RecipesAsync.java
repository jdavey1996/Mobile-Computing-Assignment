package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import retrofit2.http.Url;

//https://developer.android.com/reference/android/os/AsyncTask.html
public class RecipesAsync extends AsyncTask<String, String, ArrayList<RecipeConstructor>> {
    Activity activity;
    Context ctx;
    ProgressDialog progress;

    public RecipesAsync(Activity activity, Context ctx) {
        this.ctx = ctx;
        this.activity = activity;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(ctx, R.style.ProgressDialogTheme);
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
    protected ArrayList<RecipeConstructor> doInBackground(String... params) {
        String query = params[0];
        String type = params[1];
        String amount = params[2];

                try {
                    publishProgress();

                    //Get list of recipes based on search query, in JSON format.
                    //http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables
                    Uri uri = Uri.parse("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/search")
                            .buildUpon()
                            .appendQueryParameter("query", query)
                            .appendQueryParameter("type", type)
                            .appendQueryParameter("number", amount)
                            .build();

                    URL recipeSearchUrl = new URL(uri.toString());

                    HttpConnection getRecipeList = new HttpConnection(recipeSearchUrl);
                    getRecipeList.addHeader("X-Mashape-Key", "1q0nIfwEzpmshsRYUrOjcTOg1u07p13tuq7jsn0HSdAdoMC7p2");
                    getRecipeList.addHeader("Accept", "application/json");

                    JSONArray recipeInfo = new JSONObject(getRecipeList.getTextData()).getJSONArray("results");

                    //Add downloaded data to RecipeConstructor objects and add to an array list to return.
                    ArrayList<RecipeConstructor> data = new ArrayList<RecipeConstructor>();
                    for (int i = 0; i < recipeInfo.length(); i++) {
                        RecipeConstructor temp = new RecipeConstructor(
                                recipeInfo.getJSONObject(i).getString("id"),
                                recipeInfo.getJSONObject(i).getString("title"),
                                recipeInfo.getJSONObject(i).getString("readyInMinutes"));
                        data.add(temp);
                        Log.i("ids",recipeInfo.getJSONObject(i).getString("id"));
                    }

                    //Download thumbnail images for each recipe and save in a temporary file (cache) on the device
                    Storage saveImg = new Storage();
                    for (int i = 0; i < recipeInfo.length(); i++) {
                        String recipeId = recipeInfo.getJSONObject(i).getString("id");
                        //Check if image is already saved in cache. Only download if it isn't.
                        if(saveImg.checkImgCached(ctx,recipeId+"_thumbnail"))
                        {
                            //Image already saved, no need to download - remove this if statement later, have if not statement only.
                        }
                        else
                        {
                            URL recipeImageUrl = new URL("https://spoonacular.com/recipeImages/" + recipeId + "-90x90.jpg");
                            HttpConnection getImageHttpCon = new HttpConnection(recipeImageUrl);
                            //Cache image.
                            saveImg.saveTempImg(ctx, recipeId+"_thumbnail", getImageHttpCon.getImageData());
                        }

                    }
                    return data;
                } catch (Exception e) {
                    return null;
                }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progress.setMessage("Loading recipe list...");
        progress.show();
    }

    @Override
    protected void onPostExecute(ArrayList<RecipeConstructor> result) {
        progress.dismiss();
        if (result == null) {
            Toast.makeText(ctx, "Error occurred. Unable to get recipes", Toast.LENGTH_LONG).show();
        } else {
            if (result.size() > 0) {
                try {
                    //Load search results activity and return the parcelable array containing the downloaded recipes.
                    Intent loadRecipesActivity = new Intent(ctx, SearchResultsActivity.class);
                    loadRecipesActivity.putParcelableArrayListExtra("recipes", result);
                    ctx.startActivity(loadRecipesActivity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(ctx, "No recipes match your search criteria.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(ctx, "cancelled", Toast.LENGTH_SHORT).show();
    }
}
