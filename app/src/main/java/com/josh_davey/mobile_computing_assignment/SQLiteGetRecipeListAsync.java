package com.josh_davey.mobile_computing_assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class SQLiteGetRecipeListAsync extends AsyncTask<String, String, ArrayList<RecipeConstructor>> {

    Context ctx;
    ProgressDialog progress;
    public SQLiteGetRecipeListAsync(Context ctx) {
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
    protected ArrayList<RecipeConstructor> doInBackground(String... params) {
        publishProgress();
        SQLiteDb db = new SQLiteDb(ctx);
        ArrayList<RecipeConstructor> recipes = new ArrayList<RecipeConstructor>();

        Cursor cursor = db.getTABLE_RECIPE_INFO();
        try {
            while (cursor.moveToNext()) {
                recipes.add(new RecipeConstructor(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
            }
            cursor.close();
            db.closeDbrCon();

            return recipes;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progress.setMessage("Loading...");
        progress.show();
    }

    @Override
    protected void onPostExecute(ArrayList<RecipeConstructor> data) {
        progress.dismiss();

        if (data == null) {
            Toast.makeText(ctx, "Database error occurred, please try again later.", Toast.LENGTH_SHORT).show();
        } else {
            if (data.size() != 0) {
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
