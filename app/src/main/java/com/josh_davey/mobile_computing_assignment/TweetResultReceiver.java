package com.josh_davey.mobile_computing_assignment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.twitter.sdk.android.tweetcomposer.TweetUploadService;

public class TweetResultReceiver extends BroadcastReceiver {

    public TweetResultReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TweetUploadService.UPLOAD_SUCCESS.equals(intent.getAction())) {
            // success
            Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show();
        } else {
            // failure
            Toast.makeText(context, "Not posted", Toast.LENGTH_SHORT).show();
        }
    }
}
