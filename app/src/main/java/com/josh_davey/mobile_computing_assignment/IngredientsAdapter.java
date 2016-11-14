package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class IngredientsAdapter extends ArrayAdapter<RecipeIngredientsConstructor> {
    Activity activity;
    Context ctx;
    public IngredientsAdapter(Activity activity, Context ctx, ArrayList<RecipeIngredientsConstructor > data) {
        super(activity, 0, data);
        this.activity = activity;
        this.ctx = ctx;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater taskInflater = LayoutInflater.from(getContext());
        if (view == null) {
            view = taskInflater.inflate(R.layout.recipe_ingredients_item, parent, false);
        }

        //Get item at current position.
        final RecipeIngredientsConstructor item = getItem(position);

        TextView ingredientName = (TextView) view.findViewById(R.id.ingredientName);
        ingredientName.setText(item.getIngredient());

        TextView ingredientQuantity = (TextView) view.findViewById(R.id.ingredientQuantity);
        ingredientQuantity.setText("- "+item.getAmount()+" "+item.getUnit());

        return view;
    }
}

