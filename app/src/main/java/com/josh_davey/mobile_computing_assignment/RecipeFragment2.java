package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecipeFragment2 extends Fragment{
    //https://developer.android.com/guide/components/fragments.html
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_fragment2_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = getActivity().getIntent();
        ArrayList<RecipeIngredientsConstructor> list = intent.getParcelableArrayListExtra("ingredients");

        ListView ingredientsList = (ListView)view.findViewById(R.id.ingredientsList);
        IngredientsAdapter arrayAdapter = new IngredientsAdapter(getActivity(),getContext(),list);
        ingredientsList.setAdapter(arrayAdapter);
    }
}
