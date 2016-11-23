package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class RecipeDetailAsync extends AsyncTask<String, String, RecipeDetailAsync.ReturnObject>{
    //Variables.
    Activity activity;
    Context ctx;
    ProgressDialog progress;

    public RecipeDetailAsync(Activity activity, Context ctx) {
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
    protected ReturnObject doInBackground(String... params) {
        String recipeId = params[0];
        String title = params[1];
        String readyInTime = params[2];
        try {
            //Publish progress so onProgressUpdate method starts - shows dialog.
            publishProgress();

        //Getting recipe instructions.
            //Set URL for recipe instructions API endpoint.
            URL recipeInstructionsUrl = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/+" + recipeId + "/analyzedInstructions");

            //Starts HttpConnection for instructions URL and adds headers including the api key and accepted format.
            HttpConnection getRecipeInstructions = new HttpConnection(recipeInstructionsUrl);
            getRecipeInstructions.addHeader("X-Mashape-Key", "1q0nIfwEzpmshsRYUrOjcTOg1u07p13tuq7jsn0HSdAdoMC7p2");
            getRecipeInstructions.addHeader("Accept", "application/json");

            //Get downloaded recipe instructions, loop through each and add them to an arraylist.
            JSONArray recipeInstructions = new JSONArray(getRecipeInstructions.getTextData()).getJSONObject(0).getJSONArray("steps");
            ArrayList<String> instructions = new ArrayList<String>();
            for (int i = 0; i < recipeInstructions.length(); i++) {
                instructions.add(recipeInstructions.getJSONObject(i).getString("step"));
            }

        //Getting recipe ingredients.
            //Set URL for recipe ingredients API endpoint.
            URL recipeIngredientsUrl = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/" + recipeId + "/information");

            //Starts HttpConnection for ingredients URL and adds headers including the api key and accepted format.
            HttpConnection getRecipeIngredients = new HttpConnection(recipeIngredientsUrl);
            getRecipeIngredients.addHeader("X-Mashape-Key", "1q0nIfwEzpmshsRYUrOjcTOg1u07p13tuq7jsn0HSdAdoMC7p2");
            getRecipeIngredients.addHeader("Accept", "application/json");

            //Get downloaded recipe ingredients, loop through each and add them to an arraylist, using the RecipeIngredientsConstructor.
            JSONArray recipeIngredients = new JSONObject(getRecipeIngredients.getTextData()).getJSONArray("extendedIngredients");
            ArrayList<RecipeIngredientsConstructor> ingredients = new ArrayList<RecipeIngredientsConstructor>();
            for (int i = 0; i < recipeIngredients.length(); i++) {
                ingredients.add(new RecipeIngredientsConstructor(
                        recipeIngredients.getJSONObject(i).getString("name"),
                        recipeIngredients.getJSONObject(i).getString("amount"),
                        recipeIngredients.getJSONObject(i).getString("unitShort")
                ));
            }

        //Getting recipe image.
            //Set URL for recipe image API endpoint.
            URL recipeImageUrl = new URL("https://spoonacular.com/recipeImages/" + recipeId + "-556x370.jpg");

            //Starts HttpConnection for image URL and adds headers including the api key and accepted format.
            HttpConnection getImageHttpCon = new HttpConnection(recipeImageUrl);
            /*Get and save image as temp. This could be saved permanently now but as it's not guaranteed the recipe will load fully,
              it's best to temporarily save it, and once the recipe has loaded in RecipeActivity, save it permanently along with
              the rest of the data.*/
            Storage saveImg = new Storage();
            saveImg.saveTempImg(ctx, recipeId + "_full_size_temp", getImageHttpCon.getImageData());

            //Use inner class ReturnObject to costruct and object containing all recipe information to return to onPostExecute.
            ReturnObject returnObject = new ReturnObject(recipeId, title, readyInTime, instructions, ingredients);
            return returnObject;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        //Set progress dialog message and show dialog.
        progress.setMessage("Loading selected recipe...");
        progress.show();
    }

    @Override
    protected void onPostExecute(ReturnObject result) {
        //Stop progress dialog, remove visibility.
        progress.dismiss();
        //If result is null, an error occurred. If not, use data.
        if (result == null) {
            Toast.makeText(ctx, "Error occurred. Unable to get recipe details.", Toast.LENGTH_LONG).show();
        } else {
            try {
                //Start an intent for RecipeActivity, adding extras containing all details about that recipe.
                Intent intent = new Intent(ctx, RecipeActivity.class);
                intent.putExtra("recipeId", result.recipeId);
                intent.putExtra("title", result.title);
                intent.putExtra("readyIn", result.readyInTime);
                intent.putStringArrayListExtra("steps", result.recipeInstructions);
                intent.putParcelableArrayListExtra("ingredients", result.recipeIngredients);
                ctx.startActivity(intent);
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    //Constructor object for returning values from doInBackground to onPostExecute.
    class ReturnObject {
        public String recipeId;
        public String title;
        public String readyInTime;
        public ArrayList<String> recipeInstructions;
        public ArrayList<RecipeIngredientsConstructor> recipeIngredients;

        public ReturnObject(String recipeId, String title, String readyInTime, ArrayList<String> recipeInstructions, ArrayList<RecipeIngredientsConstructor> recipeIngredients) {
            this.recipeId = recipeId;
            this.title = title;
            this.readyInTime = readyInTime;
            this.recipeInstructions = recipeInstructions;
            this.recipeIngredients = recipeIngredients;
        }
    }
}
