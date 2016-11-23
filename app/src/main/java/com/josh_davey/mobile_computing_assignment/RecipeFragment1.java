package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/*References:
    https://developer.android.com/guide/components/fragments.html*/
public class RecipeFragment1 extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate view for fragment.
        View view = inflater.inflate(R.layout.recipe_fragment1_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Get intent from RecipeActivity.
        Intent intent = getActivity().getIntent();
        //Get title of recipe from intent.
        String recipeTitle = intent.getStringExtra("title");
        //Get ready in value for recipe from intent.
        String readyIn = intent.getStringExtra("readyIn");

        //Set title textview to recipe title from intent.
        TextView title = (TextView)view.findViewById(R.id.recipeTitle);
        title.setText(recipeTitle);

        //Set ready in textview to recipe value from intent.
        TextView readyInMinutes = (TextView)view.findViewById(R.id.readyInMinutes);
        readyInMinutes.setText("Ready in "+readyIn+"minutes");
    }
}
