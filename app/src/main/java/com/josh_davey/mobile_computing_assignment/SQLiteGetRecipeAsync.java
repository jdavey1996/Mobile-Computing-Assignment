package com.josh_davey.mobile_computing_assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class SQLiteGetRecipeAsync extends AsyncTask<String, String, SQLiteGetRecipeAsync.ReturnObject> {

    Context ctx;
    ProgressDialog progress;
    public SQLiteGetRecipeAsync(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = new ProgressDialog(ctx, R.style.ProgressDialogTheme);
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
    }

    @Override
    protected SQLiteGetRecipeAsync.ReturnObject doInBackground(String... params) {
        publishProgress();

        String recipeId = params[0];
        String recipeTitle = params[1];
        String readyIn = params[2];
        try {
            SQLiteDb sql = new SQLiteDb(ctx);

            //Get ingredients from sqlite database and add to an arraylist.
            Cursor c1 = sql.getTABLE_RECIPE_INGREDIENTS(recipeId);
            ArrayList<RecipeIngredientsConstructor> ingredients = new ArrayList<RecipeIngredientsConstructor>();
            while (c1.moveToNext()) {

                ingredients.add(new RecipeIngredientsConstructor(c1.getString(2), c1.getString(3), c1.getString(4)));
            }
            c1.close();

            //Get instructions from sqlite database and add to an arraylist.
            Cursor c2 = sql.getTABLE_RECIPE_INSTRUCTIONS(recipeId);
            ArrayList<String> instructions = new ArrayList<String>();
            while (c2.moveToNext()) {
                instructions.add(c2.getString(2));
            }
            c2.close();

            //Close databse read connection.
            sql.closeDbrCon();

            //Create object will all data to return to onPostExecute method.
            ReturnObject returnObject = new ReturnObject(recipeId,recipeTitle,readyIn,ingredients,instructions);
            return  returnObject;
        }catch (Exception e)
        {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progress.setMessage("Loading...");
        progress.show();
    }

    @Override
    protected void onPostExecute(SQLiteGetRecipeAsync.ReturnObject data) {
        progress.dismiss();
        if (data == null ||data.ingredients == null || data.instructions == null)
        {
            Toast.makeText(ctx, "Database error occurred, please try again later.", Toast.LENGTH_SHORT).show();
        }
        else
        {

            Intent intent = new Intent(ctx, RecipeActivity.class);
            intent.putExtra("loadedFromCache",true);
            intent.putExtra("recipeId", data.recipeId);
            intent.putExtra("title", data.recipeTitle);
            intent.putExtra("readyIn", data.readyIn);
            intent.putStringArrayListExtra("steps", data.instructions);
            intent.putParcelableArrayListExtra("ingredients",data.ingredients);
            ctx.startActivity(intent);
        }
    }

    class ReturnObject
    {
        String recipeId;
        String recipeTitle;
        String readyIn;
        ArrayList<RecipeIngredientsConstructor> ingredients= new ArrayList<RecipeIngredientsConstructor>();
        ArrayList<String> instructions= new ArrayList<String>();

        public ReturnObject(String recipeId, String recipeTitle, String readyIn, ArrayList<RecipeIngredientsConstructor> ingredients, ArrayList<String>instructions)
        {
            this.recipeId = recipeId;
            this.recipeTitle = recipeTitle;
            this.readyIn = readyIn;
            this.ingredients = ingredients;
            this.instructions = instructions;
        }
    }
}
