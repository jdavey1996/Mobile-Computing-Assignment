package com.josh_davey.mobile_computing_assignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;

public class SQLiteDeleteRecipesAsync extends AsyncTask<Object, String, String> {

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
            SQLiteDb db = new SQLiteDb(ctx);
            //Clearing data from database.
            db.clearDatabaseTables();
            //Clear any images that are saved using a non-temporary name eg.3020Thumbnail instead of 3020_Thumbnail_Temp
            Storage storage = new Storage();
            storage.clearImgCache(ctx,false);

            return "success";
        }catch (Exception e)
        {
            return null;
        }
    }


    @Override
    protected void onPostExecute(String result) {
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
