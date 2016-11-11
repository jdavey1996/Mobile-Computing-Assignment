package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;

public class SearchResultsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        //Get arraylist containing downloaded data.
        Intent intent = getIntent();
        ArrayList<RecipeConstructor>recipes = intent.getParcelableArrayListExtra("recipes");

        //Create instance of custom array adapter, passing the arraylist of data.
        SearchResultsAdapter searchResultsAdapter = new SearchResultsAdapter(this,this,recipes);

        //Set adapter for the listview - adds data to the list.
        ListView searchResults = (ListView)findViewById(R.id.searchResultsListView);
        searchResults.setAdapter(searchResultsAdapter);
    }
}
