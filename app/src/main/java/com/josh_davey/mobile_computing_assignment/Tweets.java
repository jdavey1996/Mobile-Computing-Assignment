package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.webkit.CookieManager;
import android.widget.EditText;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.MediaService;
import com.twitter.sdk.android.core.services.StatusesService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

/*References:
    http://stackoverflow.com/questions/27267809/using-custom-login-button-with-twitter-fabric
    https://futurestud.io/tutorials/retrofit-synchronous-and-asynchronous-requests
    http://stackoverflow.com/questions/28075136/how-can-i-use-retrofit-library-with-progressbar
    http://stackoverflow.com/questions/31785698/android-adding-image-to-tweet-using-fabric-twitter-rest-api-and-retrofit
    https://docs.fabric.io/android/twitter/access-rest-api.html
    https://dev.twitter.com/rest/reference/post/statuses/update
    http://stackoverflow.com/questions/34562950/post-multipart-form-data-using-retrofit-2-0-including-image
    http://stackoverflow.com/questions/29743676/how-to-logout-from-twitter-using-fabric-sdk-for-android*/

public class Tweets {
    //Variables.
    Context ctx;
    Activity activity;
    TwitterAuthClient mTwitterAuthClient;
    TwitterApiClient twitterApiClient;
    Uri savedImageUri;
    EditText tweetInput;

    public Tweets(Context ctx, Activity activity, TwitterAuthClient mTwitterAuthClient, Uri savedImageUri, EditText tweetInput)
    {
        this.ctx = ctx;
        this.activity = activity;
        this.mTwitterAuthClient = mTwitterAuthClient;
        this.savedImageUri = savedImageUri;
        this.tweetInput = tweetInput;
    }

    public void checkActiveSession()
    {
        //If there's no active session, authorise and initiate callback for authorisation.
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null) {
            loginTwitter();
        }
        else
        {
            /*Check if an image has been captured for tweeting. If it hasn't, send the tweet.
              If it has, upload the image to twitter prior to tweeting.*/
            if(savedImageUri == null) {
                sendTweet(null);
            }
            else
            {
                uploadMedia(savedImageUri);
            }
        }
    }

    //Login to twitter method.
    public void loginTwitter()
    {
        //Attempt to authorise twitter client.
        mTwitterAuthClient.authorize(activity, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

            //If successful, send either send the tweet or upload media, depending on whether an image has been captured for tweeting.
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                if(savedImageUri == null) {
                    sendTweet(null);
                }
                else
                {
                    uploadMedia(savedImageUri);
                }
            }

            //If login was not successful, toast to user.
            @Override
            public void failure(TwitterException e) {
                Toast.makeText(ctx, "Error occurred, unable to log in.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void uploadMedia(Uri uri) {
        //Get apiclient instance that's authenticated already.
        twitterApiClient = TwitterCore.getInstance().getApiClient();

        //Get captured image.
        File photo = new File(uri.getPath());

        //Create request body with image to upload.
        RequestBody file = RequestBody.create(MediaType.parse("image/*"), photo);

        //Create a progress dialog to notify the user, and show it.
        final ProgressDialog uploadImgDialog = new ProgressDialog(ctx,R.style.ProgressDialogTheme);
        uploadImgDialog.setIndeterminate(true);
        uploadImgDialog.setCancelable(true);
        uploadImgDialog.setCanceledOnTouchOutside(true);
        uploadImgDialog.setTitle("Tweeting..");
        uploadImgDialog.setMessage("Uploading image...");
        uploadImgDialog.show();

        //Upload image, request callback - using asynchronous retrofit calls.
        MediaService ms = twitterApiClient.getMediaService();
        final Call<Media> uploadImgCall = ms.upload(file, null, null);
        uploadImgCall.enqueue(new Callback<Media>() {
            @Override
            public void success(Result<Media> result) {
                //If successful, send the tweet, containing the mediaIdString of the uploaded image.
                sendTweet(result.data.mediaIdString);
                //Remove dialog.
                uploadImgDialog.dismiss();
            }
            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(ctx, "Unable to upload image, tweet not sent.", Toast.LENGTH_SHORT).show();
                uploadImgDialog.dismiss();
            }
        });

        //Cancel listener to cancel uploading image for tweet.
        uploadImgDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                uploadImgCall.cancel();
            }
        });
    }

    /*Send tweet to twitter account using the current authenticated user session. This is called via the Retrofit api,
      therefore this doesn't need to be within an asynctask as the call itself runs asynchronously.*/
    //https://docs.fabric.io/android/twitter/access-rest-api.html
    //https://dev.twitter.com/rest/reference/post/statuses/update
    //Used for request body initialising Retrofit 2.0- http://stackoverflow.com/questions/34562950/post-multipart-form-data-using-retrofit-2-0-including-image
    public void sendTweet(String mediaIdString)
    {
        //Create and show progress dialog.
        final ProgressDialog tweetingDialog = new ProgressDialog(ctx,R.style.ProgressDialogTheme);
        tweetingDialog.setIndeterminate(true);
        tweetingDialog.setCancelable(true);
        tweetingDialog.setCanceledOnTouchOutside(true);
        tweetingDialog.setTitle("Tweeting..");
        tweetingDialog.setMessage("Sending tweet...");
        tweetingDialog.show();

        //Get twitter instance and status service in order to make call to twitter statuses api.
        twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();

        //Send tweet, with or without an image mediaIdString and request callbacks.
        final Call<Tweet> tweetCall = statusesService.update(tweetInput.getText().toString(), null, false, null, null, null, false, false, mediaIdString);
        tweetCall.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                //Close dialog and activity if successful & Toast to user.
                tweetingDialog.dismiss();
                Toast.makeText(ctx, "Your tweet has been tweeted!", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
            public void failure(TwitterException exception) {
                tweetingDialog.dismiss();
                Toast.makeText(ctx, "Error occurred, unable to post tweet.", Toast.LENGTH_SHORT).show();
            }
        });

        //Cancel listener to cancel sending tweet.
        tweetingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                tweetCall.cancel();
            }
        });
    }

    //Clears session, logs user out and removes all cookies - removes traces of user login left.
    public void unlinkTwitter()
    {
        if(TwitterCore.getInstance().getSessionManager().getActiveSession() != null) {
            //Clears active session.
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
            TwitterCore.getInstance().logOut();
            //Removes device cookies.
            //http://stackoverflow.com/questions/29743676/how-to-logout-from-twitter-using-fabric-sdk-for-android
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
            Toast.makeText(ctx, "Twitter has now been disconnected.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(ctx, "No active twitter session currently exists.", Toast.LENGTH_SHORT).show();
        }
    }
}
