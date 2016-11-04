package com.josh_davey.mobile_computing_assignment;

import android.os.Environment;

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
}
