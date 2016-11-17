package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.twitter.sdk.android.core.TwitterCore;

import java.io.File;
import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        //Initialise toolbar and set it up as the supportActionBar so the onCreateOptionsMenu and onOptionsItemSelected methods link correctly to the toolbar.
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Add tabs to the tab layout - See ref 2.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.recipeTabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Ingredients"));
        tabLayout.addTab(tabLayout.newTab().setText("Steps"));
        //Ensures the tabs stretch to fill the layout container.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Creates an instance of the FragmentAdapter class, passing the fragment manager and count of tabs - see ref 3.
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),this);

        //Sets the viewpager adapter as the FragmentAdapter created above.
        final ViewPager viewPager = (ViewPager) findViewById(R.id.recipeViewPager);
        viewPager.setAdapter(fragmentAdapter);
        //Keep all fragments loaded, ready to use - See ref 4.
        viewPager.setOffscreenPageLimit(2);
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

        ImageView recipeImg = (ImageView)findViewById(R.id.recipeDetailImg);
        Storage getImage = new Storage();
        String id =  getIntent().getStringExtra("recipeId");
        String imagePath;

        //If not loaded from cache
        if(!getIntent().getBooleanExtra("loadedFromCache",false)) {
            imagePath = id + "_full_size_temp";
            Toast.makeText(this, "NOT LOADED FROM CACHE", Toast.LENGTH_SHORT).show();
        }
        else
        {
            imagePath = id + "_full_size";
        }

        recipeImg.setImageBitmap(getImage.getTepImg(this, imagePath));
        saveRecipe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearRecent:

                return true;

            case R.id.unlinkTwitter:
                Tweets tweets = new Tweets(this,null,null,null,null);
                tweets.unlinkTwitter();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startTweetActivity(View view)
    {
        //Loads tweet activity as overlay where you can compose a tweet with an image.
        Intent intent = new Intent(this,TweetActivity.class);
        startActivity(intent);
    }


    //Save data in database.
    public void saveRecipe()
    {
       if(!getIntent().getBooleanExtra("loadedFromCache",false)) {
           String id = getIntent().getStringExtra("recipeId");
           String title = getIntent().getStringExtra("title");
           String readyIn = getIntent().getStringExtra("readyIn");
           ArrayList<String> instructions = getIntent().getStringArrayListExtra("steps");
           ArrayList<RecipeIngredientsConstructor> ingredients = getIntent().getParcelableArrayListExtra("ingredients");

           SQLiteDb sql = new SQLiteDb(this);
           sql.insertInto_TABLE_RECIPE_INFO(id, title, readyIn);
           sql.insertInto_TABLE_RECIPE_INGREDIENTS(id, ingredients);
           sql.insertInto_TABLE_RECIPE_INSTRUCTIONS(id, instructions);


           //Get temporary saved images and save in permanent location.
           Storage getImage = new Storage();
           Bitmap fullsizetemp = getImage.getTepImg(this,id+"_full_size_temp");
           getImage.saveTempImg(this,id+"_full_size",fullsizetemp);

           Bitmap thumbnailtemp = getImage.getTepImg(this,id+"_thumbnail_temp");
           getImage.saveTempImg(this,id+"_thumbnail",thumbnailtemp);
       }
        else
       {
           Toast.makeText(this,"Loaded from cache", Toast.LENGTH_SHORT).show();
       }

    }

}

