package com.josh_davey.mobile_computing_assignment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class RecipeFragment1 extends Fragment{
    //https://developer.android.com/guide/components/fragments.html


    Intent intent;
    String recipeTitle;
    String readyIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_fragment1_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        intent = getActivity().getIntent();
        recipeTitle = intent.getStringExtra("title");
        readyIn = intent.getStringExtra("readyIn");

        TextView title = (TextView)view.findViewById(R.id.recipeTitle);
        TextView readyInMinutes = (TextView)view.findViewById(R.id.readyInMinutes);

        title.setText(recipeTitle);
        readyInMinutes.setText("Ready in "+readyIn+"minutes");

    }




       /* //Save data.
        SQLiteDbAsync sqLiteDbAsync = new SQLiteDbAsync(getContext());
        sqLiteDbAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                "save",
                id,
                recipeTitle,
                readyIn,
                thumbnailImg,
                fullImg,
                ingredients,
                instructions);*/


}
