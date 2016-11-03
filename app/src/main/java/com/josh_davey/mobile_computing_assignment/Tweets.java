package com.josh_davey.mobile_computing_assignment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;

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
