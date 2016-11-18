package com.josh_davey.mobile_computing_assignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SQLiteSaveRecipeAsync extends AsyncTask<Object, String, String> {

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
        publishProgress();

        String recipeId = (String) params[0];
        String recipeTitle = (String)  params[1];
        String readyIn = (String)  params[2];
        ArrayList<RecipeIngredientsConstructor> ingredients = (ArrayList<RecipeIngredientsConstructor>) params[3];
        ArrayList<String> instructions = (ArrayList<String>) params[4];

        try {

            SQLiteDb sql = new SQLiteDb(ctx);
            sql.insertInto_TABLE_RECIPE_INFO(recipeId, recipeTitle, readyIn,getDateTime());
            sql.insertInto_TABLE_RECIPE_INGREDIENTS(recipeId, ingredients);
            sql.insertInto_TABLE_RECIPE_INSTRUCTIONS(recipeId, instructions);

            //Get temporary saved images and save in permanent location. - NO need to encrpyt images as files on internal storage are private to this application.
            Storage getImage = new Storage();
            Bitmap fullsizetemp = getImage.getTepImg(ctx,recipeId+"_full_size_temp");
            getImage.saveTempImg(ctx,recipeId+"_full_size",fullsizetemp);

            Bitmap thumbnailtemp = getImage.getTepImg(ctx,recipeId+"_thumbnail_temp");
            getImage.saveTempImg(ctx,recipeId+"_thumbnail",thumbnailtemp);

            return "success";
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    protected void onPostExecute(String result) {
        if(result == null)
        {
            Toast.makeText(ctx, "Error occurred whilst caching this recipe..", Toast.LENGTH_SHORT).show();
        }
        else if (result == "success")
        {
            Toast.makeText(ctx, "recipe cached.", Toast.LENGTH_SHORT).show();
        }
    }
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
