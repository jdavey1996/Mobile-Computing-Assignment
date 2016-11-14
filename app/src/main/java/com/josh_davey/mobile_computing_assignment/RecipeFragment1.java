package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeFragment1 extends Fragment{
    //https://developer.android.com/guide/components/fragments.html
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_fragment1_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Intent intent = getActivity().getIntent();
        String recipeTitle = intent.getStringExtra("title");
        String readyIn = intent.getStringExtra("readyIn");

        TextView title = (TextView)view.findViewById(R.id.recipeTitle);
        TextView readyInMinutes = (TextView)view.findViewById(R.id.readyInMinutes);

        title.setText(recipeTitle);
        readyInMinutes.setText("Ready in "+readyIn+"minutes");
    }



}
