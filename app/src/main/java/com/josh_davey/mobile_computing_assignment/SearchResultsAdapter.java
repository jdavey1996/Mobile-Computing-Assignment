package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchResultsAdapter extends ArrayAdapter<RecipeConstructor> {
    Activity activity;
    Context ctx;
    public SearchResultsAdapter(Activity activity, Context ctx, ArrayList<RecipeConstructor > data) {
        super(activity, 0, data);
        this.activity = activity;
        this.ctx = ctx;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        //Inflate layout using recipe list item layout.
        LayoutInflater taskInflater = LayoutInflater.from(getContext());
        if (view == null) {
            view = taskInflater.inflate(R.layout.recipe_list_item, parent, false);
        }

        //Get item at current position.
        RecipeConstructor item = getItem(position);

        //Set recipeTitle TextView with data from arraylist.
        TextView recipeTitle = (TextView) view.findViewById(R.id.recipeTitle);
        recipeTitle.setText(item.getTitle());

        //Set recipeReadyIn TextView with data from arraylist.
        TextView recipeReadyIn = (TextView) view.findViewById(R.id.recipeReadyIn);
        recipeReadyIn.setText("Ready in "+item.getReadyInMinutes()+" minutes");

        //Set recipeThumbnail ImageVie the correct cached image based on the corresponding recipeId.
        ImageView recipeThumbnail = (ImageView)view.findViewById(R.id.recipeThumbnail);
        Storage getImage = new Storage();
        recipeThumbnail.setImageBitmap(getImage.getTepImg(ctx,item.getId()));

        //Onclick method for clicking a list item.
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}

