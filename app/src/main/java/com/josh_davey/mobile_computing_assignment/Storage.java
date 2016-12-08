package com.josh_davey.mobile_computing_assignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/*References:
    https://developer.android.com/training/basics/data-storage/files.html
    http://stackoverflow.com/questions/26986637/can-android-clear-files-from-the-cache-directory-of-my-application-while-it-is-r*/

public class Storage {
    // Checks if external storage is available for read and write.
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    //Save bitmap image to internal storage. This either saves as temporary in the internal cache directory or a permanent location based on a boolean parameter.
    public void saveImg(Context ctx, String name, Bitmap image, Boolean temp) {
        //Directory location.
        File dir;
        if(temp) {
            //Set directory as cache directory - for temporary images.
             dir= ctx.getCacheDir();

        }
        else
        {
            //Set directory as permanent directory - for saved recipe images.
            dir = ctx.getDir("saved_recipe_images",ctx.MODE_PRIVATE);
        }
        //Create file to write to.
        File f = new File(dir, name);
        //Write to file.
        try {
            FileOutputStream out = new FileOutputStream(f);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Get bitmap image from internal storage. This either gets it from the internal cache directory or a permanement location based on a boolean parameter.
    public Bitmap getImg(Context ctx, String name, Boolean temp)
    {
        //Directory location.
        File dir;
        if(temp) {
            //Specifies cache directory for getting temporary images.
            dir= ctx.getCacheDir();

        }
        else
        {
            //Specifies permanent directory location - mode private means only accessible to this app.
            dir = ctx.getDir("saved_recipe_images",ctx.MODE_PRIVATE);
        }
        //Get file from directory.
        File f = new File(dir, name);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(f);
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            return null;
        }
    }

    /*Clear all temporary image cache. This removes all temporary images; images that are saved
     to the device so they can be loaded across activities when a search occurs.*/
    public Boolean clearImgCache(Context ctx)
    {
        try {
            File[] directory = ctx.getCacheDir().listFiles();
            if (directory != null) {
                for (File file : directory) {
                    if(file.getName().contains("thumbnail_temp")|| file.getName().contains("full_size_temp")) {
                        file.delete();
                    }
                }
            }
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }

    //Remove all saved recipe images. This would be called when a user wishes to clear their recently viewed recipes.
    public Boolean removeSavedImg(Context ctx)
    {
        try {
            File[] directory = ctx.getDir("saved_recipe_images", ctx.MODE_PRIVATE).listFiles();
            if (directory != null) {
                for (File file : directory) {
                        file.delete();
                }
            }
            return true;
        }catch (Exception e)
        {
            return null;
        }
    }
}
