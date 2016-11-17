package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Fragment1 extends Fragment {
    //https://developer.android.com/guide/components/fragments.html
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText query = (EditText)view.findViewById(R.id.searchQuery);
        final Spinner type = (Spinner)view.findViewById(R.id.searchType);
        final Spinner amount = (Spinner)view.findViewById(R.id.searchAmount);



        Button getBtn = (Button)view.findViewById(R.id.submitQuery);
        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecipesAsync searchRecipes = new RecipesAsync(getActivity(),getContext());
                if (searchRecipes.getStatus() != AsyncTask.Status.RUNNING) {
                    //http://stackoverflow.com/questions/30618600/asynctask-takes-a-long-time-before-entering-doinbackground
                    searchRecipes.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,query.getText().toString(),type.getSelectedItem().toString(),amount.getSelectedItem().toString());
                }else {
                    Toast.makeText(getContext(), "running", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView recentlyViewed =(TextView)view.findViewById(R.id.recentlyViewedBtn);
        recentlyViewed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CHECK IF ANY RECENTLY VIEWED EXIST
                SQLiteDb db = new SQLiteDb(getContext());
                ArrayList<RecipeConstructor> test = new ArrayList();
                Cursor c = db.getTABLE_RECIPE_INFO();
                try {
                    while (c.moveToNext()) {

                        test.add(new RecipeConstructor(c.getString(0),c.getString(1),c.getString(2)));
                    }
                } finally {
                    if (test.size() != 0) {
                        Intent intent = new Intent(getContext(), SearchResultsActivity.class);
                        intent.putExtra("loadedFromCache",true);
                        intent.putParcelableArrayListExtra("recipes", test);
                        getContext().startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Your recently viewed list is empty.", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
       /* //GET RECENT SEARCHES....
        SQLiteDb db = new SQLiteDb(getContext());
        ArrayList test = new ArrayList();
        Cursor c = db.getTABLE_SEARCH_HISTORY();

        try {
            while (c.moveToNext()) {
                test.add(c.getString(1));
            }
        } finally {
            ListView listView = (ListView)getActivity().findViewById(R.id.lstRecentSearches);
            TextView emptyText = (TextView)getActivity().findViewById(R.id.emptyTxt);
            ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1, test);
            listView.setAdapter(arrayAdapter);
            listView.setEmptyView(emptyText);
            c.close();
        }*/
    }
}
