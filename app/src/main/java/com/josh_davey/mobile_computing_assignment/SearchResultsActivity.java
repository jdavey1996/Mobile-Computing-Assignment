package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Adds layout to activity.
        setContentView(R.layout.activity_search_results);

        //Get intent.
        Intent intent = getIntent();

        //Get arraylist containing recipe search results from intent.
        ArrayList<RecipeConstructor> recipes = intent.getParcelableArrayListExtra("recipes");

        //Get boolean value to check if recipe results have been downloaded or loaded from the SQLite database.
        Boolean loadedFromCache = intent.getBooleanExtra("loadedFromCache", false);

        //Create instance of custom array adapter, passing the arraylist of data.
        SearchResultsAdapter searchResultsAdapter = new SearchResultsAdapter(this, this, recipes, loadedFromCache);

        //Set adapter for the listview - adds data to the list.
        ListView searchResults = (ListView) findViewById(R.id.searchResultsListView);
        searchResults.setAdapter(searchResultsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*When this activity is destroyed, run a method that clears any temporary images.
          Passes true to this method so temporary images are removed, not permanent one.*/
        Storage storage = new Storage();
        storage.clearImgCache(this, true);
    }
}
