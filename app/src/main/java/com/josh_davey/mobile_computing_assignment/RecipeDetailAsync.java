package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

//https://developer.android.com/reference/android/os/AsyncTask.html
public class RecipeDetailAsync extends AsyncTask<String, String, RecipeDetailAsync.ReturnObject>{
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
    protected ReturnObject doInBackground(String... params) {
        String recipeId = params[0];
        String title = params[1];
        String readyInTime = params[2];
        try {
            publishProgress();
            //Get recipe instructions.
            URL recipeInstructionsUrl = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/+" + recipeId + "/analyzedInstructions");

            HttpConnection getRecipeInstructions = new HttpConnection(recipeInstructionsUrl);
            getRecipeInstructions.addHeader("X-Mashape-Key", "1q0nIfwEzpmshsRYUrOjcTOg1u07p13tuq7jsn0HSdAdoMC7p2");
            getRecipeInstructions.addHeader("Accept", "application/json");

            JSONArray recipeInstructions = new JSONArray(getRecipeInstructions.getTextData()).getJSONObject(0).getJSONArray("steps");
            ArrayList<String> instructions = new ArrayList<String>();
            for (int i = 0; i < recipeInstructions.length(); i++) {
                instructions.add(recipeInstructions.getJSONObject(i).getString("step"));
                Log.i("instruct", instructions.get(i));
            }

            //Get recipe ingredients.
            URL recipeIngredientsUrl = new URL("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/" + recipeId + "/information");
            HttpConnection getRecipeIngredients = new HttpConnection(recipeIngredientsUrl);
            getRecipeIngredients.addHeader("X-Mashape-Key", "1q0nIfwEzpmshsRYUrOjcTOg1u07p13tuq7jsn0HSdAdoMC7p2");
            getRecipeIngredients.addHeader("Accept", "application/json");

            JSONArray recipeIngredients = new JSONObject(getRecipeIngredients.getTextData()).getJSONArray("extendedIngredients");
            ArrayList<RecipeIngredientsConstructor> ingredients = new ArrayList<RecipeIngredientsConstructor>();
            for (int i = 0; i < recipeIngredients.length(); i++) {

                ingredients.add(new RecipeIngredientsConstructor(
                        recipeIngredients.getJSONObject(i).getString("name"),
                        recipeIngredients.getJSONObject(i).getString("amount"),
                        recipeIngredients.getJSONObject(i).getString("unitShort")
                ));
            }

            //Download thumbnail images for selected recipe and save in a temporary file (cache) on the device
            Storage saveImg = new Storage();

            URL recipeImageUrl = new URL("https://spoonacular.com/recipeImages/" + recipeId + "-556x370.jpg");
            HttpConnection getImageHttpCon = new HttpConnection(recipeImageUrl);
            //Cache image.
            saveImg.saveTempImg(ctx, recipeId + "_full_size_temp", getImageHttpCon.getImageData());


            ReturnObject returnObject = new ReturnObject(recipeId, title, readyInTime, instructions, ingredients);
            return returnObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    @Override
    protected void onProgressUpdate(String... values) {
        progress.setMessage("Loading selected recipe...");
        progress.show();

    }

    @Override
    protected void onPostExecute(ReturnObject result) {
        progress.dismiss();
        if (result == null) {
            Toast.makeText(ctx, "Error occurred. Unable to get recipe details.", Toast.LENGTH_LONG).show();
        } else {
            try {
                Intent intent = new Intent(ctx, RecipeActivity.class);
                intent.putExtra("recipeId", result.recipeId);
                intent.putExtra("title", result.title);
                intent.putExtra("readyIn", result.readyInTime);
                intent.putStringArrayListExtra("steps", result.recipeInstructions);
                intent.putParcelableArrayListExtra("ingredients", result.recipeIngredients);
                ctx.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
        Toast.makeText(ctx, "cancelled", Toast.LENGTH_SHORT).show();
    }

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
