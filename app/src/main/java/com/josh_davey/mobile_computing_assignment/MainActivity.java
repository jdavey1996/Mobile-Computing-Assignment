package com.josh_davey.mobile_computing_assignment;

import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import net.sqlcipher.database.SQLiteDatabase;

import io.fabric.sdk.android.Fabric;
/*REFERENCES:
    https://developer.android.com/reference/android/support/design/widget/TabLayout.html
    http://stackoverflow.com/questions/18341779/preload-fragments-view-in-viewpager-on-first-run
    https://www.simplifiedcoding.net/android-tablayout-example-using-viewpager-fragments/#
 */
public class MainActivity extends AppCompatActivity {

    //Twitter API key and secret, generated automatically.
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "IBzqCFJ6tTHZHHBF5PSsd7LsU";
    private static final String TWITTER_SECRET = "KzDQs98rknhe3eYSLcm9hVI8BVH1PoT8inPMQi2EmCQNPh684D";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Adds layout to activity.
        setContentView(R.layout.activity_main);

        //Loads data for Encrypted SQLite database, using SQLCipher.
        SQLiteDatabase.loadLibs(this);

        //Initialises the fabric plugin with twitter authentication. This was automatically generated by the plugin.
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        //Initialise toolbar and set it up as the supportActionBar so the onCreateOptionsMenu and onOptionsItemSelected methods link correctly to the toolbar.
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Add tabs to the tab layout.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Recipe search"));
        tabLayout.addTab(tabLayout.newTab().setText("Local supermarkets"));
        //Ensures the tabs stretch to fill the layout container.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Creates an instance of the FragmentAdapter class, passing the fragment manager and count of tabs.
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),this);
        //Sets the viewpager adapter as the FragmentAdapter created above.
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(fragmentAdapter);
        //Keep all (2) fragments loaded, ready to use.
        viewPager.setOffscreenPageLimit(1);
        //Add listener to the view pager to keep the selected tab and selected page in sync when user swipes between them instead of selecting tab buttons.
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

    //Create toolbar menu using create toolbar_menu resource file.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //Toolbar options overflow menu button functions.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearRecent:
                //Runs SQLiteDeleteRecipesAsync, this deletes any recently viewed recipes by removing contents from the SQLite database.
                SQLiteDeleteRecipesAsync sqLiteDeleteRecipesAsync = new SQLiteDeleteRecipesAsync(this);
                sqLiteDeleteRecipesAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return true;

            case R.id.unlinkTwitter:
                //This runs the unlink twitter method, clearing any active sessions and cookies.
                Tweets tweet = new Tweets(this,this,null,null,null);
                tweet.unlinkTwitter();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

