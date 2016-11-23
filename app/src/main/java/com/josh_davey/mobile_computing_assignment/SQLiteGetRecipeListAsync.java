package com.josh_davey.mobile_computing_assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;

public class SQLiteGetRecipeListAsync extends AsyncTask<String, String, ArrayList<RecipeConstructor>> {
    //Variables.
    Context ctx;
    ProgressDialog progress;

    public SQLiteGetRecipeListAsync(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Create progress dialog.
        progress = new ProgressDialog(ctx, R.style.ProgressDialogTheme);
        //Ensure progress dialog cannot be cancelled.
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
    }

    @Override
    protected ArrayList<RecipeConstructor> doInBackground(String... params) {
        //Publish progress so onProgressUpdate method starts - shows dialog.
        publishProgress();

        //Get instance of SQLiteDb class.
        SQLiteDb db = new SQLiteDb(ctx);

        //Get recipe list from sqlite database and add to an arraylist.
        //This runs a method within the SQLiteDb class, which returns a cursor containing data. Once used, the cursor is closed.
        Cursor cursor = db.getTABLE_RECIPE_INFO();
        try {
            ArrayList<RecipeConstructor> recipes = new ArrayList<RecipeConstructor>();
            while (cursor.moveToNext()) {
                recipes.add(new RecipeConstructor(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
            }
            cursor.close();

            //Close database read connection.
            db.closeDbrCon();

            //Return recipes from SQLite database.
            return recipes;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        //Set progress dialog message and show dialog.
        progress.setMessage("Loading...");
        progress.show();
    }

    @Override
    protected void onPostExecute(ArrayList<RecipeConstructor> data) {
        //Stop progress dialog, remove visibility.
        progress.dismiss();

        //If null, and error has occurred.
        if (data == null) {
            Toast.makeText(ctx, "Database error occurred, please try again later.", Toast.LENGTH_SHORT).show();
        } else {
            //If amount of acquired recipes is 0, no recently viewed recipes exist.
            if (data.size() != 0) {
                /*Start intent to load list of recipes from database in SearchResultsActivity.
                  Put boolean extra to state it was loaded from cached data, and put parcelable arraylist containing acquired data.*/
                Intent intent = new Intent(ctx, SearchResultsActivity.class);
                intent.putExtra("loadedFromCache", true);
                intent.putParcelableArrayListExtra("recipes", data);
                ctx.startActivity(intent);
            } else {
                Toast.makeText(ctx, "Your recently viewed list is empty.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
