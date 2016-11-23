package com.josh_davey.mobile_computing_assignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*References:
    http://tips.androidhive.info/2013/10/android-insert-datetime-value-in-sqlite-database/*/
public class SQLiteSaveRecipeAsync extends AsyncTask<Object, String, String> {
    //Variables.
    Context ctx;

    public SQLiteSaveRecipeAsync(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Object... params) {
        String recipeId = (String) params[0];
        String recipeTitle = (String)  params[1];
        String readyIn = (String)  params[2];
        ArrayList<RecipeIngredientsConstructor> ingredients = (ArrayList<RecipeIngredientsConstructor>) params[3];
        ArrayList<String> instructions = (ArrayList<String>) params[4];

        try {
            /*Get temporary saved images and save in permanent location. -
              No need to encrpyt images as files on internal storage are private to this application.*/
            Storage getImage = new Storage();
            Bitmap fullsizetemp = getImage.getTepImg(ctx,recipeId+"_full_size_temp");
            getImage.saveTempImg(ctx,recipeId+"_full_size",fullsizetemp);

            Bitmap thumbnailtemp = getImage.getTepImg(ctx,recipeId+"_thumbnail_temp");
            getImage.saveTempImg(ctx,recipeId+"_thumbnail",thumbnailtemp);

            SQLiteDb sql = new SQLiteDb(ctx);

            //Insert data into database tables.
            sql.insertInto_TABLE_RECIPE_INFO(recipeId, recipeTitle, readyIn,getDateTime());
            sql.insertInto_TABLE_RECIPE_INGREDIENTS(recipeId, ingredients);
            sql.insertInto_TABLE_RECIPE_INSTRUCTIONS(recipeId, instructions);

            return "success";
        }catch (Exception e)
        {
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        //Toast an error if unable to save recipe.
        if(result == null)
        {
            Toast.makeText(ctx, "Unfortunately this recipe could not be cached.", Toast.LENGTH_SHORT).show();
        }
    }

    //Method to get current data and time.
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
