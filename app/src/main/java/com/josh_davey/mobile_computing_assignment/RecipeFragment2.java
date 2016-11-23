package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/*References:
    https://developer.android.com/guide/components/fragments.html*/
public class RecipeFragment2 extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate view for fragment.
        View view = inflater.inflate(R.layout.recipe_fragment2_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Get intent from RecipeActivity.
        Intent intent = getActivity().getIntent();
        //Get ingredients list from intent.
        ArrayList<RecipeIngredientsConstructor> ingredients = intent.getParcelableArrayListExtra("ingredients");
        /*Set ingredients listview adapter to an instance of the IngredientsAdapter class.
          This instance uses the ingredients arraylist from the intent as data.*/
        ListView ingredientsList = (ListView)view.findViewById(R.id.ingredientsList);
        IngredientsAdapter arrayAdapter = new IngredientsAdapter(getActivity(),getContext(),ingredients);
        ingredientsList.setAdapter(arrayAdapter);
    }
}
