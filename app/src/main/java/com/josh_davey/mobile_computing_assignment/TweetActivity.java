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
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.twitter.sdk.android.core.models.Image;
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
    //Variables.
    TwitterAuthClient mTwitterAuthClient;
    Uri savedImageUri;
    EditText tweetInput;
    TextView captureImgStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);

        //Sets the activity size - makes a popup window. http://stackoverflow.com/questions/16007750/how-to-create-custom-popupwindow-from-intent-call
        getWindow().setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        tweetInput = (EditText) findViewById(R.id.tweetInput);
        //Gets instance of captureImgStatus edittext and sets it to unclickable
        captureImgStatus = (TextView) findViewById(R.id.captureImgStatus);
        captureImgStatus.setClickable(false);
    }

    public void startTwitter(View view) {
        mTwitterAuthClient = new TwitterAuthClient();
        Tweets twitter = new Tweets(this, this, mTwitterAuthClient, savedImageUri, tweetInput);
        twitter.checkActiveSession();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        //http://stackoverflow.com/questions/30455513/twitter-sdk-requestcode
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            mTwitterAuthClient.onActivityResult(requestCode, responseCode, intent);
        } else if (requestCode == 1) {
            if (responseCode == RESULT_OK) {
                Toast.makeText(TweetActivity.this, "saved image", Toast.LENGTH_SHORT).show();
                captureImgStatus.setText("Image added - click to remove");
                captureImgStatus.setClickable(true);

            } else {
                //Error saving captured image.
                //Removed the saved uri - image didn't get saved.
                savedImageUri = null;

            }
        }
    }

    //Take photo intent - https://developer.android.com/training/camera/photobasics.html.
    public void captureImg(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                String directory = "appimages";
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + directory;

                File outputDir = new File(path);
                outputDir.mkdirs();

                File newFile = new File(path + "/" + "latestImgToTweet.png");

                savedImageUri = Uri.fromFile(newFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, savedImageUri);
                startActivityForResult(takePictureIntent, 1);
            } else {
                Toast.makeText(TweetActivity.this, "Unable to take photo", Toast.LENGTH_SHORT).show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImg(null);
                } else {
                    //If permission is not granted, error message is displayed.
                    Toast.makeText(this, "WRITE_EXTERNAL_STORAGE permission is disabled. Please grant this permission to add images to tweets.", Toast.LENGTH_SHORT).show();
                }
        }
    }

    //Used to clear tweet input edittext when 'x' to the right of it is pressed
    public void clearInput(View view) {
        tweetInput.setText("");
    }

    //Clears the saved image uri varaible when the remove text is pressed. This prevents the captured image from being tweeted.
    public void clearImage(View view) {
        if (captureImgStatus.getText().toString() == "Image added - click to remove") {
            //Removing uri from varaible so no is attached to the tweet.
            savedImageUri = null;
            //Setting captureImgStatus text.
            captureImgStatus.setText("No image added");
            //Disables clicking.
            captureImgStatus.setClickable(false);
        }
    }

}
