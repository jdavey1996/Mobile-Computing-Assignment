package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Adds layout to activity.
        setContentView(R.layout.activity_recipe);

        //Initialise toolbar and set it up as the supportActionBar so the onCreateOptionsMenu and onOptionsItemSelected methods link correctly to the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Add tabs to the tab layout.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.recipeTabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Overview"));
        tabLayout.addTab(tabLayout.newTab().setText("Ingredients"));
        tabLayout.addTab(tabLayout.newTab().setText("Steps"));
        //Ensures the tabs stretch to fill the layout container.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Creates an instance of the FragmentAdapter class, passing the fragment manager and count of tabs.
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), this);

        //Sets the viewpager adapter as the FragmentAdapter created above.
        final ViewPager viewPager = (ViewPager) findViewById(R.id.recipeViewPager);
        viewPager.setAdapter(fragmentAdapter);
        //Keep all fragments loaded, ready to use.
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

        //Variables to store image path in.
        String imagePath;
        //Get recipe ID for loaded recipe.
        String id = getIntent().getStringExtra("recipeId");
        //Get instance of Storage class to get image.
        Storage getImage = new Storage();
        //Set imagePath variable with file name depending on whether the recipe has just been downloaded or has been viewed from recently viewed.
        if (!getIntent().getBooleanExtra("loadedFromCache", false)) {
            imagePath = id + "_full_size_temp";
        } else {
            imagePath = id + "_full_size";
        }

        //Set image for recipe. Attempts to get it, toasts if unable to.
        ImageView recipeImg = (ImageView) findViewById(R.id.recipeDetailImg);
        try {
            recipeImg.setImageBitmap(getImage.getTepImg(this, imagePath));
        }catch (Exception e)
        {
            Toast.makeText(this, "Unable to get image for this recipe.", Toast.LENGTH_SHORT).show();
        }

        //Call save recipe method.
        saveRecipe();
    }

    //Load activity for composing a tweet.
    public void startTweetActivity(View view) {
        //Checks if device is connected to either a wifi network or mobile network. Toast if not.
        NetworkStatus networkStatus = new NetworkStatus(this);
        if(networkStatus.checkConnection()) {
            //Loads tweet activity as overlay where you can compose a tweet with an image.
            Intent intent = new Intent(this, TweetActivity.class);
            startActivity(intent);
        }
    }

    //Save data in database.
    public void saveRecipe() {
        //If boolean extra "loadedFromCache" is false, recipe has only just been viewed so save it in database.
        if (!getIntent().getBooleanExtra("loadedFromCache", false)) {
            //Get all recipe details from intent.
            String id = getIntent().getStringExtra("recipeId");
            String title = getIntent().getStringExtra("title");
            String readyIn = getIntent().getStringExtra("readyIn");
            ArrayList<RecipeIngredientsConstructor> ingredients = getIntent().getParcelableArrayListExtra("ingredients");
            ArrayList<String> instructions = getIntent().getStringArrayListExtra("steps");

            //Call asynctask to save a recipe in the SQLite database, passing all information about the selected recipe.
            SQLiteSaveRecipeAsync sqLiteSaveRecipeAsync = new SQLiteSaveRecipeAsync(this);
            sqLiteSaveRecipeAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, id, title, readyIn, ingredients, instructions);
        }
    }
}

