package com.josh_davey.mobile_computing_assignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//https://developer.android.com/training/basics/data-storage/files.html
//http://stackoverflow.com/questions/3394765/how-to-check-available-space-on-android-device-on-sd-card

public class Storage {
    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }


//http://stackoverflow.com/questions/7540386/android-saving-and-loading-a-bitmap-in-cache-from-diferent-activities
//https://developer.android.com/training/basics/data-storage/files.html

    //Save temporary bitmap image.
    public void saveTempImg(Context ctx, String name, Bitmap image) {
        File cacheDir = ctx.getCacheDir();
        File f = new File(cacheDir, name);
        try {
            FileOutputStream out = new FileOutputStream(f);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            Log.i("saved",f.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Get temporary image by name.
    public Bitmap getTepImg(Context ctx, String name)
    {
        File cacheDir = ctx.getCacheDir();
        File f = new File(cacheDir, name);
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(f);
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
/*
    public Boolean checkImgCached(Context ctx, String name)
    {
        File cacheDir = ctx.getCacheDir();
        File f = new File(cacheDir, name);
        if(f.exists())
        {
            return true;
        }
        else
        {
            return false;
        }
    }*/

    public Boolean clearImgCache(Context ctx, Boolean temp)
    {
        String contains1, contains2;
        if(temp)
        {
            contains1 = "full_size_temp";
            contains2= "thumbnail_temp";
        }
        else
        {
            contains1 = "full_size";
            contains2= "thumbnail";
        }
        //http://stackoverflow.com/questions/26986637/can-android-clear-files-from-the-cache-directory-of-my-application-while-it-is-r
        try {
            File[] directory = ctx.getCacheDir().listFiles();
            if (directory != null) {
                for (File file : directory) {
                    if(file.getName().contains(contains1)|| file.getName().contains(contains2)) {
                        file.delete();
                        Log.i("deleted", file.getName());
                    }
                }
            }
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }
}
