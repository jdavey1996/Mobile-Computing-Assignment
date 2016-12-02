package com.josh_davey.mobile_computing_assignment;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SQLiteDeleteRecipesAsync extends AsyncTask<Object, String, String> {
    //Variables.
    Context ctx;

    public SQLiteDeleteRecipesAsync(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            //Run method to clear all data from all database tables - removing saved viewed recipes.
            SQLiteDb db = new SQLiteDb(ctx);
            db.clearDatabaseTables();
            /*Run method to clear all images that are saved in a permanent storage location*/
            Storage storage = new Storage();
            storage.removeSavedImg(ctx);

            //Return success so onPostExecute knows doInBackground succeeded.
            return "success";
        }catch (Exception e)
        {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        //If result is null error occurred. Else succeeded. Toast corresponding message to user.
        if(result == null)
        {
            Toast.makeText(ctx, "Error occurred whilst clearing recently viewed recipes...", Toast.LENGTH_SHORT).show();
        }
        else if (result == "success")
        {
            Toast.makeText(ctx, "Recently viewed recipes deleted.", Toast.LENGTH_SHORT).show();
        }
    }
}
