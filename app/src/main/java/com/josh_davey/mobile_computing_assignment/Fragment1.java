package com.josh_davey.mobile_computing_assignment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/*References:
    https://developer.android.com/guide/components/fragments.html
    http://stackoverflow.com/questions/30618600/asynctask-takes-a-long-time-before-entering-doinbackground*/
public class Fragment1 extends Fragment {
    //Variables.
    EditText query;
    Spinner type, amount;
    NetworkStatus checkNetworkStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Set fragment layout.
        View view = inflater.inflate(R.layout.fragment1_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get instance of user inputs to get search parameters that can be passed to the asynctask.
        query  = (EditText)view.findViewById(R.id.searchQuery);
        type  = (Spinner)view.findViewById(R.id.searchType);
        amount = (Spinner)view.findViewById(R.id.searchAmount);

        //Get instance of network status to call checkConnection method.
        checkNetworkStatus = new NetworkStatus(getContext());

        //Button functionality to search for recipes.
        Button searchRecipesBtn = (Button)view.findViewById(R.id.searchBtn);
        searchRecipesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecipesAsync searchRecipesAsync = new RecipesAsync(getActivity(),getContext());
                //Check if async task isn't currently running.
                if (searchRecipesAsync.getStatus() != AsyncTask.Status.RUNNING) {
                    //Checks if device is connected to either a wifi network or mobile network. Error message is defined within method.
                    if(checkNetworkStatus.checkConnection()) {
                        //Execute asynctask on THREAD_POOL_EXECUTOR (Downloading recipe search data), this allows multiple asynctasks to be executing at the same time.
                        searchRecipesAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query.getText().toString(), type.getSelectedItem().toString(), amount.getSelectedItem().toString());
                    }
                }
            }
        });

        //Button functionality to get recently viewed recipes - getting from SQLite database.
        Button recentlyViewed =(Button) view.findViewById(R.id.recentlyViewedBtn);
        recentlyViewed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Execute asynctask on THREAD_POOL_EXECUTOR (Getting recipe data from SQLite)
                SQLiteGetRecipeListAsync getRecipeListAsync = new SQLiteGetRecipeListAsync(getContext());
                getRecipeListAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        //Image view (x to right of query EditText), acting as a button to clear entry on click.
        ImageView clearQueryTxt = (ImageView)view.findViewById(R.id.clearSearchQueryBtn);
        clearQueryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.setText("");
            }
        });
    }
}
