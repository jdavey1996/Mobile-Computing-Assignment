package com.josh_davey.mobile_computing_assignment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

////http://stackoverflow.com/questions/27267809/using-custom-login-button-with-twitter-fabric
//*****************8https://docs.fabric.io/android/twitter/compose-tweets.html#results*** USED FOR ALL TWITTER STUFF
public class Tweets {
    Context ctx;
    Activity activity;
    TwitterAuthClient mTwitterAuthClient;

    public Tweets(Context ctx, Activity activity, TwitterAuthClient mTwitterAuthClient)
    {
        this.ctx = ctx;
        this.activity = activity;
        this.mTwitterAuthClient = mTwitterAuthClient;
    }

    public void checkActiveSession()
    {
        //If there's no active session, authorise and initiate callback for authorisation.
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null) {
            loginTwitter();
        }
        else
        {
            createTweet();
        }
    }

    public void loginTwitter()
    {
        mTwitterAuthClient.authorize(activity, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                createTweet();
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
                Toast.makeText(ctx, "Cannot log in", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void createTweet()
    {
        final TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        final Intent intent = new ComposerActivity.Builder(ctx)
                .session(session)
                .createIntent();
        ctx.startActivity(intent);
    }






}
