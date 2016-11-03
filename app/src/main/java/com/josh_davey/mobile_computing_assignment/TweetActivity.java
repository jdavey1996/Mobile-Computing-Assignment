package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;

import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

public class TweetActivity extends Activity {

    TwitterAuthClient mTwitterAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
    }

    public void startTweet(View view)
    {
        mTwitterAuthClient = new TwitterAuthClient();

        Tweets twitter = new Tweets(this,this,mTwitterAuthClient);
        //If there's no active session, authorise and initiate callback for authorisation.
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null) {
            twitter.loginTwitter();
        }
        else
        {
            twitter.createTweet();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        //http://stackoverflow.com/questions/30455513/twitter-sdk-requestcode
        if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            mTwitterAuthClient.onActivityResult(requestCode, responseCode, intent);
        }
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

}
