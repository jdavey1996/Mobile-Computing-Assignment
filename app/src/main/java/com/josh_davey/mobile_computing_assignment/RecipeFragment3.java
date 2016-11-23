package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/*References:
    https://developer.android.com/guide/components/fragments.html*/
public class RecipeFragment3 extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate view for fragment.
        View view = inflater.inflate(R.layout.recipe_fragment3_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Get intent from RecipeActivity.
        Intent intent = getActivity().getIntent();
        //Get instructions arraylist from intent.
        ArrayList<String> instructions = intent.getStringArrayListExtra("steps");
        //Set instructions listview adapter as a simple ArrayAdapter instance, populated by instructions from the intent.
        ListView instructionsList = (ListView)view.findViewById(R.id.instructionsList);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1, instructions);
        instructionsList.setAdapter(arrayAdapter);
    }
}
