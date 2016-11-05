package com.josh_davey.mobile_computing_assignment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
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
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;

////http://stackoverflow.com/questions/27267809/using-custom-login-button-with-twitter-fabric
//*****************8https://docs.fabric.io/android/twitter/compose-tweets.html#results*** USED FOR ALL TWITTER STUFF
public class Tweets {
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
            if(savedImageUri == null) {
                sendTweet(null);
            }
            else
            {
                uploadMedia(savedImageUri);
            }
        }
    }

    public void loginTwitter()
    {
        mTwitterAuthClient.authorize(activity, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                Toast.makeText(ctx, twitterSessionResult.data.getUserName(), Toast.LENGTH_SHORT).show();
                if(savedImageUri == null) {
                    sendTweet(null);
                }
                else
                {
                    uploadMedia(savedImageUri);
                }
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
                Toast.makeText(ctx, "Cannot log in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadMedia(Uri uri) {
        //Get apiclient instance that's authenticated already.
        twitterApiClient = TwitterCore.getInstance().getApiClient();

        //Get image taken.
        File photo = new File(uri.getPath());

        //Create request body with image to upload.
        RequestBody file = RequestBody.create(MediaType.parse("image/*"), photo);

        //Upload image, request callback.
        MediaService ms = twitterApiClient.getMediaService();
        Call<Media> uploadImgCall = ms.upload(file, null, null);
        uploadImgCall.enqueue(new Callback<Media>() {
            @Override
            public void success(Result<Media> result) {
                Toast.makeText(ctx, "Image uploaded" + result.data.mediaIdString, Toast.LENGTH_SHORT).show();
                sendTweet(result.data.mediaIdString);
            }
            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(ctx, "Image not uploaded" + exception, Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void sendTweet(String mediaIdString)
    {
        /*****THIS METHOD IS STILL BEING TESTED - uploads captured image and if successful, tweets it.*/
        //https://docs.fabric.io/android/twitter/access-rest-api.html
        //http://stackoverflow.com/questions/31785698/android-adding-image-to-tweet-using-fabric-twitter-rest-api-and-retrofit
        //*************https://dev.twitter.com/rest/reference/post/statuses/update CHECK THIS OUT....*******
        //Used for request body initialising Retroyfit 2.0- http://stackoverflow.com/questions/34562950/post-multipart-form-data-using-retrofit-2-0-including-image
        twitterApiClient = TwitterCore.getInstance().getApiClient();
        StatusesService statusesService = twitterApiClient.getStatusesService();
        Call<Tweet> tweetCall = statusesService.update(tweetInput.getText().toString(), null, false, null, null, null, false, false, mediaIdString);
        tweetCall.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                //Do something with result
                Toast.makeText(ctx, "Tweeted", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
            public void failure(TwitterException exception) {
                //Do something on failure
                Toast.makeText(ctx, "Not Tweeted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
