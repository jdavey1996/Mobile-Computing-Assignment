package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //Get arraylist containing downloaded data.
        Intent intent = getIntent();
        ArrayList<RecipeConstructor> recipes = intent.getParcelableArrayListExtra("recipes");


        Boolean loadedFromCache = intent.getBooleanExtra("loadedFromCache", false);
        Toast.makeText(this, loadedFromCache.toString(), Toast.LENGTH_SHORT).show();
        //Create instance of custom array adapter, passing the arraylist of data.
        SearchResultsAdapter searchResultsAdapter = new SearchResultsAdapter(this, this, recipes, loadedFromCache);

        //Set adapter for the listview - adds data to the list.
        ListView searchResults = (ListView) findViewById(R.id.searchResultsListView);
        searchResults.setAdapter(searchResultsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Clear temporary images - used to get downloaded images in different activities. Temporary imgs no longer needed as this activity is closing.
        Storage storage = new Storage();
        storage.clearImgCache(this, true);
    }
}
