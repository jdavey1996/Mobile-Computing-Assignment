package com.josh_davey.mobile_computing_assignment;

import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "IBzqCFJ6tTHZHHBF5PSsd7LsU";
    private static final String TWITTER_SECRET = "KzDQs98rknhe3eYSLcm9hVI8BVH1PoT8inPMQi2EmCQNPh684D";

//REFERENCES:
    //1 - https://developer.android.com/training/implementing-navigation/lateral.html
    //2 - https://developer.android.com/reference/android/support/design/widget/TabLayout.html
    //3 - https://developer.android.com/reference/android/support/v4/app/FragmentPagerAdapter.html
    //4 - http://stackoverflow.com/questions/18341779/preload-fragments-view-in-viewpager-on-first-run
    //5 - https://www.simplifiedcoding.net/android-tablayout-example-using-viewpager-fragments/#
    //6 - https://developer.android.com/training/appbar/index.html

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialises the fabric plugin with twitter authentication. This was automatically generated by the plguin.
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        //Initialise toolbar and set it up as the supportActionBar so the onCreateOptionsMenu and onOptionsItemSelected methods link correctly to the toolbar.
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Add tabs to the tab layout - See ref 2.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Recipe search"));
        tabLayout.addTab(tabLayout.newTab().setText("Local supermarkets"));
        //Ensures the tabs stretch to fill the layout container.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Creates an instance of the FragmentAdapter class, passing the fragment manager and count of tabs - see ref 3.
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),this);

        //Sets the viewpager adapter as the FragmentAdapter created above.
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentAdapter);
        //Keep all fragments loaded, ready to use - See ref 4.
        viewPager.setOffscreenPageLimit(1);
        //Add listener to the view pager to keep the selected tab and selected page in sync.
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Listener for when a tab item is selected, unselected or reselected.
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            /*This loads the correct fragment when a tab item is pressed, implemented by calling the viewpager and
              passing the tab position. This links to the getItem method in the FragmentAdapter.*/
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}

