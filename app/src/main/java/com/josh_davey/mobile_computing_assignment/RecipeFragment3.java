package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecipeFragment3 extends Fragment{
    //https://developer.android.com/guide/components/fragments.html
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_fragment3_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Intent intent = getActivity().getIntent();
        ArrayList<String> list = intent.getStringArrayListExtra("steps");

        ListView ingredientsList = (ListView)view.findViewById(R.id.instructionsList);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1, list);
        ingredientsList.setAdapter(arrayAdapter);
    }



}
