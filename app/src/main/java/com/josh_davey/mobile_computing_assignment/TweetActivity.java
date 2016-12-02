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
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.io.File;

/*References:
    http://stackoverflow.com/questions/16007750/how-to-create-custom-popupwindow-from-intent-call
    http://stackoverflow.com/questions/30455513/twitter-sdk-requestcode
    https://developer.android.com/training/camera/photobasics.html.*/

public class TweetActivity extends Activity {
    //Variables.
    TwitterAuthClient mTwitterAuthClient;
    Uri savedImageUri;
    EditText tweetInput;
    TextView captureImgStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Adds layout to activity.
        setContentView(R.layout.activity_tweet);

        //Sets the activity size - makes it a popup window.
        getWindow().setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        //Gets instance of tweetInput edittext.
        tweetInput = (EditText) findViewById(R.id.tweetInput);

        //Gets instance of captureImgStatus textview and sets it to unclickable
        captureImgStatus = (TextView) findViewById(R.id.captureImgStatus);
        captureImgStatus.setClickable(false);
    }

    public void startTwitter(View view) {
        /*Starts the checkActiveSession method. This checks if the device contains a login token for twitter.
          This occurs when attempting to send a tweet. All Twitter functionality is handled in Tweets.class,
          and checkActiveSession is the only method needing to be called to send a tweet as the rest follows on from it.*/
        mTwitterAuthClient = new TwitterAuthClient();
        Tweets twitter = new Tweets(this, this, mTwitterAuthClient, savedImageUri, tweetInput);
        twitter.checkActiveSession();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        //If requestcode is for twitter authentication, run the onActivityResult of auth client, handling managing session tokens.
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            mTwitterAuthClient.onActivityResult(requestCode, responseCode, intent);
        }
        //If request code is 1, it's referring to captureImg methods activity result.
        else if (requestCode == 1) {
            //If result is successful, an image has been added, so change the text to allow it to be removed from the tweet.
            if (responseCode == RESULT_OK) {
                captureImgStatus.setText("Image added - click to remove");
                captureImgStatus.setClickable(true);
            } else {
                //Error saving captured image.
                //Removed the saved uri - image didn't get saved.
                savedImageUri = null;
            }
        }
    }

    //Method for capturing an image.
    public void captureImg(View view) {
        //Check permissions to write to external storage. Only allows image to be captured if permission is granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Create an intent to load camera.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //If intent is created successfully.
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                Storage storage = new Storage();
                //Check if storage is readable and writable before attempting to take picture.
                if(storage.isExternalStorageWritable()) {
                    //Create file to store image, within external storage picture directory.
                    File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + "latestImgToTweet.png");

                    //Set uri to image path so it can be accessed when uploading to twitter.
                    savedImageUri = Uri.fromFile(newFile);

                    //Start the activity and put extra to contain the uri of the image when an action has occurred in the intent.
                    //Once an action in the intent occurs, this uses code from the onActivityResult method.
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, savedImageUri);
                    startActivityForResult(takePictureIntent, 1);
                }
                else
                {
                    //Error has ocurred storage isn't readable or writable.
                    Toast.makeText(this,"Storage isn't readable or writable, unable to add image to tweet.",Toast.LENGTH_SHORT);
                }
            } else {
                //Error has occurred, couldn't load camera intent.
                Toast.makeText(TweetActivity.this, "Unable to take photo", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Error has occurred, permissions are not enabled. Request permission to write external storage.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    //Method to use permission request result.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //If permissions are granted, load the captureImg method again, allowing the user to continue.
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

    /*Clears the saved image uri variable when the remove text is pressed.
     This prevents the captured image from being tweeted if the user changes their mind.*/
    public void clearImage(View view) {
        //Removing uri from variable so it's no longer attached to the tweet.
        savedImageUri = null;
        //Setting captureImgStatus text.
        captureImgStatus.setText("No image added");
        //Disables clicking the status text.
        captureImgStatus.setClickable(false);
    }
}
