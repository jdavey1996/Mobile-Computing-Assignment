package com.josh_davey.mobile_computing_assignment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.MediaService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.Card;


import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class TweetActivity extends Activity {

    TwitterAuthClient mTwitterAuthClient;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
    }

    public void startTwitter(View view)
    {
        mTwitterAuthClient = new TwitterAuthClient();
        Tweets twitter = new Tweets(this,this,mTwitterAuthClient);
        twitter.checkActiveSession();
    }

    //Clears session, logs user out and removes all cookies - removes traces of user login left.
    public void logoutTwitter(View view)
    {
        TwitterCore.getInstance().getSessionManager().clearActiveSession();
        TwitterCore.getInstance().logOut();
        //http://stackoverflow.com/questions/29743676/how-to-logout-from-twitter-using-fabric-sdk-for-android
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        //http://stackoverflow.com/questions/30455513/twitter-sdk-requestcode
        if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            mTwitterAuthClient.onActivityResult(requestCode, responseCode, intent);
        }
        else if(requestCode == 1 && responseCode == RESULT_OK) {
            Toast.makeText(TweetActivity.this, "saved image", Toast.LENGTH_SHORT).show();

            //startTwitter(null);

        }
    }

    //Take photo intent.
    public void dispatchTakePictureIntent(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                String directory = "appimages";
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + directory;

                File outputDir = new File(path);
                outputDir.mkdirs();

                File newFile = new File(path + "/" + "latestImgToTweet.png");
                uri = Uri.fromFile(newFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, 1);
            }
            else
            {
                Toast.makeText(TweetActivity.this, "Unable to take photo", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent(null);
                }
                else
                {
                    //If permission is not granted, error message is displayed.
                    Toast.makeText(this, "Permissions disabled.", Toast.LENGTH_SHORT).show();;
                }
        }
    }

    public void tweetStatusTest(View view) {
        // final TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

       /* File photo = new File(uri.getPath());

        RequestBody file = RequestBody.create(MediaType.parse(photo.toString()), "application/octet-stream");


        MediaService ms = twitterApiClient.getMediaService();*/

//https://docs.fabric.io/android/twitter/access-rest-api.html
//http://stackoverflow.com/questions/31785698/android-adding-image-to-tweet-using-fabric-twitter-rest-api-and-retrofit
        EditText text = (EditText)findViewById(R.id.tweetTxt);

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<Tweet> call = statusesService.update(text.getText().toString(), null, false, null, null, null, false, false, null);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                //Do something with result
                Toast.makeText(TweetActivity.this, "Tweeted", Toast.LENGTH_SHORT).show();
            }

            public void failure(TwitterException exception) {
                //Do something on failure
                Toast.makeText(TweetActivity.this, "Not Tweeted", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
