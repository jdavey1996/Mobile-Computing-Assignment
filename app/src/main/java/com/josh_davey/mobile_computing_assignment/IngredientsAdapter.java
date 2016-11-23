package com.josh_davey.mobile_computing_assignment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/*References:
    https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 */
public class IngredientsAdapter extends ArrayAdapter<RecipeIngredientsConstructor> {
    //Variables.
    Activity activity;
    Context ctx;

    public IngredientsAdapter(Activity activity, Context ctx, ArrayList<RecipeIngredientsConstructor > data) {
        super(activity, 0, data);
        this.activity = activity;
        this.ctx = ctx;
    }

    //This method is called for the length of data in the arraylist, setting the textfield values and adding each to a listview.
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        //Inflate view for recipe ingredients item.
        LayoutInflater taskInflater = LayoutInflater.from(getContext());
        if (view == null) {
            view = taskInflater.inflate(R.layout.recipe_ingredients_item, parent, false);
        }

        //Get item at current position.
        final RecipeIngredientsConstructor item = getItem(position);

        //Set textfields to the correct values based on the position as the arraylist is looped through.
        TextView ingredientName = (TextView) view.findViewById(R.id.ingredientName);
        ingredientName.setText(item.getIngredient());

        TextView ingredientQuantity = (TextView) view.findViewById(R.id.ingredientQuantity);
        ingredientQuantity.setText("- "+item.getAmount()+" "+item.getUnit());

        return view;
    }
}

