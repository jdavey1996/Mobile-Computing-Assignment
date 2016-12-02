package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/*References:
    http://stackoverflow.com/questions/19167954/use-uri-builder-in-android-or-create-url-with-variables*/
public class RecipesAsync extends AsyncTask<String, String, ArrayList<RecipeConstructor>> {
    //Variables.
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
        //Create progress dialog.
        progress = new ProgressDialog(ctx, R.style.ProgressDialogTheme);
        //Ensure progress dialog cannot be cancelled except via the button.
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        //Creates cancel button to cancel async and dismiss progress dialog.
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
                    //Publish progress so onProgressUpdate method starts - shows dialog.
                    publishProgress();

                //Getting recipes based on search query.
                    //Construct URI for api endpoint.
                    Uri uri = Uri.parse("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/search")
                            .buildUpon()
                            .appendQueryParameter("query", query.trim())
                            .appendQueryParameter("type", type)
                            .appendQueryParameter("number", amount)
                            .build();
                    //Convert URI to URL.
                    URL recipeSearchUrl = new URL(uri.toString());

                    //Starts HttpConnection for URL and adds headers including the api key and accepted format.
                    HttpConnection getRecipeList = new HttpConnection(recipeSearchUrl);
                    getRecipeList.addHeader("X-Mashape-Key", "1q0nIfwEzpmshsRYUrOjcTOg1u07p13tuq7jsn0HSdAdoMC7p2");
                    getRecipeList.addHeader("Accept", "application/json");

                    //Get results from HttpConnection and store in a JSONArray.
                    JSONArray recipeInfo = new JSONObject(getRecipeList.getTextData()).getJSONArray("results");

                    //Add downloaded data to RecipeConstructor objects and add to an array list.
                    ArrayList<RecipeConstructor> data = new ArrayList<RecipeConstructor>();
                    for (int i = 0; i < recipeInfo.length(); i++) {
                        RecipeConstructor temp = new RecipeConstructor(
                                recipeInfo.getJSONObject(i).getString("id"),
                                recipeInfo.getJSONObject(i).getString("title"),
                                recipeInfo.getJSONObject(i).getString("readyInMinutes"));
                        data.add(temp);
                    }

                //Getting images for each recipe.
                    //Loop through all recipe ids, get the thumbnail for each, and save in a temporary file.
                    for (int i = 0; i < recipeInfo.length(); i++) {
                        //Get recipe id.
                        String recipeId = recipeInfo.getJSONObject(i).getString("id");
                        //Set recipe thumbnail image URL.
                        URL recipeImageUrl = new URL("https://spoonacular.com/recipeImages/" + recipeId + "-90x90.jpg");
                        //Get image and save.
                        HttpConnection getImageHttpCon = new HttpConnection(recipeImageUrl);
                        Storage saveImg = new Storage();
                        saveImg.saveImg(ctx, recipeId+"_thumbnail_temp", getImageHttpCon.getImageData(),true);
                    }

                    //Return downloaded data to onPostExecute.
                    return data;
                } catch (Exception e) {
                    return null;
                }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        //Set progress dialog message and show dialog.
        progress.setMessage("Loading recipe list...");
        progress.show();
    }

    @Override
    protected void onPostExecute(ArrayList<RecipeConstructor> result) {
        //Stop progress dialog, remove visibility.
        progress.dismiss();
        //If result is null, an error occurred. If not, use data.
        if (result == null) {
            Toast.makeText(ctx, "Error occurred. Unable to get recipes", Toast.LENGTH_LONG).show();
        } else {
            //Check if result contains any data, if not, no recipes match search criteria.
            if (result.size() > 0) {
                try {
                    //Start an intent for SearchResultsActivity, adding extra containing a parcelable ArrayList including all downloaded data.
                    Intent loadRecipesActivity = new Intent(ctx, SearchResultsActivity.class);
                    loadRecipesActivity.putParcelableArrayListExtra("recipes", result);
                    ctx.startActivity(loadRecipesActivity);
                } catch (Exception e) {
                }
            } else {
                Toast.makeText(ctx, "No recipes match your search criteria.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
